package kr.co.dunet.app.goodallbeta;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import kr.co.dunet.app.goodallbeta.domain.Topic;

public class MqttService extends Service {


    private static final String TAG = "MqttService";
    private final IBinder binder = new MqttBinder();

    //Binder 리턴
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    //원칙 1 . Binder 클래스는 클라이언트쪽에서 호출가능한 공개메서드 형태로 만든다.
   public class MqttBinder extends Binder {

        public  MqttService getService() {
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
            Handler handler = new Handler(getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {

                    JSONObject json = null;
                    Topic topic;

                    try{
                        json = new JSONObject(new String(message.getPayload()));

                        if(json.getString("type").equals("msg")){
                            topic = new Topic(json.getString("rid") ,  json.getString("id") , json.getString("room_code"),
                                    json.getString("room_name"),json.getString("nickname"),
                                    new String(Base64.decodeBase64(json.getString("msg").getBytes())),json.getString("datetime"),json.getString("key"));

                            Log.d(TAG , "받은 메시지 : " + message);

                        }

                    }catch (JSONException e){
                        e.printStackTrace();
                    }

                }
            });

        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {

        }
    }


}
