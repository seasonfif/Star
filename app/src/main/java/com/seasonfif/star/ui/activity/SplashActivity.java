package com.seasonfif.star.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.seasonfif.star.R;
import com.seasonfif.star.widget.titanic.Titanic;
import com.seasonfif.star.widget.titanic.TitanicTextView;
import com.seasonfif.star.widget.titanic.Typefaces;

/**
 * Created by seasonfif on 2018/2/4.
 */

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
        /*setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        tv.setTypeface(Typefaces.get(this, "Satisfy-Regular.ttf"));

        final Titanic titanic = new Titanic();
        titanic.start(tv);

        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                titanic.cancel();
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }, 2000);*/
    }
}
