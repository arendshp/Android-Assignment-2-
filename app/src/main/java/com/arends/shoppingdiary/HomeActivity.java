package com.arends.shoppingdiary;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.arends.shoppingdiary.model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;


public class HomeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FloatingActionButton fab_add;
    private FloatingActionButton fab_date;
    private DatabaseReference mDatabaseReference;
    private RecyclerView mRecyclerView;
    private TextView totalSumResult;
    private TextView mTitle;
    private Context context;
    Calendar calendar = Calendar.getInstance();



    //global variable

    private String where, item, payment_amount;
    private int amount;
    private String post_key;
    private int mYear, mMonth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.toolbar);
        mTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        mTitle.setText(toolbar.getTitle());
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        totalSumResult = findViewById(R.id.txt_total_amount);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Shopping List");
        mDatabaseReference.keepSynced(true);

        mRecyclerView = findViewById(R.id.recycler_home);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);

        //total sum number

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int totalamount = 0;

                for (DataSnapshot snap : snapshot.getChildren()){

                    Data data = snap.getValue(Data.class);

                    totalamount += data.getAmount();

                    String stringTotal = String.valueOf("R " + totalamount +" .00");

                    totalSumResult.setText(stringTotal);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //initialize fab
        fab_add = findViewById(R.id.fab_add);
        fab_date = findViewById(R.id.fab_search);

        //onclick listener fab_add
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                customDialog();

            }
        });

        //onclick listener fab_search
        fab_date.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(year,month,dayOfMonth);
                        calendar.getTime();
                    }
                },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();

                }
        });
     //end of oncreate method
    }

    private void customDialog(){

        AlertDialog.Builder mydialog = new AlertDialog.Builder(HomeActivity.this);

        LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);
        View myview = inflater.inflate(R.layout.input_data,null);

        AlertDialog dialog = mydialog.create();

        dialog.setView(myview);

        EditText where = myview.findViewById(R.id.edt_where);
        EditText item = myview.findViewById(R.id.edt_item);
        EditText payment_type = myview.findViewById(R.id.edt_payment_type);
        EditText amount = myview.findViewById(R.id.edt_amount);
        Button btn_submit = myview.findViewById(R.id.btn_submit);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mWhere = where.getText().toString().trim();
                String mItem = item.getText().toString().trim();
                String mPayment = payment_type.getText().toString().trim();
                String mAmount = amount.getText().toString().trim();

                int amount = Integer.parseInt(mAmount);

                if (TextUtils.isEmpty(mWhere)){
                    where.setError("Required field");
                    return;
                }
                if (TextUtils.isEmpty(mItem)){
                    item.setError("Required field");
                    return;
                }
                if (TextUtils.isEmpty(mPayment)){
                    payment_type.setError("Required field");
                }

                //generate random id
                String id  = mDatabaseReference.push().getKey();

                //generate current date
                String date = DateFormat.getDateInstance().format(new Date());

                //instance of model class
                Data data = new Data(mWhere, mItem, mPayment, amount, date, id);

                mDatabaseReference.child(id).setValue(data);

                Toast.makeText(HomeActivity.this, "Data added", Toast.LENGTH_SHORT).show();

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    protected void onStart(){
        super.onStart();

        FirebaseRecyclerAdapter<Data, MyViewHolder>adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>
                (
                        Data.class,
                        R.layout.item_data,
                        MyViewHolder.class,
                        mDatabaseReference
                ) {
            @Override
            protected void populateViewHolder(MyViewHolder myViewHolder, Data data, int i) {

                myViewHolder.setDate(data.getDate());
                myViewHolder.setWhere(data.getWhere());
                myViewHolder.setItem(data.getItem());
                myViewHolder.setPaymentType(data.getPayment_type());
                myViewHolder.setAmount(data.getAmount());

                myViewHolder.myview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        post_key = getRef(i).getKey();
                        where = data.getWhere();
                        item = data.getItem();
                        payment_amount = data.getPayment_type();
                        amount = data.getAmount();



                        updateData();
                    }
                });



            }
        };

        mRecyclerView.setAdapter(adapter);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        View myview;

        public MyViewHolder(View itemView) {
            super(itemView);
            myview = itemView;
        }

        public void setWhere(String where){
            TextView mWhere = myview.findViewById(R.id.where);
            mWhere.setText(where);
        }

        public void setItem(String item){
            TextView mItem = myview.findViewById(R.id.item);
            mItem.setText(item);
        }

        public void setPaymentType(String payment_type){
            TextView mPaymentType = myview.findViewById(R.id.payment_type);
            mPaymentType.setText(payment_type);
        }

        public void setDate(String date){
            TextView mDate = myview.findViewById(R.id.date);
            mDate.setText(date);
        }

        public void setAmount(int amount){

            TextView mAmount = myview.findViewById(R.id.amount);
            String stAmount = String.valueOf(amount);
            mAmount.setText(stAmount);
        }
    }

    public void updateData(){

        AlertDialog.Builder mydialog = new AlertDialog.Builder(HomeActivity.this);

        LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);

        View mView = inflater.inflate(R.layout.update_input, null);

        AlertDialog dialog = mydialog.create();

        dialog.setView(mView);

        final EditText edt_where = mView.findViewById(R.id.edt_where_update);
        final EditText edt_item = mView.findViewById(R.id.edt_item_update);
        final EditText edt_payment = mView.findViewById(R.id.edt_payment_type_update);
        final EditText edt_amount = mView.findViewById(R.id.edt_amount_update);

        edt_where.setText(where);
        edt_where.setSelection(where.length());

        edt_item.setText(item);
        edt_item.setSelection(item.length());

        edt_payment.setText(payment_amount);
        edt_payment.setSelection(payment_amount.length());

        edt_amount.setText(String.valueOf(amount));
        edt_amount.setSelection(String.valueOf(amount).length());



        Button btnUpdate = mView.findViewById(R.id.btn_update);
        Button btnDelete = mView.findViewById(R.id.btn_delete);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mWhere = edt_where.getText().toString().trim();
                String mItem = edt_item.getText().toString().trim();
                String mPayment = edt_payment.getText().toString().trim();
                String mAmount = edt_amount.getText().toString().trim();

                int intamount = Integer.parseInt(mAmount);


                String date = DateFormat.getDateInstance().format(new Date());

                //generate random id
                String id  = mDatabaseReference.push().getKey();

                //instance of model class
                Data data = new Data(mWhere, mItem, mPayment, intamount, date, id);

                mDatabaseReference.child(post_key).setValue(data);

                dialog.dismiss();

                Toast.makeText(HomeActivity.this, "Data updated", Toast.LENGTH_SHORT).show();

            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDatabaseReference.child(post_key).removeValue();

                dialog.dismiss();

                Toast.makeText(HomeActivity.this, "Data deleted", Toast.LENGTH_SHORT).show();

            }
        });

        dialog.show();
    }
}