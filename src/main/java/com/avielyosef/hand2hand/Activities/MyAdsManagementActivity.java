package com.avielyosef.hand2hand.Activities;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
    public String selection;
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
                                    LayoutInflater factory = LayoutInflater.from(MyAdsManagementActivity.this);
                                    final View mView = factory.inflate(R.layout.ads_management_layout, null);
                                    AlertDialog.Builder builder;
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        builder = new AlertDialog.Builder(MyAdsManagementActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
                                    } else {
                                        builder = new AlertDialog.Builder(MyAdsManagementActivity.this);
                                    }
                                    builder.setNegativeButton("Update Ad", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            final EditText etTitle = (EditText) mView.findViewById(R.id.ads_man_title);
                                            final EditText etDescription = (EditText)mView.findViewById(R.id.ads_man_description);
                                            final EditText etPrice = (EditText) mView.findViewById(R.id.ads_man_price);
                                            RadioGroup mRadioGroup = (RadioGroup) mView.findViewById(R.id.ads_man_radio_group);
                                            mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                                @Override
                                                public void onCheckedChanged(RadioGroup group, int checkedId) {
                                                    RadioButton rb = (RadioButton) mView.findViewById(checkedId);
                                                    selection = rb.getText().toString();
                                                }
                                            });
                                            if(validateFields(etPrice)){
                                                String adId = getItem(position).getAdId();
                                                Toast.makeText(MyAdsManagementActivity.this, "Updating Ad..",
                                                        Toast.LENGTH_SHORT).show();
                                                saveTheRightFields(adId,
                                                        etTitle.getText().toString(),
                                                        etDescription.getText().toString(),
                                                        etPrice.getText().toString(),selection);
                                            }
                                        }})
                                            .setView(mView)
                                            .show();
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
    /**
     * validate the fields
     * @return if the fields are valid
     */
    private boolean validateFields(EditText etPrice){
        etPrice.setError(null);

        String price = etPrice.getText().toString();
        boolean cancel = false;
        View focusView = null;

        // Check for a valid price.
        if(!android.text.TextUtils.isDigitsOnly(price)){
            etPrice.setError("Please use digits only!");
            focusView = etPrice;
            cancel = true;
        }

        if (cancel) {
            // There was an error; focus the first
            // form field with an error.
            focusView.requestFocus();
        }
        return !cancel;
    }

    private void saveTheRightFields(String adId, String title,
                                    String description, String price,
                                    String selection){
        final DatabaseReference mRef = database.getReference("allAds/"+adId);

        if(!TextUtils.isEmpty(title)) mRef.child("title").setValue(title);

        if(!TextUtils.isEmpty(description)) mRef.child("description").setValue(description);

        if(!TextUtils.isEmpty(price)) mRef.child("price").setValue(Integer.valueOf(price));

        if(!TextUtils.isEmpty(selection)) mRef.child("category").setValue(selection);

    }
}
