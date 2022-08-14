package com.elijah.ukeme.hymn.book;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       Toolbar toolbar = findViewById(R.id.toolbar);
        TextView textView = toolbar.findViewById(R.id.textview_title);
        setSupportActionBar(toolbar);
        textView.setText(toolbar.getTitle());
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        drawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId){
            case R.id.drawer_home:
                Toast.makeText(this, "Home Menu", Toast.LENGTH_SHORT).show();
                break;

            case R.id.drawer_index:
                Intent intoIndex = new Intent(this,indexActivity.class);
                startActivity(intoIndex);
                break;

            case R.id.drawer_order_of_mass:
                Toast.makeText(this, "Order of Mass Menu", Toast.LENGTH_SHORT).show();
                break;

            case R.id.drawer_contact:
                Toast.makeText(this, "Contact Us Menu", Toast.LENGTH_SHORT).show();
                break;

            case R.id.drawer_about:
                aboutUsDialog();
                break;

            case R.id.drawer_rate:
                rateUsDialog();
                break;

            case R.id.drawer_share:
                Toast.makeText(this, "Share App Menu", Toast.LENGTH_SHORT).show();
                break;

            case R.id.drawer_logout:
                logoutDialog();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }

    }

    public void aboutUsDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("CATHOLIC HYMN BOOK");
        alertDialogBuilder.setMessage("This App was developed in Nigeria.\nDeveloper and Designer: Elijah Ukeme\nAddress: Kubwa, Abuja, Nigeria\nE-mail:ukemedmet@gmail.com");
        alertDialogBuilder.setPositiveButton("CONTACT US", (dialogInterface, i) -> {
    });
        alertDialogBuilder.setNegativeButton("CLOSE", (dialogInterface, i) -> {

        });
    AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        }

        public void rateUsDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("CATHOLIC HYMN BOOK");
        builder.setMessage("We hope this App help you in your praises and worship to God, and we kindly ask you to help us;\nPlease rate us on Google Play Store!");
        builder.setPositiveButton("RATE US",(dialog, i) ->
                Toast.makeText(this,"Still working on rating fragment", Toast.LENGTH_SHORT).show());
        builder.setNegativeButton("LATER", (dialogInterface, i) -> {

        });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        public void logoutDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("LOGGING OUT");
        builder.setMessage("Do you really want to log out from Catholic Hymn Book Application?");
        builder.setPositiveButton("YES",(dialog, which) -> {
            finish();
            });
            builder.setNegativeButton("NO", (dialogInterface, i) -> {

                });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

}