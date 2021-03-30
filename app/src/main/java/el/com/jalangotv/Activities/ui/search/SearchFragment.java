package el.com.jalangotv.Activities.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import el.com.jalangotv.Adapters.NewsAdapter;
import el.com.jalangotv.Adapters.SearchNewsAdapter;
import el.com.jalangotv.R;
import el.com.jalangotv.ViewNewsActivity;
import el.com.jalangotv.models.News;

public class SearchFragment extends Fragment {
View root;
    public NewsAdapter adapter;
    private FirebaseAuth mAuth;
    private RecyclerView SearchRecyclerView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference NewsRef = db.collection("News");



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
         root = inflater.inflate(R.layout.fragment_search, container, false);
         SearchRecyclerView = root.findViewById(R.id.Recyclerview_search);
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
        adapter = new NewsAdapter(transaction);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        SearchRecyclerView.setHasFixedSize(true);
        SearchRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        SearchRecyclerView.setNestedScrollingEnabled(false);
        SearchRecyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new NewsAdapter.OnItemCickListener() {
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
                }
            }
        });


        }
    //...end fetch..


    @Override
    public void onStart() {
        super.onStart();
        FetchNews();
    }
}