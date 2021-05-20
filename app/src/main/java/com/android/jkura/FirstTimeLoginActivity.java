package com.android.jkura;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.jkura.extras.SessionManager;
import com.android.jkura.extras.StudentModel;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;


public class FirstTimeLoginActivity extends AppCompatActivity {
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    private TextInputLayout editTextPassword, editTextConfirmPass;
    private TextView firstTimeTextView;
    private MaterialButton submitButton;
    private ConstraintLayout layout;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time_login);

        sessionManager = new SessionManager(this);

        firstTimeTextView = findViewById(R.id.first_time_label);
        editTextPassword = findViewById(R.id.et_password);
        editTextConfirmPass = findViewById(R.id.et_password_conf);
        submitButton = findViewById(R.id.submitPass);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();

        signIn();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mAuth.getCurrentUser().getEmail() != null){
                    String password = editTextPassword.getEditText().getText().toString().trim();
                    String passwordConfirm = editTextConfirmPass.getEditText().getText().toString().trim();

                    if (password.equals(passwordConfirm)){

                        //emulate fetch details from school server to get email details
                        final StudentModel student = new StudentModel(
                                "Bsc. Mathematics and Computer Science",
                                password,
                                "John Doe",
                                "SCM 211-0212-2017",
                                mAuth.getCurrentUser().getEmail(),
                                "School of Mathematical Sciences",
                                "Pure and Applied Mathematics"
                        );

                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                        DatabaseReference ref = firebaseDatabase.getReference("Students/"+student.getStudentRegNo());

                        ref.setValue(student).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    sessionManager.setRegNo(student.getStudentRegNo());
                                    sessionManager.setEmailDetails(student.getStudentEmail());
                                    Intent mainIntent = new Intent(FirstTimeLoginActivity.this, HomeActivity.class);
                                    mainIntent.putExtra(LoginActivity.KEY_STUDENT, (Parcelable) student);
                                    FirstTimeLoginActivity.this.startActivity(mainIntent);
                                    FirstTimeLoginActivity.this.finish();
                                } else {
                                    Snackbar snackbar = Snackbar.make(layout,"There was an error while submitting your password." , Snackbar.LENGTH_INDEFINITE);
                                    snackbar.setAction("TRY AGAIN", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            signIn();
                                        }
                                    });
                                    snackbar.show();
                                }
                            }
                        });
                    }
                } else {
                    signIn();
                }

            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                assert account != null;
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }


    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            firstTimeTextView.setText(R.string.provide_pass);

                        } else {
                            layout = findViewById(R.id.layout1);
                            Snackbar snackbar = Snackbar.make(layout,"Account creation failed" , Snackbar.LENGTH_INDEFINITE);
                            snackbar.setAction("TRY AGAIN", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    signIn();
                                }
                            });
                            snackbar.show();
                        }
                    }
                });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

}

