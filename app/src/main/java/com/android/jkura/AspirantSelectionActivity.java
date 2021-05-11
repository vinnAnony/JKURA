package com.android.jkura;

import android.content.Intent;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AspirantSelectionActivity extends AppCompatActivity implements View.OnClickListener {
    TextView aspirantPositionTV;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aspirant_selection);

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

        //final Query dataQuery = mCountriesRef.orderByChild("aspirantPosition").equalTo(VotingPosition);
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
                Toast.makeText(AspirantSelectionActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
                hideLoader();
            }
        });

        aspirantPositionTV = findViewById(R.id.aspirantPosition);
        aspirantPositionTV.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.equals(aspirantPositionTV)){
            startActivity(new Intent(this,VoteSubmissionActivity.class));
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