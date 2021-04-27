package com.android.jkura;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.button.MaterialButton;

import androidx.appcompat.app.AppCompatActivity;

public class FirstTimeLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time_login);

        MaterialButton submitOTP = findViewById(R.id.submitOTPBtn);
        submitOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FirstTimeLoginActivity.this,ProvidePasswordActivity.class));
            }
        });
    }
}