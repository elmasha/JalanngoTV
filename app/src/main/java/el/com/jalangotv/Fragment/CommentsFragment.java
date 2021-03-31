package el.com.jalangotv.Fragment;

import android.os.Bundle;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.util.HashMap;

import el.com.jalangotv.R;


public class CommentsFragment extends Fragment {
View root;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference newsRef = db.collection("News");
    CollectionReference categoryRef = db.collection("Category");
    private FloatingActionButton sendComment;
    private TextInputLayout InputComment;

    private String Doc_ID;


    public CommentsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
   root = inflater.inflate(R.layout.fragment_comments, container, false);
        Bundle bundle = this.getArguments();

        if (bundle != null) {
            Doc_ID = bundle.getString("Doc_comment");
            Toast.makeText(getContext(), Doc_ID, Toast.LENGTH_SHORT).show();
        }
        sendComment = root.findViewById(R.id.send_comment);
        InputComment = root.findViewById(R.id.input_comment);

        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = InputComment.getEditText().getText().toString();
                if (comment.isEmpty()){

                }else {
                    AddComment();
                }
            }
        });
   return root;
    }

    private void AddComment() {

        HashMap<String ,Object> hashMap = new HashMap<>();
        hashMap.put("comment",InputComment.getEditText().getText().toString());

        newsRef.document(Doc_ID).collection("Comments").document().set(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful())
                {
                    //----Likes count
                    commentCount(Doc_ID);
                    ///___end likes

                }else {
                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }



}