package el.com.jalangotv.Activities.ui.profile;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import el.com.jalangotv.Activities.LogInActivity;
import el.com.jalangotv.Activities.RegisterActivity;
import el.com.jalangotv.R;
import el.com.jalangotv.models.JtvUsers;
import el.com.jalangotv.models.News;


public class ProfileFragment extends Fragment {
View root;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference JTvUserRef = db.collection("JtvUsers");
    private TextView profileUsername,profileEmail,logOut;
    private CircleImageView profileImage;
    private FirebaseAuth mAuth;
    private AlertDialog dialog2;
    public ProfileFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mAuth = FirebaseAuth.getInstance();
        root = inflater.inflate(R.layout.fragment_profile, container, false);
        profileUsername = root.findViewById(R.id.Profile_userName);
        profileEmail = root.findViewById(R.id.Profile_email);
        profileImage = root.findViewById(R.id.profileImage);
        logOut = root.findViewById(R.id.logOut);


        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                dialog2 = builder.create();
                dialog2.show();
                builder.setMessage("Are you sure to Log out..\n");
                builder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                Log_out();

                            }
                        });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog2.dismiss();
                    }
                });
                builder.setCancelable(false);
                builder.show();


            }
        });
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), RegisterActivity.class));
            }
        });

        LoadDetails();
    return  root;
    }

    private void Log_out() {
        mAuth.signOut();
        Intent logout = new Intent(getContext(),LogInActivity.class);
        logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(logout);

    }

    private String usename, email, profile;
    void LoadDetails() {

        String uid = mAuth.getCurrentUser().getUid();
            JTvUserRef.document(uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot,
                                    @javax.annotation.Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        return;
                    }
                    if (documentSnapshot.exists()) {
                        JtvUsers users = documentSnapshot.toObject(JtvUsers.class);
                        usename = users.getUserName();
                        email = users.getEmail();
                        profile = users.getProfileImage();
                        if (profile != null) {
                            Picasso.get().load(profile).fit().into(profileImage);
                        }
                        profileUsername.setText(usename);
                        profileEmail.setText(email);

                    } else {

                    }


                }
            });


    }


}