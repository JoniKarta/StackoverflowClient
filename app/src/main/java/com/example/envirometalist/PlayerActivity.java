package com.example.envirometalist;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.envirometalist.fragments.player.PlayerFragmentMap;
import com.example.envirometalist.fragments.search.SearchFragment;
import com.example.envirometalist.model.User;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class PlayerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private TextView userName, userEmail;
    private ImageTakenListener imageTakenListener;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private DrawerLayout drawerLayout;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hum_menu_player);
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
        user =  (User) getIntent().getExtras().get("User");
        setUserName();
        setUserEmail();
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragmentContainer, new PlayerFragmentMap(user));
        fragmentTransaction.commit();

    }

    public void setUserName() {
        userName.setText(user.getUsername());
    }

    public void setUserEmail() {
        userEmail.setText(user.getEmail());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        fragmentTransaction = fragmentManager.beginTransaction();
        if (item.getItemId() == R.id.nav_home) {
            fragmentTransaction.replace(R.id.fragmentContainer, new PlayerFragmentMap(user));
        }
        if (item.getItemId() == R.id.nav_search) {
            fragmentTransaction.replace(R.id.fragmentContainer, new SearchFragment(user));
        }
        if (item.getItemId() == R.id.nav_logout) {
            startActivity(new Intent(PlayerActivity.this, LoginActivity.class));
            finish();
        }
        fragmentTransaction.commit();
        return true;
    }
    Uri imageUri;
    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {

            switch (requestCode) {
                case 0: // From GALLERY
                    imageUri = data.getData();
                    Log.i("gallery","outside glide");
                    Glide.with(this)
                            .asBitmap()
                            .load(imageUri)
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    if (imageUri != null && imageTakenListener != null) {
                                        Log.i("gallery","insideIf");
                                        imageTakenListener.onFinishProcImage(resource);
                                    }
                                    Log.i("gallery","outside if");
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                }
                            });

                    break;
                case 1: // From camera
                    if (imageUri != null && imageTakenListener != null) {
                        Log.i("camera","inside if");
                        imageTakenListener.onFinishProcImage((Bitmap) data.getExtras().get("data"));
                    }
                    Log.i("camera","outside if");
                    break;
            }
        }
    }

    /**
     * This is a callback interface which gets called while image processing from camera has been activated
     */
    public interface ImageTakenListener {
        void onFinishProcImage(Bitmap bitmap);
    }

    /**
     * Register for the ImageTakenListener
     */
    public void setImageTakenListener(ImageTakenListener imageTakenListener) {
        this.imageTakenListener = imageTakenListener;
    }
}