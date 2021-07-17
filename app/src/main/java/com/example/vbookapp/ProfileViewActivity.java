package com.example.vbookapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileViewActivity extends AppCompatActivity {
    TextView welcome,username,email;
    CircleImageView profile;
    LinearLayout nameLayout, emailLayout;
    Button sumbit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);

        //Intialized
        welcome = findViewById(R.id.textView2);
        profile = findViewById(R.id.profile_image);
        nameLayout = findViewById(R.id.namelayout);
        emailLayout = findViewById(R.id.emaillayout);
        sumbit = findViewById(R.id.submit);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);

        welcome.setTranslationX(-300);
        profile.setTranslationY(-500);
        nameLayout.setTranslationX(0);
        emailLayout.setTranslationX(0);
        sumbit.setTranslationX(300);

        welcome.setAlpha(0);
        profile.setAlpha(0);
        nameLayout.setAlpha(0);
        emailLayout.setAlpha(0);
        sumbit.setAlpha(0);

        welcome.animate().translationX(0).alpha(1).setDuration(1000).setStartDelay(700).start();
        profile.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(900).start();
        nameLayout.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(1000).start();
        emailLayout.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(1200).start();
        sumbit.animate().translationX(0).alpha(1).setDuration(1000).setStartDelay(1200).start();

        //data from database
        FirebaseFirestore.getInstance()
                .collection("User")
                .document(FirebaseAuth.getInstance().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot snapshot = task.getResult();
                            Glide.with(ProfileViewActivity.this).load(snapshot.getString("image")).into(profile);
                            username.setText(snapshot.getString("user_name"));
                            email.setText(snapshot.getString("E-mail"));
                        }
                    }
                });
    }
}