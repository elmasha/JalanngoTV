package el.com.jalangotv.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;
import el.com.jalangotv.models.Comments;
import el.com.jalangotv.R;

public class CommentsAdapter extends FirestoreRecyclerAdapter<Comments, CommentsAdapter.CommentViewHolder> {

    private OnItemCickListener listener;
    public Context context;


    public CommentsAdapter(@NonNull FirestoreRecyclerOptions<Comments>options) {
        super(options);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onBindViewHolder(@NonNull CommentViewHolder holder, int position, @NonNull Comments model) {

        if (model.getUserName()!= null){
            holder.userName.setText(model.getUserName());
        }

        holder.comment.setText(model.getComment());
        //Picasso.get().load(model.getUserImage()).fit().into(holder.userImage);


        if (model.getTimestamp()!=null) {
            long milisecond = model.getTimestamp().getTime();
            String date = DateFormat.format("EEE-MMM-yyyy hh:mm", new Date(milisecond)).toString();
            holder.date.setText(date);
        }





    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_comment,parent,false);

        return new CommentViewHolder(v);
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
    }

    ///Delete item
    public void deleteItem (int position) {
     getSnapshots().getSnapshot(position).getReference().delete();
    }

    class CommentViewHolder extends RecyclerView.ViewHolder{
       private TextView userName,comment,date;
       private CircleImageView userImage;
       private RelativeLayout ord_layout;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.username);
            comment = itemView.findViewById(R.id.username_comment);
            userImage  = itemView.findViewById(R.id.profile_comment);
            date = itemView.findViewById(R.id.comment_date);

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


    public static String getTimeAgo(long timestamp) {

        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();//get your local time zone.
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        sdf.setTimeZone(tz);//set time zone.
        String localTime = sdf.format(new Date(timestamp * 1000));
        Date date = new Date();
        try {
            date = sdf.parse(localTime);//get local date
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(date == null) {
            return null;
        }

        long time = date.getTime();

        Date curDate = currentDate();
        long now = curDate.getTime();
        if (time > now || time <= 0) {
            return null;
        }

        int timeDIM = getTimeDistanceInMinutes(time);

        String timeAgo = null;

        if (timeDIM == 0) {
            timeAgo = "less than a minute";
        } else if (timeDIM == 1) {
            return "1 minute";
        } else if (timeDIM >= 2 && timeDIM <= 44) {
            timeAgo = timeDIM + " minutes";
        } else if (timeDIM >= 45 && timeDIM <= 89) {
            timeAgo = "about an hour";
        } else if (timeDIM >= 90 && timeDIM <= 1439) {
            timeAgo = "about " + (Math.round(timeDIM / 60)) + " hours";
        } else if (timeDIM >= 1440 && timeDIM <= 2519) {
            timeAgo = "1 day";
        } else if (timeDIM >= 2520 && timeDIM <= 43199) {
            timeAgo = (Math.round(timeDIM / 1440)) + " days";
        } else if (timeDIM >= 43200 && timeDIM <= 86399) {
            timeAgo = "about a month";
        } else if (timeDIM >= 86400 && timeDIM <= 525599) {
            timeAgo = (Math.round(timeDIM / 43200)) + " months";
        } else if (timeDIM >= 525600 && timeDIM <= 655199) {
            timeAgo = "about a year";
        } else if (timeDIM >= 655200 && timeDIM <= 914399) {
            timeAgo = "over a year";
        } else if (timeDIM >= 914400 && timeDIM <= 1051199) {
            timeAgo = "almost 2 years";
        } else {
            timeAgo = "about " + (Math.round(timeDIM / 525600)) + " years";
        }
        return timeAgo + " ago";
    }

    public static Date currentDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    private static int getTimeDistanceInMinutes(long time) {
        long timeDistance = currentDate().getTime() - time;
        return Math.round((Math.abs(timeDistance) / 1000) / 60);
    }
}
