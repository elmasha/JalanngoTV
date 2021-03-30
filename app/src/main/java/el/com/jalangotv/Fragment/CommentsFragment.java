package el.com.jalangotv.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import el.com.jalangotv.R;


public class CommentsFragment extends Fragment {
View root;


    public CommentsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
   root = inflater.inflate(R.layout.fragment_comments, container, false);

   return root;
    }
}