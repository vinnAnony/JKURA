package com.android.jkura;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.jkura.extras.AspirantModel;
import com.android.jkura.extras.AspirantSelectionAdapter;
import com.android.jkura.extras.CheckNet;
import com.android.jkura.extras.SessionManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AspirantSelectionActivity extends AppCompatActivity {
    TextView aspirantPositionTV,aspirantSchoolTV,aspirantDepartmentTV;
    private RecyclerView mRecyclerView;
    private AspirantSelectionAdapter mAdapter;
    public ProgressBar mProgressBar;
    private RelativeLayout mProgressBarRL;
    private FirebaseStorage mStorage;
    private DatabaseReference mDatabaseRef,mAspirantsRef;
    private ValueEventListener mDBListener;
    private List<AspirantModel> mAspirants;

    private String VotingPosition ;
    private String VotingSchool;
    private String VotingDepartment;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aspirant_selection);

        sessionManager = new SessionManager(this);

        aspirantDepartmentTV = findViewById(R.id.aspirantDepartment);
        aspirantSchoolTV = findViewById(R.id.aspirantSchool);
        aspirantPositionTV = findViewById(R.id.aspirantPosition);

        mRecyclerView = findViewById(R.id.aspirantsRV);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mProgressBar = findViewById(R.id.aspirantSelectPB);
        mProgressBarRL = findViewById(R.id.aspirantSelectPBRL);

        showLoader();
        loadVoteSessionInfo();

        mAspirants = new ArrayList<>();
        mAdapter = new AspirantSelectionAdapter (AspirantSelectionActivity.this, mAspirants, VotingPosition,VotingDepartment,VotingSchool);
        mRecyclerView.setAdapter(mAdapter);

        mStorage = FirebaseStorage.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        if (VotingPosition.equals("Delegate"))
            mAspirantsRef = FirebaseDatabase.getInstance().getReference("Aspirants/"+VotingSchool+"/"+VotingDepartment+"/"+VotingPosition);
        else if (VotingPosition.equals("School Representative"))
            mAspirantsRef = FirebaseDatabase.getInstance().getReference("Aspirants/"+VotingSchool+"/"+VotingPosition);

        if (CheckNet.isConnected(getApplicationContext())){
            getRegNo();

            mDBListener = mAspirantsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mAspirants.clear();

                    for (DataSnapshot aspirantSnapshot : dataSnapshot.getChildren()) {
                        AspirantModel upload = aspirantSnapshot.getValue(AspirantModel.class);
                        upload.setKey(aspirantSnapshot.getKey());
                        mAspirants.add(upload);
                    }
                    mAdapter.notifyDataSetChanged();
                    hideLoader();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    //Toast.makeText(AspirantSelectionActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
                    hideLoader();
                }
            });
        }else {
            hideLoader();

            ConstraintLayout cLyt = findViewById(R.id.contraintASA);
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
    private void loadVoteSessionInfo(){
        Intent i=this.getIntent();
        VotingPosition=i.getExtras().getString("POSITION_KEY");
        VotingDepartment=i.getExtras().getString("DEPARTMENT_KEY");
        VotingSchool=i.getExtras().getString("SCHOOL_KEY");


        aspirantPositionTV.setText(VotingPosition);
        aspirantDepartmentTV.setText(VotingDepartment);
        aspirantSchoolTV.setText(VotingSchool);
    }
    private String getVotersInfo(){
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (acct != null) {
            String voterName = acct.getDisplayName();
            String voterGivenName = acct.getGivenName();
            String voterFamilyName = acct.getFamilyName();
            String voterEmail = acct.getEmail();
            String voterId = acct.getId();
            Uri voterPhoto = acct.getPhotoUrl();
            return voterEmail;
        }
        else {
            return null;
        }
    }

    private void getRegNo(){
        final String currentVoterEmail = getVotersInfo();
        DatabaseReference mStudentRef = FirebaseDatabase.getInstance().getReference("Students");

        Query regNoQuery = mStudentRef.orderByChild("studentEmail").equalTo(currentVoterEmail);
        regNoQuery.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String studentRegNo;
                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                            studentRegNo = postSnapshot.child("studentRegNo").getValue(String.class);
                            sessionManager.setRegNo(studentRegNo);

                            Log.e("PostSnapshot.", "Value is: " + studentRegNo);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("TAG", "Failed to read value.", databaseError.toException());
                    }
                });
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