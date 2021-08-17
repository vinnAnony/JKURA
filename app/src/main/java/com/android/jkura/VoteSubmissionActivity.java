package com.android.jkura;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.jkura.extras.CheckNet;
import com.android.jkura.extras.SessionManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class VoteSubmissionActivity extends AppCompatActivity implements View.OnClickListener {
    //Popup
    ImageView popAspImage;
    TextView popAspName,popAspPosition;
    MaterialButton popCancelBtn,popAcceptBtn;
    EditText popPassword;
    TextInputLayout popPassTIL;

    String aspName,aspCourse,aspImageURL,aspRegNo,aspPosition,aspDepartment,aspSchool;
    TextView aspirantPosition,aspirantName,aspirantCourse;
    ImageView aspirantImage;

    MaterialButton submitVote;
    ProgressBar mProgressBar;
    RelativeLayout mProgressBarRL;

    private DatabaseReference mVotesRef,mVoterRef;
    private ValueEventListener mDBListener;

    private static String CurrentStudentRegNo;
    private SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_submission);

        sessionManager = new SessionManager(this);
        CurrentStudentRegNo = sessionManager.getRegNo();

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

        mVoterRef = FirebaseDatabase.getInstance().getReference("Students/"+CurrentStudentRegNo);

        if (aspPosition.equals("Delegate")){
            mVotesRef = FirebaseDatabase.getInstance().getReference("Votes/"+aspSchool+"/"+aspDepartment+"/"+aspPosition+"/"+aspRegNo);
        }else if (aspPosition.equals("School Representative")){
            mVotesRef = FirebaseDatabase.getInstance().getReference("Votes/"+aspSchool+"/"+aspPosition+"/"+aspRegNo);
        }

        //insert here

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
        popPassword = dialogView.findViewById(R.id.popPasswordET);
        popPassTIL = dialogView.findViewById(R.id.passwordTIL);
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
                /*:TODO -STEP 1 > Verify vote status
                        -STEP 2 > Verify password
                        -STEP 3 > Insert voter has already voted in Votes & Students
                        -STEP 4 > Increment votes
                  */
                verifyPassword();
            }
        });

    }

    private void verifyPassword(){
        final String pass = popPassword.getText().toString();
        final String[] fbPass = new String[1];
        if (pass!=null && !pass.equals("")){
            mVoterRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    fbPass[0] = dataSnapshot.child("password").getValue(String.class);
                    Log.e("FB Pass", "Value is: " + fbPass[0]);
                    if (pass.equals(fbPass[0])) {
                        verifyVotedStatus();
                    }
                    else {
                        popPassTIL.setError("Invalid Password!");
                        Toast.makeText(VoteSubmissionActivity.this, "Invalid Password!", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.e("TAG", "Failed to read value.", error.toException());
                }
            });

        }
        else {
            Toast.makeText(this, "Enter password!", Toast.LENGTH_LONG).show();
            popPassTIL.setError("Enter password");
        }
    }

    private void submitVote(){
        String key = mVotesRef.push().getKey();
        String voteType = "";

        if (aspPosition.equals("Delegate"))
            voteType = "dlgtVoted";
        else if (aspPosition.equals("School Representative"))
            voteType = "schRepVoted";

        final String finalVoteType = voteType;
        mVotesRef.child(key).setValue(CurrentStudentRegNo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mVoterRef.child(finalVoteType).setValue(1).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(VoteSubmissionActivity.this, "Thanks for voting!", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(VoteSubmissionActivity.this,HomeActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(VoteSubmissionActivity.this, "An error occurred.Please try again!", Toast.LENGTH_LONG).show();
                        Log.d("Submit Vote:-", e.getLocalizedMessage());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(VoteSubmissionActivity.this, "An error occurred.Please try again!", Toast.LENGTH_LONG).show();
                Log.d("Submit Vote:-", e.getLocalizedMessage());
            }
        });
    }

    private void verifyVotedStatus(){
        String voteType = "";
        if (aspPosition.equals("Delegate"))
            voteType = "dlgtVoted";
        else if (aspPosition.equals("School Representative"))
            voteType = "schRepVoted";

        final String finalVoteType = voteType;
        mVoterRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int voteStatus = dataSnapshot.child(finalVoteType).getValue(Integer.class);
                Log.e("Vote Status", "Value is: " + voteStatus);
                if (voteStatus == 0) {
                    submitVote();
                }
                else if(voteStatus == 1){
                    popPassTIL.setError("Already voted!");
                    Toast.makeText(VoteSubmissionActivity.this, "Already voted!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(VoteSubmissionActivity.this,HomeActivity.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("TAG", "Failed to read value.", error.toException());
            }
        });
    }


    @Override
    public void onClick(View v) {
        if (v.equals(submitVote)){
            if (CheckNet.isConnected(getApplicationContext())) {
                confirmVotePopUp();
            }else {
                ConstraintLayout cLyt = findViewById(R.id.constraintVSA);
                Snackbar snackbar = Snackbar.make(cLyt,"Oops!Check your internet connection",Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                        overridePendingTransition( 0, 0);
                        startActivity(getIntent());
                        overridePendingTransition( 0, 0);
                    }
                });
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
            }
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