package el.com.jalangotv.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import el.com.jalangotv.R;
import el.com.jalangotv.models.News;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderAdapterVH> {

    private Context context;
    private List<News> mSliderItems = new ArrayList<>();


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
        Picasso.get().load(sliderItem.getNews_image()).fit().into(viewHolder.imageView);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "" + sliderItem.getHeadline(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getCount() {
        return mSliderItems.size();
    }

    // view holder class for initializing our view holder.
    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        // variables for our view and image view.
        View itemView;
        ImageView imageView;
        TextView headline,likes,comment,category,viewsCount;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            // initializing our views.
            imageView = itemView.findViewById(R.id.news_image);
            headline = itemView.findViewById(R.id.news_headline);
            likes = itemView.findViewById(R.id.latest_view_likes);
            comment = itemView.findViewById(R.id.latest_view_comment);
            category = itemView.findViewById(R.id.latest_view_category);
            viewsCount = itemView.findViewById(R.id.latest_view_views);
            this.itemView = itemView;
        }
    }

}
