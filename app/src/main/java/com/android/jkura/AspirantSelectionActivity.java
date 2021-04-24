package com.android.jkura;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AspirantSelectionActivity extends AppCompatActivity implements View.OnClickListener {
    TextView aspirantPositionTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aspirant_selection);

        aspirantPositionTV = findViewById(R.id.aspirantPosition);
        aspirantPositionTV.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(aspirantPositionTV)){
            startActivity(new Intent(this,VoteSubmissionActivity.class));
        }
    }
}