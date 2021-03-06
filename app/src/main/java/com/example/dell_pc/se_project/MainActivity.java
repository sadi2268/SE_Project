package com.example.dell_pc.se_project;

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MyActivity";
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    ProgressDialog myProgressDialog;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.v(TAG,"Acceeesssss");
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {


                    Log.v(TAG,"Deniedddddd");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
            else{

        }



        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }

        };

        Button signUpPage = (Button) findViewById(R.id.goto_signup_page);
        signUpPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUpActivity = new Intent(MainActivity.this,signUpPageActivity.class);
                startActivity(signUpActivity);
            }
        });


        Button signIn = (Button) findViewById(R.id.signin_button);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = ((EditText) findViewById(R.id.signin_id)).getText().toString().trim();
                String password = ((EditText) findViewById(R.id.signin_password)).getText().toString().trim();
                myProgressDialog = new ProgressDialog(MainActivity.this,0);
                myProgressDialog.setMessage("Loading");
                myProgressDialog.setCanceledOnTouchOutside(false);
                myProgressDialog.show();

                if(!email.isEmpty() && !password.isEmpty()) {
                    signinAccount(email, password);
                }
                else{
                    Toast.makeText(MainActivity
                            .this, "Email or Password is empty",Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

    public void signinAccount(String email,String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                        myProgressDialog.cancel();
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            //Log.w(TAG, "signInWithEmail:failed", task.getException());
                            try {
                                throw task.getException();
                            } catch(FirebaseAuthInvalidCredentialsException e) {
                                Toast.makeText(MainActivity.this, "Invalid email or password",Toast.LENGTH_SHORT).show();

                            } catch(Exception e) {

                                Toast.makeText(MainActivity.this,"Sign in failed (Check Internet Connection)" +
                                        "" ,Toast.LENGTH_SHORT).show();
                                Log.e(TAG, e.getMessage());
                            }


                        }else{

                            //when successfull login
                            startActivity(new Intent(MainActivity.this,MapsActivity.class));

                            Toast.makeText(MainActivity.this, "Sign in successfull",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    /*

    The email address is badly formatted.
    The password is invalid or the user does not have a password.

    Sign-in Failed: An internal error has occurred. [ WEAK_PASSWORD  ]
     */

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


}
