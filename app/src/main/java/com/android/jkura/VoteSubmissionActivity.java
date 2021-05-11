package com.android.jkura;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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

    String aspName,aspCourse,aspImageURL,aspRegNo,aspPosition,aspDepartment,aspSchool;
    TextView aspirantPosition,aspirantName,aspirantCourse;
    ImageView aspirantImage;

    MaterialButton submitVote;
    ProgressBar mProgressBar;
    RelativeLayout mProgressBarRL;
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

        mProgressBar = findViewById(R.id.voteSubmitPB);
        mProgressBarRL = findViewById(R.id.voteSubmitPBRL);

        Intent i=this.getIntent();
        aspName= Objects.requireNonNull(i.getExtras()).getString("NAME_KEY");
        aspCourse=i.getExtras().getString("COURSE_KEY");
        aspImageURL=i.getExtras().getString("IMAGE_KEY");
        aspRegNo=i.getExtras().getString("REG_NO_KEY");
        aspPosition=i.getExtras().getString("POSITION_KEY");
        aspDepartment=i.getExtras().getString("DEPARTMENT_KEY");
        aspSchool=i.getExtras().getString("SCHOOL_KEY");

        aspirantPosition.setText(aspPosition);
        aspirantName.setText(aspName);
        aspirantCourse.setText(aspCourse);
        Picasso.with(this)
                .load(aspImageURL)
                .placeholder(R.drawable.placeholder)
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

        if (aspPosition.equals("School Representative"))
            popAspPosition.setText(aspPosition+" for "+aspSchool);
        else if (aspPosition.equals("Delegate"))
            popAspPosition.setText(aspPosition+" for "+aspDepartment);
        popAspName.setText(aspName);

        Picasso.with(this)
                .load(aspImageURL)
                .placeholder(R.drawable.placeholder)
                .fit()
                .centerCrop()
                .transform(new CropCircleTransformation())
                .into(popAspImage);

        popCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmVoteDialog.dismiss();
            }
        });

        popAcceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Submit vote
            }
        });
    }
    @Override
    public void onClick(View v) {
        if (v.equals(submitVote)){
            confirmVotePopUp();
        }
    }

    public void showLoader(){
        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBarRL.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
    public void hideLoader(){
        mProgressBar.setVisibility(View.GONE);
        mProgressBarRL.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}