package com.gautam.medicinetime;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import butterknife.OnClick;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.gautam.medicinetime.medicine.MedicineActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class AddPrescriptionActivity extends AppCompatActivity {


    Spinner spinner;
    String SelectedType="";
    String vendorId,vendorPhone;
    String mUid;
    private FirebaseAuth mAuth;

    EditText addProduct_pName,addProduct_address,addProduct_availble,addProduct_Price;
    Button add_new_prescriptionBTN;
    ImageView selected_img;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    //image picked will be saved in this
    Uri image_rui=null;

    //permission constants
    private static final int CAMERA_REQUEST_CODE =100;
    private static final int STORAGE_REQUEST_CODE =200;


    //permission constants
    private static final int IMAGE_PICK_CAMERA_CODE =300;
    private static final int IMAGE_PICK_GALLERY_CODE=400;

    //permission array
    String[] cameraPermessions;
    String[] storagePermessions;

    //progresses bar
    ProgressDialog pd;

    EditText company_name, name, exp_date, price;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_prescription);
        checkforuserlogin();
        pd= new ProgressDialog(this);
        company_name = findViewById(R.id.company_name);
        name = findViewById(R.id.name);
        exp_date = findViewById(R.id.exp_date);
        price = findViewById(R.id.price);


        //init permissions
        cameraPermessions=new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermessions=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        add_new_prescriptionBTN = findViewById(R.id.add_new_prescriptionBTN);
        selected_img = findViewById(R.id.selected_img);

        selected_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageDialog();
            }
        });

        add_new_prescriptionBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addnew();
            }
        });

    }

    public void checkforuserlogin() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            mUid = user.getUid();
        }
        else{
            startActivity(new Intent(AddPrescriptionActivity.this,RegisterAcitvity.class));
        }
    }


    void addnew() {
        String pId, uId, pname, pcompany_name, pexp_date, pprice, imgurl;
        pname=name.getText().toString();
        pcompany_name=company_name.getText().toString();
        pexp_date=exp_date.getText().toString();
        pprice=price.getText().toString();
        if(pname.isEmpty()){
            Toast.makeText(this, "name is empty", Toast.LENGTH_SHORT).show();
        }
        else if(pcompany_name.isEmpty()){
            Toast.makeText(this, "type is empty", Toast.LENGTH_SHORT).show();
        }
        else if(pexp_date.isEmpty()){
            Toast.makeText(this, "Location is empty", Toast.LENGTH_SHORT).show();
        }else if(pprice.isEmpty()) {
            Toast.makeText(this, "Author name is empty", Toast.LENGTH_SHORT).show();
        }else if(image_rui==null){
            Toast.makeText(this, "Select Image", Toast.LENGTH_SHORT).show();
        }
        else{
            startAddingPrescription(pname,pcompany_name,pexp_date,pprice);
        }
    }

    private void startAddingPrescription(String pname, String pcompany_name, String pexp_date, String pprice) {
        pd.setMessage("publishing post...");
        pd.setCancelable(false);
        pd.show();
        final String timestamp= String.valueOf(System.currentTimeMillis());
        String filePathName="Posts/"+"post_"+timestamp;


        Bitmap bitmap=((BitmapDrawable)selected_img.getDrawable()).getBitmap();

        ByteArrayOutputStream bout=new ByteArrayOutputStream();
        //image compress
        bitmap.compress(Bitmap.CompressFormat.PNG,100,bout);
        byte[] data=bout.toByteArray();

        StorageReference ref= FirebaseStorage.getInstance().getReference().child(filePathName);


        ref.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                while(!uriTask.isSuccessful());

                String downloadUri=uriTask.getResult().toString();

                if(uriTask.isSuccessful()){
                    //uri is received upload post to firebase database
                    DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Prescription");

                    String bId=ref.push().getKey();
                    Map<String, Object> hashMap=new HashMap<>();
                    //put info
                    hashMap.put("pId", bId);
                    hashMap.put("uId",mUid);
                    hashMap.put("name",pname);
                    hashMap.put("company_name",pcompany_name);
                    hashMap.put("vendorNo",vendorPhone);
                    hashMap.put("exp_date",pexp_date);
                    hashMap.put("imgurl",downloadUri);
                    hashMap.put("price",pprice);

                    ref.child(bId).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            pd.dismiss();
                            Toast.makeText(AddPrescriptionActivity.this, "Product Uploaded", Toast.LENGTH_SHORT).show();

                            //reset view
                            selected_img.setImageURI(null);
                            image_rui=null;

                            startActivity(new Intent(AddPrescriptionActivity.this, MedicineActivity.class));

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(AddPrescriptionActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            pd.dismiss();

                        }
                    });

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddPrescriptionActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.selected_img)
    void onSelectImageClick() {
        showImageDialog();
    }

    private void showImageDialog() {

        String[] options={"Camera","Gallery"};

        //dialog box
        AlertDialog.Builder builder=new AlertDialog.Builder(this);

        builder.setTitle("Choose Action");


        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(which==0){
                    //camera clicked
                    if(!checkCameraPermission()){
                        requestCameraPermission();
                    }
                    else {
                        pickFromCamera();
                    }
                }
                if(which==1){
                    //camera clicked

                    if(!checkStoragePermission()){
                        requestStoragePermission();
                    }
                    else {
                        pickFromGallery();
                    }
                }
            }
        });
        builder.create().show();
    }




    private void pickFromCamera() {

        ContentValues cv=new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE,"Temp Pick");
        cv.put(MediaStore.Images.Media.DESCRIPTION,"Temp Descr");
        image_rui = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,cv);


        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,image_rui);
        startActivityForResult(intent,IMAGE_PICK_CAMERA_CODE);
    }

    private void pickFromGallery() {

        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);
    }

    private boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result;
    }


    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this ,storagePermessions,STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)==(PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);

        return result&&result1;
    }


    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this,cameraPermessions,CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean cameraAccepted=grantResults[0]== PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted=grantResults[1]== PackageManager.PERMISSION_GRANTED;

                    if(cameraAccepted&&storageAccepted){

                        pickFromCamera();
                    }
                    else {

                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if(grantResults.length>1){
                    boolean storageAccepted=false;
                    try {
                        storageAccepted=grantResults[1]== PackageManager.PERMISSION_GRANTED;
                    }catch (ArrayIndexOutOfBoundsException e){

                    }
                    if(storageAccepted){

                        pickFromGallery();
                    }
                    else {
                        //Toast.makeText(this, "gallery both permission needed", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    boolean storageAccepted=false;
                    try {
                        storageAccepted=grantResults[0]== PackageManager.PERMISSION_GRANTED;
                    }catch (ArrayIndexOutOfBoundsException e){

                    }
                    if(storageAccepted){

                        pickFromGallery();
                    }
                    else {
                        //Toast.makeText(this, "gallery both permission needed", Toast.LENGTH_SHORT).show();
                    }
                }

            }
            break;
        }



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==IMAGE_PICK_GALLERY_CODE){
                image_rui=data.getData();

                selected_img.setImageURI(image_rui);
            }
            else if(requestCode==IMAGE_PICK_CAMERA_CODE){

                selected_img.setImageURI(image_rui);

            }
        }
    }

}