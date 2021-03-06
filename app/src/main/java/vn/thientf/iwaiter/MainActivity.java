package vn.thientf.iwaiter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import vn.thientf.iwaiter.Fragment.FragmentMenu;
import vn.thientf.iwaiter.Fragment.FragmentUser;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final int REQUEST_SCAN = 1111;
    String headerTitle;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    int containerViewId = R.id.main_container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_scan) {
            // Scan table qr code
            // save ->res id, table id ->
            /*new fm menu (id, tb id)
            * replace (fm menu)
            * */

            //save resId & tableId to GlobalData
            //this will change fragment depend on the existing of resId in Database
            startActivityForResult(new Intent(getApplicationContext(),StartActivity.class),REQUEST_SCAN);
       //     checkRestaurantId(GlobalData.getInstance().getCurrRes());
        } else if (id == R.id.nav_menu) {
            FragmentMenu fragmentMenu = new FragmentMenu();
            replaceFragment(fragmentMenu);
            changeTitle("Menu");
        } else if (id == R.id.nav_orders) {

        } else if (id == R.id.nav_history) {

        } else if (id == R.id.nav_info) {
             FragmentUser fragmentUser = new FragmentUser();
             replaceFragment(fragmentUser);
             changeTitle("Info");
        } else if (id == R.id.nav_signout) {
            FirebaseAuth.getInstance().signOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void changeTitle(String menu) {
    }

    void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();

        fragmentManager.beginTransaction()
                .replace(R.id.main_container,fragment)
                .commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
      //  FirebaseUser currentUser=FirebaseAuth.getInstance().getCurrentUser();
     //   if (currentUser==null){
     //       this.startActivity(new Intent(getBaseContext(),LoginActivity.class));
     //       finish();
      //  }
    }

    void checkRestaurantId(final String resId) {
        if (resId == null)
            return;
        DatabaseReference restaurantRef = database.getReference(getString(R.string.RestaurantsRef));
        restaurantRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(resId)) {
                    Toast.makeText(getBaseContext(), "Restaurant not exist!", Toast.LENGTH_LONG).show();
                } else {
                    //Open Menu of this Restaurant
                    getFragmentManager().beginTransaction()
                            .replace(containerViewId, new FragmentMenu())
                            .addToBackStack("menu")
                            .commit();
                    Toast.makeText(getBaseContext(), "Welcome <3", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    //    if (requestCode==REQUEST_SCAN && requestCode==RESULT_OK){
            String qrcode = data.getStringExtra("result");
            Toast.makeText(this, qrcode,
                    Toast.LENGTH_LONG).show();
    //    }
    }
}
