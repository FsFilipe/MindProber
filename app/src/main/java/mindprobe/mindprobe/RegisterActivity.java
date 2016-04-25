package mindprobe.mindprobe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText birthdate = (EditText) findViewById(R.id.regbirthdate);
        final EditText name = (EditText) findViewById(R.id.regname);
        final EditText e_mail = (EditText) findViewById(R.id.regemail);
        final EditText password = (EditText) findViewById(R.id.regpassword);
        final Button button_register = (Button) findViewById(R.id.regbutton);



    }
}
