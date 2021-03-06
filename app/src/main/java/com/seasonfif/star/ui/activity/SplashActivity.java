package com.seasonfif.star.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.seasonfif.star.R;
import com.seasonfif.star.utils.OAuthShared;
import com.seasonfif.star.widget.titanic.Titanic;
import com.seasonfif.star.widget.titanic.TitanicTextView;
import com.seasonfif.star.widget.titanic.Typefaces;

/**
 * Created by seasonfif on 2018/2/4.
 */

public class SplashActivity extends BaseActivity {

    @BindView(R.id.tv)
    TitanicTextView tv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        tv.setTypeface(Typefaces.get(this, "Satisfy-Regular.ttf"));

        final Titanic titanic = new Titanic();
        titanic.start(tv);

        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                titanic.cancel();
                aboard();

            }
        }, 2000);
    }

    private void aboard() {
        String token = OAuthShared.getToken(this);
        if (TextUtils.isEmpty(token)){
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        }else{
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
        }
        finish();
    }
}
