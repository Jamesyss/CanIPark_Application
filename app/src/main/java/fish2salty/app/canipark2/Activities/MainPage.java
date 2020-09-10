package fish2salty.app.canipark2.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import fish2salty.app.canipark2.NavigationMenu.Bookmark.BookmarkFragment;
import fish2salty.app.canipark2.NavigationMenu.History.HistoryFragment;
import fish2salty.app.canipark2.NavigationMenu.Home.HomeFragment;
import fish2salty.app.canipark2.NavigationMenu.Noti.NotificationFragment;
import fish2salty.app.canipark2.NavigationMenu.Promotion.PromotionFragment;
import fish2salty.app.canipark2.NavigationMenu.Review.ReviewFragment;
import fish2salty.app.canipark2.NavigationMenu.Setting.SettingFragment;
import fish2salty.app.canipark2.R;

public class MainPage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    FirebaseAuth mAuth;
    FirebaseUser currentUser ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle(null);
        //getSupportActionBar().hide();

        // ini

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        getSupportFragmentManager().beginTransaction().replace(R.id.container,new HomeFragment()).commit();

        updateNavHeader();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.container,//แก้ตรงR.id.nav_homeแก้ตัวหลัง.idก็พอ
                        new HomeFragment()).commit();
                break;
            case R.id.nav_noti:
                getSupportFragmentManager().beginTransaction().replace(R.id.container,//แก้ตรงR.id.nav_notiแก้ตัวหลัง.idก็พอ
                        new NotificationFragment()).commit();
                break;
            case R.id.nav_spdeal:
                getSupportFragmentManager().beginTransaction().replace(R.id.container,//แก้ตรงR.id.nav_notiแก้ตัวหลัง.idก็พอ
                        new PromotionFragment()).commit();
                break;
            case R.id.nav_bookmark:
                getSupportFragmentManager().beginTransaction().replace(R.id.container,//แก้ตรงR.id.nav_ินนาทฟพาแก้ตัวหลัง.idก็พอ
                        new BookmarkFragment()).commit();
                break;
            case R.id.nav_review:
                getSupportFragmentManager().beginTransaction().replace(R.id.container,//แก้ตรงR.id.containerแก้ตัวหลัง.idก็พอ
                        new ReviewFragment()).commit();
                break;
            case R.id.nav_history:
                getSupportFragmentManager().beginTransaction().replace(R.id.container,//แก้ตรงR.id.containerแก้ตัวหลัง.idก็พอ
                        new HistoryFragment()).commit();
                break;
            case R.id.nav_setting:
                getSupportFragmentManager().beginTransaction().replace(R.id.container,//แก้ตรงR.id.containerแก้ตัวหลัง.idก็พอ
                        new SettingFragment()).commit();
                break;
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                Intent loginActivity = new Intent(getApplicationContext(),Login.class);
                startActivity(loginActivity);
                finish();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void updateNavHeader() {

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.nav_username);
        TextView navUserMail = headerView.findViewById(R.id.nav_user_mail);
        ImageView navUserPhot = headerView.findViewById(R.id.nav_user_photo);

        navUserMail.setText(currentUser.getEmail());
        navUsername.setText(currentUser.getDisplayName());

        // now we will use Glide to load user image
        // first we need to import the library

        Glide.with(this).load(currentUser.getPhotoUrl()).into(navUserPhot);

    }
}
