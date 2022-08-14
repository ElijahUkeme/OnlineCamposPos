package activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.elijah.ukeme.onlinecampospos.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import org.w3c.dom.Text;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private ImageView saveButton,closeButton;
    private CircleImageView profileImage;
    private TextView imagePicker;
    private EditText username,phoneNumber;
    private String getType,checker="";
    private Uri imageUri;
    private String myUri = "";
    private StorageTask uploadTask;
    private StorageReference myProfileImageStorageRef;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
     boolean cancel = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_settings);
        saveButton = findViewById(R.id.save_button);
        closeButton = findViewById(R.id.close_button);
        profileImage = findViewById(R.id.profile_image);
        imagePicker = findViewById(R.id.textview_profile_image_picker);
        username = findViewById(R.id.editText_name);
        phoneNumber = findViewById(R.id.phone_number_settings);
        getType = getIntent().getStringExtra("type");
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(getType);
        myProfileImageStorageRef = FirebaseStorage.getInstance().getReference().child("Profile Image");

        displayExistingUserInformation();

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getType.equalsIgnoreCase("Customers")){
                    Intent intent = new Intent(SettingsActivity.this,CustomerMapsActivity.class);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(SettingsActivity.this,VendorMapsActivity.class);
                    startActivity(intent);
                }
            }
        });
        imagePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checker = "clicked";
                CropImage.activity()
                        .setAspectRatio(1, 1)
                        .start(SettingsActivity.this);
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checker.equalsIgnoreCase("clicked")){
                    validateInputs();
                }else {
                    updateOnlyData();
                }
            }
        });
    }

    private void updateOnlyData() {
        if (username.getText().toString().isEmpty()){
            username.setError("Please Enter Your Name");
            cancel = true;
            username.requestFocus();
        }else if (phoneNumber.getText().toString().isEmpty()) {
            phoneNumber.setError("Please Enter Your Phone Number");
            cancel = true;
            phoneNumber.requestFocus();
        }else {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Updating Profile");
            progressDialog.setMessage("Please wait while we are updating your profile information");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            HashMap<String,Object> userMap = new HashMap<>();
            userMap.put("uid",mAuth.getCurrentUser().getUid());
            userMap.put("userName",username.getText().toString());
            userMap.put("phoneNumber",phoneNumber.getText().toString());

            databaseReference.child(mAuth.getCurrentUser().getUid()).updateChildren(userMap);
            progressDialog.dismiss();

            if (getType.equalsIgnoreCase("Vendor")) {

                startActivity(new Intent(SettingsActivity.this, VendorMapsActivity.class));
                Toast.makeText(SettingsActivity.this, "Profile Information Updated Successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                startActivity(new Intent(SettingsActivity.this, CustomerMapsActivity.class));
                Toast.makeText(SettingsActivity.this, "Profile Information Updated Successfully", Toast.LENGTH_SHORT).show();
                finish();
            }


        }
    }

    private void validateInputs() {
        if (username.getText().toString().isEmpty()){
            username.setError("Please Enter Your Name");
            cancel = true;
            username.requestFocus();
        }else if (phoneNumber.getText().toString().isEmpty()){
            phoneNumber.setError("Please Enter Your Phone Number");
            cancel = true;
            phoneNumber.requestFocus();
        }else if (checker.equalsIgnoreCase("clicked")){
            uploadProfilePicture();
        }
    }

    private void uploadProfilePicture() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Updating Profile");
        progressDialog.setMessage("Please wait while we are updating your profile information");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        if (imageUri !=null){

            final StorageReference fileRef = myProfileImageStorageRef.child(mAuth.getCurrentUser().getUid()+".jpg");
            uploadTask = fileRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        myUri = downloadUri.toString();

                        HashMap<String,Object> userMap = new HashMap<>();
                        userMap.put("uid",mAuth.getCurrentUser().getUid());
                        userMap.put("userName",username.getText().toString());
                        userMap.put("phoneNumber",phoneNumber.getText().toString());
                        userMap.put("profileImage",myUri);

                        databaseReference.child(mAuth.getCurrentUser().getUid()).updateChildren(userMap);
                        progressDialog.dismiss();

                        if (getType.equalsIgnoreCase("Vendor")) {

                            startActivity(new Intent(SettingsActivity.this, VendorMapsActivity.class));
                            Toast.makeText(SettingsActivity.this, "Profile Information Updated Successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            startActivity(new Intent(SettingsActivity.this, CustomerMapsActivity.class));
                            Toast.makeText(SettingsActivity.this, "Profile Information Updated Successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } else {
                        Toast.makeText(SettingsActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            });
        }else {
            Toast.makeText(SettingsActivity.this,"Image is not selected",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK
        && data !=null){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            profileImage.setImageURI(imageUri);
        }else {
            Toast.makeText(SettingsActivity.this,"Error Occurred,Please try Again",Toast.LENGTH_SHORT).show();
            if (getType.equalsIgnoreCase("Customers")){
                Intent intent = new Intent(SettingsActivity.this,CustomerMapsActivity.class);
                startActivity(intent);
            }else {
                Intent intent = new Intent(SettingsActivity.this,VendorMapsActivity.class);
                startActivity(intent);
            }
        }
    }
    private void displayExistingUserInformation(){
        databaseReference.child(mAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()&& snapshot.getChildrenCount()>0){

                            try {


                            String name = snapshot.child("userName").getValue().toString();
                            String phone = snapshot.child("phoneNumber").getValue().toString();
                            String profilePicture = snapshot.child("profileImage").getValue().toString();

                            username.setText(name);
                            phoneNumber.setText(phone);
                            Picasso.get().load(profilePicture).into(profileImage);
                            }catch (Exception ignored){}
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}