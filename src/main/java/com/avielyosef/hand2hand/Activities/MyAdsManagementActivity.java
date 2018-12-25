package com.avielyosef.hand2hand.Activities;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.avielyosef.hand2hand.R;
import com.avielyosef.hand2hand.Util.Ad;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyAdsManagementActivity extends AppCompatActivity {

    private FirebaseListAdapter<Ad> adapter;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private ListView myAds;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_myads);

        mAuth = FirebaseAuth.getInstance();

        setListenerOnMyAds();
        myAds = (ListView) findViewById(R.id.my_Ads_lv);
    }

    private void setListenerOnMyAds(){
        final DatabaseReference mRef = database.getReference("allAds/");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                final FirebaseUser user = mAuth.getCurrentUser();
                if(user != null){
                    adapter = new FirebaseListAdapter<Ad>(
                            MyAdsManagementActivity.this,
                            Ad.class,
                            R.layout.custom_myads,
                            mRef.orderByChild("uid").equalTo(user.getUid())) {
                        @Override
                        protected void populateView(View v, final Ad model, final int position) {
                            TextView title = (TextView)v.findViewById(R.id.myAdsTitle);
                            TextView description = (TextView)v.findViewById(R.id.myAdsDescription);
                            TextView price = (TextView)v.findViewById(R.id.myAdsPrice);
                            ImageView myAdsEdit = (ImageView)v.findViewById(R.id.myAds_edit);
                            ImageView myAdsTrash = (ImageView)v.findViewById(R.id.myAds_trash);

                            title.setText(getItem(position).getTitle());
                            description.setText(getItem(position).getDescription());
                            price.setText(String.valueOf(getItem(position).getPrice()));

                            myAdsEdit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //TODO start conversation window
                                }
                            });
                            myAdsTrash.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AlertDialog.Builder builder;
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        builder = new AlertDialog.Builder(MyAdsManagementActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
                                    } else {
                                        builder = new AlertDialog.Builder(MyAdsManagementActivity.this);
                                    }
                                    builder.setTitle("Delete Ad")
                                            .setMessage("Are you sure you want to delete this Ad?")
                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // continue with delete
                                                    String adPosition = getItem(position).getAdId();
                                                    dataSnapshot.child(adPosition).getRef().setValue(null);
                                                    Toast.makeText(MyAdsManagementActivity.this, "Removing Ad..",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // do nothing
                                                }
                                            })
                                            .setIcon(R.drawable.baseline_warning_black_18dp)
                                            .show();
                                }
                            });

                        }
                    };
                    myAds.setAdapter(adapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
