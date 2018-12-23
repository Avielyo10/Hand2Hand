package com.avielyosef.hand2hand.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.avielyosef.hand2hand.Util.Ad;
import com.avielyosef.hand2hand.Util.CustomAdapter;
import com.avielyosef.hand2hand.DB.MyDB;
import com.avielyosef.hand2hand.R;
import com.avielyosef.hand2hand.Util.PaidUser;
import com.avielyosef.hand2hand.Util.RegUser;
import com.avielyosef.hand2hand.Util.User;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static boolean PAID_USER;
    private FirebaseListAdapter<Ad> adapter;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private ListView allAds;

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

    private void setListenerOnAds(){
        final DatabaseReference mRef = database.getReference("allAds/");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ad : dataSnapshot.getChildren()){
                    adapter = new FirebaseListAdapter<Ad>(
                            MainActivity.this,
                            Ad.class,
                            R.layout.custom_layout,
                            mRef) {//TODO sort!
                        @Override
                        protected void populateView(View view, Ad myAd, int position) {
                               String title = getItem(position).getTitle();
                               String description = getItem(position).getDescription();
                               int price = getItem(position).getPrice();
                               String sPrice = String.valueOf(price)+" NIS";
                               Boolean isPaid = getItem(position).isPaid();

                                TextView tvTitle =  (TextView)view.findViewById(R.id.customTitle);
                                TextView tvDescription =  (TextView)view.findViewById(R.id.customDescription);
                                TextView tvPrice =  (TextView)view.findViewById(R.id.customPrice);
                                ImageView customStar = (ImageView)view.findViewById(R.id.customStar);
                                ImageView customPhone = (ImageView)view.findViewById(R.id.customCall);
                                ImageView customMail = (ImageView)view.findViewById(R.id.customMail);
                                ImageView customResize = (ImageView)view.findViewById(R.id.customSeeAll);

                                tvTitle.setText(title);
                                tvDescription.setText(description);
                                tvPrice.setText(sPrice);

                                if(isPaid){
                                    try{
                                        customStar.setVisibility(View.VISIBLE);
                                    }catch (Exception e){}
                                }else{
                                    try{
                                        customStar.setVisibility(View.GONE);
                                    }catch (Exception e){}
                                }
                                customMail.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Toast.makeText(MainActivity.this, "Email - not available",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                                customPhone.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Toast.makeText(MainActivity.this, "Phone - not available",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                                customResize.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Toast.makeText(MainActivity.this, "Resize - not available",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        };
                    }
                allAds.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

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

    @Override
    public void onStart(){
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        updateUI(user);
    }

    private void updateUI(FirebaseUser user){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        View headerView = navigationView.getHeaderView(0);
        TextView tvUsername = (TextView) headerView.findViewById(R.id.nav_header_name);
        TextView tvEmail = (TextView) headerView.findViewById(R.id.nav_header_email);
        ImageView premuimStar = (ImageView) findViewById(R.id.nav_header_star);

        if (user != null){//user is logged in
            menu.findItem(R.id.nav_login).setVisible(false);
            menu.findItem(R.id.nav_logout).setVisible(true);
            menu.findItem(R.id.nav_ad).setVisible(true);
            menu.findItem(R.id.nav_upgrade).setVisible(true);
            tvUsername.setText("Hello "+ user.getDisplayName() + " !");
            tvEmail.setText(user.getEmail());
            try{
                premuimStar.setVisibility(View.GONE);
            }catch (Exception e){
                Log.e("MainActivityUpdateUI",e.getMessage());
            }
            if(PAID_USER){
                menu.findItem(R.id.nav_upgrade).setVisible(false);
                try{
                    premuimStar.setVisibility(View.VISIBLE);
                }catch (Exception e){
                    Log.e("MainActivityUpdateUI",e.getMessage());
                }
            }
        } else {
            menu.findItem(R.id.nav_login).setVisible(true);
            menu.findItem(R.id.nav_logout).setVisible(false);
            menu.findItem(R.id.nav_ad).setVisible(false);
            menu.findItem(R.id.nav_upgrade).setVisible(false);
            try{
                premuimStar.setVisibility(View.GONE);
            }catch (Exception e){
                Log.e("MainActivityUpdateUI",e.getMessage());
            }
            tvUsername.setText("Hand2Hand");
            tvEmail.setText("Welcome visitor!");
        }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            Toast.makeText(MainActivity.this, "Refreshing...",
                    Toast.LENGTH_SHORT).show();
            FirebaseUser user = mAuth.getCurrentUser();
            updateUI(user);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_login) {
            // Handle the login action
            Intent mIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(mIntent);

        } else if (id == R.id.nav_logout) {
            // Handle the logout action
            signOut();
        } else if (id == R.id.nav_ad) {
            // Handle the user Ads action
            Intent mIntent = new Intent(this, AdsActivity.class);
            startActivity(mIntent);
        } else if (id == R.id.nav_upgrade) {
            //show pop-up
            Intent mIntent = new Intent(this, upgradeActivity.class);
            startActivity(mIntent);
        } else if (id == R.id.nav_manage) {
            // Handle the manage Ads action
            FirebaseUser user = mAuth.getCurrentUser();
            if(user!=null){
                //TODO manage ads
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

    private void signOut(){
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null){
            Toast.makeText(MainActivity.this, "Goodbye "+user.getDisplayName()+" !",
                    Toast.LENGTH_SHORT).show();
            mAuth.signOut();
        }
        onStart();
    }
 }
