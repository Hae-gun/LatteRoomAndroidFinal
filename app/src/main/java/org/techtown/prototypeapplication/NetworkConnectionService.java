package org.techtown.prototypeapplication;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import org.techtown.prototypeapplication.VO.SharedObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class NetworkConnectionService extends Service {
    // 공유객체. Singleton pattern
    private SharedObject shared = SharedObject.getInstance();

    private Socket socket;
    private BufferedReader br;
    private PrintWriter pr;

    private String userId = "";
    private String userPw = "";

    private Boolean alreadyLogin = false;

    public Boolean getAlreadyLogin() {
        return alreadyLogin;
    }

    public void setAlreadyLogin(Boolean alreadyLogin) {
        this.alreadyLogin = alreadyLogin;
    }

    // 서버 IP Address
    private final String host = "70.12.60.97";
    // Port Number
    private final int port = 55566;
    Thread t;
    Thread getData;
    IBinder mBinder = new MyBinder();

    // 바인더로 사용될 객체.
    class MyBinder extends Binder {
        NetworkConnectionService getService() {
            return NetworkConnectionService.this;
        }
    }

    public NetworkConnectionService() {
    }


    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(mReceiver, filter);

        Thread connectSocket = new Thread(() -> {
            connectServer();
            if (socket.isClosed())
                connectServer();
        });

        connectSocket.start();
        startForegroundService();
        Log.i("BinderHash", "Service Hash: " + this.hashCode());

    }

    public SharedObject getShared() {
        return this.shared;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    void startForegroundService() {
        Log.i("AliveTime", "startForegroundService()");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_service);

        Builder builder;
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "snowdeer_service_channel";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "SnowDeer Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                    .createNotificationChannel(channel);

            builder = new Notification.Builder(this, CHANNEL_ID);
        } else {
            builder = new Notification.Builder(this);
        }
//        Bitmap bm = BitmapFactory.decodeResource(getResources(),R.drawable.bitmapimage);
//        .setLargeIcon(bm)
        builder.setAutoCancel(false)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContent(remoteViews)
                .setContentIntent(pendingIntent);

        startForeground(1, builder.build());
        t = new Thread(() -> {
            while (true) {
                Log.i("AliveTime", "스레드가 산거였어");
                if (socket != null)
                    Log.i("AliveTime", "" + socket.hashCode());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }

        });
//        t.start();
    }

    @SuppressLint("WrongConstant")
    @RequiresApi(api = Build.VERSION_CODES.N)
    void stopForegroundService() {
//        t.interrupt();
        stopForeground(1);
        try {
            br.close();
            pr.close();
            socket.close();
        } catch (IOException e) {
            Log.i("test", "failed closing IO ");
        }
    }

    public BufferedReader getBr() {
        return br;
    }

    public PrintWriter getPr() {
        return pr;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserPw(String userPw) {
        this.userPw = userPw;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserPw() {
        return userPw;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        try {
//            socket.close();
//            br.close();
//            pr.close();
//            getData.interrupt();
//        } catch (IOException e) {
//            Log.i("failed to close",e.toString());
//        }
        if (t.isAlive())
            t.interrupt();

        Log.i("AliveTime", "죽었어");
        startForegroundService();
        Log.i("AliveTime", "부활");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    // 서버 접속 메서드.
    // 데이터 보내고 받는 Thread 포함.
    private void connectServer() {
        try {
            socket = new Socket(host, port);
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pr = new PrintWriter(socket.getOutputStream());


            getData = new Thread(() -> {
                String msg = "";
                while (true) {
                    try {
                        if ((msg = br.readLine()) != null) {
                            Log.i("EchoMsg", msg);
                            Intent showIntent = new Intent(getApplicationContext(),MainActivity.class);

                            showIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|
                                    Intent.FLAG_ACTIVITY_SINGLE_TOP|
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            showIntent.putExtra("fromServer",msg);
                            startActivity(showIntent);

                        }
                    } catch (IOException e) {
                        Log.i("error", e.toString());
                        break;
                    }
                }
                Log.i("error", "서버가 닫혔습니다.");
            });
            getData.start();
            Thread sendData = new Thread(() -> {
                while (true) {

                    String sendMsg = shared.pop();
                    Log.i("sendData", sendMsg);
                    pr.println(sendMsg);
                    pr.flush();
                    Log.i("sendData", sendMsg);


                }
            });
            sendData.start();
        } catch (IOException e) {
            Log.i("Test", e.toString());
            try {
                br.close();
                pr.close();
                socket.close();
            } catch (IOException e2) {
                Log.i("Failed Socket Close", e2.toString());

            }
        }
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        private Boolean changeWifi = false;
        private SharedObject shared = SharedObject.getInstance();

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                //와이파이 상태변화
                case WifiManager.WIFI_STATE_CHANGED_ACTION:
                    //와이파이 상태값 가져오기
                    int wifistate = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
                    switch (wifistate) {
                        case WifiManager.WIFI_STATE_DISABLING: //와이파이 비활성화중
                            shared.put("WifiChange");
                            Toast.makeText(context, "와이파이 비활성화중", Toast.LENGTH_SHORT).show();
                            Log.i("chkWifi", "와이파이 비활성화중");
                            Log.i("shared", "" + shared.hashCode());

//                            try {
//                                br.close();
//                                pr.close();
//                                socket.close();
//                            } catch (IOException e) {
//                                Log.i("wifiDisConn", "wifiDisConn Failed");
//                            }
                            break;
                        case WifiManager.WIFI_STATE_DISABLED:  //와이파이 비활성화
                            Toast.makeText(context, "와이파이 비활성화", Toast.LENGTH_SHORT).show();
                            Log.i("chkWifi", "와이파이 비활성화");
//                            changeWifi = true;
                            try {
                                socket.close();
                                br.close();
                                pr.close();
                                getData.interrupt();
                            } catch (IOException e) {
                                Log.i("failed to close", e.toString());
                            }
                            stopForegroundService();
                            break;
                        case WifiManager.WIFI_STATE_ENABLING:  //와이파이 활성화중
                            Toast.makeText(context, "와이파이 활성화중", Toast.LENGTH_SHORT).show();
                            Log.i("chkWifi", "와이파이 활성화중");

                            break;
                        case WifiManager.WIFI_STATE_ENABLED:   //와이파이 활성화
                            Toast.makeText(context, "와이파이 활성화", Toast.LENGTH_SHORT).show();
                            Log.i("chkWifi", "와이파이 활성화");
                            Log.i("shared", "" + shared.hashCode());


//                            if (changeWifi){
//                                changeWifi = false;
//                                connectServer();
//                            }
//                            else if(socket.isConnected()&&socket!=null){
//                                connectServer();
//                            }
                            break;
                        default:
                            Toast.makeText(context, "알수없음.", Toast.LENGTH_SHORT).show();
                            Log.i("chkWifi", "알수없으");
                            break;
                    }
                    break;

                //네트워크 상태변화
                case WifiManager.NETWORK_STATE_CHANGED_ACTION:
                    NetworkInfo info = (NetworkInfo) intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                    //네트워크 상태값 가져오기
                    NetworkInfo.DetailedState state = info.getDetailedState();

                    String typename = info.getTypeName();
                    if (state == NetworkInfo.DetailedState.CONNECTED) { //네트워크 연결
                        Toast.makeText(context, "네트워크 연결", Toast.LENGTH_SHORT).show();
                        Log.i("chkWifi", "네트워크 연결");
                        Log.i("chkWifi", "inCONNECTED: " + socket.isConnected());
                        Log.i("chkWifi", "inCONNECTED: " + socket.isClosed());

                            Log.i("myIO", "input: " + socket.isInputShutdown() + " output: "
                                    + socket.isOutputShutdown());

                        shared.put("restart");

                    } else if (state == NetworkInfo.DetailedState.DISCONNECTED) { //네트워크 끊음
                        Toast.makeText(context, "네트워크 끊음", Toast.LENGTH_SHORT).show();
                        Log.i("chkWifi", "네트워크 끊음");
                        Log.i("chkWifi", "inDISCONNECTED:" + socket.isConnected());
                        Log.i("chkWifi", "inDISCONNECTED:" + socket.isClosed());
                        try {
                            socket.close();
                            br.close();
                            pr.close();
                            getData.interrupt();
                            Log.i("myIO", "input: " + socket.isInputShutdown() + "output: "
                                    + socket.isOutputShutdown());
                            Log.i("chkWifi", "Closed All IO");
                        } catch (IOException e) {
                            Log.i("failed to close", e.toString());
                        }
                    }
                    break;
            }
        }
    };


}
