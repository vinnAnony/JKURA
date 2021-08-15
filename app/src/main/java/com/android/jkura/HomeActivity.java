package com.android.jkura;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.android.jkura.extras.ActiveSession;
import com.android.jkura.extras.ActiveSessionsAdapter;
import com.android.jkura.extras.SessionManager;
import com.android.jkura.extras.StudentModel;
import com.android.jkura.extras.VoteDisplayAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

public class HomeActivity extends AppCompatActivity {

    private StudentModel studentModel;
    private final List<ActiveSession> activeSessions = new ArrayList<>();
    private final String TAG = "Home Activity";
    ImageView foreLogOut;
    private HashMap<String, Integer> schoolRepTally = new HashMap<>();
    private HashMap<String, Integer> delegateTally = new HashMap<>();
    private ViewPager2 pieContainer;
    private SessionManager sessionManager;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        foreLogOut = findViewById(R.id.logoutImg);
        pieContainer = findViewById(R.id.pie_container);
        sessionManager = new SessionManager(this);
        studentModel = sessionManager.getStudentDetails();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        setActiveSessionsDisplay();

        foreLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutPrompt();
            }
        });

    }

    private void setActiveSessionsDisplay() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Current Positions");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()){
                    ActiveSession session = snap.getValue(ActiveSession.class);

                    assert session != null;
                    if (session.getSchool().equals(studentModel.getStudentSchool())){
                        if (session.getPosition().equals("School Representative")){
                            activeSessions.add(session);
                        } else if (session.getDepartment().equals(studentModel.getStudentDepartment())){
                            activeSessions.add(session);
                        }
                    }
                }
                fetchVotes();
                setRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void setRecyclerView() {
        ActiveSessionsAdapter adapter = new ActiveSessionsAdapter();
        RecyclerView recyclerView = findViewById(R.id.activeSessions);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.setData(activeSessions);
    }

    private void fetchVotes(){

        if (checkIfSchoolRepActive()){
            fetchSchoolRepVotes();
        }

    }

    private void fetchSchoolRepVotes() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Votes/"+studentModel.getStudentSchool()+"/School Representative");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "onDataChange: Snap "+ snapshot);

                HashMap<String, Integer> voteTallies = new HashMap<>();

                for (DataSnapshot snap: snapshot.getChildren()){
                    String aspirantReg = snap.getKey();
                    int tally = 0;
                    for (DataSnapshot votes: snap.getChildren()){
                        tally++;
                    }
                    voteTallies.put(aspirantReg, tally);
                }

                voteTallies.put("Title", 1);
                schoolRepTally = voteTallies;

                if (checkIfDelegateActive()){
                    fetchDelegateVotes();
                } else {
                    initializeLinearRecycler();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void fetchDelegateVotes() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Votes/"+studentModel.getStudentSchool()+"/"+studentModel.getStudentDepartment()+"/Delegate");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "onDataChange: Snap Delegate "+ snapshot);

                HashMap<String, Integer> voteTallies = new HashMap<>();

                for (DataSnapshot snap: snapshot.getChildren()){
                    String aspirantReg = snap.getKey();
                    int tally = 0;
                    for (DataSnapshot votes: snap.getChildren()){
                        tally++;
                    }
                    voteTallies.put(aspirantReg, tally);
                }

                voteTallies.put("Title", 2);
                delegateTally = voteTallies;
                initializeLinearRecycler();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initializeLinearRecycler() {
        List<HashMap<String, Integer>> votesList = new ArrayList<>();
        if (!schoolRepTally.isEmpty()){
            votesList.add(schoolRepTally);
        }
        if (!delegateTally.isEmpty()){
            votesList.add(delegateTally);
        }

        VoteDisplayAdapter voteDisplayAdapter = new VoteDisplayAdapter(this);
        voteDisplayAdapter.setData(votesList);
        pieContainer.setAdapter(voteDisplayAdapter);

    }

    private boolean checkIfSchoolRepActive() {

        boolean returnValue = false;

        for (ActiveSession session: activeSessions){
            if (session.getPosition().equals("School Representative")){
                returnValue = true;
                break;
            }
        }

        return returnValue;

    }

    private boolean checkIfDelegateActive() {

        boolean returnValue = false;

        for (ActiveSession session: activeSessions){
            if (session.getDepartment().equals(studentModel.getStudentDepartment())){
                if (session.getPosition().equals("Delegate")){
                    returnValue = true;
                    break;
                }
            }
        }

        return returnValue;

    }

    private void logoutPrompt() {
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.logout_confirm_popup, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setCancelable(false);
        final AlertDialog confirmLogoutDialog = builder.create();
        confirmLogoutDialog.show();

        Button yesButton = dialogView.findViewById(R.id.popYesBtn);
        Button noButton = dialogView.findViewById(R.id.popNoBtn);

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sessionManager.resetData();
                mGoogleSignInClient.signOut();
                Intent mainIntent = new Intent(HomeActivity.this, FirstTimeLoginActivity.class);
                startActivity(mainIntent);
                HomeActivity.this.finishAffinity();
            }
        });
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmLogoutDialog.dismiss();
            }
        });
    }
}