package el.com.jalangotv.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;
import java.util.List;

import el.com.jalangotv.R;
import el.com.jalangotv.models.Category;

public class CategoryAdapter extends FirestoreRecyclerAdapter<Category, CategoryAdapter.CategoryViewHolder> {

    private OnItemCickListener listener;
    public Context context;
    private int state =0;



    public CategoryAdapter(@NonNull FirestoreRecyclerOptions<Category> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CategoryViewHolder holder, int position, @NonNull Category model) {
        holder.Title.setText(model.getTitle());


    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_category,parent,false);
        this.context = parent.getContext();
        return new CategoryViewHolder(v);
    }


    ///Delete item
    public void deleteItem (int position) {
     getSnapshots().getSnapshot(position).getReference().delete();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder{
       private TextView Title, desc, time;
       private View onView;
       private View view;


        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            Title = itemView.findViewById(R.id.Title);
            onView = itemView.findViewById(R.id.onView);
            Title.setTextColor(ContextCompat.getColor(context,R.color.green));
            onView.setVisibility(View.GONE);
            Title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    state =1;
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null){
                        listener.onItemClick(getSnapshots().getSnapshot(position),position);
                        if (state == 1){
                            state=2;
                            Title.setTextColor(ContextCompat.getColor(context,R.color.orange));
                            onView.setVisibility(View.VISIBLE);

                        }else if (state ==2 ){
                            Title.setTextColor(ContextCompat.getColor(context,R.color.green));
                            onView.setVisibility(View.GONE);
                        }
                        else if (state ==0){
                            Title.setTextColor(ContextCompat.getColor(context,R.color.green));
                            onView.setVisibility(View.GONE);
                        }


                    }
                }
            });



        }
    }


    public interface  OnItemCickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemCickListener listener){

        this.listener = listener;

    }

}
