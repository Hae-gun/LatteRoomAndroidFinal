package org.techtown.prototypeapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.techtown.prototypeapplication.VO.LatteMessage;
import org.techtown.prototypeapplication.VO.SharedObject;
import org.techtown.prototypeapplication.NetworkConnectionService.MyBinder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    private static final String DEVICE_ID = "Android01";
    private static final String DEVICE_TYPE = "USER";
    private static final int LOCATION = 1;

    private WifiManager wifiManager;
    private WifiInfo wifiInfo;

    private NetworkConnectionService ms;
    private boolean isService = false;




    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").create();
//    Socket socket;
//    BufferedReader br;
//    PrintWriter pr;
    Handler handler;
    SharedObject shared = SharedObject.getInstance();
    RoomListFragment roomListFragment = new RoomListFragment();
    MainFragment mfrag = new MainFragment(roomListFragment);

    FirstFragment  ffrag = new FirstFragment(mfrag);

    public static String getDeviceId() {

        return DEVICE_ID;
    }

    public static String getDeviceType() {
        return DEVICE_TYPE;
    }

    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyBinder mb = (MyBinder) service;
            ms = mb.getService();
            if(ms.getBr()!=null) {
                Log.i("BinderHash", "" + ms.getBr().hashCode());
            }
            isService = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isService = false;
        }
    };


    @Override
    protected void onStart() {
        Log.i("LatteTest","onStart 시작");
        super.onStart();

        tryToReadSSID();




//        wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        wifiInfo = wifiManager.getConnectionInfo();
////        NetworkI
//        String ssid = wifiInfo.getSSID();
//
//        Log.i("LatteTest",ssid);


//
//        Thread t = new Thread(() -> {
//            try {
//
//                socket = new Socket("70.12.60.97", 55566);
//                Log.i("LatteTest","서버접속");
//                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                pr = new PrintWriter(socket.getOutputStream());
//
//                pr.println(getDeviceId());
//                pr.println(getDeviceType());
//                LatteMessage startMsg= new LatteMessage(getDeviceId(),"SensorList",null);
//                String initMsg = gson.toJson(startMsg,LatteMessage.class);
//                pr.println(initMsg);
//                pr.flush();
//
//                Thread sendMsg = new Thread(()->{
//                    while(true){
//                        Log.i("LatteTest","sendMsg 실행");
//                        String msg = shared.pop();
//                        pr.println(msg);
//                        pr.flush();
//                    }
//                });
//                sendMsg.start();
//                Thread getMsg = new Thread(()->{
//                    String result = "";
//                    try{
//                        while((result=br.readLine())!=null){
//                            Log.i("LatteTest","서버로 부터 옴 "+result);
//                            Bundle bundle = new Bundle();
//                            Message message=new Message();
//                            bundle.putString("result",result);
//                            message.setData(bundle);
//                            handler.sendMessage(message);
//                            roomListFragment.setArguments(bundle);
//
//                        }
//                    }catch(IOException e2){
//                        Log.i("LatteTest",e2.toString());
//                    }
//                });
//                getMsg.start();
//            }catch(Exception e){
//                Log.i("LatteTest",e.toString());
//            }
//        });
//        t.start();
//
//        Log.i("LatteTest","onStart 종료");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String result = intent.getStringExtra("fromServer");
        Log.i("onNewIntent","Data From Service: "+result);


            Bundle bundle = new Bundle();
            if(result.equals("AlarmOn")||result.equals("AlarmOff")) {
                bundle.putString("result", result);
                roomListFragment.setArguments(bundle);
                replaceFragment(roomListFragment);
            }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(MainActivity.this, NetworkConnectionService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);

        Button destroyService = findViewById(R.id.destroyService);

        destroyService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ms.onDestroy();
            }
        });
        if(Build.VERSION.SDK_INT>=26){
            getApplicationContext().startForegroundService(intent);
        }
        else{
            getApplicationContext().startService(intent);
        }

//       handler= new Handler(){
//            @Override
//            public void handleMessage(@NonNull Message msg) {
//                super.handleMessage(msg);
////                String result = msg.getData().getString("result");
////                Log.i("LatteTest",msg.toString());
////                if("result".equals(result)){
////                    roomListFragment.getRoomListTv().setText(result+" from server");
////                }
//            }
//        };





        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.container,ffrag).commit();
    }

    public String getConnectedWiFiSSID(){
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        return info.getBSSID();
    }


    public void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container,fragment).commit();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == LOCATION){
            //User allowed the location and you can read it now
            tryToReadSSID();
        }
    }

    private void tryToReadSSID() {
        //If requested permission isn't Granted yet
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Request permission from user
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION);
        }else{//Permission already granted
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if(wifiInfo.getSupplicantState() == SupplicantState.COMPLETED){
                String ssid = wifiInfo.getSSID();//Here you can access your SSID
                shared.put(ssid);
                Log.i("LatteTest","inSSID: "+ssid);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
    }



    //와이파이 상태변화 수신



}
