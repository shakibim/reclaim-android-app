package com.example.reclaimcse489;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EditProfile extends AppCompatActivity {
    private EditText editName, editEmail, editPhone, editAddress;
    private Button saveButton, backBtn;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Window window = getWindow();
        window.setNavigationBarColor(getResources().getColor(R.color.darkgreen));
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        editPhone = findViewById(R.id.editPhone);
        editAddress = findViewById(R.id.editAddress);
        saveButton = findViewById(R.id.savebtn);
        backBtn = findViewById(R.id.backbtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {

            db.collection("users").document(user.getUid()).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null && task.getResult().exists()) {
                                String name = task.getResult().getString("username");
                                String email = task.getResult().getString("email");
                                String phone = task.getResult().getString("phone");
                                String address = task.getResult().getString("address");

                                // Autofill the fields
                                editName.setText(name);
                                editEmail.setText(email);
                                editPhone.setText(phone);
                                editAddress.setText(address);
                            } else {
                                Toast.makeText(EditProfile.this, "User data not found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(EditProfile.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        saveButton.setOnClickListener(v -> {
            String name = editName.getText().toString();
            String email = editEmail.getText().toString();
            String phone = editPhone.getText().toString();
            String address = editAddress.getText().toString();

            if (!name.isEmpty() && !email.isEmpty() && !phone.isEmpty() && !address.isEmpty()) {

                if (user != null) {

                    db.collection("users").document(user.getUid())
                            .update("username", name, "email", email, "phone", phone, "address", address)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(EditProfile.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(EditProfile.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            } else {
                Toast.makeText(EditProfile.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
