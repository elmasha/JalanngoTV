package el.com.jalangotv.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import el.com.jalangotv.R;
import id.zelory.compressor.Compressor;

import static com.theartofdev.edmodo.cropper.CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE;

public class RegisterActivity extends AppCompatActivity {
    private TextInputLayout userName,Email,Password;
    private Button btnRegister;
    private TextView to_login,addPhoto;
    private CircleImageView profileImage;
    private Uri ImageUri;


    private FirebaseAuth mAuth;
    FirebaseStorage storage;
    StorageReference storageReference;
    private Bitmap compressedImageBitmap;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference JTvUserRef = db.collection("JtvUsers");
    private UploadTask uploadTask;
    ProgressDialog progressDialog;
    private CallbackManager mCallbackManager;
    private LoginButton loginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        userName = findViewById(R.id.input_userName);
        Email = findViewById(R.id.input_email);
        Password = findViewById(R.id.input_Password);
        addPhoto = findViewById(R.id.add_imageProfile);
        profileImage = findViewById(R.id.user_image);
        btnRegister = findViewById(R.id.BtnRegister);
        to_login = findViewById(R.id.to_logIn);
        loginButton = findViewById(R.id.button_sign_in);



        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                        .setMinCropResultSize(500, 500)
                        .setAspectRatio(1, 1)
                        .setRequestedSize(500, 500)
                        .start(RegisterActivity.this);
            }
        });
        to_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),LogInActivity.class));
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Validation()){

                }else {
                    signIn();
                }
            }
        });




        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();

        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
            }
        });
// ...

    }

    private void handleFacebookAccessToken(AccessToken accessToken) {


        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            String uid = mAuth.getCurrentUser().getUid();
                            updateUI(user.getPhotoUrl(),user.getDisplayName(),uid);
                        } else {
                            // If sign in fails, display a message to the user.

                        }
                    }
                });
    }

    private void updateUI(Uri photoUrl, String displayName,String uid) {

        HashMap<String, Object> store = new HashMap<>();
        store.put("UserName", displayName);
        store.put("Email", "facebook");
        store.put("ProfileImage", photoUrl);
        store.put("timestamp", FieldValue.serverTimestamp());
        JTvUserRef.document(uid).set(store).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    startActivity(new Intent(getApplicationContext(),DashboardActivity.class));
                }else {
                    Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void signIn() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading....");
        progressDialog.show();
        String email = Email.getEditText().getText().toString();
        String password = Password.getEditText().getText().toString();
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){
                    UploadDetails();
                }else {
                    Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean Validation() {
        String  username  = userName.getEditText().getText().toString();
        String email = Email.getEditText().getText().toString();
        String password = Password.getEditText().getText().toString();

        if (username.isEmpty()){
            userName.setError("UserName required.");
            return false;
        }else if (email.isEmpty()){
            Email.setError("Enter email.");
            return false;
        }else if (password.isEmpty()){
            Password.setError("Password required.");
            return false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Email.setError("Please enter a Valid email");
            return false;
        }else {
            userName.setError(null);
            Email.setError(null);
            Password.setError(null);
            return true;
        }

    }





    private void UploadDetails() {


        if (ImageUri == null){

            Toast.makeText(getApplicationContext(), "No image selected", Toast.LENGTH_SHORT).show();


        }else {

            File newimage = new File(ImageUri.getPath());
            String  username  = userName.getEditText().getText().toString();
            String email = Email.getEditText().getText().toString();
            String password = Password.getEditText().getText().toString();
            String uid = mAuth.getCurrentUser().getUid();

            try {
                Compressor compressor = new Compressor(this);
                compressor.setMaxHeight(1200);
                compressor.setMaxWidth(628);
                compressor.setQuality(6);
                compressedImageBitmap = compressor.compressToBitmap(newimage);

            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            compressedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();


            final StorageReference ref = storageReference.child("images/thumbs" + UUID.randomUUID().toString());
            uploadTask = ref.putBytes(data);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();

                        String url34 = downloadUri.toString();
                        HashMap<String, Object> store = new HashMap<>();
                        store.put("UserName", username);
                        store.put("Email", email);
                        store.put("ProfileImage", url34);
                        store.put("timestamp", FieldValue.serverTimestamp());
                        JTvUserRef.document(uid).set(store).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){

                                    Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                    startActivity(new Intent(getApplicationContext(),LogInActivity.class));


                                }else {
                                    progressDialog.dismiss();
                                    Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }else {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });



        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
            switch (requestCode) {
                case CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);

                    //data.getData returns the content URI for the selected Image

                    ImageUri = result.getUri();
                    profileImage.setImageURI(ImageUri);

                    break;
            }
    }
}