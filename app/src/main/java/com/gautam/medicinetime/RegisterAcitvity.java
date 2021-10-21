package com.gautam.medicinetime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.gautam.medicinetime.medicine.MedicineActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterAcitvity extends AppCompatActivity {


    EditText address,country,email,password,phoneNo,uName;

    private FirebaseAuth mAuth;

    Button gotoLogin,registerBtn,gotoAdmin;

    //loading screen
    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_acitvity);

        address=findViewById(R.id.address);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        phoneNo=findViewById(R.id.phoneNo);
        uName=findViewById(R.id.uName);

        gotoAdmin=findViewById(R.id.gotoAdmin);
        gotoLogin=findViewById(R.id.gotoLogin);
        registerBtn=findViewById(R.id.registerBtn);



        //init firebase
        mAuth = FirebaseAuth.getInstance();

        gotoLogin.setOnClickListener(
                v -> startActivity(new Intent(RegisterAcitvity.this, LoginActivity.class)));
        gotoAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(UserRegister.this, adminLogin.class));
            }
        });
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //user info
                String saddress,scountry,semail,spassword,sphoneNo,suName;
                semail= email.getText().toString();
                suName= uName.getText().toString();
                spassword= password.getText().toString();
                sphoneNo= phoneNo.getText().toString();
                saddress= address.getText().toString();
                scountry="India";
                if (!Patterns.EMAIL_ADDRESS.matcher(semail).matches()){
                    email.setError("Invalided Email");
                    email.setFocusable(true);
                }
                else if(spassword.length()<6){
                    password.setError("Password length at least 6 characters");
                    password.setFocusable(true);
                }
                else if(suName.isEmpty()){
                    uName.setError("Name is empty");
                    uName.setFocusable(true);
                }
                else if(sphoneNo.length()<10){
                    phoneNo.setError("PhoneNo length at least 10 characters");
                    phoneNo.setFocusable(true);
                }
                else if(saddress.length()<4){
                    address.setError("RollNo length at least 4 characters");
                    address.setFocusable(true);
                }
                else {
                    SharedPreferences.Editor editor;
                    editor= PreferenceManager.getDefaultSharedPreferences(RegisterAcitvity.this).edit();
                    editor.putString("username", semail.trim());
                    editor.putString("password", spassword.trim());
                    editor.apply();

                    registerUser(suName,semail,sphoneNo,spassword,saddress);

                }
            }
        });
    }

    private void registerUser(final String name, String email, final String phone, final String password, final String address) {

        ProgressDialog pd= new ProgressDialog(this);
        pd.setMessage("login...");
        pd.setCancelable(false);
        pd.show();
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            // progressDialog.dismiss();


                            FirebaseUser user = mAuth.getCurrentUser();

                            String email= user.getEmail();
                            String uid=user.getUid();
                            final HashMap<Object,String> hashMap=new HashMap<>();

                            //check if commander is allocated or  not
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                            hashMap.put("uName",name);
                            hashMap.put("email",email);
                            hashMap.put("phoneNo",phone);
                            hashMap.put("password",password);
                            hashMap.put("country","India");
                            hashMap.put("address",address);
                            hashMap.put("uId",uid);
                            final FirebaseDatabase database=FirebaseDatabase.getInstance();

                            DatabaseReference reference=database.getReference("Users");

                            reference.child(uid).setValue(hashMap);

                            //sucess
                            Toast.makeText(RegisterAcitvity.this, "Registered with "+user.getEmail(), Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(RegisterAcitvity.this, MedicineActivity.class));
                            finish();

                        }
                        else {
                            //progressDialog.dismiss();

                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //progressDialog.dismiss();

                Toast.makeText(RegisterAcitvity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStart() {

        //init firebase
//        mAuth = FirebaseAuth.getInstance();
//        if(mAuth.getCurrentUser()!=null) {
//            startActivity(new Intent(UserRegister.this, HomePage.class));
//            finish();
//        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final FirebaseAuth mAuth=FirebaseAuth.getInstance();
        String username=prefs.getString("username","");
        String pass=prefs.getString("password","");

        if(username.equals("")&&pass.equals("")) {
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        }
        else {
            //     progressDialog.setMessage("Logging...");
            // progressDialog.show();
            ProgressDialog pd= new ProgressDialog(this);
            pd.setMessage("login...");
            pd.setCancelable(false);
            pd.show();
            mAuth.signInWithEmailAndPassword(username, pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // progressDialog.dismiss();

;
                                // Sign in success, update UI with the signed-in user's information

                                FirebaseUser user = mAuth.getCurrentUser();
                                startActivity(new Intent(RegisterAcitvity.this, MedicineActivity.class));
                                finish();

                            } else {
                                // If sign in fails, display a message to the user.
                                //progressDialog.dismiss();



                                Toast.makeText(RegisterAcitvity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();

                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //progressDialog.dismiss();

                }
            });

        }
        super.onStart();
    }


    public boolean onSupportNavigateUp(){

        onBackPressed();//go baack

        return super.onSupportNavigateUp();
    }
}