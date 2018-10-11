package com.example.lenovo.engineer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;


public class main_menu extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainMenu";
    private BottomNavigationView bottomBar;
    private FloatingActionButton fab;
    private GoogleSignInOptions gso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(main_menu.this, FavSchedule.class);
                startActivity(intent);
            }
        });


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // Removing this will cause the navigation view to not respond to click events
        navigationView.bringToFront();

        View header = navigationView.getHeaderView(0);
        GoogleSignInAccount account = GoogleSignInHelper.getInstance(getApplicationContext(), gso).getAccount();

        View navView;
        navView = header.findViewById(R.id.name);
        ((TextView) navView).setText(account.getDisplayName());
        navView = header.findViewById(R.id.email);
        ((TextView) navView).setText(account.getEmail());
        navView = header.findViewById(R.id.profilePic);
        Uri imguri = account.getPhotoUrl();

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.mipmap.ic_logo_round)
                .error(R.mipmap.ic_logo_round);
        Glide.with(this).load(imguri).apply(options).into(((ImageView) navView));

        bottomBar = findViewById(R.id.main_menu_bnv_bottom_bar);
        bottomBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.bottom_bar_home:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.main_menu_fl_container, new HomeFragment(), "Home")
                                .addToBackStack("Home")
                                .commit();
                        fab.show();
                        break;
                    case R.id.bottom_bar_schedule:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.main_menu_fl_container, new ScheduleFragment(), "Schedule")
                                .addToBackStack("Schedule")
                                .commit();
                        fab.show();
                        break;
                    case R.id.bottom_bar_maps:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.main_menu_fl_container, new MapsFragment(), "Maps")
                                .addToBackStack("Maps")
                                .commit();
                        fab.hide();
                        break;
                }
                return true;
            }
        });
        // Set initial/default fragment
        bottomBar.setSelectedItemId(R.id.bottom_bar_home);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        if(getSupportFragmentManager().getFragments().size()==0
                || getSupportFragmentManager().getFragments().size()==1){
            finish();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Log.d(TAG, "Menu item clicked.");
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        if (id == R.id.Home) {
            bottomBar.setSelectedItemId(R.id.bottom_bar_home);
            fab.show();
        } else if (id == R.id.nav_schedule) {
            bottomBar.setSelectedItemId(R.id.bottom_bar_schedule);
            fab.show();
        } else if (id == R.id.nav_maps) {
            bottomBar.setSelectedItemId(R.id.bottom_bar_maps);
            fab.hide();
        } else if (id == R.id.nav_about) {
            fragment = new AboutFragment();
            fab.hide();
        } else if (id == R.id.committee_head) {
            fragment=new CommitteeHeadsFragment();
            fab.hide();
        } else if (id == R.id.developers) {
            fragment=new DevelopersFragment();
            fab.hide();
        } else if (id == R.id.sponsors) {
            fragment=new SponsorsFragment();
            fab.hide();
        } else if (id == R.id.help){
            fragment = new HelpFragment();
            fab.hide();
        } else if (id == R.id.logout) {
            GoogleSignInHelper.getInstance(this, gso).getClient().signOut();
            Log.d(TAG, "Logout Successful");
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.main_menu_fl_container, fragment);
            ft.addToBackStack("Fragment");
            ft.commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
