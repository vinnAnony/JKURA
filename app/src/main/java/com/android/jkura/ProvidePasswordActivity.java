package com.android.jkura;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.button.MaterialButton;

import androidx.appcompat.app.AppCompatActivity;

public class ProvidePasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provide_password);

        MaterialButton proceedLogin = findViewById(R.id.submitFirstPasswordBtn);
        proceedLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProvidePasswordActivity.this,HomeActivity.class));
            }
        });
    }
}