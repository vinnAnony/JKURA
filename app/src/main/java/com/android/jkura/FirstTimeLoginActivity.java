package com.android.jkura;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.jkura.extras.SessionManager;
import com.android.jkura.extras.StudentModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;


public class FirstTimeLoginActivity extends AppCompatActivity {
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    private TextInputLayout editTextPassword, editTextConfirmPass;
    private TextView firstTimeTextView, emailNoticeTextView;
    private MaterialButton submitButton;
    private ConstraintLayout layout;

    private SessionManager sessionManager;

    boolean doubleBackToExitPressedOnce = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time_login);

        sessionManager = new SessionManager(this);

        firstTimeTextView = findViewById(R.id.first_time_label);
        editTextPassword = findViewById(R.id.et_password);
        editTextConfirmPass = findViewById(R.id.et_password_conf);
        submitButton = findViewById(R.id.submitPass);
        emailNoticeTextView = findViewById(R.id.email_notice);

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

                if (Objects.requireNonNull(mAuth.getCurrentUser()).getEmail() != null) {
                    String password = Objects.requireNonNull(editTextPassword.getEditText()).getText().toString().trim();
                    String passwordConfirm = Objects.requireNonNull(editTextConfirmPass.getEditText()).getText().toString().trim();

                    if (password.equals(passwordConfirm)) {

                        Random random = new Random();
                        int randomNumber = 100 + random.nextInt(700);
                        //emulate fetch details from school server to get email details
                        final StudentModel student = new StudentModel(
                                "Bsc. Mathematics and Computer Science",
                                password,
                                "John Doe",
                                "SCM211-"+randomNumber+"-2017",
                                mAuth.getCurrentUser().getEmail(),
                                "School of Mathematical Sciences",
                                "Pure and Applied Mathematics",
                                0,
                                0
                        );

                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                        final DatabaseReference ref = firebaseDatabase.getReference("Students/" + student.getStudentRegNo());

                        final String replacedMail = replaceDot(Objects.requireNonNull(mAuth.getCurrentUser().getEmail()));

                        DatabaseReference RegRef = firebaseDatabase.getReference("Emails/" + replacedMail);
                        RegRef.setValue(student.getStudentRegNo()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    ref.setValue(student).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                sessionManager.saveStudentDetails(student);
                                                sessionManager.setReplacedMail(replacedMail);
                                                Intent mainIntent = new Intent(FirstTimeLoginActivity.this, HomeActivity.class);
                                                mainIntent.putExtra(LoginActivity.KEY_STUDENT, (Parcelable) student);
                                                FirstTimeLoginActivity.this.startActivity(mainIntent);
                                                FirstTimeLoginActivity.this.finish();
                                            } else {
                                                Snackbar snackbar = Snackbar.make(layout, "There was an error while submitting your password.", Snackbar.LENGTH_INDEFINITE);
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
                            }
                        });

                    }
                } else {
                    signIn();
                }

            }
        });

    }

    public static String replaceDot(String email) {

        String regexTarget = ".";
        return email.replace(regexTarget, "-");

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                final GoogleSignInAccount account = task.getResult(ApiException.class);
                assert account != null;
                boolean result = explodeEmail(Objects.requireNonNull(account.getEmail()));
                if(result){

                    String email = replaceDot(account.getEmail());
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference ref = database.getReference("Emails/"+email);

                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                navigateToLogin();
                            } else {
                                firebaseAuthWithGoogle(account.getIdToken());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
            }
        }
        if (resultCode==RESULT_CANCELED) {

            if (doubleBackToExitPressedOnce) {
                finishAffinity();
                return;
            }
            else {
                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, "Please select an account to continue or click BACK again to exit", Toast.LENGTH_LONG).show();
                signIn();
            }

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 8000);
        }
    }

    private void navigateToLogin() {
        Intent intent = new Intent(FirstTimeLoginActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private boolean explodeEmail(String email) {
        String[] atSignSplit = email.split("@");
        Log.d(TAG, "explodeEmail: First" + Arrays.toString(atSignSplit));

        String[] domainSplit = atSignSplit[1].split("\\.");
        Log.d(TAG, "explodeEmail: Second" + Arrays.toString(domainSplit));

        if (domainSplit[1].equals("jkuat")){
            if (domainSplit[0].equals("students")) {
                return true;
            }else{
                displayError();
                return false;
            }
        } else {
            displayError();
            return false;
        }

    }

    private void displayError() {

        mGoogleSignInClient.signOut();

        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.email_account_error_popup, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setCancelable(false);
        final AlertDialog confirmVoteDialog = builder.create();
        confirmVoteDialog.show();

        Button okButton = dialogView.findViewById(R.id.popCancelBtn);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmVoteDialog.dismiss();
                signIn();
            }
        });

    }

    private void setContentVisible(){
        editTextConfirmPass.setVisibility(View.VISIBLE);
        editTextPassword.setVisibility(View.VISIBLE);
        submitButton.setVisibility(View.VISIBLE);
        firstTimeTextView.setText(R.string.provide_pass);
        emailNoticeTextView.setVisibility(View.INVISIBLE);
    }

//    private void setContentInvisible(){
//        editTextConfirmPass.setVisibility(View.INVISIBLE);
//        editTextPassword.setVisibility(View.INVISIBLE);
//        submitButton.setVisibility(View.INVISIBLE);
//        firstTimeTextView.setText(R.string.first_time_login);
//        emailNoticeTextView.setVisibility(View.VISIBLE);
//    }


    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                           setContentVisible();

                        } else {
                            layout = findViewById(R.id.layout1);
                            Snackbar snackbar = Snackbar.make(layout, "Account creation failed", Snackbar.LENGTH_INDEFINITE);
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

