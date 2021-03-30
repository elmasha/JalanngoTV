package el.com.jalangotv;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;

import el.com.jalangotv.Activities.DashboardActivity;

public class MainActivity extends AppCompatActivity {

    private ImageView iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv = findViewById(R.id.iv);
        Animation myanim = AnimationUtils.loadAnimation(this, R.anim.myanim);

        iv.startAnimation(myanim);
        final Intent i = new Intent(this, DashboardActivity.class);
        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(2600);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {

                        startActivity(new Intent(MainActivity.this, DashboardActivity.class));

                }
            }


        };

        timer.start();
    }
}