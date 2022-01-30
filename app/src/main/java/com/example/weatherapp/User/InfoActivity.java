package com.example.weatherapp.User;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weatherapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class InfoActivity extends AppCompatActivity {

    EditText names,emails,citys;
    Button updates;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        names= (EditText) findViewById(R.id.name);
        emails= (EditText) findViewById(R.id.email);
        citys= (EditText) findViewById(R.id.city);
        updates = ( Button) findViewById(R.id.update);


        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();


        final TextView nameText = (TextView) findViewById(R.id.name);
        final TextView emailText = (TextView) findViewById(R.id.email);
        final TextView cityText = (TextView) findViewById(R.id.city);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        updates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emails.getText().toString();
                String name  = names.getText().toString();
                String city = citys.getText().toString();

                HashMap hashMap = new HashMap();
                hashMap.put("city",city);
                hashMap.put("email",email);
                hashMap.put("fullName",name);



                reference.child(userID).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {

                        Toast.makeText(InfoActivity.this, "Data is Successfully Updated", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });






    }



}