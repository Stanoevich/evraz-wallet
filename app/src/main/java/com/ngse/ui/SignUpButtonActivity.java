package com.ngse.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;



import org.evrazcoin.evrazwallet.R;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SignUpButtonActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.new_activity_sign_up_button);
        findViewById(R.id.sign_up_button).setOnClickListener(v -> {
            Intent intent = new Intent(SignUpButtonActivity.this, SignUpInfoActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.buttonLogin).setOnClickListener(v -> {
            Intent intent = new Intent(SignUpButtonActivity.this, ImportActivty.class);
            intent.putExtra("model", ImportActivty.ACCOUNT_MODEL);
            startActivity(intent);
     /*       Intent intent = new Intent(SignUpButtonActivity.this, ModelSelectActivity.class);
            startActivity(intent);*/
        });
    }
}
