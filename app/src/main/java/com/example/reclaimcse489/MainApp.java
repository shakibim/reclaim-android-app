package com.example.reclaimcse489; // Ensure this matches the package name of HomeFragment
import android.view.MenuItem;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainApp extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment = new HomeFragment();
    PostFragment postFragment = new PostFragment();
    ProfileFragment profileFragment = new ProfileFragment();
    HistoryFragment historyFragment = new HistoryFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_app);
        EdgeToEdge.enable(this);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, homeFragment).commit();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem Item) {
                Fragment selectedFragment = null;
                switch (Item.getItemId()) {
                    case R.id.item0:
                        selectedFragment = homeFragment;
                        break;
                    case R.id.item1:
                        selectedFragment = postFragment;
                        break;
                    case R.id.item2:
                        selectedFragment = historyFragment;
                        break;
                    case R.id.item3:
                        selectedFragment = profileFragment;
                        break;
                }
                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, selectedFragment).commit();
                    return true;
                }
                return false;
            }
        });
    }
}