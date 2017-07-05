package kr.co.dunet.app.goodallbeta;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;

/**
 * Created by hanbit on 2017-06-07.
 */

public class ChatApplication extends Application implements Application.ActivityLifecycleCallbacks{

    private boolean isGuest = false;
    private boolean roomAdmin = false;
    private String userName = "";
    private String userId = null;
    private String chatRoom = null;
    private static ChatApplication myApp;
    private boolean isAppfinishState = false;
    private Boolean appFirst = false;
    private volatile IMqttAsyncClient mqttClient;


    public boolean isGuest() {
        return isGuest;
    }

    public void setGuest(boolean guest) {
        isGuest = guest;
    }

    public boolean isRoomAdmin() {
        return roomAdmin;
    }

    public void setRoomAdmin(boolean roomAdmin) {
        this.roomAdmin = roomAdmin;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getChatRoom() {
        return chatRoom;
    }

    public void setChatRoom(String chatRoom) {
        this.chatRoom = chatRoom;
    }

    public ChatApplication(){
        myApp = this;
    }

    public static ChatApplication getInstance(){
        return myApp;
    }

    public void finishApp(Activity activity){

        isAppfinishState = true;
        activity.finish();

    }


    @Override
    public void onActivityResumed(Activity activity) {
        if(isAppfinishState == true){
            activity.finish();

            if(activity.isTaskRoot() == true){
                isAppfinishState = false;
            }
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    public Boolean getAppFirst() {
        return appFirst;
    }

    public void setAppFirst(Boolean appFirst) {
        this.appFirst = appFirst;
    }

    public IMqttAsyncClient getMqttClient() {
        return mqttClient;
    }

    public void setMqttClient(IMqttAsyncClient mqttClient) {
        this.mqttClient = mqttClient;
    }
}
