package com.android.jkura;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;

import com.android.jkura.extras.SessionManager;
import com.android.jkura.extras.StudentModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    SessionManager sessionManager;
    StudentModel studentModel;
    TextInputLayout editTextPassword;

    public static final String KEY_STUDENT = "student";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sessionManager = new SessionManager(this);
        editTextPassword = findViewById(R.id.et_password);
        studentModel = getIntent().getParcelableExtra(KEY_STUDENT);

        MaterialButton proceedLogin = findViewById(R.id.proceedLogin);
        proceedLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editTextPassword.setError(null);
                String userPassword = editTextPassword.getEditText().getText().toString().trim();
                if(userPassword.equals(studentModel.getPassword())){
                    Intent mainIntent = new Intent(LoginActivity.this, HomeActivity.class);
                    mainIntent.putExtra(LoginActivity.KEY_STUDENT, (Parcelable) studentModel);
                    startActivity(mainIntent);
                    LoginActivity.this.finish();
                } else {
                    editTextPassword.setError("Please enter the correct password!");
                }
            }
        });
    }
}