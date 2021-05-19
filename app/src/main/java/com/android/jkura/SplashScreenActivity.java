package com.android.jkura;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.android.jkura.extras.ActiveSession;
import com.android.jkura.extras.AspirantModel;
import com.android.jkura.extras.SessionManager;
import com.android.jkura.extras.StudentModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashScreenActivity extends AppCompatActivity {

    private StudentModel studentModel;
    private final String TAG = "SplashScreen";
    private FirebaseAuth auth;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        sessionManager = new SessionManager(this);

        Log.d(TAG, "onCreate: Reg" + sessionManager.getRegNo());

        auth = FirebaseAuth.getInstance();
        //setActiveSessions();
        //addDummyStudents();

        if (sessionManager.getEmail() == null) {
            navigateToFirstTime();
        } else {

            Log.d(TAG, "onCreate: Current User" + auth.getCurrentUser().getEmail());

            if (auth.getCurrentUser().getEmail().equals(sessionManager.getEmail())){
                navigateToLogin();
            } else {
                sessionManager.resetData();
                navigateToFirstTime();
            }

        }

    }

    private void navigateToLogin() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference ref = firebaseDatabase.getReference("Students").child(sessionManager.getRegNo());

        Log.d(TAG, "navigateToLogin: Reg Number "+ sessionManager.getRegNo());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Log.d(TAG, "onDataChange: Snap "+ snapshot);
                    studentModel = snapshot.getValue(StudentModel.class);
                    assert studentModel != null;
                    Log.d(TAG, "onDataChange: Student "+ studentModel.getPassword());
                    Log.d(TAG, "onDataChange: Student "+ studentModel.getStudentCourse());
                    //hide progress
                    Intent mainIntent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    mainIntent.putExtra(LoginActivity.KEY_STUDENT, (Parcelable) studentModel);
                    SplashScreenActivity.this.startActivity(mainIntent);
                    SplashScreenActivity.this.finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //display error
            }
        });
    }

    private void navigateToFirstTime() {
        int SPLASH_DISPLAY_LENGTH = 1000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent mainIntent = new Intent(SplashScreenActivity.this, FirstTimeLoginActivity.class);
                SplashScreenActivity.this.startActivity(mainIntent);
                SplashScreenActivity.this.finish();

            }
        }, SPLASH_DISPLAY_LENGTH);

    }


    private void setActiveSessions(){

        ActiveSession session = new ActiveSession(
                "School of Mathematical Sciences",
                "Pure and Applied Mathematics",
                "School Representative",
                ""
        );

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference ref = firebaseDatabase.getReference("Current Positions");

        ref.push().setValue(session).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(SplashScreenActivity.this, "Session Added", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SplashScreenActivity.this, "Session not Added", Toast.LENGTH_LONG).show();
                }
            }
        });

        ActiveSession session1 = new ActiveSession(
                "Bsc. Mathematics and Computer Science",
                "Pure and Applied Mathematics",
                "Delegate",
                ""
        );


        ref.push().setValue(session1).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(SplashScreenActivity.this, "Session Added", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SplashScreenActivity.this, "Session not Added", Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    private void addDummyData() {

        StudentModel dummyStudent = new StudentModel(
                "Bsc. Mathematics and Computer Science",
                "Jos1234@",
                "Josephat Ndungu Maina",
                "SCM 211-0214-2017",
                "maina.josephat@students.jkuat.ac.ke",
                "School of Mathematical Sciences",
                "Pure and Applied Mathematics"
        );

        AspirantModel dummyAspirant = new AspirantModel(
            dummyStudent.getStudentName(),
                dummyStudent.getStudentSchool(),
                dummyStudent.getStudentDepartment(),
                dummyStudent.getStudentEmail(),
                dummyStudent.getStudentRegNo()
        );

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference ref = firebaseDatabase.getReference("Students/"+dummyStudent.getStudentRegNo());

        ref.setValue(dummyStudent).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(SplashScreenActivity.this, "Added Student", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SplashScreenActivity.this, "Failed to add Student", Toast.LENGTH_LONG).show();
                }
            }
        });


        DatabaseReference refAspirant = firebaseDatabase.getReference("Aspirants/"+dummyStudent.getStudentRegNo());
        refAspirant.setValue(dummyAspirant).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(SplashScreenActivity.this, "Added Aspirant", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SplashScreenActivity.this, "Failed to add aspirant", Toast.LENGTH_LONG).show();
                }
            }
        });


    }
}