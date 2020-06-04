package org.techtown.prototypeapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.techtown.prototypeapplication.VO.SharedObject;


public class RoomListFragment extends Fragment {

    SharedObject shared = SharedObject.getInstance();
    private TextView roomListTv;
    private TextView mainTemperature;
    private String result = "";
    private boolean alarmBoolean = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_room_list, container, false);
        mainTemperature = rootView.findViewById(R.id.mainTemperature);
        roomListTv = rootView.findViewById(R.id.mainDate);

        shared.put("result");





//        ImageButton main_alarm = rootView.findViewById(R.id.main_alarm);
//        main_alarm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(alarmBoolean){
//                    main_alarm.setImageResource(R.drawable.icon_alarm_on);
//                }else{
//                    main_alarm.setImageResource(R.drawable.icon_alarm_off);
//                }
//            }
//        });


        ToggleButton toggleAlarm = rootView.findViewById(R.id.toggleAlarm);
        toggleAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toggleAlarm.isChecked()) {
                    toggleAlarm.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_alarm_on));
                    shared.put("AlarmOn");
                } else {
                    toggleAlarm.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_alarm_off));
                    shared.put("AlarmOff");
                }
            }
        });

//        Thread t= new Thread(()->{
//
//            result = getArguments().getString("result");
//        });
//        t.start();
//        roomListTv.setText(result);

        // Inflate the layout for this fragment
        return rootView;
    }
    private Bundle bundle;
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    @Override
    public void onResume() {
        super.onResume();

            if(getArguments()!=null) {
                String result = getArguments().getString("result");
                Log.i("onResume",result);
                mainTemperature.setText(result);
            }

    }

    public TextView getRoomListTv() {
        return this.roomListTv;
    }


}
