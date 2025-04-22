package com.example.reclaimcse489;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class signin extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText emailEditText, passwordEditText;
    private CheckBox rememberMeCheckBox, rememberPasswordCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        Window window = getWindow();
        window.setNavigationBarColor(getResources().getColor(R.color.darkgreen));
        mAuth = FirebaseAuth.getInstance();
        emailEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        rememberMeCheckBox = findViewById(R.id.rememberMeCheckBox);
        rememberPasswordCheckBox = findViewById(R.id.rememberPasswordCheckBox);
        Button signupbtn = findViewById(R.id.signup);
        Button loginbtn = findViewById(R.id.login);
        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(signin.this, Signup.class);
                startActivity(i);
            }
        });

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // Check if email and password are entered
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(signin.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(signin.this, task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(signin.this, "Authentication Successful", Toast.LENGTH_SHORT).show();

                                // Save credentials if "Remember Me" and/or "Remember Password" are checked
                                SharedPreferences preferences = getSharedPreferences("userPrefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();

                                if (rememberMeCheckBox.isChecked()) {
                                    editor.putBoolean("rememberMe", true);
                                    editor.putString("email", email);
                                } else {
                                    editor.remove("rememberMe");
                                    editor.remove("email");
                                }

                                if (rememberPasswordCheckBox.isChecked()) {
                                    editor.putBoolean("rememberPassword", true);
                                    editor.putString("password", password);
                                } else {
                                    editor.remove("rememberPassword");
                                    editor.remove("password");
                                }

                                editor.apply(); // Save preferences

                                // Proceed to MainApp activity
                                Intent i = new Intent(signin.this, MainApp.class);
                                i.putExtra("isLoggedIn", true); // Pass logged-in flag
                                startActivity(i);
                                finish(); // Close signin activity so the user can't go back
                            } else {
                                // If sign-in fails, display a message to the user
                                Toast.makeText(signin.this, "Authentication Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        SharedPreferences preferences = getSharedPreferences("userPrefs", MODE_PRIVATE);
        if (preferences.getBoolean("rememberMe", false)) {
            emailEditText.setText(preferences.getString("email", ""));
            if (preferences.getBoolean("rememberPassword", false)) {
                passwordEditText.setText(preferences.getString("password", ""));
            }
            rememberMeCheckBox.setChecked(true);
            rememberPasswordCheckBox.setChecked(true);
        }
    }
}
