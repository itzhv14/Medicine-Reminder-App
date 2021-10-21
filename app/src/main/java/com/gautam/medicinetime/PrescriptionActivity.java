package com.gautam.medicinetime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PrescriptionActivity extends AppCompatActivity {


    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager  layoutManager;
    TextView txtFullName;

    List<PrescriptionModel> prescriptionModelList;
    AdapterPrescription adapterPrescription;
    Button addPrescriptions;

    String mUid;

    public void checkforuserlogin() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            mUid = user.getUid();
        }
        else{
            startActivity(new Intent(PrescriptionActivity.this,RegisterAcitvity.class));
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescription);
        checkforuserlogin();
        prescriptionModelList = new ArrayList<>();
        addPrescriptions =findViewById(R.id.add_new_prescription);
        //load recycleBook
        recyclerView =(RecyclerView)findViewById(R.id.recyclerview_prescriptions);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        loadPrescriptions();

        addPrescriptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(PrescriptionActivity.this, AddPrescriptionActivity.class);
                startActivity(i);
            }
        });

    }

    private void loadPrescriptions() {
        //path
        Query ref = FirebaseDatabase.getInstance().getReference("Prescription");
        //get all data from this ref
        prescriptionModelList.clear();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    PrescriptionModel products = ds.getValue(PrescriptionModel.class);
//                    book.setbId(ds.getKey());
                    //adding each object
                    if(products.getuId().equals(mUid))
                        prescriptionModelList.add(products);
                    Log.e("e",products.exp_date+products.getuId());
                    Toast.makeText(getApplicationContext(), products.toString(), Toast.LENGTH_SHORT).show();
                }

                Log.e("e", prescriptionModelList.size()+"");
                //adapter
                adapterPrescription= new AdapterPrescription(PrescriptionActivity.this, prescriptionModelList, 0);
                recyclerView.setLayoutManager(new LinearLayoutManager(PrescriptionActivity.this, LinearLayoutManager.VERTICAL, false));


                //set adapter to recycle
                recyclerView.setAdapter(adapterPrescription);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //     Toast.makeText(getActivity(), "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}