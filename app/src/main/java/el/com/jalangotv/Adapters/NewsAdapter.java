package el.com.jalangotv.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;


import com.google.firebase.firestore.DocumentSnapshot;
import com.snov.timeagolibrary.PrettyTimeAgo;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import el.com.jalangotv.R;
import el.com.jalangotv.TimeAgo2;
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
        holder.likes.setText(model.getLikesCount()+"");
        holder.category.setText(model.getCategory());
        holder.viewCount.setText(model.getViewsCount()+"");

        Picasso.get().load(model.getNews_image()).fit().into(holder.homeNewsImage);

        long milisecond = model.getTimestamp().getTime();
        String date = DateFormat.format("dd-MMM-yyyy | hh:mm a",new Date(milisecond)).toString();

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);


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


    public String covertTimeToText(String dataDate) {

        String convTime = null;

        String prefix = "";
        String suffix = "Ago";

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date pasTime = dateFormat.parse(dataDate);

            Date nowTime = new Date();

            long dateDiff = nowTime.getTime() - pasTime.getTime();

            long second = TimeUnit.MILLISECONDS.toSeconds(dateDiff);
            long minute = TimeUnit.MILLISECONDS.toMinutes(dateDiff);
            long hour   = TimeUnit.MILLISECONDS.toHours(dateDiff);
            long day  = TimeUnit.MILLISECONDS.toDays(dateDiff);

            if (second < 60) {
                convTime = second + " Seconds " + suffix;
            } else if (minute < 60) {
                convTime = minute + " Minutes "+suffix;
            } else if (hour < 24) {
                convTime = hour + " Hours "+suffix;
            } else if (day >= 7) {
                if (day > 360) {
                    convTime = (day / 360) + " Years " + suffix;
                } else if (day > 30) {
                    convTime = (day / 30) + " Months " + suffix;
                } else {
                    convTime = (day / 7) + " Week " + suffix;
                }
            } else if (day < 7) {
                convTime = day+" Days "+suffix;
            }

        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("ConvTimeE", e.getMessage());
        }

        return convTime;
    }


}
