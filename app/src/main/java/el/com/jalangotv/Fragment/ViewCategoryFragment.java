package el.com.jalangotv.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import el.com.jalangotv.Activities.DashboardActivity;
import el.com.jalangotv.Adapters.CategoryNewsAdapter;
import el.com.jalangotv.Adapters.NewsAdapter;
import el.com.jalangotv.R;
import el.com.jalangotv.ViewNewsActivity;
import el.com.jalangotv.models.News;


public class ViewCategoryFragment extends Fragment {
View root;
private  String value1;
    public CategoryNewsAdapter adapter1;
    private RecyclerView CategoryRecyclerView,TrendRecyclerView,newsRecycler;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference newsRef = db.collection("News");

    public ViewCategoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_view_category, container, false);
        newsRecycler = root.findViewById(R.id.Recyclerview_categoryNews);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String myValue = bundle.getString("message");

        }


        DashboardActivity activity = (DashboardActivity) getActivity();

        Bundle results = activity.getMyData();
         value1 = results.getString("val1");

        return root;
    }


    //----Fetch news--
    private void FetchNews() {

        Query query = newsRef.orderBy("timestamp", Query.Direction.DESCENDING)
                .whereEqualTo("category",value1);
        FirestoreRecyclerOptions<News> transaction = new FirestoreRecyclerOptions.Builder<News>()
                .setQuery(query, News.class)
                .setLifecycleOwner(this)
                .build();
        adapter1 = new CategoryNewsAdapter(transaction);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        newsRecycler.setHasFixedSize(true);
        newsRecycler.setLayoutManager(layoutManager);
        newsRecycler.setNestedScrollingEnabled(false);
        newsRecycler.setAdapter(adapter1);

        adapter1.setOnItemClickListener(new CategoryNewsAdapter.OnItemCickListener() {
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