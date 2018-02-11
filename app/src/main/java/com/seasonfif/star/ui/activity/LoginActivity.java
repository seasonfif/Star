package com.seasonfif.star.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.dd.CircularProgressButton;
import com.seasonfif.star.R;
import com.seasonfif.star.constant.Constants;
import com.seasonfif.star.model.BasicToken;
import com.seasonfif.star.model.CreateAuthorization;
import com.seasonfif.star.net.LoginService;
import com.seasonfif.star.net.RetrofitEngine;
import com.seasonfif.star.utils.OAuthShared;
import com.seasonfif.star.utils.Utils;
import okhttp3.Credentials;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zhangqiang on 2018/2/8.
 */

public class LoginActivity extends BaseActivity {

  @BindView(R.id.user_name_et)
  TextInputEditText userNameEt;
  @BindView(R.id.user_name_layout)
  TextInputLayout userNameLayout;
  @BindView(R.id.password_et)
  TextInputEditText passwordEt;
  @BindView(R.id.password_layout)
  TextInputLayout passwordLayout;
  @BindView(R.id.btn_login)
  CircularProgressButton loginBn;
  @BindView(R.id.oauth_login_bn)
  Button oauthBn;


  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    ButterKnife.bind(this);

    loginBn.setIndeterminateProgressMode(true);
    loginBn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        if (loginBn.getProgress() == -1 || loginBn.getProgress() == 0 || loginBn.getProgress() == 100) {
          loginBn.setProgress(0);
          login(userNameEt.getText().toString(), passwordEt.getText().toString());
        }
      }
    });

    oauthBn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        oauth();
      }
    });
  }

  private void login(String email, String password) {
    if (!validate(email, password)) {
      return;
    }
    loginBn.setProgress(50);
    CreateAuthorization createAuthorization = new CreateAuthorization();
    createAuthorization.client_id = Constants.CLIENT_ID;
    createAuthorization.client_secret = Constants.CLIENT_SECRET;
    createAuthorization.scopes = new String[]{"user", "public_repo", "delete_repo", "gist"};
    createAuthorization.state = "seasonfif";

    String basicToken = Credentials.basic(email, password);
    Observable<Response<BasicToken>> authorizations =
        RetrofitEngine.getRetrofitWithBaseToken(basicToken)
            .create(LoginService.class)
            .authorizations(createAuthorization);
    authorizations.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<Response<BasicToken>>() {
          @Override public void onCompleted() {

          }

          @Override public void onError(Throwable e) {
            if (Utils.isConnectNet(LoginActivity.this)){
              Toast.makeText(getBaseContext(), getString(R.string.login_account_error), Toast.LENGTH_LONG).show();
            }else{
              Toast.makeText(getBaseContext(), getString(R.string.toast_network), Toast.LENGTH_LONG).show();
            }
            loginBn.setProgress(-1);
          }

          @Override public void onNext(Response<BasicToken> basicTokenResponse) {
            OAuthShared.saveToken(LoginActivity.this, basicTokenResponse.body().getToken());
            loginBn.setProgress(100);
            Intent it = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(it);
            finish();
          }
        });
  }

  private boolean validate(String email, String password) {
    boolean valid = true;

    if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
      userNameLayout.setError(getString(R.string.login_illegal_emial));
      valid = false;
    } else {
      userNameLayout.setError(null);
    }

    if (password.isEmpty()) {
      passwordLayout.setError(getString(R.string.login_invalid_password));
      valid = false;
    } else {
      passwordLayout.setError(null);
    }

    return valid;
  }

  private void oauth() {
  }
}
