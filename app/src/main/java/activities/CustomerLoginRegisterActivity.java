package activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.elijah.ukeme.onlinecampospos.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import interfaces.showAbleMessage;

public class CustomerLoginRegisterActivity extends AppCompatActivity implements showAbleMessage {

    private TextView pageTitle,notRegistered;
    private Button loginButton;
    private EditText emailEditText,passwordEditText;
    boolean cancel = false;
    private String request = "login";
    private int count=0;
    private ProgressDialog loadingDialog;
    private FirebaseAuth mAuth;
    private DatabaseReference customerDatabaseRef;
    private String onlineCustomerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_customer_login_register);
        pageTitle = findViewById(R.id.seller_title_login);
        notRegistered = findViewById(R.id.seller_not_registered_textview);
        loginButton = findViewById(R.id.seller_login_button);
        emailEditText = findViewById(R.id.seller_login_email);
        passwordEditText = findViewById(R.id.seller_login_password);
        loadingDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();


        pageTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count ++;
                if (count>=5){
                    Intent intent = new Intent(CustomerLoginRegisterActivity.this,VendorLoginRegisterActivity.class);
                    startActivity(intent);
                }
            }
        });

        notRegistered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                request = "register";
                pageTitle.setText("Customer Registration Form");
                loginButton.setText("Register");
                notRegistered.setVisibility(View.GONE);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (request.equalsIgnoreCase("register")){
                    registerUser();
                }else {
                    login();
                }

            }
        });
    }

    private void registerUser() {

        String email, password;
        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();

        if (email.isEmpty()) {
            emailEditText.setError("Please Enter your Email Address");
            cancel = true;
            emailEditText.requestFocus();
        } else if (password.isEmpty()) {
            passwordEditText.setError("Please Enter your Password");
            cancel = true;
            passwordEditText.requestFocus();
        } else {
            //go ahead and register user
            loadingDialog.setTitle("Registration Processing...");
            loadingDialog.setMessage("Please wait while we are checking your credentials");
            loadingDialog.setCanceledOnTouchOutside(false);
            loadingDialog.show();
            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){

                                onlineCustomerId = mAuth.getCurrentUser().getUid();
                                customerDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users")
                                        .child("Customers").child(onlineCustomerId);
                                customerDatabaseRef.setValue(true);
                                loadingDialog.dismiss();
                                Toast.makeText(CustomerLoginRegisterActivity.this,"Customer Registered Successfully",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(CustomerLoginRegisterActivity.this,SelectActivity.class);
                                startActivity(intent);
                            }else {
                                loadingDialog.dismiss();
                                Toast.makeText(CustomerLoginRegisterActivity.this,"Error Occurred, Please Try Again",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void login() {
        String email,password;
        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();

        if (email.isEmpty()){
            emailEditText.setError("Please Enter your Email Address");
            cancel = true;
            emailEditText.requestFocus();
        }else if (password.isEmpty()){
            passwordEditText.setError("Please Enter your Password");
            cancel = true;
            passwordEditText.requestFocus();
        }else {
            loadingDialog.setTitle("Login Processing...");
            loadingDialog.setMessage("Please wait while we are checking your credentials");
            loadingDialog.setCanceledOnTouchOutside(false);
            loadingDialog.show();
            mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                loadingDialog.dismiss();
                                Toast.makeText(CustomerLoginRegisterActivity.this,"Login Successful",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(CustomerLoginRegisterActivity.this,SelectActivity.class);
                                startActivity(intent);

                            }else {
                                loadingDialog.dismiss();
                                showMessage("Error","Incorrect Credentials");
                            }
                        }
                    });

        }
    }

    @Override
    public void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
}