package com.android.jkura;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class VoteSubmissionActivity extends AppCompatActivity implements View.OnClickListener {
    //Popup
    ImageView popAspImage;
    TextView popAspName,popAspPosition;
    MaterialButton popCancelBtn,popAcceptBtn;

    String aspName,aspCourse,aspImageURL,aspRegNo,aspPosition;
    TextView aspirantPosition,aspirantName,aspirantCourse;
    ImageView aspirantImage;

    MaterialButton submitVote;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_submission);

        submitVote = findViewById(R.id.submitVoteBtn);
        submitVote.setOnClickListener(this);
        aspirantPosition = findViewById(R.id.aspirantSubPosition);
        aspirantName = findViewById(R.id.aspirantSubName);
        aspirantCourse = findViewById(R.id.aspirantSubCourse);
        aspirantImage = findViewById(R.id.aspirantSubImage);

        Intent i=this.getIntent();
        aspName= Objects.requireNonNull(i.getExtras()).getString("NAME_KEY");
        aspCourse=i.getExtras().getString("COURSE_KEY");
        aspImageURL=i.getExtras().getString("IMAGE_KEY");
        aspRegNo=i.getExtras().getString("REG_NO_KEY");
        aspPosition=i.getExtras().getString("POSITION_KEY");

        aspirantPosition.setText(aspPosition);
        aspirantName.setText(aspName);
        aspirantCourse.setText(aspCourse);
        Picasso.with(this)
                .load(aspImageURL)
                //.placeholder(R.drawable.placeholder)
                .fit()
                .centerCrop()
                .transform(new CropCircleTransformation())
                .into(aspirantImage);
    }

    private void confirmVotePopUp(){
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.confirm_vote_popup, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setCancelable(false);
        final AlertDialog confirmVoteDialog = builder.create();
        confirmVoteDialog.show();

        popAspImage = dialogView.findViewById(R.id.popAspirantImage);
        popAspName = dialogView.findViewById(R.id.popAspirantName);
        popAspPosition = dialogView.findViewById(R.id.popAspirantPosition);
        popCancelBtn = dialogView.findViewById(R.id.popCancelBtn);
        popAcceptBtn = dialogView.findViewById(R.id.popOkBtn);

        popCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmVoteDialog.dismiss();
            }
        });
    }
    @Override
    public void onClick(View v) {
        if (v.equals(submitVote)){
            confirmVotePopUp();
        }
    }
}