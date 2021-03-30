package el.com.jalangotv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import el.com.jalangotv.Activities.DashboardActivity;
import el.com.jalangotv.Activities.ui.home.HomeFragment;
import el.com.jalangotv.Fragment.CommentsFragment;
import el.com.jalangotv.models.News;

import static java.security.AccessController.getContext;


public class ViewNewsActivity extends AppCompatActivity {
    private TextView headline,Story,title,likeCount,commentCount,shareCount,viewsCount;
    private ImageView new_image;
    private String Doc_Id;
    private long backPressedTime;
    private long CallPressedTime;
    private Toast backToast;
    private FloatingActionButton like,comment,share,save;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference newsRef = db.collection("News");
    CollectionReference SavedNewsRef = db.collection("SavedNews");
    private int commentState = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_news);
        headline = findViewById(R.id.view_headline);
        title = findViewById(R.id.view_headlines);
        Story = findViewById(R.id.view_story);
        new_image = findViewById(R.id.view_image);
        save= findViewById(R.id.fab_save);
        like = findViewById(R.id.fab_like);
        comment = findViewById(R.id.fab_comment);
        likeCount = findViewById(R.id.like_view_count);
        commentCount = findViewById(R.id.comment_view_count);
        viewsCount = findViewById(R.id.eye_view_count);


        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (commentState == 0){
                    commentState =1;
                    getSupportFragmentManager().beginTransaction().add(R.id.comment_fragmentHost,new
                            CommentsFragment()).commit();
                }else if (commentState == 1){

                    getSupportFragmentManager().beginTransaction().remove(new CommentsFragment()).commit();
                }

            }
        });

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String doc_ID = newsRef.document().getId();
                HashMap<String,Object> likes = new HashMap<>();
                likes.put("UID",Doc_Id);
                newsRef.document(Doc_Id).collection("Likes").document(doc_ID).set(likes)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()){

                                    final DocumentReference sfDocRef = db.collection("News").document(Doc_Id);

                                    db.runTransaction(new Transaction.Function<Void>() {
                                        @Override
                                        public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                                            DocumentSnapshot snapshot = transaction.get(sfDocRef);

                                            // Note: this could be done without a transaction
                                            //       by updating the population using FieldValue.increment()
                                            double newPopulation = snapshot.getLong("likesCount") + 1;
                                            transaction.update(sfDocRef, "likesCount", newPopulation);

                                            // Success
                                            return null;
                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });



                                }else {
                                    Toast.makeText(ViewNewsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            }
                        });


            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveStory();
            }
        });

        Bundle extra = getIntent().getExtras();

        if (extra != null)
        {
            Doc_Id = extra.getString("doc_ID");
        }

        LoadDetails();

    }

    private void SaveStory() {

        if (Doc_Id != null){
            ProgressDialog  progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Please wait saving story..");
            progressDialog.show();

            HashMap<String, Object> saveNews = new HashMap<>();
            saveNews.put("Headline", headlines);
            saveNews.put("Story", stories);
            saveNews.put("category",categories);
            saveNews.put("Doc_ID",Doc_Id);
            saveNews.put("search", headlines.toLowerCase());
            saveNews.put("News_image", news_imaged);
            saveNews.put("timestamp", FieldValue.serverTimestamp());

            SavedNewsRef.document(Doc_Id).set(saveNews).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){

                        Toast.makeText(ViewNewsActivity.this, "Story saved Successful.", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }else {
                        progressDialog.dismiss();
                        Toast.makeText(ViewNewsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {

        }


    }


    private String news_imaged, headlines, stories,categories;
    private long likie,commentie,views;
    void LoadDetails() {
        if (Doc_Id !=null){

            newsRef.document(Doc_Id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot,
                                    @javax.annotation.Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        return;
                    }
                    if (documentSnapshot.exists()) {
                        News mynews = documentSnapshot.toObject(News.class);

                        headlines = mynews.getHeadline();
                        stories = mynews.getStory();
                        news_imaged = mynews.getNews_image();
                        categories= mynews.getCategory();
                        likie = mynews.getLikesCount();
                        commentie = mynews.getCommentCount();
                        views = mynews.getViewsCount();


                        if (news_imaged != null){
                            Picasso.get().load(news_imaged).fit().into(new_image);

                        }

                        headline.setText(headlines);
                        Story.setText(stories);
                        Story.setMovementMethod(new ScrollingMovementMethod());
                        title.setText(headlines);
                        likeCount.setText(likie+"");
                        commentCount.setText(commentie+"");
                        viewsCount.setText(views+"");


                    } else {

                    }


                }
            });
        }


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
            finish();
            return;
        } else {

            if (commentState == 1){
                commentState =0;
                getSupportFragmentManager().beginTransaction().remove(new CommentsFragment()).commit();
            }
        }

        backPressedTime = System.currentTimeMillis();

    }
}