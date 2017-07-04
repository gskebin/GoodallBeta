package kr.co.dunet.app.goodallbeta;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ChatRoomAct extends AppCompatActivity {

//    @BindView(R.id.btn_send)
//    Button btnSend;
//    @BindView(R.id.txt_send)
//    EditText txtSend;


    private EditText txtSend;
    private Button btnSend;
    private SendHttp mSendHttp;
    private boolean isConnect = false;
    public MqttService mqttService;
    public ServiceConnection connection = new ServiceConnection() {


        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mqttService = ((MqttService.MqttBinder) service).getService();
            isConnect = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isConnect = false;
        }
    };
//
    public void BtnClick(View v){

        String data = txtSend.getText().toString();
        Toast.makeText(getApplicationContext() , data , Toast.LENGTH_SHORT).show();
//        Toast.makeText(getApplicationContext() , "dd" , Toast.LENGTH_SHORT).show();
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        try {
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setCustomView(R.layout.actionbar_chat_room);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        setContentView(R.layout.act_chat_room);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        btnSend = (Button) findViewById(R.id.btn_send);
        txtSend = (EditText) findViewById(R.id.txt_send);

        bindService(new Intent(this, MqttService.class), connection, BIND_AUTO_CREATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_room_menu, menu);
        MenuItem item = menu.findItem(R.id.chat_room_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        //검색바 기능
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

//    @OnClick(R.id.btn_send)
//    public void btnSend(){
//        Toast.makeText(getApplicationContext() , "눌림" , Toast.LENGTH_SHORT).show();
//    }


    class SendHttp extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected Boolean doInBackground(String... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }

    }

}
