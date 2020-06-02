package com.example.envirometalist;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.envirometalist.fragments.manager.home.HomeFragment;
import com.example.envirometalist.fragments.manager.management.ElementManagementFragment;
import com.example.envirometalist.fragments.player.map.PlayerFragmentMap;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class PlayerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        navigationView.setNavigationItemSelectedListener(this);
        actionBarDrawerToggle.syncState();

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragmentContainer, new HomeFragment());
        fragmentTransaction.commit();

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        fragmentTransaction = fragmentManager.beginTransaction();
        if (item.getItemId() == R.id.nav_home) {
            fragmentTransaction.replace(R.id.fragmentContainer, new HomeFragment());
        }
        if (item.getItemId() == R.id.nav_search) {
            fragmentTransaction.replace(R.id.fragmentContainer, new PlayerFragmentMap());
        }
        if (item.getItemId() == R.id.nav_logout){
            Toast.makeText(this, "test", Toast.LENGTH_SHORT).show();
            finish();
        }
        fragmentTransaction.commit();
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            switch (requestCode) {
                case 0:
                    /*imageUri = data.getData();
                    Picasso.get().load(imageUri).into(pictureFromCamera);

                    Glide.with(this)
                            .asBitmap()
                            .load(imageUri)
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    if (imageUri != null) {
                                        bitmap = resource;
                                        uploadAndSendPhoto();
                                    }
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                }
                            });*/

                    break;
                case 1:
       /*             bitmap  = (Bitmap) data.getExtras().get("data");
                    pictureFromCamera.setImageBitmap(bitmap);
                    uploadAndSendPhoto();*/
                    break;
            }
        }
    }
}