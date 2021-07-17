package com.example.vbookapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookActivity;
import com.facebook.FacebookButtonBase;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.SetOptions;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
    TextView fb,google,logo,text;
    float v = 0;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth auth;
    FirebaseFirestore database;
    ProgressDialog progressDialog;
    LoginButton fb_login;
    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        fb = findViewById(R.id.fb_btn);
        google = findViewById(R.id.google_btn);
        logo = findViewById(R.id.logo);
        text = findViewById(R.id.textView);
        fb_login = findViewById(R.id.loginbutton);

        //transition
        fb.setTranslationY(300);
        google.setTranslationY(300);
        logo.setTranslationY(-300);
        text.setTranslationX(300);
        fb.setAlpha(v);
        google.setAlpha(v);
        logo.setAlpha(v);
        text.setAlpha(v);

        fb.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(1100).start();
        google.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(900).start();
        logo.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(700).start();
        text.animate().translationX(0).alpha(1).setDuration(1000).setStartDelay(800).start();

        //google sign in
        auth=FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        progressDialog=new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("Login");
        progressDialog.setMessage("Account login");

        if (auth.getCurrentUser()!=null){
            Intent intent=new Intent(this, ProfileViewActivity.class);
            startActivity(intent);
            finish();
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        //fb login
        fb_login.setReadPermissions("email","public_profile");
        mCallbackManager = CallbackManager.Factory.create();
        fb_login.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(getApplicationContext(),"Fb Login success",Toast.LENGTH_SHORT).show();
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fb_login.performClick();
            }
        });
    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            firebaseAuthWithGoogle(account.getIdToken());
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
        }
    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        progressDialog.show();
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = auth.getCurrentUser();
                            Map<String, Object> map = new HashMap<>();
                            map.put("user_id",user.getUid());
                            map.put("user_name",user.getDisplayName());
                            map.put("E-mail",user.getEmail());
                            map.put("image",user.getPhotoUrl().toString());
                            database.collection("User")
                                    .document(user.getUid())
                                    .set(map, SetOptions.merge());

                            Intent intent=new Intent(getApplicationContext(), ProfileViewActivity.class);
                            startActivity(intent);
                            Toast.makeText(getApplicationContext(),"Sign up with google",Toast.LENGTH_SHORT).show();
                            // updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            // Log.w(TAG, "signInWithCredential:failure", task.getException());
                            //updateUI(null);
                            Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void handleFacebookAccessToken(AccessToken token) {


        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = auth.getCurrentUser();
                            Map<String, Object> map = new HashMap<>();
                            map.put("user_id",user.getUid());
                            map.put("user_name",user.getDisplayName());
                            map.put("E-mail",user.getEmail());
                            map.put("image",user.getPhotoUrl().toString());
                            database.collection("User")
                                    .document(user.getUid())
                                    .set(map, SetOptions.merge());

                            Intent intent=new Intent(getApplicationContext(),ProfileViewActivity.class);
                            startActivity(intent);
                            Toast.makeText(getApplicationContext(),"Sign up with facebook",Toast.LENGTH_SHORT).show();

                            // updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });
    }

}