package kr.co.dunet.app.goodallbeta;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LoginAct extends AppCompatActivity {

    EditText inputEmail;
    EditText inputPassword;
    private HttpService http = null;
    private SendHttp mSendHttp = null;
    private String msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setCustomView(R.layout.actionbar_email);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        setContentView(R.layout.act_login);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);


        inputEmail = (EditText) findViewById(R.id.input_email);
        inputPassword = (EditText) findViewById(R.id.input_password);

        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (inputEmail.getText().toString().equals("") || inputPassword.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "아이디나 비번이 공백입니다.", Toast.LENGTH_LONG).show();
                    return;
                }

                hideSoftInputWindow(v);

                //AsyncTask 를 사용하여 email 과 password 를 받아서 백그라운드 단으로 email 과 password 를  http 서비스로 보냄.
                mSendHttp = new SendHttp();
                mSendHttp.execute(inputEmail.getText().toString(), inputPassword.getText().toString());

            }
        });

        findViewById(R.id.findPassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), passwordModifyAct.class));
            }
        });

    }

    public void hideSoftInputWindow(View edit_view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edit_view.getWindowToken(), 0);
    }

    private class SendHttp extends AsyncTask<String, Integer, Boolean> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {

            http = HttpService.getInstance();
            http.setUrlString("/member/login");

            List<BasicNameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("id", params[0]));
            nameValuePairs.add(new BasicNameValuePair("pwd", params[1]));

            http.setNameValuePairs(nameValuePairs);

            return http.sendData();

        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if (result) {
                Log.d("json data", http.getJson());

                JSONObject jsonObj = null;

                if (http.getJson() != null) {

                    try {
                        jsonObj = new JSONObject(http.getJson());
                        String resultMsg = jsonObj.getString("result");
                        msg = jsonObj.getString("msg");
                        if (resultMsg.equals("0000")) {
                            String nickName = jsonObj.getString("nickname");
                            String id = jsonObj.getString("id");
                            loginSuccess(id, nickName);
                            Log.d("로그인 성공! ", "로그인 !");
                            Toast.makeText(getApplicationContext(), "로그인됩니다", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), ListAct.class));
                        } else {
                            Toast.makeText(getApplicationContext(), "아이디 혹은 비밀번호가 틀렸습니다.", Toast.LENGTH_LONG).show();
                            Log.d("틀린 항목  :   ", msg);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            } else {
                Toast.makeText(getApplicationContext() , "서버측 응답 없음" , Toast.LENGTH_LONG).show();
            }
        }
    }

    private void loginSuccess(String id, String nickName) {

        DBManager mDbManager = DBManager.getInstance(this);

        ContentValues addRowValue = new ContentValues();
        addRowValue.put("id" , id);
        addRowValue.put("nickname" ,nickName);

        mDbManager.insertMember(addRowValue);

        ChatApplication app = (ChatApplication) getApplicationContext();
        app.setUserId(id);
        app.setUserName(nickName);
        app.setGuest(false);

    }

}
