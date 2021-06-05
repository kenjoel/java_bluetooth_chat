package com.kenjoel.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.kenjoel.Detail_Fragment;
import com.kenjoel.eighth.R;
import com.kenjoel.interfaces.Display_Interface;

public class HomeStuff extends AppCompatActivity  implements Display_Interface {
    private FrameLayout fragment, fragment2;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_stuff);

        fragment = findViewById(R.id.fragment_container_view_tag);
        fragment2 = findViewById(R.id.fragment_detail);

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        DemoFragment demoFragment = new DemoFragment();

        fragmentTransaction.add(fragment.getId(), demoFragment).commit();
    }

    @Override
    public void onVersionItemClicked(String s) {
        Toast.makeText(this, "My ladies" + s, Toast.LENGTH_LONG).show();

        Bundle bundle = new Bundle();
        bundle.putString("first_name", s);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Detail_Fragment detail_fragment = new Detail_Fragment();
        detail_fragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment_detail, detail_fragment);
        fragmentTransaction.commit();
    }
}