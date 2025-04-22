package com.example.reclaimcse489;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        Window window = getWindow();
        window.setNavigationBarColor(getResources().getColor(R.color.darkgreen));
        Button btnGetStarted = findViewById(R.id.getStartedButton);
        btnGetStarted.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, signin.class);
            startActivity(i);
        });
        
        Intent serviceIntent = new Intent(this, NetworkListenerService.class);
        startService(serviceIntent);

    }


}
