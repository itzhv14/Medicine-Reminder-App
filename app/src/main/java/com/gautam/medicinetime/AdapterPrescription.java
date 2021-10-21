package com.gautam.medicinetime;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gautam.medicinetime.medicine.MedicineActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterPrescription extends RecyclerView.Adapter<AdapterPrescription.MyHolder>  {

    Context context;
    List<PrescriptionModel> productsList;
    boolean isUser;
    int action;//0 view 1 update 2 remove
    public AdapterPrescription(Context context, List<PrescriptionModel> productsList,int action) {
        this.context = context;
        this.productsList = productsList;
        this.action=action;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_image,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        final String name= productsList.get(position).getName();
        final String company_name= productsList.get(position).getCompany_name();
        final String total_price= productsList.get(position).getPrice();
        final String exp_date= productsList.get(position).getExp_date();
        final String imageUrl= productsList.get(position).getImgurl();

        //setdata
        Picasso.get().load(imageUrl).into(holder.img);
        holder.pName.setText(""+name);
        holder.totalPrice.setText("Total Price : "+ total_price);
        holder.company_name.setText("company name : "+ company_name);
        holder.exp_date.setText("exp date : "+ exp_date);
        //handle click

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(productsList.get(position));
            }
        });
    }

    private void showDialog(final PrescriptionModel book) {
        //this function is for removing book from database
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage("Do You Want To Delete This Product?");
        alertDialogBuilder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        final ProgressDialog pd=new ProgressDialog(context);
                        pd.setMessage("Deleting..");

                        StorageReference picRef= FirebaseStorage.getInstance().getReferenceFromUrl(book.getImgurl());
                        picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                //image
                                Query fquery= FirebaseDatabase.getInstance().getReference("Prescription").orderByChild("pId").equalTo(book.getpId());
                                fquery.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for(DataSnapshot ds:dataSnapshot.getChildren()){
                                            ds.getRef().removeValue();
                                        }
                                        Toast.makeText(context, "Deleted Book", Toast.LENGTH_SHORT).show();
                                        pd.dismiss();
                                        context.startActivity(new Intent(context, MedicineActivity.class));
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
                                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder{
        ImageView img;
        TextView pName,company_name,totalPrice,exp_date;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            img=itemView.findViewById(R.id.img);
            pName=itemView.findViewById(R.id.pName);
            company_name=itemView.findViewById(R.id.company_name);
            totalPrice=itemView.findViewById(R.id.totalPrice);
            exp_date=itemView.findViewById(R.id.exp_date);
        }
    }

}