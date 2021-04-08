package el.com.jalangotv.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import el.com.jalangotv.R;

public class LogInActivity extends AppCompatActivity {
        private FirebaseAuth mAuth;
        private TextInputLayout email,password;
        private Button btn_login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        mAuth = FirebaseAuth.getInstance();
      email = findViewById(R.id.input_LogInEmail);
      password = findViewById(R.id.input_LogInPassword);

    btn_login = findViewById(R.id.BtnLogIn);


    btn_login.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!Validation()){

            }else {
                LogIn();
            }
        }
    });



    }

    private void LogIn() {

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("please wait...");
        progressDialog.show();
        String em = email.getEditText().getText().toString();
        String pass = password.getEditText().getText().toString();

        mAuth.signInWithEmailAndPassword(em,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){
                    progressDialog.dismiss();
                    startActivity(new Intent(getApplicationContext(),DashboardActivity.class));
                }else {
                    progressDialog.dismiss();
                    Toast.makeText(LogInActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private boolean Validation() {
    String em = email.getEditText().getText().toString();
    String pass = password.getEditText().getText().toString();

    if (em.isEmpty()){
        email.setError("empty required");
        return false;
    }else if (pass.isEmpty()){
        password.setError("Password required");
        return false;
    } else {

        return true;
    }

    }
}