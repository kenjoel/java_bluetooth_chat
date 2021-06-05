package com.kenjoel.eighth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kenjoel.RoomImplementation;
import com.kenjoel.dbUtils.LocalDb;
import com.kenjoel.dbUtils.UserInfo;

public class signup extends AppCompatActivity implements View.OnClickListener {
    private EditText mName, mPassword;
    private Button mSignUp;
    private TextView mLogmeIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mName = findViewById(R.id.signupText);
        mPassword = findViewById(R.id.signup_passwoord);
        mSignUp = findViewById(R.id.mSignup);
        mLogmeIn = findViewById(R.id.logmein);

        mSignUp.setOnClickListener(this);
        mLogmeIn.setOnClickListener(this);

    }




    private void getVerification(){
        String name = mName.getText().toString().trim();
        String password = mPassword.getText().toString().trim();

        if(name.isEmpty() || name.length() < 2){
            Toast.makeText(this, "Please enter valid name", Toast.LENGTH_LONG).show();
        }
        else if(password.isEmpty() || password.length() < 3){
            Toast.makeText(this, "You can surely make a better password", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        getVerification();
        if( v == mSignUp){
            String name = mName.getText().toString().trim();
            String password = mPassword.getText().toString().trim();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UserInfo userInfo = new UserInfo();
                    userInfo.setUsername(name);
                    userInfo.setPassword(password);

                    LocalDb localDb = RoomImplementation.getInstance().getDbInstance();
                    localDb.dao().create(userInfo);
                }
            }).start();

            Intent intent = new Intent(signup.this, MainActivity.class);
            startActivity(intent);
        }

        if (v == mLogmeIn){
            Intent intent = new Intent(signup.this, MainActivity.class);
            startActivity(intent);
        }

        }
}
