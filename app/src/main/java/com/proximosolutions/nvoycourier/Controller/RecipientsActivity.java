package com.proximosolutions.nvoycourier.Controller;

import android.app.SearchManager;
import android.content.Context;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.proximosolutions.nvoycourier.MainLogic.Customer;
import com.proximosolutions.nvoycourier.R;

import java.util.ArrayList;

public class RecipientsActivity extends AppCompatActivity
        implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {


    private SearchManager searchManager;
    private android.widget.SearchView searchView;
    private UserSearchExpListAdapter listAdapter;
    private ExpandableListView listView;
    private ArrayList<ParentRow> parentList = new ArrayList<ParentRow>();
    private ArrayList<ParentRow> showTheseParentList = new ArrayList<ParentRow>();
    private ArrayList<String> myAcceptedFriends;
    private ArrayList<String> myPendingFriends;
    private ArrayList<String> myNewFriends;
    private MenuItem searchItem;
    private Button sendRequest;
    private EditText requestMail;

    View suspendCourier;

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipients);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        databaseReference.child("Customers").child(EncodeString(FirebaseAuth.getInstance().getCurrentUser().getEmail()))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        refreshList();
                        Log.d("Customer","Friend List Refreshed");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        sendRequest = (Button)findViewById(R.id.btn_send_request);
        requestMail = (EditText)findViewById(R.id.text_send_request);

        //View.OnClickListener listener = sendRequest.get

        sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = firebaseDatabase.getReference();
                databaseReference.child("Customers").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String emailAddr = requestMail.getText().toString();
                        emailAddr = EncodeString(emailAddr);
                        Customer temp = dataSnapshot.child(emailAddr).getValue(Customer.class);
                        if(temp==null){
                            requestMail.setError("Invalid User!");
                        }else{

                            sendRequest();

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });





    }

    /*@Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        suspendCourier = inflater.inflate(R.layout.search_courier, container, false);
        return suspendCourier;

    }*/

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    public void refreshList(){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        myAcceptedFriends = new ArrayList<>();
        myPendingFriends = new ArrayList<>();
        myNewFriends = new ArrayList<>();

        databaseReference.child("Customers").child(EncodeString(FirebaseAuth.getInstance().getCurrentUser().getEmail())).child("friends")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> friendList = dataSnapshot.getChildren();
                        for (DataSnapshot courier : friendList) {
                            if(courier.getValue().equals("Accepted")){
                                myAcceptedFriends.add(courier.getKey().replaceAll("\"",""));
                            }
                            if(courier.getValue().equals("Pending")){
                                myPendingFriends.add(courier.getKey().replaceAll("\"",""));
                            }
                            if(courier.getValue().equals("New")){
                                myNewFriends.add(courier.getKey().replaceAll("\"",""));
                            }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        databaseReference.child("Customers").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                parentList = new ArrayList<ParentRow>();
                Iterable<DataSnapshot> courierList = dataSnapshot.getChildren();


                for (DataSnapshot courier : courierList) {
                    Customer tempCustomer = courier.getValue(Customer.class);

                    String name = tempCustomer.getFirstName() + " " + tempCustomer.getLastName();
                    String email = tempCustomer.getUserID();
                    email = EncodeString(email);
                    boolean isFriend = myPendingFriends.contains(email) ||
                            myAcceptedFriends.contains(email);
                    String tempStatus;
                    if(myNewFriends.contains(email)){
                        tempStatus = "New Request!";
                    }else{
                        if(!isFriend){
                            continue;
                        }
                        tempStatus = "";
                        if(myPendingFriends.contains(email)){
                            tempStatus = "Pending Request";
                        }else if(myAcceptedFriends.contains(email)){
                            tempStatus = "Valid Recipient";
                        }
                    }
                    email = DecodeString(email);
                    ChildRow childRow = new ChildRow(email,tempStatus,R.drawable.ic_menu_courier_payments);
                    ParentRow parentRow = null;
                    parentRow = new ParentRow(name, childRow);
                    parentList.add(parentRow);
                }
                listView = (ExpandableListView)findViewById(R.id.expandable_list_search_courier);
                listAdapter = new UserSearchExpListAdapter(getBaseContext(), parentList);
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

    public void sendRequest(){
        String emailAddr = requestMail.getText().toString();
        emailAddr = EncodeString(emailAddr);
        if(!emailAddr.contains("@") || emailAddr.equals("")){
            requestMail.setError("Invalid Email Address!");
        }else{
            if(myAcceptedFriends.contains(emailAddr)){
                requestMail.setError("Recipient Already Added!");
            }else{
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = firebaseDatabase.getReference();
                //Map<String,String> map = new HashMap<String, String>() ;
                //map.put(emailAddr,"Pending");
                databaseReference
                        .child("Customers")
                        .child(EncodeString(FirebaseAuth
                                .getInstance()
                                .getCurrentUser()
                                .getEmail())).child("friends").child(emailAddr).setValue("Pending");

                databaseReference
                        .child("Customers")
                        .child(emailAddr).child("friends").child(EncodeString(FirebaseAuth
                        .getInstance()
                        .getCurrentUser()
                        .getEmail())).setValue("New");
                Toast.makeText(this, "Request Sent!",
                        Toast.LENGTH_LONG).show();
            }

        }
    }

    private void expandAll() {
        int count = listAdapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            listView.expandGroup(i);
            //listView.collapseGroup(i);
        }

    }

    @Override
    public boolean onClose() {
        listAdapter.filterData("");
        //expandAll();
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        listAdapter.filterData(query);
        //expandAll();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newQuery) {
        listAdapter.filterData(newQuery);
        //expandAll();

        return false;
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


    public static String EncodeString(String string) {
        return string.replace(".", ",");
    }

    public static String DecodeString(String string) {
        return string.replace(",", ".");
    }
}
