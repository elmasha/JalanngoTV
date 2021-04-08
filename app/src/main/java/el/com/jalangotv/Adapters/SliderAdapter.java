package el.com.jalangotv.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import el.com.jalangotv.R;
import el.com.jalangotv.ViewNewsActivity;
import el.com.jalangotv.models.News;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderAdapterVH> {

    private Context context;
    private List<News> mSliderItems = new ArrayList<>();
    private String Doc_Id;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    // constructor for our adapter class.
    public SliderAdapter(Context context, List<News> mSliderItems) {
        this.context = context;
        this.mSliderItems = mSliderItems;
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_latest, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, int position) {
        final News sliderItem = mSliderItems.get(position);
        viewHolder.headline.setText(sliderItem.getHeadline());
        viewHolder.likes.setText(sliderItem.getLikesCount()+"");
        viewHolder.comment.setText(sliderItem.getCommentCount()+"");
        viewHolder.category.setText(sliderItem.getCategory());
        viewHolder.viewsCount.setText(sliderItem.getViewsCount()+"");
        viewHolder.stories.setText(sliderItem.getDoc_ID());
        long milisec =sliderItem.getTimestamp().getTime();
        viewHolder.date.setText(getTimeAgo(milisec));
        Picasso.get().load(sliderItem.getNews_image()).fit().into(viewHolder.imageView);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Doc_Id = sliderItem.getDoc_ID();
                if (Doc_Id !=null ){
                    Intent toVendorPref = new Intent(context, ViewNewsActivity.class);
                    toVendorPref.putExtra("doc_ID",Doc_Id);
                    context.startActivity(toVendorPref);
                    viewsCount(Doc_Id);
                }
            }
        });
    }



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
    public int getCount() {
        return mSliderItems.size();
    }

    // view holder class for initializing our view holder.
    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        // variables for our view and image view.
        View itemView;
        ImageView imageView;
        TextView headline,likes,comment,category,viewsCount,stories,date;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            // initializing our views.
            imageView = itemView.findViewById(R.id.news_image);
            headline = itemView.findViewById(R.id.news_headline);
            likes = itemView.findViewById(R.id.latest_view_likes);
            comment = itemView.findViewById(R.id.latest_view_comment);
            category = itemView.findViewById(R.id.latest_view_category);
            viewsCount = itemView.findViewById(R.id.latest_view_views);
            stories = itemView.findViewById(R.id.news_story);
            date = itemView.findViewById(R.id.news_time);
            this.itemView = itemView;
        }
    }



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





}
