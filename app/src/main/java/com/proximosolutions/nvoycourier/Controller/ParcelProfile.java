package com.proximosolutions.nvoycourier.Controller;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

    private Button removeParcelBtn;
    private Button trackParcelBtn;
    private Button viewLocationBtn;
    private Button viewTransactionsBtn;
    private String currentUserType;
    private String customerState;
    private Parcel currentParcel;
    private Customer sender;
    private Customer receiver;
    private Courier courier;
    private boolean isSenderFetched = false;
    private boolean isReceiverFetched = false;
    private boolean isCourierFetched = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);;
        setContentView(R.layout.activity_parcel_profile);
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

        /*removeParcelBtn = (Button) findViewById(R.id.btn_remove_parcel);
        removeParcelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(currentParcel.getStatus()!=Parcel.IN_TRANSIT || currentParcel.getStatus()!=Parcel.DELIVERED  ){
                    databaseReference.child("Parcels").child(currentParcel.getParcelID()).child("status").setValue(Parcel.CANCELLED);
                }else{
                    Toast.makeText(ParcelProfile.this, "You cannot remove this right now",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        trackParcelBtn = (Button) findViewById(R.id.btn_track_parcel);
        trackParcelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri gmmIntentUri;
                if(currentParcel.getStatus()==Parcel.ACCEPTED){

                    StringBuilder uriString = new StringBuilder("google.navigation:q=");
                    uriString.append(currentParcel.getCurrentLocation().getLatitude());
                    uriString.append(",");
                    uriString.append(currentParcel.getCurrentLocation().getLongitude());
                    uriString.append("&avoid=tfh");

                    gmmIntentUri = Uri.parse(uriString.toString());
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);



                }



            }
        });

        viewLocationBtn = (Button) findViewById(R.id.btn_view_location);
        viewLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringBuilder uriString = new StringBuilder("google.streetview:cbll=");
                uriString.append(currentParcel.getCurrentLocation().getLatitude());
                uriString.append(",");
                uriString.append(currentParcel.getCurrentLocation().getLongitude());

                Uri gmmIntentUri = Uri.parse(uriString.toString());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });
*/



    }

    private void updateView(){
        ((TextView)findViewById(R.id.text_parcel_contact_courier)).setText(courier.getContactNumber());
        ((TextView)findViewById(R.id.text_parcel_contact_courier)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number = courier.getContactNumber();
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
        ((TextView)findViewById(R.id.text_parcel_courier)).setText(courier.getFirstName()+" "+courier.getLastName());
        ((TextView)findViewById(R.id.text_parcel_delivery_fair)).setText(String.valueOf(currentParcel.getDeliveryFair())+" LKR");
        switch(currentParcel.getStatus()){
            case Parcel.NEW:
                ((TextView)findViewById(R.id.text_parcel_status)).setText("New Parcel");
                break;
            case Parcel.ACCEPTED:
                ((TextView)findViewById(R.id.text_parcel_status)).setText("Waiting for courier");
                break;
            case Parcel.CANCELLED:
                ((TextView)findViewById(R.id.text_parcel_status)).setText("Cancelled Parcel");
                break;
            case Parcel.DELIVERED:
                ((TextView)findViewById(R.id.text_parcel_status)).setText("Successfully Delivered");
                break;
            case Parcel.IN_TRANSIT:
                ((TextView)findViewById(R.id.text_parcel_status)).setText("Parcel In Transit");
                break;
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
