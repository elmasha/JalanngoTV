package el.com.jalangotv.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.text.format.DateFormat;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import el.com.jalangotv.R;
import el.com.jalangotv.models.News;

public class LatestNewsAdapter extends FirestoreRecyclerAdapter<News, LatestNewsAdapter.LatestNewsViewHolder> {

    private OnItemCickListener listener;
    public Context context;


    public LatestNewsAdapter(@NonNull FirestoreRecyclerOptions<News>options) {
        super(options);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onBindViewHolder(@NonNull LatestNewsViewHolder holder, int position, @NonNull News model) {
        holder.Headline.setText(model.getHeadline());
        holder.Story.setText(model.getStory());
        holder.category.setText(model.getCategory());

        Picasso.get().load(model.getNews_image()).fit().into(holder.NewsImage);


//            long milisecond = model.getTimestamp().getTime();
//        if (milisecond == 0){
//
//        }
//        String date = DateFormat.format("dd-MMM-yyyy | hh:mm",new Date(milisecond)).toString();
//
//        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
//        holder.date.setText(date);






    }

    @NonNull
    @Override
    public LatestNewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_latest,parent,false);

        return new LatestNewsViewHolder(v);
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
    }

    ///Delete item
    public void deleteItem (int position) {
     getSnapshots().getSnapshot(position).getReference().delete();
    }

    class LatestNewsViewHolder extends RecyclerView.ViewHolder{
       private TextView Headline,Story,date,category;
       private ImageView NewsImage;
       private RelativeLayout ord_layout;

        public LatestNewsViewHolder(@NonNull View itemView) {
            super(itemView);

            Headline = itemView.findViewById(R.id.news_headline);
            Story = itemView.findViewById(R.id.news_story);
            date  = itemView.findViewById(R.id.new_date);
            NewsImage = itemView.findViewById(R.id.news_image);
            category = itemView.findViewById(R.id.news_cat);







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
