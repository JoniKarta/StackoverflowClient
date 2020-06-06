package com.example.envirometalist;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.envirometalist.fragments.manager.ManagerFragmentMap;
import com.example.envirometalist.fragments.search.SearchFragment;
import com.example.envirometalist.model.User;
import com.google.android.material.navigation.NavigationView;

public class ManagerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView userName, userEmail;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private DrawerLayout drawerLayout;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hum_menu_manager);
        initNavigationView();
        initFragmentManager();
        user =  (User) getIntent().getExtras().get("User");
        setUserName();
        setUserEmail();

    }
    public void setUserName() {
        userName.setText(user.getUsername());
    }

    public void setUserEmail() {
        userEmail.setText(user.getEmail());
    }

    /** Initialize all the navigation components views */
    private void initNavigationView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        View header = navigationView.getHeaderView(0);
        userName = header.findViewById(R.id.userName);
        userEmail = header.findViewById(R.id.userEmail);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        navigationView.setNavigationItemSelectedListener(this);
        actionBarDrawerToggle.syncState();
    }

    /** Initialize fragment manager which handle the selected fragment */
    private void initFragmentManager() {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragmentContainer, new ManagerFragmentMap(user));
        fragmentTransaction.commit();
    }

    /** Set the relevant fragment which the manager selected */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        fragmentTransaction = fragmentManager.beginTransaction();
        if (item.getItemId() == R.id.nav_management) {
            fragmentTransaction.replace(R.id.fragmentContainer, new ManagerFragmentMap(user));
        }
        if (item.getItemId() == R.id.nav_search) {
            fragmentTransaction.replace(R.id.fragmentContainer, new SearchFragment(user));
        }

        if (item.getItemId() == R.id.nav_logout){
            startActivity(new Intent(ManagerActivity.this,LoginActivity.class));
            finish();
        }
        fragmentTransaction.commit();
        return true;
    }

}