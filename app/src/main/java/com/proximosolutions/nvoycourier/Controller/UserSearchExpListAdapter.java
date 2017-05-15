package com.proximosolutions.nvoycourier.Controller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.proximosolutions.nvoycourier.MainLogic.Customer;
import com.proximosolutions.nvoycourier.R;

import java.util.ArrayList;

/**
 * Created by Isuru Tharanga on 5/5/2017.
 */

public class UserSearchExpListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<ParentRow> parentRowList;
    private ArrayList<ParentRow> originalList;


    public UserSearchExpListAdapter(Context context, ArrayList<ParentRow> originalList) {
        this.context = context;
        this.parentRowList = new ArrayList<>();
        this.parentRowList.addAll(originalList);
        this.originalList = new ArrayList<>();
        this.originalList.addAll(originalList);
    }

    @Override
    public int getGroupCount() {
        return parentRowList.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return parentRowList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return parentRowList.get(groupPosition).getChild();
    }

    @Override
    public long getGroupId(int groupID) {
        return groupID;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean b, View convertView, ViewGroup viewGroup) {
        ParentRow parentRow = (ParentRow)getGroup(groupPosition);

        if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater)context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.parent_row,null);
        }

        TextView heading = (TextView)convertView.findViewById(R.id.parent_text);
        ImageView newRequest = (ImageView)convertView.findViewById(R.id.new_request_icon);
        heading.setText(parentRow.getName().trim());
        if(parentRow.getChild().getStatus().equals("New Request!")){
            newRequest.setVisibility(View.VISIBLE);
        }
        if(parentRow.getChild().getStatus().equals("Pending Request")){
            newRequest.setColorFilter(Color.BLUE);
            newRequest.setVisibility(View.VISIBLE);
                    //(ImageView)convertView.findViewById(R.id.new_request_icon);
        }
        if(parentRow.getChild().getStatus().equals("Valid Recipient")){
            newRequest.setVisibility(View.INVISIBLE);
            //(ImageView)convertView.findViewById(R.id.new_request_icon);
        }


        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup viewGroup) {
        ChildRow childRow = (ChildRow)getChild(groupPosition,childPosition);
        System.out.println(groupPosition+" "+childPosition);
        if(convertView==null){
            LayoutInflater layoutInflater = (LayoutInflater)context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.child_row,null);
        }


        ImageView childIcon = (ImageView)convertView.findViewById(R.id.child_icon);
        childIcon.setImageResource(R.drawable.ic_menu_courier_payments);

        final View childText = (View)convertView.findViewById(R.id.child_text_container);
        String text = childRow.getText().trim();
        System.out.println(text);
        ((TextView)convertView.findViewById(R.id.child_text)).setText(text);
        ((TextView)convertView.findViewById(R.id.child_text_state)).setText(childRow.getStatus().trim());
        final View finalConvertView = convertView;

        final View finalConvertView1 = convertView;
        childText.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                        final DatabaseReference databaseReference = firebaseDatabase.getReference();
                        String str = ((TextView)childText.findViewById(R.id.child_text)).getText().toString();

                        databaseReference.child("Customers").child(EncodeString(str.trim())).addListenerForSingleValueEvent(new ValueEventListener(){


                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Intent userProfile = new Intent(finalConvertView.getContext(),UserProfile.class);
                                Customer user = dataSnapshot.getValue(Customer.class);

                                userProfile.putExtra("customer",user);
                                userProfile.putExtra("customerState",((TextView) finalConvertView1.findViewById(R.id.child_text_state)).getText());
                                userProfile.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                //databaseReference.child("Couriers").child(EncodeString(((TextView)childText.findViewById(R.id.child_text)).getText().toString().trim())).removeEventListener(this);
                                context.startActivity(userProfile);




                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }



                        });
                        Log.d("Database","Data Change listener attached to Couriers node");


                    }
                }

        );


        System.out.println(((TextView)convertView.findViewById(R.id.child_text)).getText().toString());
        return convertView;
    }

    public static String EncodeString(String string) {
        return string.replace(".", ",");
    }

    public static String DecodeString(String string) {
        return string.replace(",", ".");
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

    public void filterData(String query){
        query = query.toLowerCase();
        parentRowList.clear();
        if(query.isEmpty() || query.equals("")){
            parentRowList.addAll(originalList);
        }else{
            for(ParentRow parentRow:originalList){
                if(parentRow.getName().toLowerCase().contains(query)){
                    parentRowList.add(parentRow);
                }

            }

        }
        notifyDataSetChanged();

    }
}
