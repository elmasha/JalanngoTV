package el.com.jalangotv.Fragment.ui.savednews;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Transaction;

import el.com.jalangotv.Adapters.SavedAdapter;
import el.com.jalangotv.R;
import el.com.jalangotv.ViewNewsActivity;
import el.com.jalangotv.models.News;


public class SavedNewsFragment extends Fragment {
View root;
    public SavedAdapter adapter;
    private FirebaseAuth mAuth;
    private RecyclerView SavedRecyclerView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference SavedNewsRef = db.collection("SavedNews");
    public SavedNewsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_category, container, false);
        SavedRecyclerView = root.findViewById(R.id.Recyclerview_saved);
        WebView webView = root.findViewById(R.id.WebView);
        webView.loadUrl("https://www.youtube.com/results?search_query=jalango+tv");

        return  root;
    }


    //----Fetch news--
    private void FetchNews() {

//        String UID = mAuth.getCurrentUser().getUid();
        Query query = SavedNewsRef.orderBy("timestamp", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<News> transaction = new FirestoreRecyclerOptions.Builder<News>()
                .setQuery(SavedNewsRef, News.class)
                .setLifecycleOwner(this)
                .build();
        adapter = new SavedAdapter(transaction);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        SavedRecyclerView.setHasFixedSize(true);
        SavedRecyclerView.setLayoutManager(layoutManager);
        SavedRecyclerView.setNestedScrollingEnabled(false);
        SavedRecyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new SavedAdapter.OnItemCickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                News news = documentSnapshot.toObject(News.class);
                String headline = news.getHeadline();
                String story = news.getStory();
                String image = news.getNews_image();
                String doc_id = news.getDoc_ID();
                if (doc_id !=null |headline != null | story != null | image != null){
                    Intent toVendorPref = new Intent(getActivity(), ViewNewsActivity.class);
                    toVendorPref.putExtra("Headline",headline);
                    toVendorPref.putExtra("Story",story);
                    toVendorPref.putExtra("Image",image);
                    toVendorPref.putExtra("doc_ID",doc_id);
                    startActivity(toVendorPref);
                    viewsCount(doc_id);
                }
            }
        });
    }
    //...end fetch..



    //----Likes count
    private void viewsCount(String doc_Id){

        final DocumentReference sfDocRef = db.collection("News").document(doc_Id);

        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(sfDocRef);

                // Note: this could be done without a transaction
                //       by updating the population using FieldValue.increment()
                double newPopulation = snapshot.getLong("viewsCount") + 1;
                transaction.update(sfDocRef, "viewsCount", newPopulation);

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
    ///___end likes



    @Override
    public void onStart() {
        super.onStart();
      //  FetchNews();
    }
}