package el.com.jalangotv.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
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

import el.com.jalangotv.R;
import id.zelory.compressor.Compressor;

import static com.theartofdev.edmodo.cropper.CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE;


public class AddNewsActivity extends AppCompatActivity {

    private TextInputLayout headlines,story;
    private Spinner category;
    private Button Add_new;
    private ImageView Photo;
    private Uri imageUri;
    private FirebaseAuth mAuth;
    FirebaseStorage storage;
    StorageReference storageReference;
    private Bitmap compressedImageBitmap;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference newsRef = db.collection("News");
    CollectionReference categoryRef = db.collection("Category");

    private String Doc_ID;
    private UploadTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_news);

        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        //----id from xml ----
        headlines = findViewById(R.id.headline);
        story = findViewById(R.id.story);
        category = findViewById(R.id.category);
        Add_new = findViewById(R.id.Add_news);
        Photo = findViewById(R.id.Add_image);


        Photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                        .setMinCropResultSize(800, 200)
                        .setAspectRatio(1, 1)
                        .start(AddNewsActivity.this);
            }
        });


        Add_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!Validation()){

                }else {
                    UploadNews();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
            switch (requestCode) {
                case CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);

                    //data.getData returns the content URI for the selected Image

                    imageUri = result.getUri();
                    Photo.setImageURI(imageUri);

                    break;
            }
    }

    private void UploadNews() {
        if (imageUri == null){

            Toast.makeText(getApplicationContext(), "No image selected", Toast.LENGTH_SHORT).show();


        }else {

            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Please Wait publishing story..");
            progressDialog.show();



            File newimage = new File(imageUri.getPath());
            String Headlines = headlines.getEditText().getText().toString();
            String Story = story.getEditText().getText().toString();
            String Category = category.getSelectedItem().toString();
            try {
                Compressor compressor = new Compressor(this);
                compressor.setMaxHeight(800);
                compressor.setMaxWidth(200);
                compressor.setQuality(10);
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
                        Doc_ID = newsRef.document().getId();
                        HashMap<String, Object> store = new HashMap<>();
                        store.put("Headline", Headlines);
                        store.put("Story", Story);
                        store.put("category", Category);
                        store.put("Doc_ID",Doc_ID);
                        store.put("search", Headlines.toLowerCase());
                        store.put("News_image", url34);
                        store.put("likesCount", 0);
                        store.put("commentCount", 0);
                        store.put("shareCount", 0);
                        store.put("timestamp", FieldValue.serverTimestamp());
                        newsRef.document(Doc_ID).set(store).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){

                                    Toast.makeText(AddNewsActivity.this, "Story Published successfuly", Toast.LENGTH_SHORT).show();

                                }else {
                                    Toast.makeText(AddNewsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }else {
                        Toast.makeText(AddNewsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddNewsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });



        }

    }

    private boolean Validation() {
    String Headlines = headlines.getEditText().getText().toString();
    String Story = story.getEditText().getText().toString();
    String Category = category.getSelectedItem().toString();

        if (Headlines.isEmpty()) {
            headlines.setError("Fill the empty field");
            return false;
        }else if (Story.isEmpty()) {
            story.setError("Fill the empty field");
            return false;
        }else if (Category.equals("Select category")) {
            Toast.makeText(this, "Category  empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            headlines.setError(null);
            story.setError(null);
            return true;
        }

    }
}