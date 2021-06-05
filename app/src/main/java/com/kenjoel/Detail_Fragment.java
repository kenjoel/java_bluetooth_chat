package com.kenjoel;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.kenjoel.eighth.R;
import com.kenjoel.ui.MainChatActivity;

public class Detail_Fragment extends Fragment {
    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.right_fragment, container, false);
        assert container != null;
        context = container.getContext();

       TextView hName = view.findViewById(R.id.babe_first_name);
       Button mButton = view.findViewById(R.id.mButton);

       mButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
               builder.setSmallIcon(R.drawable.ic_launcher_background);
               builder.setContentTitle("Hello World");
               builder.setContentText("Hello world is a phrase so common that every computer scientist at  some point has had to use ir");

               NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

               notificationManager.notify(0, builder.build());

               Intent intent = new Intent(getActivity(), MainChatActivity.class);
               startActivity(intent);
           }
       });

       if(getArguments() != null && getArguments().getString("first_name") != null){
           String bundle = getArguments().getString("first_name");
           hName.setText(bundle);
       }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
