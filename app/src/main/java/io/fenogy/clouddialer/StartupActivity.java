package io.fenogy.clouddialer;

import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class StartupActivity extends AppCompatActivity {

    ImageButton mAdminButton;
    ImageButton mAgentButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        mAdminButton = (ImageButton)findViewById(R.id.imageButtonAdmin);
        mAgentButton = (ImageButton)findViewById(R.id.imageButtonAgent);

        mAdminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartupActivity.this, AdminLogin.class));
            }
        });

        mAgentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartupActivity.this, AgentLogin.class));
            }
        });
    }
}
