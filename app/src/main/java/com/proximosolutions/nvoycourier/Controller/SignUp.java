package com.proximosolutions.nvoycourier.Controller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.proximosolutions.nvoycourier.MainLogic.Customer;
import com.proximosolutions.nvoycourier.R;

import static com.proximosolutions.nvoycourier.Controller.MapsActivity.DecodeString;
import static com.proximosolutions.nvoycourier.Controller.MapsActivity.EncodeString;

public class SignUp extends AppCompatActivity {

    private EditText firstName;
    private EditText lastName;
    private EditText nic;
    private EditText contactNo;
    private EditText userID;
    private Button signUpBtn;
    private EditText password;
    private EditText reTypePassword;
    FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        firstName = (EditText)findViewById(R.id.sign_up_firstName);
        lastName = (EditText)findViewById(R.id.sign_up_lastName);
        nic = (EditText)findViewById(R.id.sign_up_national_ic);
        contactNo = (EditText)findViewById(R.id.sign_up_contact_no);
        userID = (EditText)findViewById(R.id.sign_up_email_address);
        password = (EditText)findViewById(R.id.sign_up_password);
        reTypePassword = (EditText)findViewById(R.id.sign_up_password_retype);
        signUpBtn = (Button)findViewById(R.id.btn_sign_up);


        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateInputs()){

                    final Customer tempCustomer = new Customer();
                    tempCustomer.setFirstName(firstName.getText().toString());
                    tempCustomer.setLastName(lastName.getText().toString());
                    tempCustomer.setContactNumber(contactNo.getText().toString());
                    tempCustomer.setUserID(EncodeString(userID.getText().toString()));
                    tempCustomer.setNic(nic.getText().toString());
                    tempCustomer.setActive(true);
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference dataReference = database.getReference();
                    mAuth = FirebaseAuth.getInstance();
                    mAuth.createUserWithEmailAndPassword(DecodeString(tempCustomer.getUserID()), password.getText().toString());

                    dataReference.child("Customers").child(tempCustomer.getUserID()).setValue(tempCustomer);

                    signInUser(DecodeString(tempCustomer.getUserID()),password.getText().toString());


                }
            }
        });

    }

    private void signInUser(String email, String password){
        showProgressWindow(true);
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null){

                        Intent mapsActivity = new Intent(SignUp.this,MapsActivity.class);
                        mapsActivity.putExtra("userID",FirebaseAuth.getInstance().getCurrentUser().getEmail());
                        startActivity(mapsActivity);
                        mapsActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        Log.d("User Status","Maps Opened1!");

                }else{
                        signUpBtn.setError("Some error occurred");
                }
                showProgressWindow(false);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //Write your logic here
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean validateInputs(){
        boolean flag = true;
        if(firstName.getText().toString().equals("")){
            firstName.setError("This field is required");
            flag = false;
        }
        if(lastName.getText().toString().equals("")){
            lastName.setError("This field is required");
            flag = false;
        }
        if(nic.getText().toString().equals("")){
            nic.setError("This field is required");
            flag = false;
        }
        if(contactNo.getText().toString().equals("")){
            contactNo.setError("This field is required");
            flag = false;
        }
        if(userID.getText().toString().equals("")){
            userID.setError("This field is required");
            flag = false;
        }
        if(password.getText().toString().equals("")){
            password.setError("This field is required");
            flag = false;
        }
        if(password.getText().toString().length()<8){
            password.setError("At least 8 characters required");
            flag = false;
        }
        if(!password.getText().toString().equals(reTypePassword.getText().toString())){
            reTypePassword.setError("Passwords do not match");
            flag = false;
        }
        if(reTypePassword.getText().toString().equals("")){
            reTypePassword.setError("This field is required");
            flag = false;
        }
        if(contactNo.getText().toString().length()!=10){
            contactNo.setError("Invalid phone number");
            flag = false;
        }
        if(!((nic.getText().toString().toUpperCase()).endsWith("V"))){
            nic.setError("Invalid NIC number");
            flag = false;
        }
        if(!userID.getText().toString().contains("@")){
            userID.setError("Invalid email address");
            flag = false;
        }


        return flag;
    }

    private void showProgressWindow(boolean state) {
        if (state) {

            progressDialog = progressDialog.show(this, "Login", "Please wait...", false, false);
        } else {
            progressDialog.dismiss();
        }

    }



}
