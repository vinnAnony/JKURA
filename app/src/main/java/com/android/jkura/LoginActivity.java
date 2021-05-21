package com.android.jkura;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.jkura.extras.SessionManager;
import com.android.jkura.extras.StudentModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
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

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    SessionManager sessionManager;
    StudentModel studentModel;
    TextInputLayout editTextPassword;
    TextView currentUserEmail;

    public static final String KEY_STUDENT = "student";
    private final String TAG = "Login Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sessionManager = new SessionManager(this);
        editTextPassword = findViewById(R.id.et_password);
        studentModel = sessionManager.getStudentDetails();

        currentUserEmail = findViewById(R.id.loggedEmail);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (acct != null) {
            String voterEmail = acct.getEmail();
            currentUserEmail.setText(voterEmail);
        }

        MaterialButton proceedLogin = findViewById(R.id.proceedLogin);
        proceedLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userPassword = Objects.requireNonNull(editTextPassword.getEditText()).getText().toString().trim();

                editTextPassword.setError(null);
                if(userPassword.equals(studentModel.getPassword())){
                    sessionManager.setLoginValue(true);
                    Intent mainIntent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(mainIntent);
                    LoginActivity.this.finish();
                } else {
                    editTextPassword.setError("Please enter the correct password!");
                }
            }
        });
    }
}