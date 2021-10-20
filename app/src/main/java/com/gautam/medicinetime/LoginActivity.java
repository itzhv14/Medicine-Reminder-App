package com.gautam.medicinetime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gautam.medicinetime.medicine.MedicineActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {


    Button user_login_signIn,gotoRegister;
    TextView user_login_password,user_login_email;

    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        user_login_signIn=findViewById(R.id.user_login_signIn);
        user_login_password=findViewById(R.id.user_login_password);
        user_login_email=findViewById(R.id.user_login_email);
        gotoRegister=findViewById(R.id.gotoRegister);
        linearLayout=findViewById(R.id.linearLayout);


        gotoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterAcitvity.class));
            }
        });
        user_login_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sign
                String email,password;
                email=user_login_email.getText().toString();
                password=user_login_password.getText().toString();
                if(!email.equals("")||!password.equals("")){
                    SharedPreferences.Editor editor;
                    editor= PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit();
                    editor.putString("username", email.trim());
                    editor.putString("password", password.trim());
                    editor.apply();
                    signIn(email,password);
                }
            }
        });
    }

    private void signIn(String email, String password) {
        final ProgressDialog progressDialog;
        //  progressDialog=new ProgressDialog(this,R.style.Theme_AppCompat_DayNight_DarkActionBar);
        // progressDialog.setMessage("Logging...");
        //  progressDialog.show();

        //to show loading screen

        final FirebaseAuth mAuth;
        mAuth= FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //    progressDialog.dismiss();
                            // Sign in success, update UI with the signed-in user's information;
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(LoginActivity.this, MedicineActivity.class));
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            //    progres


                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}