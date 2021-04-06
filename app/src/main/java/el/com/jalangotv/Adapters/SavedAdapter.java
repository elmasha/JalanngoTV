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

//        long milisecond = model.getTimestamp().getTime();
//        String date = DateFormat.format("dd-MMM-yyyy | hh:mm a",new Date(milisecond)).toString();
//
//        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
//        holder.orderTime.setText(date);

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
       private TextView headline,story,likes,views,comment;
       private ImageView homeNewsImage;
       private RelativeLayout ord_layout;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);

            headline = itemView.findViewById(R.id.saved_headline);
            story = itemView.findViewById(R.id.saved_story);
            homeNewsImage  = itemView.findViewById(R.id.saved_image);
            likes = itemView.findViewById(R.id.save_likes);
            views = itemView.findViewById(R.id.save_views);
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

}
