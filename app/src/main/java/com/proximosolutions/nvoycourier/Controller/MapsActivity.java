package com.proximosolutions.nvoycourier.Controller;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.location.*;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;
import com.proximosolutions.nvoycourier.MainLogic.ConfigInfo;
import com.proximosolutions.nvoycourier.MainLogic.Courier;
import com.proximosolutions.nvoycourier.MainLogic.Customer;
import com.proximosolutions.nvoycourier.MainLogic.Parcel;
import com.proximosolutions.nvoycourier.R;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener{

    private GoogleMap mMap;
    private GoogleApiClient mClient;
    private String userEmail;
    private TextView searchField;
    private SlidingUpPanelLayout slidingUpPanelLayout;
    private FloatingActionButton recipientsBtn;
    private FloatingActionButton parcelsBtn;
    private FloatingActionButton profileBtn;
    private ArrayList<Courier> nearbyCouriers;
    private volatile ArrayList<Customer> recipients;
    private ArrayList<String> recipient_emails;
    private Parcel currentParcel;
    private String currentCourierID;
    private Courier currentCourier;
    private Courier currentUser;
    private Customer currentRecipient;
    private ConfigInfo nvoyConfigInfo;
    private Parcel trackingParcel;
    private ProgressDialog progressDialog;
    private Spinner spinner;


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        updateConfigInfo();
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }
        updateProfile();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        nearbyCouriers = new ArrayList<>();
        recipients = new ArrayList<>();
        //currentParcel = new Parcel();



        parcelsBtn = (FloatingActionButton) findViewById(R.id.button_parcels);
        parcelsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent parcelsActivity = new Intent(MapsActivity.this, ParcelsActivity.class);
                startActivity(parcelsActivity);
            }
        });

        profileBtn = (FloatingActionButton) findViewById(R.id.button_profile);
        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent userProfile = new Intent(MapsActivity.this,UserProfile.class);


                userProfile.putExtra("customer",currentUser);
                userProfile.putExtra("customerState","Unknown");
                userProfile.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //databaseReference.child("Couriers").child(EncodeString(((TextView)childText.findViewById(R.id.child_text)).getText().toString().trim())).removeEventListener(this);
                startActivity(userProfile);
            }
        });

        slidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        slidingUpPanelLayout.setAnchorPoint((float) 0.14);
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);


        userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        /*FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dataReference = database.getReference();
        dataReference.child("Customers").child(EncodeString(userEmail)).child("friends")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> friendList = dataSnapshot.getChildren();
                        recipient_emails = new ArrayList<String>();

                        for (DataSnapshot friend : friendList) {
                            if ((friend.getValue().toString()).equals("New")) {
                                Intent intent = new Intent(MapsActivity.this, RecipientsActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                PendingIntent pendingIntent = PendingIntent.getActivity(MapsActivity.this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(MapsActivity.this);
                                notificationBuilder.setContentTitle("New Recipient Request");
                                notificationBuilder.setContentText("From: " + DecodeString(friend.getKey().toString()));
                                notificationBuilder.setAutoCancel(true);
                                notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
                                notificationBuilder.setContentIntent(pendingIntent);
                                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                notificationManager.notify(0, notificationBuilder.build());
                            }

                            if ((friend.getValue().toString()).equals("Accepted")) {
                                recipient_emails.add(friend.getKey().toString());
                            }
                        }

                        updateCustomers();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });*/


    }


    private void showProgressWindow(boolean state) {
        if (state) {

            progressDialog = progressDialog.show(this, "Waiting for courier", "Be patient while the courier accepts your request", false, false);
        } else {
            progressDialog.dismiss();
        }

    }

    public static String EncodeString(String string) {
        return string.replace(".", ",");
    }

    public static String DecodeString(String string) {
        return string.replace(",", ".");
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //mMap.setOnMarkerClickListener(this);
        if (mMap != null) {
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {

                    if (marker.getTitle().equals("Me")) {
                        return null;
                    }
                    View v = getLayoutInflater().inflate(R.layout.info_layout, null);

                    TextView name = (TextView) v.findViewById(R.id.courier_name);
                    TextView contact = (TextView) v.findViewById(R.id.courier_contact);
                    Button button = (Button) v.findViewById(R.id.courier_button_contact);

                    name.setText(marker.getTitle());
                    contact.setText(marker.getSnippet());

                    return v;
                }
            });
        }


        mClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mClient.connect();
    }

    private LocationRequest mLocationReq;

    @Override
    protected void onResume() {
        super.onResume();
        if (mClient != null) {
            mClient.connect();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mClient != null) {
            mClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
               ActivityCompat.requestPermissions(this,new String[]   {
                        Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},PackageManager.PERMISSION_GRANTED);



                return;
            }
        }

        mMap.setMyLocationEnabled(true);

        checkGPS();
        mLocationReq = LocationRequest.create();
        mLocationReq.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationReq.setInterval(30000);



        try{

            LocationServices.FusedLocationApi.requestLocationUpdates(mClient,mLocationReq,this);
            Location l = LocationServices.FusedLocationApi.getLastLocation(mClient);
            LatLng ll = new LatLng(l.getLatitude(),l.getLongitude());
            CameraUpdate camUpdate = CameraUpdateFactory.newLatLngZoom(ll,15);
            mMap.animateCamera(camUpdate);
            updateMyLocation(ll);
        }catch (Exception e){

        }



        //LocationServices.FusedLocationApi.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this)
    }

    private void updateConfigInfo(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dataReference = database.getReference();
        dataReference.child("ConfigInfo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                nvoyConfigInfo = dataSnapshot.getValue(ConfigInfo.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void updateMyLocation(LatLng location){
        if(currentUser!=null){
            currentUser.setLocation(new com.proximosolutions.nvoycourier.MainLogic.Location());
            currentUser.getLocation().setLatitude(String.valueOf(location.latitude));
            currentUser.getLocation().setLongitude(String.valueOf(location.longitude));
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference dataReference = database.getReference();
            dataReference.child("Couriers")
                    .child(currentUser.getUserID())
                    .child("location")
                    .child("latitude").setValue(currentUser.getLocation().getLatitude());
            dataReference.child("Couriers")
                    .child(currentUser.getUserID())
                    .child("location")
                    .child("longitude").setValue(currentUser.getLocation().getLongitude());

        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Connection Suspended",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection Failed!",
                Toast.LENGTH_SHORT).show();
    }

    Marker marker;

    @Override
    public void onLocationChanged(Location location) {

        /*Toast.makeText(this, "Lcation Changed",
                Toast.LENGTH_SHORT).show();*/
        if(location == null){
            Toast.makeText(this, "Cannot get current location",
                    Toast.LENGTH_SHORT).show();
        }else{
            LatLng ll = new LatLng(location.getLatitude(),location.getLongitude());
            //mMap.addMarker(new MarkerOptions().position(ll).title("My Location"));
            /*CameraUpdate camUpdate = CameraUpdateFactory.newLatLngZoom(ll,15);
            mMap.animateCamera(camUpdate);*/
            if(marker != null){
                marker.remove();
            }

            MarkerOptions markerOptions = new MarkerOptions()
                    .title("Me")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.myself))
                    .position(ll);
            marker = mMap.addMarker(markerOptions);
            updateMyLocation(ll);


        }


    }



    private void checkGPS(){
        LocationManager lm = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            new AlertDialog.Builder(this)
                    .setTitle("Enable GPS")
                    .setMessage("Turn on your GPS in high accuracy mode from settings")
                    .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent settings = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(settings);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

        }

    }

    private void updateProfile(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dataReference = database.getReference();
        dataReference.child("Couriers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> customerList = dataSnapshot.getChildren();
                ArrayList<Courier> couriers = new ArrayList<Courier>();
                ArrayList<String> custName = new ArrayList<String>();
                for (DataSnapshot courier : customerList) {
                    Courier temp = courier.getValue(Courier.class);

                    if(temp.getUserID().equals(EncodeString(FirebaseAuth.getInstance().getCurrentUser().getEmail().toString())) ){
                        currentUser = temp;
                    }
                }




            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /*private void updateCouriers(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dataReference = database.getReference();

        dataReference.child("Couriers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> courierList = dataSnapshot.getChildren();
                ArrayList<Courier> couriers = new ArrayList<Courier>();
                for (DataSnapshot courier : courierList) {
                    Courier tempCourier = new Courier();
                    tempCourier.setUserID(courier.child("userID").getValue().toString());
                    tempCourier.setFirstName(courier.child("firstName").getValue().toString());
                    tempCourier.setLastName(courier.child("lastName").getValue().toString());
                    tempCourier.setContactNumber(courier.child("contactNumber").getValue().toString());
                    tempCourier.setActive((boolean)courier.child("active").getValue());
                    tempCourier.setExpressCourier((boolean)courier.child("expressCourier").getValue());
                    com.proximosolutions.nvoycourier.MainLogic.Location courierLocation = new com.proximosolutions.nvoycourier.MainLogic.Location();
                    courierLocation.setLatitude(courier.child("location").child("latitude").getValue().toString());
                    courierLocation.setLongitude(courier.child("location").child("longitude").getValue().toString());
                    tempCourier.setCurrentLocation(courierLocation);
                    if(tempCourier.isActive()){
                        couriers.add(tempCourier);
                    }


                }
                nearbyCouriers = couriers;
                addCourierMarkers(couriers);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }*/

    ArrayList<Marker> markers;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    /*public void addCourierMarkers(ArrayList<Courier> couriers){
        if(markers!=null){
            for(Marker marker:markers){
                if(marker != null){
                    marker.remove();
                }
            }

        }
        markers = new ArrayList<>();
        for(Courier courier: couriers){
            if(courier.isExpressCourier()){
                LatLng ll = new LatLng(Double.parseDouble(courier.getCurrentLocation().getLatitude()) ,Double.parseDouble(courier.getCurrentLocation().getLongitude()));
                MarkerOptions markerOptions = new MarkerOptions()
                        .title(courier.getFirstName()+" "+courier.getLastName())
                        .snippet(courier.getUserID())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.express_courier))
                        .position(ll);
                Marker tempMarker = mMap.addMarker(markerOptions);

                markers.add(tempMarker);
            }else{
                LatLng ll = new LatLng(Double.parseDouble(courier.getCurrentLocation().getLatitude()) ,Double.parseDouble(courier.getCurrentLocation().getLongitude()));
                MarkerOptions markerOptions = new MarkerOptions()
                        .title(courier.getFirstName()+" "+courier.getLastName())
                        .snippet(courier.getUserID())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.normal_courier))
                        .position(ll);
                Marker tempMarker = mMap.addMarker(markerOptions);

                markers.add(tempMarker);

            }


            Toast.makeText(this, "Courier Markers Updated",
                    Toast.LENGTH_SHORT).show();
        }
    }*/


    /*@Override
    public boolean onMarkerClick(Marker marker) {

        for(Marker m: markers){
            if(m.getTitle().equals("Me")){
                m.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.myself));
            }
        }
        if(!marker.getTitle().equals("Me")){
            //marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.courier_waiting));
            currentCourier = null;
            currentCourierID = null;
            currentCourierID = null;
            ((TextView)findViewById(R.id.text_delivery_method)).setText("");
            ((TextView)findViewById(R.id.text_selected_courier)).setText("");
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
            ((TextView)findViewById(R.id.text_selected_courier)).setText("Selected: "+marker.getTitle());
            currentCourierID = marker.getSnippet();
            //((TextView)findViewById(R.id.text_selected_courier_contact)).setText(marker.getSnippet());

        }


        Toast.makeText(this, marker.getTitle(),
                Toast.LENGTH_SHORT).show();
        return true;
    }*/


}
