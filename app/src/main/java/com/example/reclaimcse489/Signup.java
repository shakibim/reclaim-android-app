package com.example.reclaimcse489;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Signup extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText usernameEditText, emailEditText, phoneEditText, addressEditText, passwordEditText;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Window window = getWindow();
        window.setNavigationBarColor(getResources().getColor(R.color.darkgreen));
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize the views
        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        addressEditText = findViewById(R.id.addressEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        registerButton = findViewById(R.id.registerButton1);
        registerButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String phone = phoneEditText.getText().toString().trim();
            String address = addressEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            if (email.isEmpty() || phone.isEmpty() || address.isEmpty() || password.isEmpty() || username.isEmpty()) {
                Toast.makeText(Signup.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            } else {
                // Call method to create an account with Firebase
                createAccount(email, password, username, phone, address);
            }
        });
        Button btnSignin = findViewById(R.id.signinbtn);
        btnSignin.setOnClickListener(v -> {
            Intent i = new Intent(Signup.this, signin.class);
            startActivity(i);
        });
    }
    private void createAccount(String email, String password, String username, String phone, String address) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        saveUserDetails(user.getUid(), username, email, phone, address);
                        Toast.makeText(Signup.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Signup.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void saveUserDetails(String userId, String username, String email, String phone, String address) {
        if (username == null || username.isEmpty() ||
                email == null || email.isEmpty() ||
                phone == null || phone.isEmpty() ||
                address == null || address.isEmpty()) {
            Toast.makeText(Signup.this, "Please fill all the fields correctly.", Toast.LENGTH_SHORT).show();
            return;
        }
        User user = new User(username, email, phone, address);
        db.collection("users").document(userId)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(Signup.this, "User details saved successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Signup.this, "Error saving user details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
