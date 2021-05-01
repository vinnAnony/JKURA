package com.android.jkura;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SplashScreenActivity extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGTH = 1000;
    private final String TAG = "SplashScreen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                //Just to test//
                //To be deleted//
                //TODO: Delete the firebase stuff
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference ref = firebaseDatabase.getReference("Test");
                ref.setValue("All working")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Intent mainIntent = new Intent(SplashScreenActivity.this, FirstTimeLoginActivity.class);
                                    SplashScreenActivity.this.startActivity(mainIntent);
                                    SplashScreenActivity.this.finish();
                                    Toast.makeText(getApplicationContext(), "Firebase hooked up", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Firebase didn't hook up", Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: "+e.toString());
                                Toast.makeText(getApplicationContext(), "Firebase didn't hook up", Toast.LENGTH_LONG).show();
                            }
                        });

            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}