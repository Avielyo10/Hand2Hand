package com.avielyosef.hand2hand.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.avielyosef.hand2hand.R;
import com.avielyosef.hand2hand.Util.Ad;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class AdsActivity extends AppCompatActivity {

    public static final int GET_FROM_GALLERY = 1;
    public String selection;
    private EditText etTitle;
    private EditText etDescription;
    private EditText etPrice;
    private RadioGroup radioGroup;
    private TextView tvCategory;
    private Button uploadPic;
    private View mProgressView;
    private View mAdsForm;
    private final String adId = randomAdId();
    private Handler handler = new Handler();
    private ProgressBar progressBar;
    private int progressStatus = 0;

    private FirebaseAuth mAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private StorageReference mStorageRef;

    /**
     * onCreate
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ads_activity);
        mAuth = FirebaseAuth.getInstance();

        etTitle = (EditText) findViewById(R.id.ads_title);
        etDescription = (EditText) findViewById(R.id.ads_description);
        etPrice = (EditText)findViewById(R.id.ads_price);
        tvCategory = (TextView) findViewById(R.id.ads_category);
        uploadPic = (Button) findViewById(R.id.ads_upload);

        radioGroup = (RadioGroup)findViewById(R.id.ads_radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb=(RadioButton) findViewById(checkedId);
                selection = rb.getText().toString();
            }
        });
        mProgressView = findViewById(R.id.ads_progress_bar);
        mAdsForm = findViewById(R.id.ads_form);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        uploadPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateFields()){
                    startActivityForResult(new Intent(Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI),
                            GET_FROM_GALLERY);
                }
            }
        });
        Button addAdBtn = (Button) findViewById(R.id.newAdBtn);
        addAdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateFields()){
                    showProgress(true);
                    Toast.makeText(AdsActivity.this, "Ad created successfully!",
                            Toast.LENGTH_SHORT).show();
                    // Write an Ad to the database
                    final FirebaseUser user = mAuth.getCurrentUser();
                    if(user != null){
                        final DatabaseReference userRef = database.getReference("allUsers/"+user.getUid()+"/phoneNum");
                        userRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                DatabaseReference myRef = database.getReference("allAds/"+adId);
                                myRef.setValue(new Ad(etTitle.getText().toString(),
                                        etDescription.getText().toString(),
                                        selection,
                                        Integer.valueOf(etPrice.getText().toString()),
                                        null,
                                        !MainActivity.PAID_USER,
                                        dataSnapshot.getValue().toString(),
                                        user.getEmail(),
                                        adId,
                                        user.getUid(),
                                        user.getDisplayName()));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        startActivity(new Intent(AdsActivity.this, MainActivity.class));
                    }
                }
            }
        });
    }

    /**
     * validate the fields
     * @return if the fields are valid
     */
    private boolean validateFields(){
        etTitle.setError(null);
        etDescription.setError(null);
        etPrice.setError(null);
        tvCategory.setError(null);


        String title = etTitle.getText().toString();
        String description = etDescription.getText().toString();
        String price = etPrice.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid title.
        if(TextUtils.isEmpty(title)){
            etTitle.setError(getString(R.string.error_field_required));
            focusView = etTitle;
            cancel = true;
        }
        // Check for a valid description.
        if(TextUtils.isEmpty(description)){
            etDescription.setError(getString(R.string.error_field_required));
            focusView = etDescription;
            cancel = true;
        }
        // Check for a valid price.
        if(TextUtils.isEmpty(price)){
            etPrice.setError(getString(R.string.error_field_required));
            focusView = etPrice;
            cancel = true;
        }else if(!android.text.TextUtils.isDigitsOnly(price)){
            etPrice.setError("Please use digits only!");
            focusView = etPrice;
            cancel = true;
        }
        if(selection == null){
            tvCategory.setError("Please select one");
            focusView = tvCategory;
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

            mAdsForm.setVisibility(show ? View.GONE : View.VISIBLE);
            mAdsForm.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mAdsForm.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mAdsForm.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Create uuid for Ads to prevent override
     * @return
     */
    public String randomAdId() { return UUID.randomUUID().toString(); }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            mStorageRef = FirebaseStorage.getInstance().getReference(adId+"/ad.jpg");
            //Detects request codes
            if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
                Uri selectedImage = data.getData();
                mStorageRef.putFile(selectedImage)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                new Thread(new Runnable() {
                                    public void run() {
                                        while (progressStatus < 100) {
                                            progressStatus += 1;
                                            // Update the progress bar and display the
                                            //current value in the text view
                                            handler.post(new Runnable() {
                                                public void run() {
                                                    progressBar.setProgress(progressStatus);
                                                    if(progressStatus == 99){
                                                        Toast.makeText(AdsActivity.this,
                                                                "Picture Uploaded Successfully!",
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                            try {
                                                // Sleep for 20 milliseconds.
                                                Thread.sleep(20);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }).start();
                            }})
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {}});
            }
        }
    }
}
