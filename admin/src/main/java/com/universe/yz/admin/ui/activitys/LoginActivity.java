/**
 * Author: Ravi Tamada
 * URL: www.androidhive.info
 * twitter: http://twitter.com/ravitamada
 */
package com.universe.yz.admin.ui.activitys;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.universe.yz.admin.R;
import com.universe.yz.admin.helper.SessionManager;
import com.universe.yz.admin.model.bean.User;
import com.universe.yz.admin.model.bean.UserRet;
import com.universe.yz.admin.model.db.RealmHelper;
import com.universe.yz.admin.model.http.response.CommonHttpResponse;
import com.universe.yz.admin.model.net.RetrofitHelper;
import com.universe.yz.admin.utils.EventUtil;
import com.universe.yz.admin.utils.RxUtil;

import org.simple.eventbus.EventBus;

import rx.functions.Action1;

public class LoginActivity extends Activity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    public static final String EXTRA_DATA = "EXTRA_DATA";
    public static boolean IsDummyLogin = false;
    private static LoginActivity theLogin;
    private String source = null;

    public static LoginActivity getInstance() {
        return theLogin;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EventBus.getDefault().register(this);

        source = getIntent().getStringExtra("source");

        inputEmail = (EditText) findViewById(R.id.user);
        inputPassword = (EditText) findViewById(R.id.password);
        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        Button btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        this.theLogin = this;

        session = SessionManager.getInstance(getApplicationContext());
/*
        if (session.isLoggedIn()) {
            finish();
        }
*/
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String user = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if (!user.isEmpty() && !password.isEmpty()) {
                    checkLogin(user, password);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter the credentials!", Toast.LENGTH_LONG)
                            .show();
                }
            }

        });

        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                RegisterActivity.start(getBaseContext(), source);
                finish();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }

    public void checkLogin(final String user, final String password) {
        pDialog.setMessage("Logging in ...");
        if(!IsDummyLogin) showDialog();

        RetrofitHelper.getMemberApi().login(user,password)
                .compose(RxUtil.<CommonHttpResponse<UserRet>>rxSchedulerHelper())
                .compose(RxUtil.<UserRet>handleUserResult())
                .subscribe(new Action1<UserRet>() {
                    @Override
                    public void call(final UserRet res) {
                        if (res != null) {
                            User user1 = new User();
                            user1.setId(res.userId);
                            user1.setName(user);
                            user1.setToken(res.token);
                            if(res.type != null) user1.setType(res.type);
                            user1.setPassword(password);
                            RealmHelper.getInstance().insertUser(user1);
                            if(!IsDummyLogin) hideDialog();
                            session.setLogin(true);
                            EventBus.getDefault().post(user1,EXTRA_DATA);
                            RealmHelper.getInstance().deleteUserById(res.userId);
                            RealmHelper.getInstance().insertUser(user1);
                            finish();
                            if ("welcome".equals(source)) {
                                MainActivity.start(getApplicationContext());
                            }
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        EventUtil.showToast(getApplicationContext(),"用户名或密码错误!");
                        if(!IsDummyLogin) hideDialog();
                    }
                });
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public static void start(Context context,String source) {
        Intent starter = new Intent(context, LoginActivity.class);
        starter.putExtra("source",source);
        starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(starter);
    }
}
