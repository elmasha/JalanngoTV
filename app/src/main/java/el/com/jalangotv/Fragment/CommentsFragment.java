package el.com.jalangotv.Fragment;

import android.os.Bundle;


import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Transaction;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import el.com.jalangotv.Adapters.CommentsAdapter;
import el.com.jalangotv.R;
import el.com.jalangotv.ViewNewsActivity;
import el.com.jalangotv.models.Comments;
import el.com.jalangotv.models.News;


public class CommentsFragment extends Fragment {
View root;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference newsRef = db.collection("News");
    CollectionReference categoryRef = db.collection("Category");
    private FloatingActionButton sendComment;
    private TextInputLayout InputComment;

    private String Doc_ID;
    private TextView doc_ID,heads,closeComment;
    private RecyclerView recyclerView;
    private CommentsAdapter adapter;
    private ProgressBar progressBar;


    public CommentsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       root = inflater.inflate(R.layout.fragment_comments, container, false);
        sendComment = root.findViewById(R.id.send_comment);
        InputComment = root.findViewById(R.id.input_comment);
        doc_ID = root.findViewById(R.id.comments);
        recyclerView = root.findViewById(R.id.Recyclerview_comment);
         progressBar = root.findViewById(R.id.spin_kit);
         closeComment = root.findViewById(R.id.close_comment);
         heads = root.findViewById(R.id.view_headline2);
        Wave wave = new Wave();
        progressBar.setIndeterminateDrawable(wave);


        ViewNewsActivity activity = (ViewNewsActivity) getActivity();
        Bundle results = activity.getMyData();
        Doc_ID = results.getString("val1");



        closeComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getActivity().getSupportFragmentManager().findFragmentById(R.id.comment_fragmentHost) != null) {
                    getActivity().getSupportFragmentManager()
                            .beginTransaction().remove(getActivity().getSupportFragmentManager().findFragmentById(R.id.comment_fragmentHost)).commit();
                }
            }
        });

        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = InputComment.getEditText().getText().toString();
                if (comment.isEmpty()){

                }else {
                    progressBar.setVisibility(View.VISIBLE);
                    AddComment();
                }
            }
        });

        LoadDetails();
   return root;
    }


    //----Fetch news--
    private void FetchComments() {

//        String UID = mAuth.getCurrentUser().getUid();
        Query query =  newsRef.document(Doc_ID).collection("Comments")
                .orderBy("timestamp", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Comments> transaction = new FirestoreRecyclerOptions.Builder<Comments>()
                .setQuery(query, Comments.class)
                .setLifecycleOwner(this)
                .build();
        adapter = new CommentsAdapter(transaction);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);


        adapter.setOnItemClickListener(new CommentsAdapter.OnItemCickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                News news = documentSnapshot.toObject(News.class);
                String headline = news.getHeadline();
                String story = news.getStory();
                String image = news.getNews_image();
//                Doc_Id = news.getDoc_ID();
//                if (Doc_Id2 !=null |headline != null | story != null | image != null){
//                    Intent tonews = new Intent(getActivity(), ViewNewsActivity.class);
//                    tonews.putExtra("Headline",headline);
//                    tonews.putExtra("Story",story);
//                    tonews.putExtra("Image",image);
//                    tonews.putExtra("doc_ID",Doc_Id2);
//                    startActivity(tonews);
//                    viewsCount(Doc_Id2);
//                }

            }
        });





    }
    //...end fetch..


    private String news_imaged, headlines, stories,categories;
    private long likie,commentie,views;
    void LoadDetails() {
        if (Doc_ID !=null){

            newsRef.document(Doc_ID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot,
                                    @javax.annotation.Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        return;
                    }
                    if (documentSnapshot.exists()) {
                        News mynews = documentSnapshot.toObject(News.class);
                        headlines = mynews.getHeadline();
                        heads.setText(headlines);

                    } else {

                    }


                }
            });
        }


    }



    private void AddComment() {

        HashMap<String ,Object> hashMap = new HashMap<>();
        hashMap.put("userImage",null);
        hashMap.put("userName","Elmasha");
        hashMap.put("comment",InputComment.getEditText().getText().toString());
        hashMap.put("likeCount",0);
        hashMap.put("timestamp", FieldValue.serverTimestamp());


        newsRef.document(Doc_ID).collection("Comments").document().set(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful())
                {
                    //----Likes count
                    commentCount(Doc_ID);
                    InputComment.getEditText().setText("");

                    ///___end likes

                }else {
                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });



    }


    private void commentCount(String doc_Id){

        final DocumentReference sfDocRef = db.collection("News").document(doc_Id);

        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(sfDocRef);

                // Note: this could be done without a transaction
                //       by updating the population using FieldValue.increment()
                double newPopulation = snapshot.getLong("commentCount") + 1;
                transaction.update(sfDocRef, "commentCount", newPopulation);

                // Success
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                InputComment.getEditText().setText("");
                progressBar.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
            }
        });

    }


    @Override
    public void onStart() {
        super.onStart();
        FetchComments();
    }


}