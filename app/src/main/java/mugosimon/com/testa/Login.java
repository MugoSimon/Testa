package mugosimon.com.testa;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.RuntimeExecutionException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class Login extends AppCompatActivity {

    private static final Pattern EMAIL_REGEX =
            Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\." +
                    "[a-zA-Z0-9_+&*-]+)*@" +
                    "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                    "A-Z]{2,7}$");
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +   //atleast one digit
                    "(?=.*[a-z])" +   //atleast one letter either upercase ama lowercase
                    "(?=\\S+$)" +     //no whitespace in the password
                    "(?=.*[@#&*^+$])" +  // atleast one special characters
                    ".{6,}" +         //minimum of 6 characters
                    "$");
    private TextInputLayout textInputLayout_email, textInputLayout_password;
    private Button button_login;
    private TextView textView_register;
    private ProgressDialog dialog_login;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //intializing layout widgets
        textInputLayout_email = findViewById(R.id.textField_email);
        textInputLayout_password = findViewById(R.id.textField_password);
        button_login = findViewById(R.id.button_login);
        textView_register = findViewById(R.id.textview_login);
        //initializing firebase
        mAuth = FirebaseAuth.getInstance();

        //textview clicked
        textView_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent register_intent = new Intent(getApplicationContext(), MainActivity.class);
                    register_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(register_intent);
                    finish();

                } catch (RuntimeException e) {

                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    if (!validateEmail() | !validatePassword()) {
                        return;
                    } else {

                        String login_email = textInputLayout_email.getEditText().getText().toString().trim();
                        String login_password = textInputLayout_password.getEditText().getText().toString().trim();
                        method_login(login_email, login_password);

                    }
                } catch (RuntimeException er) {

                    Toast.makeText(Login.this, er.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //login method.
    private void method_login(String login_email, String login_password) {
        try {
            mAuth.signInWithEmailAndPassword(login_email,login_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){

                        Intent main_intent = new Intent(Login.this, HomePage.class);
                        main_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(main_intent);
                        finish();
                    }else{

                        return;
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception error) {
            Toast.makeText(Login.this, error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateEmail() {

        String login_email = textInputLayout_email.getEditText().getText().toString().trim();
        if (TextUtils.isEmpty(login_email)) {

            textInputLayout_email.requestFocus();
            textInputLayout_email.setError("field cannot be left empty");
            textInputLayout_email.getEditText().setText(null);
            return false;

        } else if ((!EMAIL_REGEX.matcher(login_email).matches())) {

            textInputLayout_email.requestFocus();
            textInputLayout_email.setError("invalid email");
            textInputLayout_email.getEditText().setText(null);
            return false;

        } else {
            textInputLayout_email.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        String login_password = textInputLayout_password.getEditText().getText().toString().trim();
        if (TextUtils.isEmpty(login_password)) {

            textInputLayout_password.requestFocus();
            textInputLayout_password.setError("field cannot be left empty");
            textInputLayout_password.getEditText().setText(null);
            return false;

        } else if (!PASSWORD_PATTERN.matcher(login_password).matches()) {

            textInputLayout_password.requestFocus();
            textInputLayout_password.getEditText().setText(null);
            textInputLayout_password.setError("password too weak");
            return false;

        } else {

            textInputLayout_password.setError(null);
            return true;
        }
    }

}