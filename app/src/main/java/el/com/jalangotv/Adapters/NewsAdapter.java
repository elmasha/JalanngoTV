package el.com.jalangotv.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import android.icu.text.DecimalFormat;
import android.os.Build;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;


import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import el.com.jalangotv.R;
import el.com.jalangotv.models.News;

public class NewsAdapter extends FirestoreRecyclerAdapter<News, NewsAdapter.NewsViewHolder> {

    private OnItemCickListener listener;
    public Context context;


    public NewsAdapter(@NonNull FirestoreRecyclerOptions<News>options) {
        super(options);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onBindViewHolder(@NonNull NewsViewHolder holder, int position, @NonNull News model) {
        holder.headline.setText(model.getHeadline());
        holder.story.setText(model.getStory());
        holder.category.setText(model.getCategory());

        if(model.getViewsCount() >= 1000){
            DecimalFormat precision = new DecimalFormat("0.00");
            double div = model.getViewsCount() /1000;
            holder.viewCount.setText(precision.format(div)+"K ");

        }else if(model.getLikesCount() >= 1000)
        {    double divlike = model.getLikesCount() /1000;
            DecimalFormat precision = new DecimalFormat("0.00");
            holder.likes.setText(precision.format(divlike)+"K ");

        }else if (model.getCommentCount() >=1000){
            double divcomment = model.getCommentCount() /1000;
            DecimalFormat precision = new DecimalFormat("0.00");
            holder.comment.setText(precision.format(divcomment)+"K ");

        }else {
            holder.viewCount.setText(model.getViewsCount()+"");
            holder.comment.setText(model.getCommentCount()+"");
            holder.likes.setText(model.getLikesCount()+"");
        }
        Picasso.get().load(model.getNews_image()).fit().into(holder.homeNewsImage);

        long milisecond = model.getTimestamp().getTime();
        String date = DateFormat.format("dd-MMM-yyyy | hh:mm a",new Date(milisecond)).toString();

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);

        holder.date.setText(getTimeAgo(milisecond)+"");

//        Date datemilisec = DateTimeUtils.formatDate(milisecond, DateTimeUnits.SECONDS);
//        String timeAgo = DateTimeUtils.getTimeAgo(context,datemilisec, DateTimeStyle.AGO_SHORT_STRING );



    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_newshome,parent,false);

        return new NewsViewHolder(v);
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
    }

    ///Delete item
    public void deleteItem (int position) {
     getSnapshots().getSnapshot(position).getReference().delete();
    }

    class NewsViewHolder extends RecyclerView.ViewHolder{
       private TextView headline,story,date,likes,shares,comment,category,viewCount;
       private ImageView homeNewsImage;
       private RelativeLayout ord_layout;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);

            headline = itemView.findViewById(R.id.home_headline);
            story = itemView.findViewById(R.id.home_story);
            viewCount = itemView.findViewById(R.id.views_count_news);
            category = itemView.findViewById(R.id.home_category);
            homeNewsImage  = itemView.findViewById(R.id.home_image);
            likes = itemView.findViewById(R.id.likes_count_news);
            comment = itemView.findViewById(R.id.comment_counts);
            date = itemView.findViewById(R.id.news_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION && listener != null){
                        listener.onItemClick(getSnapshots().getSnapshot(position),position);
                    }
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    public interface  OnItemCickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemCickListener listener){
        this.listener = listener;
    }


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
