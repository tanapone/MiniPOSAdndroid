package minipos.miniproject.com.miniposadndroid;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import minipos.miniproject.com.miniposadndroid.Fragment.CashierFragment;
import minipos.miniproject.com.miniposadndroid.Models.UserModel;

public class MainActivity extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawer;
    private UserModel userModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        String userJsonObject = getIntent().getStringExtra("userJsonObject");
        userModel = new UserModel(userJsonObject);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new CashierFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_view);
        }
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else{
            return;
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.cashierMenu:
                getSupportFragmentManager().beginTransaction().
                        replace(R.id.fragment_container, new CashierFragment()).commit();
//                Toast.makeText(MainActivity.this,"Cashier",Toast.LENGTH_SHORT).show();
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.logoutMenu:
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                Toast.makeText(MainActivity.this,"ออกจากระบบสำเร็จ",Toast.LENGTH_SHORT).show();
                startActivity(intent);
        }
        return true;
    }

    public UserModel.User getUser(){
        return this.userModel.getUser();
    }
}
