package el.com.jalangotv.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.icu.text.DecimalFormat;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import el.com.jalangotv.R;
import el.com.jalangotv.models.News;

public class SavedAdapter extends FirestoreRecyclerAdapter<News, SavedAdapter.NewsViewHolder> {

    private OnItemCickListener listener;
    public Context context;


    public SavedAdapter(@NonNull FirestoreRecyclerOptions<News>options) {
        super(options);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onBindViewHolder(@NonNull NewsViewHolder holder, int position, @NonNull News model) {
        holder.headline.setText(model.getHeadline());
        holder.story.setText(model.getStory());
        Picasso.get().load(model.getNews_image()).fit().into(holder.homeNewsImage);

       long milisecond = model.getTimestamp().getTime();
//        String date = DateFormat.format("dd-MMM-yyyy | hh:mm a",new Date(milisecond)).toString();
//
//        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
        holder.date.setText(getTimeAgo(milisecond)+"");

        if(model.getViewsCount() >= 1000){
            double div = model.getViewsCount() /1000;
            DecimalFormat precision = new DecimalFormat("0.0");
            holder.views.setText(precision.format(div)+"K ");
        }else if(model.getLikesCount() >= 1000) {
            double divlike = model.getLikesCount() /1000;
            DecimalFormat precision = new DecimalFormat("0.0");
            holder.likes.setText(precision.format(divlike)+"K ");
        }else if (model.getCommentCount() >=1000){
            double divcomment = model.getCommentCount() /1000;
            DecimalFormat precision = new DecimalFormat("0.0");
            holder.comment.setText(precision.format(divcomment)+"K ");
        }else {
            holder.views.setText(model.getViewsCount()+"");
            holder.comment.setText(model.getCommentCount()+"");
            holder.likes.setText(model.getLikesCount()+"");
        }

    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_saved,parent,false);

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
       private TextView headline,story,likes,views,comment,date;
       private ImageView homeNewsImage;
       private RelativeLayout ord_layout;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);

            headline = itemView.findViewById(R.id.saved_headline);
            story = itemView.findViewById(R.id.saved_story);
            homeNewsImage  = itemView.findViewById(R.id.saved_image);
            likes = itemView.findViewById(R.id.save_likes);
            views = itemView.findViewById(R.id.save_views);
            date = itemView.findViewById(R.id.saved_date);
            comment = itemView.findViewById(R.id.save_comment);




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



    public static String getlongtoago(long createdAt) {
        DateFormat userDateFormat = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
        DateFormat dateFormatNeeded = new SimpleDateFormat("MM/dd/yyyy HH:MM:SS");
        Date date = null;
        date = new Date(createdAt);
        String crdate1 = dateFormatNeeded.format(date);

        // Date Calculation
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        crdate1 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(date);

        // get current date time with Calendar()
        Calendar cal = Calendar.getInstance();
        String currenttime = dateFormat.format(cal.getTime());

        Date CreatedAt = null;
        Date current = null;
        try {
            CreatedAt = dateFormat.parse(crdate1);
            current = dateFormat.parse(currenttime);
        } catch (java.text.ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Get msec from each, and subtract.
        long diff = current.getTime() - CreatedAt.getTime();
        long diffSeconds = diff / 1000;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);

        String time = null;
        if (diffDays > 0) {
            if (diffDays == 1) {
                time = diffDays + " day ago ";
            }else if  (diffDays >= 7) {

                time = diffDays + "week ago ";
            }else {
                time = diffDays + " days ago ";
            }
        } else {
            if (diffHours > 0) {
                if (diffHours == 1) {
                    time = diffHours + " hr ago";
                } else {
                    time = diffHours + " hrs ago";
                }
            } else {
                if (diffMinutes > 0) {
                    if (diffMinutes == 1) {
                        time = diffMinutes + " min ago";
                    } else {
                        time = diffMinutes + " mins ago";
                    }
                } else {
                    if (diffSeconds > 0) {
                        time = diffSeconds + " secs ago";
                    }
                }

            }

        }
        return time;
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
