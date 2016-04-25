package mindprobe.mindprobe;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {

    private EditText age;
    private EditText name;
    private EditText email;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        age = (EditText) findViewById(R.id.regage);
        name = (EditText) findViewById(R.id.regname);
        email = (EditText) findViewById(R.id.regemail);
        password = (EditText) findViewById(R.id.regpassword);

        Button button_register = (Button) findViewById(R.id.regbutton);
        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
    }

    private void register(){
        String email1 = email.getText().toString();
        String password1 = password.getText().toString();
        String age1 = age.getText().toString();
        String name1 = name.getText().toString();

        // Reset errors.
        email.setError(null);
        password.setError(null);
        name.setError(null);
        age.setError(null);

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(email1)) {
            email.setError(getString(R.string.error_field_required));
            focusView = email;
            cancel = true;
        } else if (!isEmailValid(email1)) {
            email.setError(getString(R.string.error_invalid_email));
            focusView = email;
            cancel = true;
        }else if (TextUtils.isEmpty(password1) || !isPasswordValid(password1)){
            password.setError(getString(R.string.error_invalid_password));
            focusView = password;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            Intent Login_Intent = new Intent(RegisterActivity.this,LoginActivity.class);
            RegisterActivity.this.startActivity(Login_Intent);
        }
    }


    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }
}
