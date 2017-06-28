package kr.co.dunet.app.goodallbeta;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

public class GuestAct extends AppCompatActivity {

    private HttpService http;
//    private sendHttp mSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setCustomView(R.layout.actionbar_guest);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        setContentView(R.layout.act_guest);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

    }

//    public void btnClick2(View v) {
//
//        mSend = new sendHttp();
//        mSend.execute();
//    }

//    class sendHttp extends AsyncTask<String, Integer, Boolean> {
//
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected Boolean doInBackground(String... params) {
//
//            http = HttpService.getInstance();
//            http.setUrlString("/room/room_list");
//
//
//            return http.sendData();
//
//        }
//
//        @Override
//        protected void onPostExecute(Boolean result) {
//            super.onPostExecute(result);
//
//            if (result) {
//                Toast.makeText(getApplicationContext(), "접속성공", Toast.LENGTH_LONG).show();
//                Log.d("가져온 json ", http.getJson());
//                JSONObject jsonObj = null;
//
//                if (http.getJson() != null) {
//
//                    try {
//                        jsonObj = new JSONObject(http.getJson());
//                        String resultMsg = jsonObj.getString("result");
//
//                        if(resultMsg.equals("0000")){
//                            Toast.makeText(getApplicationContext() , "가입이 성공되었어요" , Toast.LENGTH_LONG).show();
//                            startActivity(new Intent(getApplicationContext(),MainAct.class));
//                        }else{
//                            Toast.makeText(getApplicationContext() , "중복된 이메일이 있습니다." , Toast.LENGTH_LONG).show();
//                        }
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//
//            } else {
//                Toast.makeText(getApplicationContext(), "가입실패", Toast.LENGTH_LONG).show();
//            }
//        }
//
//    }
}
