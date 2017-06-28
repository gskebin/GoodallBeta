package kr.co.dunet.app.goodallbeta;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class JoinAct extends AppCompatActivity {

    public static final Pattern VAILD_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    public static final Pattern VALID_PASSWOLD_REGEX_ALPHA_NUM = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{4,16}$");

    //    private String nickname;
//    private String emailAddress;
//    private String password;
//    private String checkPassword;
    private sendHttp mSendHttp;
    private  HttpService http;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        final EditText input_email = (EditText) findViewById(R.id.input_email);
        final EditText input_password = (EditText) findViewById(R.id.input_password);
        final EditText input_check_password = (EditText) findViewById(R.id.input_check_password);
        final EditText input_nickname = (EditText) findViewById(R.id.input_nickname);
        final CheckBox checkBox = (CheckBox) findViewById(R.id.checkbox);

        findViewById(R.id.btn_join).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (input_nickname.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "공백안됨.", Toast.LENGTH_LONG).show();
                    input_nickname.setFocusable(true);
                    return;
                } else if (!(validateEmail(input_email.getText().toString().trim()))) {
                    Toast.makeText(getApplicationContext(), "이메일 형식을 지켜주세요", Toast.LENGTH_LONG).show();
                    input_email.setHint("이메일은 aaa@abc.com 이런식으로 써야합니다");
                    input_email.setText("");
                    input_email.setFocusable(true);
                    return;

                } else if (!(validatePassword(input_password.getText().toString().trim()))) {
//                    password = input_password.getText().toString();
                    Toast.makeText(getApplicationContext(), "비번치고 쉽다", Toast.LENGTH_LONG).show();
                    input_password.setFocusable(true);
                    input_password.setText("");
                    input_check_password.setText("");
                    input_password.setHint("비밀번호는 영문 숫자 특수문자(@.#$%^&*?)포함 4~16자리 ");
                    return;
                } else if (!(input_password.getText().toString().trim().equals(input_check_password.getText().toString().trim()))) {
                    Toast.makeText(getApplicationContext(), "비밀번호가 같지않아요", Toast.LENGTH_LONG).show();
                    input_password.setHint("비밀번호는 영문 숫자 특수문자(@.#$%^&*?)포함 4~16자리 ");
                    input_check_password.setText("");
                    input_password.setText("");
                    input_password.setFocusable(true);
                    return;
                } else if (!checkBox.isChecked()) {
                    Toast.makeText(getApplicationContext(), "체크박스를 체크해주십시오", Toast.LENGTH_LONG).show();
                    return;
                }



                mSendHttp = new sendHttp();
                mSendHttp.execute(input_nickname.getText().toString(), input_email.getText().toString(), input_password.getText().toString());

            }
        });

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftInputWindow(v);
            }
        });

    }



    class sendHttp extends AsyncTask<String, Integer, Boolean> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {

            http = HttpService.getInstance();
            http.setUrlString("/member/register");

            List<BasicNameValuePair> nameValuePairs = new ArrayList<>();

            nameValuePairs.add(new BasicNameValuePair("nickname", params[0]));
            nameValuePairs.add(new BasicNameValuePair("email", params[1]));
            nameValuePairs.add(new BasicNameValuePair("password", params[2]));

            http.setNameValuePairs(nameValuePairs);

            return http.sendData();

        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if (result) {
                Toast.makeText(getApplicationContext(), "접속성공", Toast.LENGTH_LONG).show();
                Log.d("가져온 json ", http.getJson());
                JSONObject jsonObj = null;

                if (http.getJson() != null) {

                    try {
                        jsonObj = new JSONObject(http.getJson());
                        String resultMsg = jsonObj.getString("result");
                        
                        if(resultMsg.equals("0000")){
                                Toast.makeText(getApplicationContext() , "가입이 성공되었어요" , Toast.LENGTH_LONG).show();
                                startActivity(new Intent(getApplicationContext(),MainAct.class));
                        }else{
                            Toast.makeText(getApplicationContext() , "중복된 이메일이 있습니다." , Toast.LENGTH_LONG).show();
                        }
                        
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            } else {
                Toast.makeText(getApplicationContext(), "가입실패", Toast.LENGTH_LONG).show();
            }
        }

    }


    private boolean validatePassword(String pw) {

        return VALID_PASSWOLD_REGEX_ALPHA_NUM.matcher(pw).matches();
    }

    private boolean validateEmail(String email) {
        return VAILD_EMAIL_ADDRESS_REGEX.matcher(email).matches();
    }

    public void hideSoftInputWindow(View edit_view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edit_view.getWindowToken(), 0);
    }

}
