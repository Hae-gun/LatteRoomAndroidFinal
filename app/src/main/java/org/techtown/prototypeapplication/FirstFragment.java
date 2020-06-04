package org.techtown.prototypeapplication;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.techtown.prototypeapplication.VO.SharedObject;
import org.techtown.prototypeapplication.VO.UserData;
import org.techtown.prototypeapplication.NetworkConnectionService.MyBinder;
import java.io.BufferedReader;
import java.io.PrintWriter;


public class FirstFragment extends Fragment {

    //    public static FirstFragment newInstance(){
//        return new FirstFragment();
//    }
    EditText email;
    EditText password;
    Button logIn;
    BufferedReader br;
    PrintWriter pr;
    SharedObject shared = SharedObject.getInstance();
    MainFragment mfrag;
    private NetworkConnectionService ms;
    private Boolean isService;

//    public void setMb(MyBinder mb) {
//        this.mb = mb;
//        ms=mb.getService();
//    }

    private MyBinder mb;
    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").create();
    public FirstFragment(MainFragment mfrag) {

        this.mfrag = mfrag;

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_first,
                container, false);
        email = rootView.findViewById(R.id.loginEmail);
        password = rootView.findViewById(R.id.loginPassword);



//        if (ms.getAlreadyLogin()) {
//
//            String mail = ms.getUserId();
//            String pw = ms.getUserPw();
//
//            UserData data = new UserData(mail, pw);
//            String jsonData = gson.toJson(data, UserData.class);
//            shared.put(jsonData);
//            Log.i("test", mail + ":" + pw);
//            //로그인 로직 처리 받아와서 맞으면 넘어가기.
//
//            ((MainActivity) getActivity()).replaceFragment(mfrag);
//        }


        logIn = rootView.findViewById(R.id.logIn);

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = email.getText().toString();
                String pw = password.getText().toString();

                UserData data = new UserData(mail, pw);
                String jsonData = gson.toJson(data, UserData.class);
                shared.put(jsonData);
                Log.i("test", mail + ":" + pw);
                //로그인 로직 처리 받아와서 맞으면 넘어가기.
//                ms.setAlreadyLogin(true);
                ((MainActivity) getActivity()).replaceFragment(mfrag);
            }
        });


        // Inflate the layout for this fragment
        return rootView;
    }

    public void putShared(SharedObject shared) {
        this.shared = shared;
    }

}
