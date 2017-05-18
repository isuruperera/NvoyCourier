package com.proximosolutions.nvoycourier.Controller;

import android.app.SearchManager;
import android.content.Context;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.SearchView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.proximosolutions.nvoycourier.MainLogic.Parcel;
import com.proximosolutions.nvoycourier.R;

import java.util.ArrayList;

import static com.proximosolutions.nvoycourier.Controller.MapsActivity.EncodeString;

public class ParcelsActivity extends AppCompatActivity
        implements SearchView.OnQueryTextListener, SearchView.OnCloseListener{

    private SearchManager searchManager;
    private android.widget.SearchView searchView;
    private ParcelSearchExpListAdapter listAdapter;
    private ExpandableListView listView;
    private ArrayList<ParentRow> parentList = new ArrayList<ParentRow>();
    private ArrayList<ParentRow> showTheseParentList = new ArrayList<ParentRow>();
    private ArrayList<String> myParcelKeys;
    private ArrayList<String> myPendingFriends;
    private ArrayList<String> myNewFriends;
    private MenuItem searchItem;
    private Button sendRequest;
    private EditText requestMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parcels);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        databaseReference.child("Couriers").child(EncodeString(FirebaseAuth.getInstance().getCurrentUser().getEmail()))
                .child("parcels")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        refreshList();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

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

    @Override
    public boolean onClose() {
        listAdapter.filterData("");
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        listAdapter.filterData(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newQuery) {
        listAdapter.filterData(newQuery);
        return false;
    }

    public void refreshList() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        myParcelKeys = new ArrayList<>();

        databaseReference.child("Couriers").child(EncodeString(FirebaseAuth.getInstance().getCurrentUser().getEmail())).child("parcels")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> parcelList = dataSnapshot.getChildren();
                        for (DataSnapshot parcel : parcelList) {
                            myParcelKeys.add(parcel.getKey());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        databaseReference.child("Parcels").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                parentList = new ArrayList<ParentRow>();
                Iterable<DataSnapshot> parcelsList = dataSnapshot.getChildren();

                for (DataSnapshot parcel : parcelsList) {
                    Parcel tempParcel = parcel.getValue(Parcel.class);

                    String name = tempParcel.getItemDescription();
                    String parcelID = tempParcel.getParcelID();
                    String parcelStatus = "Expired";
                    int tempParcelStatus = tempParcel.getStatus();
                    if(tempParcelStatus==Parcel.NEW){
                        parcelStatus = "New Parcel";
                    }else if(tempParcelStatus==Parcel.ACCEPTED){
                        parcelStatus = "Waiting for pick up";
                    }else if(tempParcelStatus==Parcel.CANCELLED){
                        parcelStatus = "Rejected";
                    }else if(tempParcelStatus==Parcel.IN_TRANSIT){
                        parcelStatus = "In transit";
                    }else if(tempParcelStatus==Parcel.DELIVERED){
                        parcelStatus = "Delivered";
                    }else if(tempParcelStatus==Parcel.MARKED_DELIVERED) {
                        parcelStatus = "Marked Delivered";
                    }else if(tempParcelStatus==Parcel.CUST_MARKED_NOT_COLLECTED) {
                        parcelStatus = "Customer marked as not collected";
                    }else if(tempParcelStatus==Parcel.CUST_MARKED_NOT_DELIVERED) {
                        parcelStatus = "Customer marked as not delivered";
                    }else if(tempParcelStatus==Parcel.TIME_OUT) {
                        parcelStatus = "Expired before response";
                    }
                    if(myParcelKeys.contains(parcelID)){
                        ChildRow childRow = new ChildRow(parcelID,parcelStatus,R.mipmap.ic_menu_parcel);
                        ParentRow parentRow = null;
                        parentRow = new ParentRow(name, childRow);
                        parentList.add(parentRow);
                    }


                }
                listView = (ExpandableListView)findViewById(R.id.expandable_list_search_parcel);
                listAdapter = new ParcelSearchExpListAdapter(ParcelsActivity.this, parentList);
                listView.setAdapter(listAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        parentList = new ArrayList<ParentRow>();
        showTheseParentList = new ArrayList<ParentRow>();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_window, menu);

        searchItem = menu.findItem(R.id.action_search1);

        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);
        searchView.requestFocus();
        //searchItem.setVisible(false);



        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }
}
