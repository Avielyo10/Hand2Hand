package com.avielyosef.hand2hand.Activities;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.avielyosef.hand2hand.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class UpgradeActivity extends AppCompatActivity {
    private View upgradeView;
    private View mProgressView;
    private RadioGroup radioGroup;
    private TextView setErrorForRadioGroup;
    private int selection;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    /**
     * onCreate
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade);
        mAuth = FirebaseAuth.getInstance();

        setErrorForRadioGroup = (TextView) findViewById(R.id.setErrorForRadioGroup);

        radioGroup = (RadioGroup) findViewById(R.id.upgrade_radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                selection = group.getCheckedRadioButtonId();
            }
        });

        upgradeView = findViewById(R.id.upgrade_form);
        mProgressView = findViewById(R.id.upgrade_progress);

        final Button upgradeBtn = (Button) findViewById(R.id.upgrade_button);
        upgradeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields()) {
                    showProgress(true);
                    Toast.makeText(UpgradeActivity.this, "Upgrading...",
                            Toast.LENGTH_SHORT).show();
                    updateUserData(mAuth.getCurrentUser());
                    updateAllAds(mAuth.getCurrentUser());
                    startActivity(new Intent(UpgradeActivity.this, MainActivity.class));
                }
            }
        });
    }

    /**
     * validate the fields
     * @return if the fields are valid
     */
    private boolean validateFields() {
        setErrorForRadioGroup.setError(null);

        boolean cancel = false;
        View focusView = null;

        // Check for a valid selection.
        if (selection <= 0) {
            setErrorForRadioGroup.setError("Please select one");
            focusView = setErrorForRadioGroup;
            cancel = true;
        }

        if (cancel) {
            // There was an error; focus the first
            // form field with an error.
            focusView.requestFocus();
        }
        return !cancel;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            upgradeView.setVisibility(show ? View.GONE : View.VISIBLE);
            upgradeView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    upgradeView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            upgradeView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * update the user data from regular user to premium one
     * @param user
     */
    private void updateUserData(FirebaseUser user) {
        if (user != null) {
            final DatabaseReference myRef = database.getReference("allUsers/"+user.getUid());
            RadioButton rb = (RadioButton)findViewById(selection);
            int numOfDays;
            if(rb.getText().toString().contains("15")){
                numOfDays = 15;
            } else if(rb.getText().toString().contains("30")){
                numOfDays = 30;
            } else if(rb.getText().toString().contains("45")){
                numOfDays = 45;
            }else{
                numOfDays = 0;
            }
            myRef.child("paidUser").setValue(true);
            myRef.child("numOfDays").setValue(numOfDays);
            myRef.child("lastDay").setValue(getTheLastPaidDay(numOfDays));
        }
    }

    /**
     * update the Ads for this user to premium Ads
     * @param user
     */
    private void updateAllAds(FirebaseUser user) {
        if (user != null) {
            DatabaseReference myRef = database.getReference("allAds/");
            myRef.addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot ad : dataSnapshot.getChildren()) {
                                DatabaseReference newRef = database.getReference("allAds/"+ad.getKey());
                                newRef.child("paid").setValue(true);
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError error) {
                            //handle databaseError
                    }
            });
        }
    }

    /**
     * calculate the date the premium period ends
     * @param numOfDays
     * @return
     */
    private String getTheLastPaidDay(int numOfDays){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, numOfDays);
        return sdf.format(cal.getTime());
    }
}