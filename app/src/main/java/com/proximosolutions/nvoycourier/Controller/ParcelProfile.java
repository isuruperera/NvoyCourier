package com.proximosolutions.nvoycourier.Controller;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.proximosolutions.nvoycourier.MainLogic.Courier;
import com.proximosolutions.nvoycourier.MainLogic.Customer;
import com.proximosolutions.nvoycourier.MainLogic.Parcel;
import com.proximosolutions.nvoycourier.R;


public class ParcelProfile extends AppCompatActivity {

    private Button acceptBtn;
    private Button rejectBtn;
    private Button navigateToSenderBtn;
    private Button streetviewSenderBtn;
    private Button navigateToReceiverBtn;
    private Button streetviewReceiverBtn;
    private String currentUserType;
    private String customerState;
    private Parcel currentParcel;
    private Customer sender;
    private Customer receiver;
    private Courier courier;
    private boolean isSenderFetched = false;
    private boolean isReceiverFetched = false;
    private boolean isCourierFetched = false;
    private ProgressDialog progressDialog;
    private ValueEventListener valueEventListenerUpdateParcelUI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);;
        setContentView(R.layout.activity_parcel_profile);

        valueEventListenerUpdateParcelUI = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Parcel updatedParcel = dataSnapshot.getValue(Parcel.class);
                updateParcelUI(updatedParcel);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        if(savedInstanceState == null){
            Bundle extras = getIntent().getExtras();
            if(extras != null){
                currentParcel = (Parcel)extras.get("parcel");

            }
        }else{
            currentParcel = (Parcel)savedInstanceState.getSerializable("parcel");

        }
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference();

        //databaseReference.child("Parcels").child(currentParcel.getParcelID()).addValueEventListener(valueEventListenerUpdateParcelUI);

        databaseReference.child("Customers").child(currentParcel.getReceiverID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                receiver = (Customer)dataSnapshot.getValue(Customer.class);
                isReceiverFetched = true;
                if(isSenderFetched && isCourierFetched){
                    updateView();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseReference.child("Customers").child(currentParcel.getSenderID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                sender = (Customer)dataSnapshot.getValue(Customer.class);
                isSenderFetched = true;
                if(isCourierFetched && isReceiverFetched){
                    updateView();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseReference.child("Couriers").child(currentParcel.getCarrierID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                courier = dataSnapshot.getValue(Courier.class);
                isCourierFetched = true;
                if(isSenderFetched && isReceiverFetched){
                    updateView();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        acceptBtn = (Button)findViewById(R.id.btn_accept_parcel);
        rejectBtn = (Button)findViewById(R.id.btn_reject_parcel);
        navigateToReceiverBtn = (Button)findViewById(R.id.btn_navigate_receiver);
        navigateToSenderBtn = (Button)findViewById(R.id.btn_navigate_sender);
        streetviewReceiverBtn = (Button)findViewById(R.id.btn_view_receiver);
        streetviewSenderBtn = (Button)findViewById(R.id.btn_view_sender);
        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(currentParcel.getStatus()==Parcel.NEW ){
                    databaseReference.child("Parcels").child(currentParcel.getParcelID()).child("status").setValue(Parcel.ACCEPTED);
                    ((TextView)findViewById(R.id.text_parcel_status)).setText("Waiting for courier");
                    rejectBtn.setText("CANCEL");
                    rejectBtn.setEnabled(true);
                    acceptBtn.setEnabled(false);
                    currentParcel.setStatus(Parcel.ACCEPTED);
                    updateView();
                }else if(currentParcel.getStatus()==Parcel.ACCEPTED){
                    databaseReference.child("Parcels").child(currentParcel.getParcelID()).child("status").setValue(Parcel.PICKUP);
                    ((TextView)findViewById(R.id.text_parcel_status)).setText("Waiting for courier");
                    rejectBtn.setText("CANCEL");
                    rejectBtn.setEnabled(false);
                    acceptBtn.setEnabled(true);
                    currentParcel.setStatus(Parcel.PICKUP);
                    updateView();
                }else if(currentParcel.getStatus()==Parcel.IN_TRANSIT){
                    databaseReference.child("Parcels").child(currentParcel.getParcelID()).child("status").setValue(Parcel.MARKED_DELIVERED);
                    ((TextView)findViewById(R.id.text_parcel_status)).setText("Marked Delivered");
                    rejectBtn.setText("");
                    rejectBtn.setEnabled(false);
                    acceptBtn.setEnabled(false);
                    currentParcel.setStatus(Parcel.MARKED_DELIVERED);
                    updateView();
                }else if(currentParcel.getStatus()==Parcel.CUST_MARKED_NOT_COLLECTED){
                   /* databaseReference.child("Parcels").child(currentParcel.getParcelID()).child("status").setValue(Parcel.ACCEPTED);
                    ((TextView)findViewById(R.id.text_parcel_status)).setText("Waiting for courier");
                    rejectBtn.setText("CANCEL");
                    rejectBtn.setEnabled(false);
                    acceptBtn.setEnabled(true);
                    currentParcel.setStatus(Parcel.PICKUP);
                    updateView();
                    updateView();*/
                }
            }
        });


        rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentParcel.getStatus()==Parcel.NEW || currentParcel.getStatus()==Parcel.ACCEPTED ){
                    databaseReference.child("Parcels").child(currentParcel.getParcelID()).child("status").setValue(Parcel.CANCELLED);
                    ((TextView)findViewById(R.id.text_parcel_status)).setText("Cancelled Parcel");
                    acceptBtn.setText("");
                    acceptBtn.setActivated(false);
                    currentParcel.setStatus(Parcel.CANCELLED);
                    updateView();

                }
            }
        });


        navigateToReceiverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentParcel.getStatus() != Parcel.DELIVERED || currentParcel.getStatus() != Parcel.CANCELLED) {
                    Uri gmmIntentUri;


                    StringBuilder uriString = new StringBuilder("google.navigation:q=");
                    uriString.append(receiver.getLocation().getLatitude());
                    uriString.append(",");
                    uriString.append(receiver.getLocation().getLongitude());
                    uriString.append("&avoid=tfh");

                    gmmIntentUri = Uri.parse(uriString.toString());
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);

                }
            }


        });


        navigateToSenderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentParcel.getStatus() != Parcel.DELIVERED || currentParcel.getStatus() != Parcel.CANCELLED) {
                    Uri gmmIntentUri;


                    StringBuilder uriString = new StringBuilder("google.navigation:q=");
                    uriString.append(sender.getLocation().getLatitude());
                    uriString.append(",");
                    uriString.append(sender.getLocation().getLongitude());
                    uriString.append("&avoid=tfh");

                    gmmIntentUri = Uri.parse(uriString.toString());
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);

                }
            }


        });


        streetviewReceiverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentParcel.getStatus() != Parcel.DELIVERED || currentParcel.getStatus() != Parcel.CANCELLED) {
                    StringBuilder uriString = new StringBuilder("google.streetview:cbll=");
                    uriString.append(receiver.getLocation().getLatitude());
                    uriString.append(",");
                    uriString.append(receiver.getLocation().getLongitude());

                    Uri gmmIntentUri = Uri.parse(uriString.toString());
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);

                }
            }
        });


        streetviewSenderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentParcel.getStatus() != Parcel.DELIVERED || currentParcel.getStatus() != Parcel.CANCELLED) {
                    StringBuilder uriString = new StringBuilder("google.streetview:cbll=");
                    uriString.append(sender.getLocation().getLatitude());
                    uriString.append(",");
                    uriString.append(sender.getLocation().getLongitude());

                    Uri gmmIntentUri = Uri.parse(uriString.toString());
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);

                }
            }
        });
        updateParcelUI(currentParcel);

    }


    private void updateParcelUI(final Parcel parcel){
        if(parcel.getStatus() ==Parcel.TIME_OUT){
            //showProgressWindow(false);
            new AlertDialog.Builder(ParcelProfile.this)
                    .setTitle("The request has expired")
                    .setMessage("The request has expired because of the delay. Next time, respond to the request within 2 minutes")

                    .setNegativeButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                            ParcelProfile.this.finish();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

        }
        if(parcel.getStatus() == Parcel.CUST_MARKED_NOT_COLLECTED){

            new AlertDialog.Builder(ParcelProfile.this)
                    .setTitle("Did you really pick up the parcel?")
                    .setMessage("The customer has marked the item as not picked up. Pick up the parcel from customer and try again.")

                    .setNegativeButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                            final DatabaseReference databaseReference = firebaseDatabase.getReference();

                            databaseReference.child("Parcels").child(currentParcel.getParcelID()).child("status").setValue(Parcel.ACCEPTED);

                            acceptBtn.setText("PICK UP");
                            acceptBtn.setEnabled(true);
                            parcel.setStatus(Parcel.ACCEPTED);
                            currentParcel=parcel;
                            dialog.dismiss();

                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        if(parcel.getStatus() ==Parcel.IN_TRANSIT ){

            acceptBtn.setText("FINISH");
            rejectBtn.setText("");
            rejectBtn.setEnabled(false);
            currentParcel = parcel;
        }
        if(parcel.getStatus() ==Parcel.CUST_MARKED_NOT_DELIVERED){

            new AlertDialog.Builder(ParcelProfile.this)
                    .setTitle("Did you really deliver?")
                    .setMessage("The customer has marked the item as not delivered. Deliver the parcel to the customer and try again.")

                    .setNegativeButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                            final DatabaseReference databaseReference = firebaseDatabase.getReference();

                            databaseReference.child("Parcels").child(currentParcel.getParcelID()).child("status").setValue(Parcel.IN_TRANSIT);

                            acceptBtn.setText("FINISH");
                            acceptBtn.setEnabled(true);
                            parcel.setStatus(Parcel.ACCEPTED);
                            currentParcel=parcel;
                            dialog.dismiss();

                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        if(parcel.getStatus() ==Parcel.MARKED_DELIVERED){
            acceptBtn.setText("FINISH");
            rejectBtn.setText("");
            acceptBtn.setEnabled(true);
            rejectBtn.setEnabled(false);
            currentParcel.setStatus(Parcel.IN_TRANSIT);
            currentParcel = parcel;
        }

        if(parcel.getStatus() ==Parcel.DELIVERED || parcel.getStatus()==Parcel.CANCELLED ){

            acceptBtn.setText("");
            rejectBtn.setText("");
            acceptBtn.setEnabled(false);
            rejectBtn.setEnabled(false);
            currentParcel = parcel;
        }

    }

    private void disableNavigation(){
        navigateToReceiverBtn.setEnabled(false);
        navigateToSenderBtn.setEnabled(false);
        streetviewReceiverBtn.setEnabled(false);
        streetviewSenderBtn.setEnabled(false);;
        navigateToReceiverBtn.setText("");
        navigateToSenderBtn.setText("");
        streetviewReceiverBtn.setText("");
        streetviewSenderBtn.setText("");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);


    }

    private void updateView(){

        if(currentParcel.getStatus()==Parcel.NEW){
            ((android.support.design.widget.CollapsingToolbarLayout)findViewById(R.id.user_profile_toolbar)).setTitle(currentParcel.getParcelID());
            ((TextView)findViewById(R.id.text_parcel_contact_sender)).setText("Currently unavailable");
            ((TextView)findViewById(R.id.text_parcel_description)).setText(currentParcel.getItemDescription());

            ((TextView)findViewById(R.id.text_parcel_contact_receiver)).setText("Currently unavailable");

            ((TextView)findViewById(R.id.text_parcel_receiver)).setText(receiver.getFirstName()+" "+receiver.getLastName());
            ((TextView)findViewById(R.id.text_parcel_courier)).setText(sender.getFirstName()+" "+sender.getLastName());
            ((TextView)findViewById(R.id.text_parcel_delivery_fair)).setText(String.valueOf(currentParcel.getDeliveryFair())+" LKR");

        }else if(currentParcel.getStatus()==Parcel.ACCEPTED || currentParcel.getStatus()==Parcel.IN_TRANSIT || currentParcel.getStatus()==Parcel.PICKUP ){
            ((android.support.design.widget.CollapsingToolbarLayout)findViewById(R.id.user_profile_toolbar)).setTitle(currentParcel.getParcelID());
            ((TextView)findViewById(R.id.text_parcel_contact_sender)).setText(sender.getContactNumber());
            ((TextView)findViewById(R.id.text_parcel_contact_sender)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String number = sender.getContactNumber();
                    Uri call = Uri.parse("tel:" + number);
                    Intent surf = new Intent(Intent.ACTION_DIAL, call);
                    startActivity(surf);
                }
            });
            ((TextView)findViewById(R.id.text_parcel_contact_receiver)).setText(receiver.getContactNumber());
            ((TextView)findViewById(R.id.text_parcel_contact_receiver)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String number = receiver.getContactNumber();
                    Uri call = Uri.parse("tel:" + number);
                    Intent surf = new Intent(Intent.ACTION_DIAL, call);
                    startActivity(surf);
                }
            });
            ((TextView)findViewById(R.id.text_parcel_receiver)).setText(receiver.getFirstName()+" "+receiver.getLastName());
            ((TextView)findViewById(R.id.text_parcel_courier)).setText(sender.getFirstName()+" "+sender.getLastName());
            ((TextView)findViewById(R.id.text_parcel_delivery_fair)).setText(String.valueOf(currentParcel.getDeliveryFair())+" LKR");

        }else{
            ((android.support.design.widget.CollapsingToolbarLayout)findViewById(R.id.user_profile_toolbar)).setTitle(currentParcel.getParcelID());
            ((TextView)findViewById(R.id.text_parcel_contact_sender)).setText("Unavailable");

            ((TextView)findViewById(R.id.text_parcel_contact_receiver)).setText("Unavailable");

            ((TextView)findViewById(R.id.text_parcel_receiver)).setText(receiver.getFirstName()+" "+receiver.getLastName());
            ((TextView)findViewById(R.id.text_parcel_courier)).setText(sender.getFirstName()+" "+sender.getLastName());
            ((TextView)findViewById(R.id.text_parcel_delivery_fair)).setText(String.valueOf(currentParcel.getDeliveryFair())+" LKR");

        }
        ((TextView)findViewById(R.id.text_parcel_description)).setText(currentParcel.getItemDescription());

        switch(currentParcel.getStatus()){
            case Parcel.NEW:
                ((TextView)findViewById(R.id.text_parcel_status)).setText("New Parcel");
                break;
            case Parcel.ACCEPTED:
                ((TextView)findViewById(R.id.text_parcel_status)).setText("Waiting for courier");
                break;
            case Parcel.CANCELLED:
                ((TextView)findViewById(R.id.text_parcel_status)).setText("Cancelled Parcel");
                disableNavigation();
                break;
            case Parcel.DELIVERED:
                ((TextView)findViewById(R.id.text_parcel_status)).setText("Successfully Delivered");
                disableNavigation();
                break;
            case Parcel.CUST_MARKED_NOT_DELIVERED:
                ((TextView)findViewById(R.id.text_parcel_status)).setText("Customer marked as not delivered");
                break;
            case Parcel.MARKED_DELIVERED:
                ((TextView)findViewById(R.id.text_parcel_status)).setText("Courier marked as delivered");
                break;
            case Parcel.CUST_MARKED_NOT_COLLECTED:
                ((TextView)findViewById(R.id.text_parcel_status)).setText("Customer marked as not collected");
                rejectBtn.setText("CANCEL");
                break;
            case Parcel.IN_TRANSIT:
                ((TextView)findViewById(R.id.text_parcel_status)).setText("Parcel In Transit");
                break;
            case Parcel.TIME_OUT:
                ((TextView)findViewById(R.id.text_parcel_status)).setText("Time out before responding");
                disableNavigation();
                break;
        }
        if(currentParcel.getStatus()==Parcel.ACCEPTED){
            acceptBtn.setText("PICK UP");
            acceptBtn.setEnabled(true);
        }
        if(currentParcel.getStatus()==Parcel.DELIVERED){
            rejectBtn.setText("");
            rejectBtn.setEnabled(false);
        }
        if(currentParcel.getStatus()==Parcel.DELIVERED){
            acceptBtn.setText("");
            acceptBtn.setEnabled(false);
        }
        if(currentParcel.getStatus()==Parcel.ACCEPTED){
            rejectBtn.setText("CANCEL");
            rejectBtn.setEnabled(true);
        }

        if(currentParcel.getStatus()==Parcel.IN_TRANSIT){
            acceptBtn.setText("FINISH");
            acceptBtn.setEnabled(true);
            rejectBtn.setText("");
            rejectBtn.setEnabled(false);
        }
        if(currentParcel.getStatus()==Parcel.CANCELLED || currentParcel.getStatus()==Parcel.TIME_OUT ){
            acceptBtn.setText("");
            acceptBtn.setEnabled(false);
            rejectBtn.setText("");
            rejectBtn.setEnabled(false);

        }



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
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference();

        databaseReference.child("Parcels").child(currentParcel.getParcelID()).addValueEventListener(valueEventListenerUpdateParcelUI);

        //lockIntent = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        //lockIntent = false;
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference();

        databaseReference.child("Parcels").child(currentParcel.getParcelID()).removeEventListener(valueEventListenerUpdateParcelUI);

    }

    @Override
    protected void onStart() {
        super.onStart();
        //lockIntent = true;
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference();

        //databaseReference.child("Parcels").child(currentParcel.getParcelID()).addValueEventListener(valueEventListenerUpdateParcelUI);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference();

        databaseReference.child("Parcels").child(currentParcel.getParcelID()).removeEventListener(valueEventListenerUpdateParcelUI);

    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference();

        databaseReference.child("Parcels").child(currentParcel.getParcelID()).removeEventListener(valueEventListenerUpdateParcelUI);

        //lockIntent = false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference();

        databaseReference.child("Parcels").child(currentParcel.getParcelID()).addValueEventListener(valueEventListenerUpdateParcelUI);

        //lockIntent = false;
    }

    public static String EncodeString(String string) {
        return string.replace(".", ",");
    }

    public static String DecodeString(String string) {
        return string.replace(",", ".");
    }
}
