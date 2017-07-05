package kr.co.dunet.app.goodallbeta;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONException;
import org.json.JSONObject;

import kr.co.dunet.app.goodallbeta.domain.Topic;

public class MqttService extends Service {


    WifiManager.WifiLock wifiLock = null;
    PowerManager.WakeLock wakeLock = null;
    private String userId;
    private volatile IMqttAsyncClient mqttClient;
    private static final String TAG = "MqttService";
    public String host = "";
    private final IBinder binder = new MqttBinder();
    private ConnectivityManager mConnectiveManager;
    public String networkName;

    //실시간으로 연결끊기면 알려주고 연결되면 알려주는 리시버 wifi, lte 등
    class MQTTBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive 켜졋어요");
            WakeUpDevice();

            boolean hasConnectivity = false;
            boolean hasChanged = false;

            NetworkInfo networkInfo = mConnectiveManager.getActiveNetworkInfo();
//			Log.d(TAG , "네트워크 이름 : " +  networkInfo.getTypeName());
            if (networkInfo != null && networkInfo.isConnected()) {
                Log.i(TAG, networkInfo.getTypeName());
                hasConnectivity = true;
                if (networkName != networkInfo.getTypeName()) {
                    hasChanged = true;
                    networkName = networkInfo.getTypeName();
                }
            } else {
                hasConnectivity = false;
                networkName = null;
            }

            Log.v(TAG,
                    "hasConn: " + hasConnectivity + " hasChange: " + hasChanged
                            + " - "
                            + (mqttClient == null || !mqttClient.isConnected()));

            if (hasConnectivity && hasChanged
                    && (mqttClient == null || !mqttClient.isConnected())) {
                doConnect();
            } else if (!hasConnectivity && mqttClient != null
                    && mqttClient.isConnected()) {
                Log.d(TAG, "통신끊김");
				/*
				 * IMqttToken token; try { token = mqttClient.disconnect();
				 * token.waitForCompletion(1000); } catch (MqttException e) {
				 * e.printStackTrace(); }
				 */
            }
            closeToWakeMode();
        }
    }


    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        host = NetworkConfig.getInstance().getMqttHost();

        registerReceiver(new MQTTBroadcastReceiver() , new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        mConnectiveManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        super.onCreate();
    }


    //Binder 리턴
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    //원칙 1 . Binder 클래스는 클라이언트쪽에서 호출가능한 공개메서드 형태로 만든다.
    public class MqttBinder extends Binder {

        public MqttService getService() {
            return MqttService.this;
        }
    }

    public class MqttCallback implements org.eclipse.paho.client.mqttv3.MqttCallback {

        @Override
        public void connectionLost(Throwable cause) {

        }

        @Override
        @SuppressLint("NewApi")
        public void messageArrived(String topic, final MqttMessage message) throws Exception {

            Log.i(TAG, "메세지가 Topic에서 왔음  (토픽 내용 : " + topic + ")");
            //데이터를 클라랑 주고 받아야되기 때문에 handler 사용
            Handler handler = new Handler(getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {

                    JSONObject json = null;
                    Topic topic;

                    try {
                        json = new JSONObject(new String(message.getPayload()));

                        if (json.getString("type").equals("msg")) {
                            topic = new Topic(json.getString("rid"), json.getString("id"), json.getString("room_code"),
                                    json.getString("room_name"), json.getString("nickname"),
                                    new String(Base64.decodeBase64(json.getString("msg").getBytes())), json.getString("datetime"), json.getString("key"));
                            Log.d(TAG, "받은 메시지 : " + message);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {

        }
    }

    //startService!
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        WakeUpDevice();

        if (mqttClient != null && !mqttClient.isConnected()) {
            setClientID();
            doConnect();
        }

        closeToWakeMode();

        return START_STICKY;
    }

    public void WakeUpDevice() {
        Log.d(TAG, "wakelock start");

        if (wifiLock == null) {
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            wifiLock = wifiManager.createWifiLock("wifilock");
            //레퍼런스 참조 카운트할래?
            wifiLock.setReferenceCounted(true);
            //타임아웃 설정.
            wifiLock.acquire();
        }

        if (wakeLock == null) {
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            //PARTIAL_WAKE_LOCK <<< cpu on / screen off / keyboard off
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "wakelock");
            wakeLock.acquire();
        }
    }

    public void closeToWakeMode() {
        Log.d(TAG, "wakelock end");
        if (wifiLock != null) {
            wifiLock.release();
            wifiLock = null;
        }

        if (wakeLock != null) {
            wakeLock.release();
            wakeLock = null;
        }
    }

    //mqttclient 객체가 있는경우
    //접속이 되어 있지 않는 경우 접속을 실행 이미 접속되있으면 return
    private void doConnect() {

        Log.d(TAG, "doConnect");

        if (userId == null) {
            return;
        }

        //IMqttToken은 비동기 작업의 완료를 추적하는 메커니즘을 제공합니다.
        //여기선 mqtt의 작업 상태를 추적하는데 사용합니다.
        IMqttToken token;
        MqttConnectOptions options = new MqttConnectOptions();
        ChatApplication app = (ChatApplication) getApplicationContext();

        if (app.getAppFirst()) {
            //setCleanSession 클라이언트와 서버가 재시작 및 재 연결시 상태를 기억하는지 여부를 설정합니다.
            //false면  재시작시 클라이언트와 서버 모두 클라이언트, 서버 및 연결의 재시작시 상태를 유지합니다
            //서버는 구독을 영구으로 처리합니다.
            //true 면 상태를 유지하고 않고 구독을 비정기로 처리합니다.
            options.setCleanSession(true);
            app.setAppFirst(false);
        } else {
            options.setCleanSession(false);
        }

        //tcp/ip 시간 초과를 기다리지 않고 클라이언트가 서버를 더 이상 사용할수 있는지 없는 여부 감지
        // how? 해당기간동안(괄호안의 값 0이면 비활성) 매우 작은 "핑" 메세지를 보내서.
        options.setKeepAliveInterval(30);

        if (mqttClient != null && mqttClient.isConnected()) {
            Log.e(TAG, "이미 mqtt 연결됨.");
            return;
        }

        try {

            app.setMqttClient(new MqttAsyncClient("", userId,
                    new MemoryPersistence()));
            mqttClient = app.getMqttClient();
            token = mqttClient.connect();
            token.waitForCompletion(3500);
            mqttClient.setCallback(new MqttCallback());

            DBManager mDbManager = DBManager.getInstance(this);
            String[] columns = new String[]{"room_code"};
            Cursor c = mDbManager.selectRoom(columns, null, null, null, null,
                    null);

            if (c != null && c.getCount() > 0) {
                while (c.moveToNext()) {
                    String room_code = c.getString(0);

                    token = mqttClient.subscribe(room_code, 1);
                    token.waitForCompletion(5000);
                }
            }

            c.close();

        } catch (MqttSecurityException e) {
            e.printStackTrace();
        } catch (MqttException e) {
            switch (e.getReasonCode()) {
                case MqttException.REASON_CODE_BROKER_UNAVAILABLE:
                case MqttException.REASON_CODE_CLIENT_TIMEOUT:
                case MqttException.REASON_CODE_CONNECTION_LOST:
                case MqttException.REASON_CODE_SERVER_CONNECT_ERROR:
                    Log.v(TAG, "c" + e.getMessage());
                    e.printStackTrace();
                    break;
                case MqttException.REASON_CODE_FAILED_AUTHENTICATION:
                    Intent i = new Intent("RAISEALLARM");
                    i.putExtra("ALLARM", e);
                    Log.e(TAG, "b" + e.getMessage());
                    break;
                default:
                    Log.e(TAG, "a" + e.getMessage());
            }
        }
    }

    //mqttclient 객체가 있는경우 아이디를 전역 클래스인 ChatApplication에 바인드.
    private void setClientID() {

        Log.d(TAG, "setClientId");
        ChatApplication app = (ChatApplication) getApplicationContext();
        String id = app.getUserId() == null ? "" : app.getUserId();
        if (!id.equals("")) {
            userId = id;
            return;
        }

        DBManager mDbManager = DBManager.getInstance(this);
        String[] columns = new String[]{"id"};
        Cursor c = mDbManager.selectMember(columns, null, null, null, null,
                null);

        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                userId = c.getString(0);
                break;
            }
        } else {
            userId = null;
        }
        c.close();

        app.setUserId(userId);
    }
}
