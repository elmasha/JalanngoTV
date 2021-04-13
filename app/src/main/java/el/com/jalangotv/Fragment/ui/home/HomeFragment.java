package el.com.jalangotv.Fragment.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;

import el.com.jalangotv.Adapters.SavedAdapter;
import el.com.jalangotv.Adapters.SliderAdapter;
import el.com.jalangotv.ViewNewsActivity;
import el.com.jalangotv.Adapters.CategoryAdapter;
import el.com.jalangotv.Adapters.LatestNewsAdapter;
import el.com.jalangotv.Adapters.NewsAdapter;
import el.com.jalangotv.R;
import el.com.jalangotv.models.News;


public class HomeFragment extends Fragment {
 View root;
    public NewsAdapter adapter1;
    public LatestNewsAdapter adapter2;
    private FirebaseAuth mAuth;
    public CategoryAdapter adapter;
    private RecyclerView topNewsRecyclerView,TrendRecyclerView,newsRecycler;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference newsRef = db.collection("News");
    CollectionReference categoryRef = db.collection("Category");
    public SavedAdapter adapterSave;

    private SliderAdapter sliderAdapter;
    private ArrayList<News> sliderDataArrayList;
    private SliderView sliderView;
    private String Doc_Id,Doc_Id2;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
         root = inflater.inflate(R.layout.fragment_home, container, false);

        mAuth = FirebaseAuth.getInstance();
        TrendRecyclerView = root.findViewById(R.id.Recyclerview_trending);
        newsRecycler = root.findViewById(R.id.Recyclerview_news);
        topNewsRecyclerView = root.findViewById(R.id.Recyclerview_moreNews);
        // creating a new array list fr our array list.
        sliderDataArrayList = new ArrayList<>();

        // initializing our slider view and
        // firebase firestore instance.
        sliderView = root.findViewById(R.id.slider);

        loadImages();
        return root;
    }



    //----Image Slider ----//
    private long likess,commentss;
    private void loadImages() {
        // getting data from our collection and after
        // that calling a method for on success listener.
                newsRef.orderBy("timestamp", Query.Direction.DESCENDING)
                        .limit(6)
                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                // inside the on success method we are running a for loop
                // and we are getting the data from Firebase Firestore
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                    // after we get the data we are passing inside our object class.
                    News sliderData = documentSnapshot.toObject(News.class);
                    News model = new News();

                    long mili = sliderData.getTimestamp().getTime();
                    // below line is use for setting our
                    // image url for our modal class.
                    model.setNews_image(sliderData.getNews_image());
                    model.setHeadline(sliderData.getHeadline());
                    model.setLikesCount(sliderData.getLikesCount());
                    model.setCommentCount(sliderData.getCommentCount());
                    model.setCategory(sliderData.getCategory());
                    model.setViewsCount(sliderData.getViewsCount());
                    model.setDoc_ID(sliderData.getDoc_ID());
                    model.setTimestamp(sliderData.getTimestamp());

                    // after that we are adding that
                    // data inside our array list.
                    sliderDataArrayList.add(model);

                    // after adding data to our array list we are passing
                    // that array list inside our adapter class.
                    sliderAdapter = new SliderAdapter(getContext(), sliderDataArrayList);


                    // belows line is for setting adapter
                    // to our slider view
                    sliderView.setSliderAdapter(sliderAdapter);

                    // below line is for setting animation to our slider.
                    sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);

                    // below line is for setting auto cycle duration.
                    sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);

                    // below line is for setting
                    // scroll time animation
                    sliderView.setScrollTimeInSec(4);

                    // below line is for setting auto
                    // cycle animation to our slider
                    sliderView.setAutoCycle(true);


                    // below line is use to start
                    // the animation of our slider view.
                    sliderView.startAutoCycle();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // if we get any error from Firebase we are
                // displaying a toast message for failure
                Toast.makeText(getContext(), "Fail to load slider data..", Toast.LENGTH_SHORT).show();
            }
        });
    }

    ///_____ImageSlider end


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

    //----Fetch LatestNews--
    private void FetchLatestNews() {

//        String UID = mAuth.getCurrentUser().getUid();
        Query query =
                newsRef.orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<News> transaction = new FirestoreRecyclerOptions.Builder<News>()
                .setQuery(query, News.class)
                .setLifecycleOwner(this)
                .build();
        adapter2 = new LatestNewsAdapter(transaction);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        TrendRecyclerView.setHasFixedSize(true);
        TrendRecyclerView.setLayoutManager(layoutManager);
        TrendRecyclerView.setNestedScrollingEnabled(false);
        TrendRecyclerView.setAdapter(adapter2);



        adapter2.setOnItemClickListener(new LatestNewsAdapter.OnItemCickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                News news = documentSnapshot.toObject(News.class);
                String headline = news.getHeadline();
                String story = news.getStory();
                String image = news.getNews_image();
                Doc_Id = news.getDoc_ID();

                if (Doc_Id !=null |headline != null | story != null | image != null){
                    Intent toVendorPref = new Intent(getActivity(), ViewNewsActivity.class);
                    toVendorPref.putExtra("Headline",headline);
                    toVendorPref.putExtra("Story",story);
                    toVendorPref.putExtra("Image",image);
                    toVendorPref.putExtra("doc_ID",Doc_Id);
                    startActivity(toVendorPref);
                viewsCount(Doc_Id);
                }

            }
        });

    }
    //...end fetch..




    //----Fetch news--
    private void FetchPopularNews() {

//        String UID = mAuth.getCurrentUser().getUid();
        Query query = newsRef.orderBy("timestamp", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<News> transaction = new FirestoreRecyclerOptions.Builder<News>()
                .setQuery(query, News.class)
                .setLifecycleOwner(this)
                .build();
        adapter1 = new NewsAdapter(transaction);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        newsRecycler.setHasFixedSize(true);
        newsRecycler.setLayoutManager(layoutManager);
        newsRecycler.setNestedScrollingEnabled(false);
        newsRecycler.setAdapter(adapter1);


        adapter1.setOnItemClickListener(new NewsAdapter.OnItemCickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                News news = documentSnapshot.toObject(News.class);
                String headline = news.getHeadline();
                String story = news.getStory();
                String image = news.getNews_image();
                 Doc_Id2 = news.getDoc_ID();
                if (Doc_Id2 !=null |headline != null | story != null | image != null){
                    Intent tonews = new Intent(getActivity(), ViewNewsActivity.class);
                    tonews.putExtra("Headline",headline);
                    tonews.putExtra("Story",story);
                    tonews.putExtra("Image",image);
                    tonews.putExtra("doc_ID",Doc_Id2);
                    startActivity(tonews);
                    viewsCount(Doc_Id2);
                }

            }
        });


    }
    //...end fetch..

    //----Fetch news--
    private void FetchNews() {

//        String UID = mAuth.getCurrentUser().getUid();
        Query query = newsRef.orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<News> transaction = new FirestoreRecyclerOptions.Builder<News>()
                .setQuery(query, News.class)
                .setLifecycleOwner(this)
                .build();
        adapterSave = new SavedAdapter(transaction);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        topNewsRecyclerView.setHasFixedSize(true);
        topNewsRecyclerView.setLayoutManager(layoutManager);
        topNewsRecyclerView.setNestedScrollingEnabled(false);
        topNewsRecyclerView.setAdapter(adapterSave);

        adapterSave.setOnItemClickListener(new SavedAdapter.OnItemCickListener() {
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


    ////-----timestamp --

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    private static final int WEEK_MILLIS = 7 * DAY_MILLIS ;

    public static String getTimeAgo(long time) {

        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }
        long now =System.currentTimeMillis();;

        long diff = now - time;
        if(diff>0) {

            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " minutes ago";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " hours ago";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else if (diff < 7 * DAY_MILLIS) {
                return diff / DAY_MILLIS + " days ago";
            } else if (diff < 2 * WEEK_MILLIS) {
                return "1 week ago";
            } else if (diff < WEEK_MILLIS * 3) {
                return diff / WEEK_MILLIS + " weeks ago";
            } else {
                java.util.Date date = new java.util.Date((long) time);
                return date.toString();
            }

        }
        else {

            diff=time-now;
            if (diff < MINUTE_MILLIS) {
                return "this minute";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute later";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " minutes later";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour later";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " hours later";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "tomorrow";
            } else if (diff < 7 * DAY_MILLIS) {
                return diff / DAY_MILLIS + " days later";
            } else if (diff < 2 * WEEK_MILLIS) {
                return "a week later";
            } else if (diff < WEEK_MILLIS * 3) {
                return diff / WEEK_MILLIS + " weeks later";
            } else {
                java.util.Date date = new java.util.Date((long) time);
                return date.toString();
            }
        }

    }


    @Override
    public void onStart() {
        super.onStart();
    FetchLatestNews();
    FetchPopularNews();
    FetchNews();
    loadImages();

    }
}