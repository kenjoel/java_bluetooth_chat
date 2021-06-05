    package com.kenjoel.eighth;

import androidx.appcompat.app.AppCompatActivity;

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
import com.kenjoel.ui.HomeStuff;

    public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mLoginName, mLoginPassword;
    private TextView mText;
    private  Button mLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoginName = findViewById(R.id.mUser);
        mLoginPassword = findViewById(R.id.mPassword);
        mLoginButton = findViewById(R.id.mLogin);
        mText = findViewById(R.id.go2signup);

        mLoginButton.setOnClickListener(this);
        mText.setOnClickListener(this);

    }


    private void getVerification(){
        String name = mLoginName.getText().toString().trim();
        String password = mLoginPassword.getText().toString().trim();

        if(name.isEmpty() || name.length() < 2){
            Toast.makeText(this, "Please enter valid name", Toast.LENGTH_LONG).show();
        }
        else if(password.isEmpty() || password.length() < 3){
            Toast.makeText(this, "You can surely make a better password", Toast.LENGTH_LONG).show();
        }
    }

        @Override
        public void onClick(View v) {
        String name = mLoginName.getText().toString().trim();
        String password = mLoginPassword.getText().toString().trim();

        if (v == mLoginButton){
            getVerification();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    LocalDb localDb = RoomImplementation.getInstance().getDbInstance();
                    UserInfo userInfo = localDb.dao().getUserInfo(name);

                    if (userInfo != null && userInfo.getUsername() != null && userInfo.getUsername().equalsIgnoreCase(name)){
                        if (userInfo.getPassword() != null && userInfo.getPassword().equalsIgnoreCase(password)){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "Hey Welcome to the App", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(MainActivity.this, HomeStuff.class);
                                    startActivity(intent);
                                }
                            });
                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "Hey Wrong credentials", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "You missed something", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }).start();
        }
        if( v == mText){
            Intent intent = new Intent(MainActivity.this, signup.class);
            startActivity(intent);
        }

        }
    }