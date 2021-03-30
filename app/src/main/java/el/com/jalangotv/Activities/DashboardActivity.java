package el.com.jalangotv.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import el.com.jalangotv.Activities.ui.categories.SavedNewsFragment;
import el.com.jalangotv.Activities.ui.home.HomeFragment;
import el.com.jalangotv.Activities.ui.profile.ProfileFragment;
import el.com.jalangotv.Activities.ui.search.SearchFragment;
import el.com.jalangotv.Adapters.CategoryAdapter;
import el.com.jalangotv.Fragment.ViewCategoryFragment;
import el.com.jalangotv.R;
import el.com.jalangotv.models.Category;

public class DashboardActivity extends AppCompatActivity {
    //----Initiate Bottom navigation----
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 7000;
    private long backPressedTime;
    private long CallPressedTime;
    private Toast backToast;
    private LinearLayout logoLayout,layoutCategory;
    private String out;
    public CategoryAdapter adapter;
    private RecyclerView CategoryRecyclerView;
    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {Manifest.permission.READ_CONTACTS,Manifest.permission.CALL_PHONE,Manifest.permission.READ_EXTERNAL_STORAGE};

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference categoryRef = db.collection("Category");
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment SelectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.navigation_home:
                            layoutCategory.setVisibility(View.VISIBLE);
                            SelectedFragment = new HomeFragment();

                            break;
                        case R.id.navigation_search:
                            layoutCategory.setVisibility(View.GONE);
                            SelectedFragment = new SearchFragment();

                            break;
                        case R.id.navigation_category:
                            layoutCategory.setVisibility(View.GONE);
                            SelectedFragment = new SavedNewsFragment();

                            break;
                        case R.id.navigation_profile:
                            layoutCategory.setVisibility(View.GONE);
                            SelectedFragment = new ProfileFragment();

                            break;

                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,
                            SelectedFragment).commit();


                    return true;
                }
            };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,new
                HomeFragment()).commit(); if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        logoLayout = findViewById(R.id.layout_logo);
        CategoryRecyclerView = findViewById(R.id.Recyclerview_category);
        layoutCategory= findViewById(R.id.layout_category);
        logoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(), AddNewsActivity.class));

            }
        });
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {


                    return false;
                }

            }
        }
        return true;
    }


    //----Fetch Product--
    private String fetchAroundID;
    private void FetchCategory() {

//        String UID = mAuth.getCurrentUser().getUid();
        Query query =
                categoryRef.orderBy("timestamp", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Category> transaction = new FirestoreRecyclerOptions.Builder<Category>()
                .setQuery(categoryRef, Category.class)
                .setLifecycleOwner(this)
                .build();
        adapter = new CategoryAdapter(transaction);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        CategoryRecyclerView.setHasFixedSize(true);
        CategoryRecyclerView.setLayoutManager(layoutManager);
        CategoryRecyclerView.setNestedScrollingEnabled(false);
        CategoryRecyclerView.setAdapter(adapter);


        adapter.setOnItemClickListener(new CategoryAdapter.OnItemCickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Category category = documentSnapshot.toObject(Category.class);

                Bundle bundle = new Bundle();
                bundle.putString("message", category.getTitle() );
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new
                        ViewCategoryFragment()).commit();
                out = category.getTitle();
                Intent intent = getIntent();
                ViewCategoryFragment fragInfo = new ViewCategoryFragment();
                fragInfo.setArguments(bundle);

            }
        });





    }
    //...end fetch..
    public Bundle getMyData() {
        Bundle hm = new Bundle();
        hm.putString("val1",out);
        return hm;
    }

    @Override
    protected void onStart() {
        super.onStart();
        FetchCategory();
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2500 > System.currentTimeMillis()) {
            backToast.cancel();
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
            super.onBackPressed();
            finish();
            return;
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,new
                    HomeFragment()).commit();
            backToast = Toast.makeText(getBaseContext(), "Double tap to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }

        backPressedTime = System.currentTimeMillis();
    }

}