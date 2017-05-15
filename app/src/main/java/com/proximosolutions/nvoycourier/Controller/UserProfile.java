package com.proximosolutions.nvoycourier.Controller;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.proximosolutions.nvoycourier.MainLogic.Courier;
import com.proximosolutions.nvoycourier.MainLogic.Customer;
import com.proximosolutions.nvoycourier.R;


public class UserProfile extends AppCompatActivity {

    private Button suspendBtn;
    private Button viewTransactionsBtn;
    private String currentUserType;
    private String customerState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //lockIntent = true;
            //this.setActionBar(new ActionBar());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
           // getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_user_profile);
        //String userName = (String)savedInstanceState.get("userName");
        if(savedInstanceState == null){
            Bundle extras = getIntent().getExtras();
            if(extras != null){
                ((android.support.design.widget.CollapsingToolbarLayout)findViewById(R.id.user_profile_toolbar)).setTitle(((Courier)extras.get("customer")).getFirstName()+" "+((Courier)extras.get("customer")).getLastName());
                //((android.support.design.widget.CollapsingToolbarLayout)findViewById(R.id.user_profile_toolbar)).set
                //String s = extras.get("email").toString();
                ((TextView)findViewById(R.id.user_email)).setText(DecodeString(((Courier)extras.get("customer")).getUserID()));
                ((TextView)findViewById(R.id.user_contact_no)).setText(((Courier)extras.get("customer")).getContactNumber());
                ((TextView)findViewById(R.id.user_nic)).setText(((Courier)extras.get("customer")).getNic());

                customerState = (String)extras.get("customerState");
                if(customerState.equals("Pending Request")){
                    ((TextView)findViewById(R.id.user_is_active)).setText("Pending Request");
                    ((Button)findViewById(R.id.btn_suspend_courier)).setText("Remove");
                }

                if(customerState.equals("New Request!")){
                    ((TextView)findViewById(R.id.user_is_active)).setText("New Request");
                    ((Button)findViewById(R.id.btn_suspend_courier)).setText("Accept");
                }

                if(customerState.equals("Valid Recipient")){
                    ((TextView)findViewById(R.id.user_is_active)).setText("Valid Recipient");
                    ((Button)findViewById(R.id.btn_suspend_courier)).setText("Remove");
                }

                if(customerState.equals("Unknown")){
                    ((TextView)findViewById(R.id.user_is_active)).setText("Active User");
                    ((Button)findViewById(R.id.btn_suspend_courier)).setText("");
                    ((Button)findViewById(R.id.btn_suspend_courier)).setActivated(false);
                }






                //((Button)findViewById(R.id.button_parcels)).setText("");

                /*if(extras.get("isActive").equals(true)){
                    ((TextView)findViewById(R.id.user_is_active)).setText("Active");
                    ((Button)findViewById(R.id.btn_suspend_courier)).setText("Suspend");
                }else{
                    ((TextView)findViewById(R.id.user_is_active)).setText("Suspended");
                    ((Button)findViewById(R.id.btn_suspend_courier)).setText("Activate");
                }
                if(extras.get("isCourier").equals(true)){
                    if(extras.get("type").equals(true)){
                        ((TextView)findViewById(R.id.user_type)).setText("Express Courier");
                    }else{
                        ((TextView)findViewById(R.id.user_type)).setText("Regular Courier");
                    }
                    //currentUserType = "Couriers";
                }else{
                    ((TextView)findViewById(R.id.user_type)).setText("Customer");
                    //currentUserType = "Customers";

                }*/

            }
        }else{
            ((android.support.design.widget.CollapsingToolbarLayout)findViewById(R.id.user_profile_toolbar))
                    .setTitle(((Courier)savedInstanceState
                            .getSerializable("customer"))
                            .getFirstName()
                            +" "+((Courier)savedInstanceState
                            .getSerializable("customer"))
                            .getLastName());
            //((android.support.design.widget.CollapsingToolbarLayout)findViewById(R.id.user_profile_toolbar)).set
            //String s = extras.get("email").toString();
            ((TextView)findViewById(R.id.user_email)).setText(DecodeString(((Courier)savedInstanceState.getSerializable("customer")).getUserID()));
            ((TextView)findViewById(R.id.user_contact_no)).setText(((Courier)savedInstanceState.getSerializable("customer")).getContactNumber());
            ((TextView)findViewById(R.id.user_nic)).setText(((Courier)savedInstanceState.getSerializable("customer")).getNic());
            customerState = (String)savedInstanceState.getSerializable("customerState");


            if(customerState.equals("Unknown")){
                ((TextView)findViewById(R.id.user_is_active)).setText("Active Courier");
                ((Button)findViewById(R.id.btn_suspend_courier)).setText("");
                ((Button)findViewById(R.id.btn_suspend_courier)).setActivated(false);
            }




        }

        ((TextView)findViewById(R.id.user_contact_no)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number = ((TextView)findViewById(R.id.user_contact_no)).getText().toString();
                Uri call = Uri.parse("tel:" + number);
                Intent surf = new Intent(Intent.ACTION_DIAL, call);
                startActivity(surf);
            }
        });

        ((TextView)findViewById(R.id.btn_view_parcels)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent parcelsActivity = new Intent(UserProfile.this, ParcelsActivity.class);
                startActivity(parcelsActivity);
            }
        });



        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference();
        suspendBtn = (Button) findViewById(R.id.btn_suspend_courier);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        /*suspendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final TextView email = (TextView)findViewById(R.id.user_email);
                if(customerState.equals("Pending Request")){
                    alertDialogBuilder.setTitle("Remove the pending request?");
                    alertDialogBuilder
                            .setMessage("Click yes to remove!")
                            .setCancelable(false)
                            .setPositiveButton("Yes",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                            databaseReference.child("Customers")
                                                    .child(EncodeString(FirebaseAuth
                                                            .getInstance()
                                                            .getCurrentUser().getEmail()))
                                                    .child("friends")
                                                    .child(EncodeString(email.getText().toString()))
                                                    .setValue("Removed");

                                            databaseReference.child("Customers")
                                                    .child(EncodeString(email.getText().toString()))
                                                    .child("friends")
                                                    .child(EncodeString(FirebaseAuth
                                                            .getInstance()
                                                            .getCurrentUser().getEmail()))
                                                    .setValue("Removed");
                                            onBackPressed();
                                            Log.d("Recipient","Pending Recipient Removed");
                                        }
                                    })

                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }

                if(customerState.equals("New Request!")){
                    alertDialogBuilder.setTitle("Accept the pending request?");
                    alertDialogBuilder
                            .setMessage("Click yes to accept!")
                            .setCancelable(false)
                            .setPositiveButton("Yes",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                            databaseReference.child("Customers")
                                                    .child(EncodeString(FirebaseAuth
                                                            .getInstance()
                                                            .getCurrentUser().getEmail()))
                                                    .child("friends")
                                                    .child(EncodeString(email.getText().toString()))
                                                    .setValue("Accepted");

                                            databaseReference.child("Customers")
                                                    .child(EncodeString(email.getText().toString()))
                                                    .child("friends")
                                                    .child(EncodeString(FirebaseAuth
                                                            .getInstance()
                                                            .getCurrentUser().getEmail()))
                                                    .setValue("Accepted");

                                            Log.d("Recipient","New Recipient Accepted");
                                        }
                                    })

                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }

                if(customerState.equals("Valid Recipient")){
                    alertDialogBuilder.setTitle("Remove the recipient?");
                    alertDialogBuilder
                            .setMessage("Click yes to remove!")
                            .setCancelable(false)
                            .setPositiveButton("Yes",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                            databaseReference.child("Customers")
                                                    .child(EncodeString(FirebaseAuth
                                                            .getInstance()
                                                            .getCurrentUser().getEmail()))
                                                    .child("friends")
                                                    .child(EncodeString(email.getText().toString()))
                                                    .setValue("Removed");

                                            databaseReference.child("Customers")
                                                    .child(EncodeString(email.getText().toString()))
                                                    .child("friends")
                                                    .child(EncodeString(FirebaseAuth
                                                            .getInstance()
                                                            .getCurrentUser().getEmail()))
                                                    .setValue("Removed");

                                            Log.d("Recipient","Exsisting Recipient Removed");
                                        }
                                    })

                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }










            }
        });*/

        /*IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.package.ACTION_LOGOUT");
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("onReceive","Logout in progress");
                //At this point you should start the login activity and finish this one
                System.out.println("REC!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                finish();
            }
        }, intentFilter);
*/
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {

        return super.onCreateView(parent, name, context, attrs);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                //lockIntent = false;
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();

        //System.out.println(getBaseContext().find);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //lockIntent = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        //lockIntent = false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        //lockIntent = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //lockIntent = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        //lockIntent = false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //lockIntent = false;
    }

    public static String EncodeString(String string) {
        return string.replace(".", ",");
    }

    public static String DecodeString(String string) {
        return string.replace(",", ".");
    }
}
