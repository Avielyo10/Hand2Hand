package com.avielyosef.hand2hand.Activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.avielyosef.hand2hand.Util.Ad;
import com.avielyosef.hand2hand.R;
import com.avielyosef.hand2hand.Util.GlideApp;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseListAdapter;
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

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int GET_FROM_GALLERY = 1;
    public static boolean PAID_USER;
    private FirebaseListAdapter<Ad> adapter;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private StorageReference mStorageRef;
    private ListView allAds;

    /**
     * onCreate
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Search Action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        allAds  = (ListView) findViewById(R.id.All_Ads);
        setListenerOnPaidUser();
        setListenerOnAds();
    }

    /**
     * Set listener on the Ads, to get all the Ads shown on our MainActivity
     */
    private void setListenerOnAds(){
        final DatabaseReference mRef = database.getReference("allAds/");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adapter = new FirebaseListAdapter<Ad>(
                        MainActivity.this,
                        Ad.class,
                        R.layout.custom_layout,
                        mRef.orderByChild("notPaid")) {
                    @Override
                    protected void populateView(final View view, Ad myAd, final int position) {
                        final String title = getItem(position).getTitle();
                        final String description = getItem(position).getDescription();
                        int price = getItem(position).getPrice();
                        final String sPrice = String.valueOf(price)+" NIS";
                        final String username = getItem(position).getUsername();
                        Boolean isPaid = getItem(position).isNotPaid();

                        TextView tvTitle =  (TextView)view.findViewById(R.id.customTitle);
                        TextView tvDescription =  (TextView)view.findViewById(R.id.customDescription);
                        TextView tvPrice =  (TextView)view.findViewById(R.id.customPrice);
                        ImageView customStar = (ImageView)view.findViewById(R.id.customStar);
                        ImageView customResize = (ImageView)view.findViewById(R.id.custom_resize);
                        final ImageView customImage = (ImageView)view.findViewById(R.id.customImage);
                        mStorageRef = FirebaseStorage.getInstance().getReference(getItem(position).getAdId()+"/ad.jpg");
                        RequestOptions options = new RequestOptions().error(R.mipmap.ic_launcher_round);
                        GlideApp.with(MainActivity.this)
                                .load(mStorageRef)
                                .diskCacheStrategy(DiskCacheStrategy.NONE )
                                .skipMemoryCache(true).apply(options).into(customImage);

                        tvTitle.setText(title);
                        tvDescription.setText(description);
                        tvPrice.setText(sPrice);
                        if(!isPaid){
                            try{
                                customStar.setVisibility(View.VISIBLE);
                            }catch (Exception e){}
                        }else{
                            try{
                                customStar.setVisibility(View.GONE);
                            }catch (Exception e){}
                        }
                        customResize.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        LayoutInflater factory = LayoutInflater.from(MainActivity.this);
                                        final View mView = factory.inflate(R.layout.resize_layout, null);
                                        AlertDialog.Builder builder;
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                            builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
                                        } else {
                                            builder = new AlertDialog.Builder(MainActivity.this);
                                        }
                                        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {}})
                                                .setView(mView)
                                                .show();

                                        TextView tvTitle1 =  (TextView)mView.findViewById(R.id.resize_title);
                                        TextView tvDescription1 =  (TextView)mView.findViewById(R.id.resize_description);
                                        TextView tvPrice1 =  (TextView)mView.findViewById(R.id.resize_price);
                                        TextView tvUsername = (TextView)mView.findViewById(R.id.resize_username);
                                        ImageView phoneCall = (ImageView)mView.findViewById(R.id.resize_phonecall);
                                        ImageView sendEmail = (ImageView)mView.findViewById(R.id.resize_email);
                                        final ImageView resizeImage = (ImageView)mView.findViewById(R.id.resize_image);
                                        mStorageRef = FirebaseStorage.getInstance().getReference(getItem(position).getAdId()+"/ad.jpg");
                                        RequestOptions options = new RequestOptions().error(R.mipmap.ic_launcher_round);
                                        GlideApp.with(MainActivity.this)
                                                .load(mStorageRef)
                                                .diskCacheStrategy(DiskCacheStrategy.NONE )
                                                .skipMemoryCache(true).apply(options).into(resizeImage);

                                        tvTitle1.setText(title);
                                        tvDescription1.setText(description);
                                        tvPrice1.setText(sPrice);
                                        tvUsername.setText(username);
                                        sendEmail.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                FirebaseUser user = mAuth.getCurrentUser();
                                                if(user != null){
                                                    String userMail = getItem(position).getUserMail();
                                                    if(!userMail.equals(user.getEmail())){
                                                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                                                "mailto",userMail, null));
                                                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Your Ad at Hand2Hand");
                                                        emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi, I'm interested in" +
                                                                " your item, is it still available?");
                                                        startActivity(Intent.createChooser(emailIntent, "Send Email"));
                                                    }else{
                                                        Toast.makeText(MainActivity.this, "You can't send an Email " +
                                                                        "to yourself",
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }
                                        });
//
                                        phoneCall.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent mIntent = new Intent(Intent.ACTION_DIAL);
                                                mIntent.setData(Uri.parse("tel:"+getItem(position).getUserPhoneNum()));
                                                startActivity(mIntent);
                                            }
                                        });
                                    }
                                });
                    }
                };
                allAds.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Set listener to check if the user is paid or not, to update the UI accordingly
     */
    private void setListenerOnPaidUser(){
        final FirebaseUser user = mAuth.getCurrentUser();
        if (user != null){
            DatabaseReference myRef = database.getReference("allUsers/"+user.getUid()+"/paidUser");
            myRef.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    try{
                        PAID_USER = dataSnapshot.getValue(Boolean.class);
                    }catch (Exception e){
                        //At the first init, there is no value to retrieve
                    }
                    Log.i("myChangeOnPaidUser",String.valueOf(PAID_USER));
                    updateUI(user);
                }
                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("setListenerOnUpdate", "Failed to read value.", error.toException());
                }
            });
        }
    }

    /**
     * onStart
     */
    @Override
    public void onStart(){
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        updateUI(user);
    }

    /**
     * Update the UI
     * @param user
     */
    private void updateUI(final FirebaseUser user){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        View headerView = navigationView.getHeaderView(0);
        TextView tvUsername = (TextView) headerView.findViewById(R.id.nav_header_name);
        TextView tvEmail = (TextView) headerView.findViewById(R.id.nav_header_email);
        ImageView premiumStar = (ImageView) findViewById(R.id.nav_header_star);
        final ImageView profilePicture = (ImageView) headerView.findViewById(R.id.profile_pic);
        if (user != null){//user is logged in
            menu.findItem(R.id.nav_login).setVisible(false);
            menu.findItem(R.id.nav_logout).setVisible(true);
            menu.findItem(R.id.nav_ad).setVisible(true);
            menu.findItem(R.id.nav_upgrade).setVisible(true);
            tvUsername.setText("Hello "+ user.getDisplayName() + " !");
            tvEmail.setText(user.getEmail());
            try{
                premiumStar.setVisibility(View.GONE);
            }catch (Exception e){
                Log.e("MainActivityUpdateUI",e.getMessage());
            }
            if(PAID_USER){
                menu.findItem(R.id.nav_upgrade).setVisible(false);
                try{
                    premiumStar.setVisibility(View.VISIBLE);
                }catch (Exception e){
                    Log.e("MainActivityUpdateUI",e.getMessage());
                }
            }
            profilePicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mAuth.getCurrentUser() != null){
                        startActivityForResult(new Intent(Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI),
                                GET_FROM_GALLERY);
                    }
                }
            });
            mStorageRef = FirebaseStorage.getInstance().getReference(user.getUid()+"/profile.jpg");
            GlideApp.with(this)
                    .load(mStorageRef)
                    .diskCacheStrategy(DiskCacheStrategy.NONE )
                    .skipMemoryCache(true).into(profilePicture);
        } else {
            menu.findItem(R.id.nav_login).setVisible(true);
            menu.findItem(R.id.nav_logout).setVisible(false);
            menu.findItem(R.id.nav_ad).setVisible(false);
            menu.findItem(R.id.nav_upgrade).setVisible(false);
            try{
                premiumStar.setVisibility(View.GONE);
            }catch (Exception e){
                Log.e("MainActivityUpdateUI",e.getMessage());
            }
            tvUsername.setText("Hand2Hand");
            tvEmail.setText("Welcome visitor!");
            profilePicture.setImageResource(R.mipmap.ic_launcher_round);
        }
        try{
            adapter.notifyDataSetChanged();
        }catch (Exception e){}
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Handle action bar items.
     * @param item
     * @return true if OK
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        FirebaseUser user = mAuth.getCurrentUser();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            Toast.makeText(MainActivity.this, "Refreshing...",
                    Toast.LENGTH_SHORT).show();
            updateUI(user);
            return true;
        }else if(id == R.id.action_settings){
            if(user != null){
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }else{
                Toast.makeText(MainActivity.this, "You need to login first.",
                        Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Handle navigation view items.
     * @param item
     * @return true if OK
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_login) {
            // Handle the login action
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        } else if (id == R.id.nav_logout) {
            // Handle the logout action
            signOut();
        } else if (id == R.id.nav_ad) {
            // Handle the user Ads action
            startActivity(new Intent(this, AdsActivity.class));
        } else if (id == R.id.nav_upgrade) {
            // Handle the user upgrade action
            startActivity(new Intent(this, UpgradeActivity.class));
        } else if (id == R.id.nav_manage) {
            // Handle the manage Ads action
            FirebaseUser user = mAuth.getCurrentUser();
            if(user!=null){
                startActivity(new Intent(MainActivity.this,MyAdsManagementActivity.class));
            }else{
                Toast.makeText(MainActivity.this, "You need to login first.",
                        Toast.LENGTH_SHORT).show();
            }

        } else if (id == R.id.nav_share) {
            Intent mIntent = new Intent(Intent.ACTION_SEND);
            mIntent.setType("text/plain");
            mIntent.putExtra(Intent.EXTRA_SUBJECT,"Hand2Hand");
            mIntent.putExtra(Intent.EXTRA_TEXT,"Hi, try this new app! https://github.com/Avielyo10/Hand2Hand");
            startActivity(Intent.createChooser(mIntent,"Share using"));

        } else if (id == R.id.nav_info) {
            Toast.makeText(MainActivity.this, "Hand2Hand v1.0",
                    Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * SignOut by click
     */
    private void signOut(){
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null){
            Toast.makeText(MainActivity.this, "Goodbye "+user.getDisplayName()+" !",
                    Toast.LENGTH_SHORT).show();
            mAuth.signOut();
        }
        onStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    final FirebaseUser user = mAuth.getCurrentUser();
    if(user != null){
        mStorageRef = FirebaseStorage.getInstance().getReference(user.getUid()+"/profile.jpg");
        //Detects request codes
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            mStorageRef.putFile(selectedImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) { updateUI(user);}})
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {}});
        }
    }
}
}
