package com.android.jkura;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

public class ProvidePinActivity extends AppCompatActivity {

    private KeyguardManager mKeyguardManager;
    private static final int REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provide_password);

        mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);

        if (!mKeyguardManager.isKeyguardSecure()) {
            // Show a message that the user hasn't set up a lock screen.
            Toast.makeText(this, "Kindly Set Up Secure Screen Lock To Proceed", Toast.LENGTH_SHORT).show();
        } else {
            showAuthenticationScreen();
        }
    }

    private void showAuthenticationScreen() {
        // Create the Confirm Credentials screen. You can customize the title and description. Or
        // we will provide a generic one for you if you leave it null
        Intent intent = mKeyguardManager.createConfirmDeviceCredentialIntent(null, null);
        if (intent != null) {
            startActivityForResult(intent, REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS);
        }
    }

    private void startHomeAct() {
        startActivity(new Intent(ProvidePinActivity.this, HomeActivity.class));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS) {
            if (resultCode == RESULT_OK) {
                startHomeAct();
            }
        } else {
            // The user canceled or didn’t complete the lock screen
            Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();
        }

    }
}


