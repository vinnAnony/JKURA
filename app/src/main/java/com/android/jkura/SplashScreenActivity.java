package com.android.jkura;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.widget.Toast;

import com.android.jkura.extras.AspirantModel;
import com.android.jkura.extras.SessionManager;
import com.android.jkura.extras.StudentModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashScreenActivity extends AppCompatActivity {

    StudentModel studentModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        SessionManager sessionManager = new SessionManager(this);

        //addDummyStudents();

        if (sessionManager.checkIfFirstTime()) {
            int SPLASH_DISPLAY_LENGTH = 1000;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    Intent mainIntent = new Intent(SplashScreenActivity.this, FirstTimeLoginActivity.class);
                    SplashScreenActivity.this.startActivity(mainIntent);
                    SplashScreenActivity.this.finish();

                }
            }, SPLASH_DISPLAY_LENGTH);


        } else {

            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference ref = firebaseDatabase.getReference("Students");

            ref.orderByChild(sessionManager.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        studentModel = snapshot.getValue(StudentModel.class);
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