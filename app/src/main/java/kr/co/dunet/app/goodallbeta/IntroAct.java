package kr.co.dunet.app.goodallbeta;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class IntroAct extends AppCompatActivity {


    private Handler handler;
    private Context mcontext;
    private static final String TAG = "IntroAct";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_intro);
        mcontext = IntroAct.this;

        handler = new Handler();
        Log.d("Intro 시작 합니다" , "sss");
        handler.postDelayed(new Thread(new Runnable() {
            @Override
            public void run() {

                ChatApplication app = (ChatApplication) getApplicationContext();
                String id = app.getUserId() == null? "" : app.getUserId();
                Log.d("Intro 시작 합니다" , id);
                String roomCode = getIntent().getStringExtra("ROOMCODE");

                if(id.equals("")){

                    //서비스 시작
                    Log.d(TAG , "서비스를 시작합니다");
                    startService(new Intent(mcontext, MqttService.class));

                    DBManager mDbManager = DBManager.getInstance(mcontext);
                    String[] columns = new String[]{"id", "nickname", "type"};
                    Cursor c = mDbManager.selectMember(columns , null, null, null, null, null);

                    //커서 객체가 비어있지 않고 다 들어있을때.
                    if(c != null && c.getCount() > 0 ){

                        Boolean isGuest = false;
                        // 커서 객체 내용물 검사
                        while(c.moveToNext()){
                            id = c.getString(0);
                            //guest 유무 확인내용 담겨있음
                            if(c.getString(2) != null && c.getString(2).equals("1")){
                                isGuest = true;
                            }
                            break;
                        }

                        c.close();
                        app.setUserId(id);
                        app.setGuest(isGuest);
                    }
                }



                startActivity(new Intent(IntroAct.this,  MainAct.class));
                finish();
                overridePendingTransition(android.R.anim.fade_in , android.R.anim.fade_out);
            }
        }), 1000);

    }

}
