package com.example.yash007.sportsapplication;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openSignUpActivity(View v)  {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }

    public void openForgetPasswordActivity(View v)  {
        Intent intent = new Intent(this, ForgetPasswordActivity.class);
        startActivity(intent);
    }
}
