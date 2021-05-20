package com.android.jkura;

import android.content.Intent;
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
import com.android.jkura.extras.SessionManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

    private static final String VotingPosition = "School Representative";
    private static final String VotingSchool = "School of Mathematical Sciences";
    private static final String VotingDepartment = "Pure and Applied Mathematics";

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aspirant_selection);

        sessionManager = new SessionManager(this);
        getRegNo();

        aspirantDepartmentTV = findViewById(R.id.aspirantDepartment);
        aspirantSchoolTV = findViewById(R.id.aspirantSchool);
        aspirantPositionTV = findViewById(R.id.aspirantPosition);
        aspirantPositionTV.setText(VotingPosition);
        aspirantDepartmentTV.setText(VotingDepartment);
        aspirantSchoolTV.setText(VotingSchool);

        mRecyclerView = findViewById(R.id.aspirantsRV);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mProgressBar = findViewById(R.id.aspirantSelectPB);
        mProgressBarRL = findViewById(R.id.aspirantSelectPBRL);

        showLoader();

        mAspirants = new ArrayList<>();
        mAdapter = new AspirantSelectionAdapter (AspirantSelectionActivity.this, mAspirants, VotingPosition,VotingDepartment,VotingSchool);
        mRecyclerView.setAdapter(mAdapter);

        mStorage = FirebaseStorage.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        if (VotingPosition.equals("Delegate"))
            mAspirantsRef = FirebaseDatabase.getInstance().getReference("Aspirants/"+VotingSchool+"/"+VotingDepartment+"/"+VotingPosition);
        else if (VotingPosition.equals("School Representative"))
            mAspirantsRef = FirebaseDatabase.getInstance().getReference("Aspirants/"+VotingSchool+"/"+VotingPosition);


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
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String studentRegNo;
                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                            studentRegNo = postSnapshot.child("studentRegNo").getValue(String.class);
                            sessionManager.setRegNo(studentRegNo);

                            Log.e("PostSnapshot.", "Value is: " + studentRegNo);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
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