package com.kenjoel.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kenjoel.eighth.R;
import com.kenjoel.interfaces.Display_Interface;


public class DemoFragment extends Fragment {

    private static final String TAG ="Bohemian" ;
    Display_Interface display_interface;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);


        try{
            display_interface = (Display_Interface) context;

        }catch (ClassCastException e){
            Log.d(TAG, "onAttach: " + e.getMessage());

        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_layout, container, false);

        ListView listView = view.findViewById(R.id.list_view);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String str = ((TextView)view).getText().toString();
                display_interface.onVersionItemClicked(str);

            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.string_array));
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
