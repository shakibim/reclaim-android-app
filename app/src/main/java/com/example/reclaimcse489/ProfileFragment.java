package com.example.reclaimcse489;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ProfileFragment extends Fragment {

    private TextView profileName, profileEmail, profilePhone, profileAddress;
    private Button logout, edit;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        profileName = rootView.findViewById(R.id.profileName);
        profileEmail = rootView.findViewById(R.id.profileEmail);
        profilePhone = rootView.findViewById(R.id.profilePhone);
        profileAddress = rootView.findViewById(R.id.profileAddress);
        logout = rootView.findViewById(R.id.logout);
        edit =rootView.findViewById(R.id.editProfileButton);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null && isAdded()) {
                    Intent i = new Intent(getActivity(), EditProfile.class);
                    startActivity(i);
                } else {
                    Toast.makeText(getContext(), "Activity is not available", Toast.LENGTH_SHORT).show();
                }
            }
        });
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {

            DocumentReference docRef = db.collection("users").document(user.getUid());
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    if (task.getResult() != null && task.getResult().exists()) {
                        String name = task.getResult().getString("username");
                        String email = task.getResult().getString("email");
                        String phone = task.getResult().getString("phone");
                        String address = task.getResult().getString("address");
                        profileName.setText(name);
                        profileEmail.setText(email);
                        profilePhone.setText(phone);
                        profileAddress.setText(address);
                    } else {
                        Toast.makeText(getContext(), "User data not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to load user data", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
        }
        logout.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(getContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), signin.class);
            startActivity(intent);
            getActivity().finish();
        });

        return rootView;
    }
}
