package el.com.jalangotv;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import el.com.jalangotv.Activities.DashboardActivity;
import el.com.jalangotv.Activities.LogInActivity;

public class MainActivity extends AppCompatActivity {

    private ImageView iv;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        iv = findViewById(R.id.iv);
        Animation myanim = AnimationUtils.loadAnimation(this, R.anim.myanim);

        iv.startAnimation(myanim);
        final Intent i = new Intent(this, DashboardActivity.class);
        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {

                    FirebaseUser current_User = mAuth.getCurrentUser();
                    String UiD = mAuth.getUid();

                    if (current_User != null)
                    {
                        if (UiD != null){
                            startActivity(new Intent(getApplicationContext(),DashboardActivity.class));
                        }

                    }else {
                        startActivity(new Intent(MainActivity.this, LogInActivity.class));
                    }
                }
            }


        };

        timer.start();
    }
}