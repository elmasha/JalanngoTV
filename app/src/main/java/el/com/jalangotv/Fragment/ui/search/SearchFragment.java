package el.com.jalangotv.Fragment.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
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

import el.com.jalangotv.Adapters.SearchNewsAdapter;
import el.com.jalangotv.R;
import el.com.jalangotv.ViewNewsActivity;
import el.com.jalangotv.models.News;

public class SearchFragment extends Fragment {
View root;
    public SearchNewsAdapter adapter;
    private FirebaseAuth mAuth;
    private RecyclerView SearchRecyclerView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference NewsRef = db.collection("News");
    private AdView adView;
    AdRequest adRequest;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
         root = inflater.inflate(R.layout.fragment_search, container, false);
         SearchRecyclerView = root.findViewById(R.id.Recyclerview_search);
        adView = (AdView) root.findViewById(R.id.adView3);
        adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

         FetchNews();
        return root;
    }

    //----Fetch news--
    private void FetchNews() {

//        String UID = mAuth.getCurrentUser().getUid();
        Query query = NewsRef.orderBy("timestamp", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<News> transaction = new FirestoreRecyclerOptions.Builder<News>()
                .setQuery(NewsRef, News.class)
                .setLifecycleOwner(this)
                .build();
        adapter = new SearchNewsAdapter(transaction);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        SearchRecyclerView.setHasFixedSize(true);
        SearchRecyclerView.setLayoutManager(layoutManager);
        SearchRecyclerView.setNestedScrollingEnabled(false);
        SearchRecyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new SearchNewsAdapter.OnItemCickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                News news = documentSnapshot.toObject(News.class);
                String headline = news.getHeadline();
                String story = news.getStory();
                String image = news.getNews_image();
                String doc_id = news.getDoc_ID();
                if (doc_id !=null |headline != null | story != null | image != null) {
                    Intent toVendorPref = new Intent(getActivity(), ViewNewsActivity.class);
                    toVendorPref.putExtra("Headline", headline);
                    toVendorPref.putExtra("Story", story);
                    toVendorPref.putExtra("Image", image);
                    toVendorPref.putExtra("doc_ID", doc_id);
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
        FetchNews();
    }
}