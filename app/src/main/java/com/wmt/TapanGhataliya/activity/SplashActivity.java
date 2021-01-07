package com.wmt.TapanGhataliya.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.wmt.TapanGhataliya.MainActivity;
import com.wmt.TapanGhataliya.R;
import com.wmt.TapanGhataliya.controller.Global;

public class SplashActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    RequestQueue requestQueue;
    Global global;
    boolean isRegistration = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        sharedPreferences = getSharedPreferences(Global.MyApplication, MODE_PRIVATE);
        requestQueue = Volley.newRequestQueue(SplashActivity.this);

        isRegistration = sharedPreferences.getBoolean(Global.CURRENT_USER_LOGIN, false);
        Log.d("isRegistration", String.valueOf(isRegistration));

        if (isRegistration == false) {


            Intent i = new Intent(SplashActivity.this, RegistrationActivity.class);
            startActivity(i);
            finish();

        } else {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // This method will be executed once the timer is over
                    Intent i = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }, 5000);
        }

    }
}
