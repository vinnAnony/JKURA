package com.android.jkura;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.jkura.extras.ActiveSession;
import com.android.jkura.extras.ActiveSessionsAdapter;
import com.android.jkura.extras.StudentModel;
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

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private StudentModel studentModel;
    private List<ActiveSession> activeSessions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        studentModel = getIntent().getParcelableExtra(LoginActivity.KEY_STUDENT);

        ImageView goAsp = findViewById(R.id.imageView2);
        goAsp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, AspirantSelectionActivity.class));
            }
        });

        AnimatedPieView animatedPieView = findViewById(R.id.pie_chart);
        AnimatedPieViewConfig config = new AnimatedPieViewConfig();
        config.startAngle(-90)// Starting angle offset
                .addData(new SimplePieInfo(30, getResources().getColor(R.color.pieBlue), "Number 1"))//Data (bean that implements the IPieInfo interface)
                .addData(new SimplePieInfo(18, getResources().getColor(R.color.pieYellow), "Number 2"))
                .addData(new SimplePieInfo(35, getResources().getColor(R.color.pieOthers), "Others"))
                .duration(2000)
        .drawText(true)
        .textSize(30);// draw pie animation duration

        setActiveSessionsDisplay();

        animatedPieView.applyConfig(config);
        animatedPieView.start();

        LinearLayout sessionActionBtn = findViewById(R.id.linearLayout2);
        sessionActionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,AspirantSelectionActivity.class));
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
                    Log.d("School", "onDataChange: Session" + session.getSchool() + " Student "+ studentModel.getStudentSchool());
                    if (session.getSchool().equals(studentModel.getStudentSchool())){

                        if (session.getPosition().equals("School Representative")){
                            activeSessions.add(session);
                        } else if (session.getDepartment().equals(studentModel.getStudentDepartment())){
                            activeSessions.add(session);
                        }
                    }

                }

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
}