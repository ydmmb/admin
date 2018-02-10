package com.universe.yz.admin.ui.fragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.SpaceDecoration;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;
import com.universe.yz.admin.base.BaseMvpFragment;
import com.universe.yz.admin.model.bean.User;
import com.universe.yz.admin.model.bean.VideoInfo;
import com.universe.yz.admin.model.db.RealmHelper;
import com.universe.yz.admin.presenter.VideoInfoPresenter;
import com.universe.yz.admin.presenter.contract.MineContract;
import com.universe.yz.admin.ui.activitys.CacheActivity;
import com.universe.yz.admin.ui.activitys.CollectionActivity;
import com.universe.yz.admin.ui.activitys.HistoryActivity;
import com.universe.yz.admin.ui.activitys.LoginActivity;
import com.universe.yz.admin.ui.activitys.SettingActivity;
import com.universe.yz.admin.ui.activitys.VideoInfoActivity;
import com.universe.yz.admin.utils.AlipayZeroSdk;
import com.universe.yz.admin.utils.BeanUtil;
import com.universe.yz.admin.utils.PermissionsActivity;
import com.universe.yz.admin.utils.ScreenUtil;
import com.universe.yz.admin.utils.StringUtils;
import com.universe.yz.admin.widget.theme.ColorTextView;
import com.universe.yz.admin.R;
import com.universe.yz.admin.helper.SessionManager;
import com.universe.yz.admin.model.bean.VideoType;
import com.universe.yz.admin.presenter.MinePresenter;
import com.universe.yz.admin.ui.adapter.MineHistoryVideoListAdapter;
import com.universe.yz.admin.utils.EventUtil;
import com.universe.yz.admin.utils.FileStorage;
import com.universe.yz.admin.utils.PermissionsChecker;
import com.universe.yz.admin.widget.CircleImageView;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.universe.yz.admin.R.id.recyclerView;

public class MineFragment extends BaseMvpFragment<MinePresenter> implements MineContract.View {
    public static final String SET_THEME = "SET_THEME";
    private static final String TAG = MineFragment.class.getSimpleName();
    public static  String uuid = "";
    MineHistoryVideoListAdapter mAdapter;
    VideoInfo videoInfo;
    @BindView(R.id.title_name)
    ColorTextView titleName;
    @BindView(R.id.rl_them)
    RelativeLayout rlThem;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(recyclerView)
    EasyRecyclerView mRecyclerView;
    @BindView(R.id.tv_history)
    TextView mTvHistory;
    @BindView(R.id.tv_down)
    TextView tvDown;
    @BindView(R.id.tv_collection)
    TextView tvCollection;
    @BindView(R.id.tv_them)
    TextView tvThem;
    @BindView(R.id.tv_reg)
    TextView tvReg;
    @BindView(R.id.tv_type)
    TextView tvType;
    @BindView(R.id.tv_img_reg)
    CircleImageView tvImgeReg;
    @BindView(R.id.tv_member)
    TextView tvMember;

    private static String username ="";

    /* 请求识别码 */
    private static final int CODE_GALLERY_REQUEST = 0xa0;//相册选取
    private static final int CODE_CAMERA_REQUEST = 0xa1; //拍照
    private static final int CODE_RESULT_REQUEST = 0xa2; //剪裁图片
    private static final int REQUEST_PERMISSION = 0xa5;  //权限请求

    private PermissionsChecker mPermissionsChecker; // 权限检测器
    static final String[] PERMISSIONS = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};
    private Uri imageUri;//原图保存地址
    private boolean isClickCamera;
    private String imagePath;

    @Override
    protected int getLayout() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void initView(LayoutInflater inflater) {

        EventBus.getDefault().register(this);
        ((AppCompatActivity) getContext()).setSupportActionBar(toolbar);
        mPermissionsChecker = new PermissionsChecker(mContext);

        toolbar.setTitle("");
        titleName.setText(getResources().getString(R.string.mine_title));

        //tvType.setVisibility(View.GONE);
        StringUtils.setIconDrawable(mContext,tvMember,MaterialDesignIconic.Icon.gmi_eye,16,15);
        StringUtils.setIconDrawable(mContext, mTvHistory, MaterialDesignIconic.Icon.gmi_account_calendar, 16, 15);
        StringUtils.setIconDrawable(mContext, tvDown, MaterialDesignIconic.Icon.gmi_time_countdown, 16, 15);
        StringUtils.setIconDrawable(mContext, tvCollection, MaterialDesignIconic.Icon.gmi_collection_bookmark, 16, 15);
        StringUtils.setIconDrawable(mContext, tvThem, MaterialDesignIconic.Icon.gmi_palette, 16, 15);

        mRecyclerView.setAdapter(mAdapter = new MineHistoryVideoListAdapter(mContext));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 3);
        gridLayoutManager.setSpanSizeLookup(mAdapter.obtainGridSpanSizeLookUp(3));
        mRecyclerView.setLayoutManager(gridLayoutManager);
        SpaceDecoration itemDecoration = new SpaceDecoration(ScreenUtil.dip2px(mContext, 8));
        itemDecoration.setPaddingEdgeSide(true);
        itemDecoration.setPaddingStart(true);
        itemDecoration.setPaddingHeaderFooter(false);
        mRecyclerView.addItemDecoration(itemDecoration);

        readImage();

        //User user = App.getInstance().getUser();
        User user = RealmHelper.getInstance().queryUser();
        Log.e(TAG,"user type:" + user.getType() );

        if(user != null) {
            tvReg.setText(user.getName());
            Log.e(TAG,"user type:" + user.getType() );
            uuid = user.getId()+"_"+user.getToken();
            tvType.setText(user.getType());
        }
        else {
            tvType.setText("");
        }
    }

    @Override
    protected void initEvent() {
        mAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                videoInfo = BeanUtil.VideoType2VideoInfo(mAdapter.getItem(position), videoInfo);
                VideoInfoActivity.start(getContext(), videoInfo);
            }
        });
    }

    @Override
    public void showError(String msg) {
        EventUtil.showToast(mContext, msg);
    }

    @Override
    public void showContent(List<VideoType> list) {
        mAdapter.clear();
        mAdapter.addAll(list);
        if (list.size() > 0) {
            mRecyclerView.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.rl_record, R.id.rl_down, R.id.rl_collection, R.id.rl_them, R.id.img_setting, R.id.tv_img_reg, R.id.tv_reg, R.id.rl_member})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_record:
                getContext().startActivity(new Intent(mContext, HistoryActivity.class));
                break;
            case R.id.rl_down:
                //EventUtil.showToast(getContext(), "敬请期待");
                getContext().startActivity(new Intent(mContext, CacheActivity.class));
                break;
            case R.id.rl_collection:
                getContext().startActivity(new Intent(mContext, CollectionActivity.class));
                break;
            case R.id.rl_them:
                EventBus.getDefault().post("", MineFragment.SET_THEME);
                break;
            case R.id.img_setting:
                getContext().startActivity(new Intent(mContext, SettingActivity.class));
                break;
            case R.id.tv_reg:
                if("登陆/注册".equals(tvReg.getText())) {
                    getContext().startActivity(new Intent(mContext, LoginActivity.class));
                }
                break;
            case R.id.tv_img_reg:
                chooseDialog();
                break;
            case R.id.rl_member:
                //wang HTTPS://QR.ALIPAY.COM/FKX086252LRGLJ0GIRBLE4
                //Li HTTPS://QR.ALIPAY.COM/FKX02131UHNUP9ISGBDE37
                // Check if login
                if(!SessionManager.getInstance(getContext()).isLoggedIn()) {
                    getContext().startActivity(new Intent(mContext, LoginActivity.class));
                }else if (AlipayZeroSdk.hasInstalledAlipayClient(getContext())) {
                    AlipayZeroSdk.startAlipayClient(getActivity(), "FKX086252LRGLJ0GIRBLE4");
                } else {
                    EventUtil.showToast(getContext(),"请先安装支付宝!");
                }

                break;
        }
    }

    @Subscriber(tag=LoginActivity.EXTRA_DATA)
    public void refreshFragment(User user) {
        if (user != null) {
            this.username = user.getName();
            uuid = user.getId()+"_"+user.getToken();
            tvReg.setText(username);
            tvType.setText(" "+ user.getType());
            Log.d(TAG, "refreshFragment: " + username);
        }else {
            Log.d(TAG, "refreshFragment:");
        }
    }

    @Subscriber(tag=SettingActivity.LOGOUT_NOTIFY)
    public void refreshLogout(String username) {
        this.username = "";
        Log.d(TAG, "Logout: " + username);
        tvReg.setText("登陆注册");
        tvType.setText("");
        LoginActivity.start(mContext,"welcome");
    }

    @Subscriber(tag = VideoInfoPresenter.Refresh_History_List)
    public void setData(String tag) {
        mPresenter.getHistoryData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void lazyFetchData() {
        super.lazyFetchData();
        mPresenter.getHistoryData();
    }

    @Override
    protected void initInject() {
        getFragmentComponent().inject(this);
    }

    private void chooseDialog() {
        new AlertDialog.Builder(this.getActivity())
                .setTitle("选择头像")//
                .setNegativeButton("相册", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
                                startPermissionsActivity();
                            } else {
                                selectFromAlbum();
                            }
                        } else {
                            selectFromAlbum();
                        }
                        isClickCamera = false;
                    }
                })
                .setPositiveButton("拍照", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //检查权限(6.0以上做权限判断)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
                                Log.d(TAG,"mPermissionsChecker.lacksPermissions(PERMISSIONS)");
                                startPermissionsActivity();
                            } else {
                                Log.d(TAG,"!mPermissionsChecker.lacksPermissions(PERMISSIONS)");
                                openCamera();
                            }
                        } else {
                            Log.d(TAG,"Direct Open Camera");
                            openCamera();
                        }
                        isClickCamera = true;
                    }
                }).show();
    }

    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(this.getActivity(), REQUEST_PERMISSION,
                PERMISSIONS);
    }

    private void openCamera() {
        File file = new FileStorage().createIconFile();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageUri = FileProvider.getUriForFile(this.getContext(), "com.universe.yz.admin.fileprovider", file);//通过FileProvider创建一个content类型的Uri
        } else {
            imageUri = Uri.fromFile(file);
        }
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
        }
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//将拍取的照片保存到指定URI
        startActivityForResult(intent, CODE_CAMERA_REQUEST);
    }

    /**
     * 从相册选择
     */
    private void selectFromAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, CODE_GALLERY_REQUEST);
        Log.d(TAG,"startActivityForResult ok ");
    }

    /**
     * 裁剪
     */
    private void cropPhoto() {
        File file = new FileStorage().createCropFile();
        Uri outputUri = Uri.fromFile(file);//缩略图保存地址
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(imageUri, "image/*");
//        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, CODE_RESULT_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CODE_GALLERY_REQUEST: // 相册
                if (Build.VERSION.SDK_INT >= 19) {
                    handleImageOnKitKat(data);
                } else {
                    handleImageBeforeKitKat(data);
                }
                break;
            case CODE_CAMERA_REQUEST:  //拍照
                if (hasSdcard()) {
                    if (resultCode == RESULT_OK) {
                        cropPhoto();
                    }
                } else {
                    Toast.makeText(mContext, "没有SDCard!", Toast.LENGTH_LONG)
                            .show();
                }

                break;
            case CODE_RESULT_REQUEST:
                Bitmap bitmap = null;
                try {
                    if (isClickCamera) {
                        bitmap = BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(imageUri));
                    } else {
                        bitmap = BitmapFactory.decodeFile(imagePath);
                    }
                    saveImage(bitmap);
                    tvImgeReg.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case REQUEST_PERMISSION://权限请求
                if (resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
//                    finish();
                } else {
                    if (isClickCamera) {
                        openCamera();
                    } else {
                        selectFromAlbum();
                    }
                }
                break;
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        imagePath = null;
        imageUri = data.getData();
        if (DocumentsContract.isDocumentUri(mContext, imageUri)) {
            //如果是document类型的uri,则通过document id处理
            String docId = DocumentsContract.getDocumentId(imageUri);
            if ("com.android.providers.media.documents".equals(imageUri.getAuthority())) {
                String id = docId.split(":")[1];//解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.downloads.documents".equals(imageUri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
            //如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(imageUri, null);
        } else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
            //如果是file类型的Uri,直接获取图片路径即可
            imagePath = imageUri.getPath();
        }

        cropPhoto();
    }

    private void handleImageBeforeKitKat(Intent intent) {
        imageUri = intent.getData();
        imagePath = getImagePath(imageUri, null);
        cropPhoto();
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过Uri和selection老获取真实的图片路径
        Cursor cursor = mContext.getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    /**
     * 检查设备是否存在SDCard的工具方法
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            // 有存储的SDCard
            return true;
        } else {
            return false;
        }
    }

    private void saveImage(Bitmap bitmap) {
        File filesDir;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){//判断sd卡是否挂载
            //路径1：storage/sdcard/Android/data/包名/files
            filesDir = mContext.getExternalFilesDir("");
        }else{//手机内部存储
            //路径：data/data/包名/files
            filesDir = mContext.getFilesDir();
        }
        FileOutputStream fos = null;
        try {
            File file = new File(filesDir,"icon.png");
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100,fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally{
            if(fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //如果本地有,就不需要再去联网去请求
    private boolean readImage() {
        File filesDir;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){//判断sd卡是否挂载
            //路径1：storage/sdcard/Android/data/包名/files
            filesDir = mContext.getExternalFilesDir("");
        }else{//手机内部存储
            //路径：data/data/包名/files
            filesDir = mContext.getFilesDir();
        }
        File file = new File(filesDir,"icon.png");
        if(file.exists()){
            //存储--->内存
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            tvImgeReg.setImageBitmap(bitmap);
            return true;
        }
        return false;
    }
}