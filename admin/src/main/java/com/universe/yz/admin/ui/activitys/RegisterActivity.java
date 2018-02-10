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
import com.universe.yz.admin.model.bean.Member;
import com.universe.yz.admin.model.http.response.CommonHttpResponse;
import com.universe.yz.admin.model.net.RetrofitHelper;
import com.universe.yz.admin.utils.RxUtil;

import java.util.List;

import rx.functions.Action1;

public class RegisterActivity extends Activity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private EditText inputFullName;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private String source = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        source = getIntent().getStringExtra("source");
        inputFullName = (EditText) findViewById(R.id.name);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        Button btnRegister = (Button) findViewById(R.id.btnRegister);
        Button btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        SessionManager session = SessionManager.getInstance(getApplicationContext());

        if (session.isLoggedIn()) {
            Intent intent = new Intent(RegisterActivity.this,
                    MainActivity.class);
            startActivity(intent);
            finish();
        }

        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String name = inputFullName.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                    registerUser(name, email, password);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "请仔细填写帐号,密码和邮箱确保无误!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                LoginActivity.start(getApplicationContext(),source);
                finish();
            }
        });
    }

    private void registerUser(final String name, final String email,
                              final String password) {
        showDialog();
        RetrofitHelper.getMemberApi().reg(name, password, email)
                .compose(RxUtil.<CommonHttpResponse<List<Member>>>rxSchedulerHelper())
                .compose(RxUtil.<List<Member>>handleUserResult())
                .subscribe(new Action1<List<Member>>() {
                    @Override
                    public void call(final List<Member> res) {
                        hideDialog();
                        LoginActivity.IsDummyLogin = true;
                        LoginActivity.getInstance().checkLogin(name, password);
                        LoginActivity.IsDummyLogin = false;
                        finish();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Toast.makeText(getApplicationContext(),
                                "请换一个名字试试,重名了!", Toast.LENGTH_LONG)
                                .show();
                        hideDialog();
                    }
                });
    }

    private void showDialog() {
        pDialog.setMessage("Registering ...");
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public static void start(Context context, String source) {
        Intent starter = new Intent(context, RegisterActivity.class);
        starter.putExtra("source",source);
        starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(starter);
    }
}