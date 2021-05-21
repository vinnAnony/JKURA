package com.android.jkura;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.android.jkura.extras.ActiveSession;
import com.android.jkura.extras.ActiveSessionsAdapter;
import com.android.jkura.extras.StudentModel;
import com.android.jkura.extras.VoteDisplayAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.razerdp.widget.animatedpieview.AnimatedPieView;
import com.razerdp.widget.animatedpieview.AnimatedPieViewConfig;
import com.razerdp.widget.animatedpieview.data.SimplePieInfo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private StudentModel studentModel;
    private final List<ActiveSession> activeSessions = new ArrayList<>();
    private final String TAG = "Home Activity";
    private HashMap<String, Integer> schoolRepTally = new HashMap<>();
    private HashMap<String, Integer> delegateTally = new HashMap<>();
    private ViewPager2 pieContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        pieContainer = findViewById(R.id.pie_container);

        studentModel = getIntent().getParcelableExtra(LoginActivity.KEY_STUDENT);

        ImageView goAsp = findViewById(R.id.imageView2);
        goAsp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, AspirantSelectionActivity.class));
            }
        });

        setActiveSessionsDisplay();

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

}