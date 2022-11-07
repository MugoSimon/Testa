package mugosimon.com.testa;

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

public class MainActivity extends AppCompatActivity {

    //static variables
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +   //atleast one digit
                    "(?=.*[a-z])" +   //atleast one letter either upercase ama lowercase
                    "(?=\\S+$)" +     //no whitespace in the password
                    "(?=.*[@#&*^+$])" +  // atleast one special characters
                    ".{6,}" +         //minimum of 6 characters
                    "$");
    private static final Pattern EMAIL_REGEX =
            Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\." +
                    "[a-zA-Z0-9_+&*-]+)*@" +
                    "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                    "A-Z]{2,7}$");
    //variables layouts from the widgets
    private Button button_submit,button_google;
    private TextInputLayout textInputLayout_name, textInputLayout_email, textInputLayout_password;
    private TextView textview_login;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initializing the widgets
        button_submit = findViewById(R.id.button_submit);
        button_google = findViewById(R.id.button_google);
        textInputLayout_name = findViewById(R.id.textField_name);
        textInputLayout_email = findViewById(R.id.textField_email);
        textInputLayout_password = findViewById(R.id.textField_password);
        textview_login = findViewById(R.id.textview_login);

        //gso calls from google to be granted access to view google accounts.
        //its an easy one.

        //textview clicked
        textview_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    Intent move_login = new Intent(getApplicationContext(), Login.class);
                    move_login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(move_login);
                    finish();

                }catch (RuntimeException e){

                    Toast.makeText(MainActivity.this, e.getMessage().toString(), Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });

        //initialzing Firebase
        mAuth = FirebaseAuth.getInstance();

        //button when clicked
        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    if (!validateUsername() | !validateEmail() | !validatePassword()) {
                        return;

                    } else {
                        Toast.makeText(MainActivity.this, "Things are alright", Toast.LENGTH_LONG).show();
                        //register the person using email and password.
                        String password = textInputLayout_password.getEditText().getText().toString().trim();
                        String email = textInputLayout_email.getEditText().getText().toString().trim();
                        registerUser(email, password);
                    }

                } catch (RuntimeException run_err) {

                    Toast.makeText(MainActivity.this, run_err.getMessage().toString(), Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });
    }

    //method register that person
    private void registerUser(String email, String password) {
        try {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    try {

                        if (task.isSuccessful()){

                            Intent next_intent = new Intent(MainActivity.this, HomePage.class);
                            next_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(next_intent);
                            finish();

                        }else{

                            Toast.makeText(MainActivity.this, "Error in registering new user", Toast.LENGTH_LONG).show();
                            return;
                        }

                    } catch (final RuntimeException e) {
                        Toast.makeText(MainActivity.this, e.getMessage().toString(), Toast.LENGTH_LONG).show();
                        return;
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();

                }
            });

        } catch (RuntimeException erro) {
            Toast.makeText(MainActivity.this, erro.getMessage().toString(), Toast.LENGTH_LONG).show();
            return;
        }

    }

    private boolean validatePassword() {
        String password = textInputLayout_password.getEditText().getText().toString().trim();
        if (TextUtils.isEmpty(password)) {

            textInputLayout_password.requestFocus();
            textInputLayout_password.setError("field cannot be left empty");
            textInputLayout_password.getEditText().setText(null);
            return false;

        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {

            textInputLayout_password.requestFocus();
            textInputLayout_password.getEditText().setText(null);
            textInputLayout_password.setError("password too weak");
            return false;

        } else {

            textInputLayout_password.setError(null);
            return true;
        }
    }

    private boolean validateEmail() {
        String email = textInputLayout_email.getEditText().getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            textInputLayout_email.requestFocus();
            textInputLayout_email.setError("field cannot be left empty");
            textInputLayout_email.getEditText().setText(null);
            return false;

        } else if ((!EMAIL_REGEX.matcher(email).matches())) {
            textInputLayout_email.requestFocus();
            textInputLayout_email.setError("invalid email");
            textInputLayout_email.getEditText().setText(null);
            return false;
        } else {

            //textInputLayout_email.getEditText().setText(null);
            return true;
        }
    }

    private boolean validateUsername() {
        String name = textInputLayout_name.getEditText().getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            textInputLayout_name.requestFocus();
            textInputLayout_name.setError("field cannot be left empty");
            textInputLayout_name.getEditText().setText(null);
            return false;

        } else if (name.length() > 15) {
            textInputLayout_name.requestFocus();
            textInputLayout_name.setError("name is too long");
            textInputLayout_name.getEditText().setText(null);
            return false;

        } else {
            textInputLayout_name.setError(null);
            return true;

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {

            FirebaseUser currrentUser = mAuth.getCurrentUser();
            if (currrentUser == null) {
                sendToStart();
            }

        }catch(RuntimeExecutionException e){
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void sendToStart() {
        Intent reverse_intent = new Intent(this, Login.class);
        reverse_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(reverse_intent);
        finish();
    }
}