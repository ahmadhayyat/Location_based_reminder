package rs.com.loctionbased.reminder.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import rs.com.loctionbased.reminder.app.activities.HomeActivity;
import rs.com.loctionbased.reminder.databinding.ActivityLoginBinding;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getName();
    private Button loginButton;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private ProgressBar loadingProgressBar;
    FirebaseAuth mAuth;
    String email, password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        usernameEditText = binding.username;
        passwordEditText = binding.password;
        loginButton = binding.login;
        loadingProgressBar = binding.loading;
        enableDisableSignInBtn(false);
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }
        loginButton.setOnClickListener(view -> {
                    loadingProgressBar.setVisibility(View.VISIBLE);
                    enableDisableSignInBtn(false);
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this, task -> {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    loadingProgressBar.setVisibility(View.GONE);
                                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            loadingProgressBar.setVisibility(View.GONE);
                                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                            finish();
                                        } else {
                                            loadingProgressBar.setVisibility(View.GONE);
                                            enableDisableSignInBtn(true);
                                            Log.w(TAG, "createUserWithEmail:failure", task1.getException());
                                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                }
        );


        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                email = usernameEditText.getText().toString().trim();
                password = passwordEditText.getText().toString().trim();
                if (email.contains("@") && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    enableDisableSignInBtn(password.length() >= 6);
                } else {
                    enableDisableSignInBtn(false);
                }

            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);

    }


    void enableDisableSignInBtn(boolean enable) {
        loginButton.setEnabled(enable);
    }
}