package fish2salty.app.canipark2.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import fish2salty.app.canipark2.R;

public class Register extends AppCompatActivity {

    public static final String TAG = "TAG";
    ImageView userImg;
    static int PReqCode = 1;
    static int REQUESCODE = 1;
    Uri pickedImgUri;
    TextView textView;
    CheckBox checkBox;

    private EditText userEmail,userTelnum,userFullName,userPassword,userPassword2,userCarnum,userCarLicense;
    private ProgressBar loadingProgress;
    private Button regBtn;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;

    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //ini views
        userEmail = findViewById(R.id.regMail);
        userTelnum = findViewById(R.id.regTelnum);
        userFullName = findViewById(R.id.regName);
        userPassword = findViewById(R.id.regPassword);
        userPassword2 = findViewById(R.id.regConfirmPass);
        userCarnum = findViewById(R.id.regCarNum);
        userCarLicense = findViewById(R.id.regCarLicense);
        checkBox = findViewById(R.id.regcheckTofU);
        textView = findViewById(R.id.regTerms);
        loadingProgress = findViewById(R.id.regProgressBar);
        regBtn = findViewById(R.id.regBtn);
        loadingProgress.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                regBtn.setVisibility(View.INVISIBLE);
                loadingProgress.setVisibility(View.VISIBLE);
                final String email = userEmail.getText().toString().trim();
                final String tellnum = userTelnum.getText().toString();
                final String fullname = userFullName.getText().toString();
                final String password = userPassword.getText().toString().trim();
                final String password2 = userPassword2.getText().toString();
                final String carnum = userCarnum.getText().toString();
                final String carlicense = userCarLicense.getText().toString();

                if(email.isEmpty() || tellnum.isEmpty() || fullname.isEmpty() || password.isEmpty()  || !password.equals(password2) || carnum.isEmpty() || carlicense.isEmpty()) {


                    // something goes wrong : all fields must be filled
                    // we need to display an error message
                    showMessage("Please Verify all fields") ;
                    regBtn.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);


                }
                else {
                    // everything is ok method will try to create the user if the email is valid and all fields are filled now we can start creating user account
                    // CreateUserAccoun
                    CreateUserAccount(email,fullname,password,tellnum,carnum,carlicense);
                }





            }
        });

        userImg = findViewById(R.id.imgUser);

        userImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= 22) {

                    checkAndRequestForPermission();

                } else {

                    openGallery();

                }

            }
        });

    }

    private void CreateUserAccount(final String email, final String fullname, String password, final String tellnum, final String carnum, final String carlicense) {


        // this method create user account with specific email and password

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()&& checkBox.isChecked()) {

                            Toast.makeText(Register.this, "Account created Created.", Toast.LENGTH_SHORT).show();
                            userID = mAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = mStore.collection("users").document(userID);
                            Map<String,Object> user = new HashMap<>();
                            user.put("fName",fullname);
                            user.put("email",email);
                            user.put("phone",tellnum);
                            user.put("carnum",carnum);
                            user.put("carlisense",carlicense);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: user Profile is created for "+ userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: " + e.toString());
                                }
                            });
                            /*// user account created successfully
                            showMessage("Account created");*/
                            // after we created user account we need to update his profile picture and name
                            updateUserInfo( fullname ,pickedImgUri,mAuth.getCurrentUser());



                        }
                        else
                        {

                            // account creation failed
                            showMessage("account creation failed" + task.getException().getMessage());
                            regBtn.setVisibility(View.VISIBLE);
                            loadingProgress.setVisibility(View.INVISIBLE);

                        }
                    }
                });








    }


    // update user photo and name
    private void updateUserInfo(final String name, Uri pickedImgUri, final FirebaseUser currentUser) {

        // first we need to upload user photo to firebase storage and get url

        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_photos");
        final StorageReference imageFilePath = mStorage.child(pickedImgUri.getLastPathSegment());
        imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                // image uploaded succesfully
                // now we can get our image url

                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        // uri contain user image url


                        UserProfileChangeRequest profleUpdate = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .setPhotoUri(uri)
                                .build();


                        currentUser.updateProfile(profleUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {
                                            // user info updated successfully
                                            showMessage("Register Complete");
                                            updateUI();
                                        }

                                    }
                                });

                    }
                });





            }
        });






    }

    private void updateUI() {

        Intent homeActivity = new Intent(getApplicationContext(), MainPage.class);
        startActivity(homeActivity);
        finish();


    }

    // simple method to show toast message
    private void showMessage(String message) {

        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();

    }

    private void openGallery() {
        //TODO: open gallery intent and wait for user to pick an image !

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUESCODE);


    }

    private void checkAndRequestForPermission() {

        if (ContextCompat.checkSelfPermission(Register.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Register.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Toast.makeText(Register.this, "Please accept for required permission", Toast.LENGTH_SHORT).show();

            } else {
                ActivityCompat.requestPermissions(Register.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PReqCode);
            }

        } else
            openGallery();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESCODE && data != null) {

            // the user has successfully picked an image
            // we need to save its reference to a Uri variable
            pickedImgUri = data.getData();
            userImg.setImageURI(pickedImgUri);
        }
    }

    public void termsOfuse(View view){
        Intent regis = new Intent(Register.this, Termstouse.class);
        startActivity(regis);
    }

    public void cancelReg(View view){
        Intent cancel = new Intent(Register.this, Login.class);
        startActivity(cancel);
    }
}
