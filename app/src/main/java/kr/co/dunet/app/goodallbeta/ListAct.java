package kr.co.dunet.app.goodallbeta;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.co.dunet.app.goodallbeta.adapter.ListViewAdapter;
import kr.co.dunet.app.goodallbeta.adapter.holder.ListData;

public class ListAct extends AppCompatActivity {

    //http://203.236.209.245/public_html/room/profile/1706211528.png 서버쪽 경로

    @BindView(R.id.recycleView)
    RecyclerView recyclerView;
    @BindView(R.id.txt_search_bar)
    EditText TxtSearchBar;

    RecyclerView.LayoutManager layoutManager;
    private ListViewAdapter adapter;
    private HttpService http;
    private ArrayList<ListData> items;
    private String folderName = "/" + "Goodall_Photo" + "/";
    private String imagePath;
    private Bitmap bitmap = null;
    private String localPath;
    private ProgressDialog dialog = null;
    private List<String> roomCodes;
//    private SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setCustomView(R.layout.actionbar_chatlist);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        setContentView(R.layout.act_list);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        new SendHttp().execute();

        Log.d("ㅇㅇㅇ", "누가먼저 ");

        TxtSearchBar.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchItem(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public void BtnTest(View v){
        startActivity(new Intent(getApplicationContext() , ChatRoomAct.class));
    }

    private void searchItem(String textToSearch) {

        for (int i = 0; i < items.size(); i++) {
            if (!items.get(i).getRoomName().contains(textToSearch)) {
                items.remove(items.get(i).getRoomName());
            }
        }
        adapter.notifyDataSetChanged();
    }


    public void btn_create_room(View v) {

        startActivity(new Intent(getApplicationContext(), RoomCreateAct.class).putExtra("list", items));
    }


    private class SendHttp extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {

            http = HttpService.getInstance();
            http.setUrlString("/room/room_list");

            return http.getRoomList();

        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if (result) {
                Log.d("서버로 부터 받아온 json data", http.getJson());

                JSONArray jsonArray;
                items = new ArrayList<>();
                roomCodes = new ArrayList<>();
                final List<String> files = new ArrayList<>();

                if (http.getJson() != null) {

                    try {
                        jsonArray = new JSONArray(http.getJson());

                        //서버로 부터 가져온 Json배열을 추출하는 메소드
                        extractJsonArray(jsonArray);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.d("사진이 담겨있는 룸코드들 ", roomCodes.toString());

                    //폰에 저장할 사진경로를 만드는 과정
                    String ex_storage = Environment.getExternalStorageDirectory().getAbsolutePath();
                    String Dir = ex_storage + folderName;

                    File photoDir = new File(Dir);

                    if (!photoDir.isDirectory()) {
                        photoDir.mkdir();
                    }


                    Log.d("룸코드가 있는 포토 갯수 ", roomCodes.size() + "");
                    //photo 갯수 세서 그만큼 for 문 돌릴예정
                    for (int i = 0; i < roomCodes.size(); i++) {

                        imagePath = photoDir + "/" + roomCodes.get(i) + ".png";

                        File imageFile = new File(imagePath);
                        Log.d("imageFile ", imageFile.toString());

                        //이미지 파일이 존재 하지않는 경우 다운로드 Thread 실행
                        //다운로드를 하고 난뒤 다운로드 Thread에서 받은파일을 다시 imageView에 붙여주는 작업을 한다.
                        if (!imageFile.exists()) {
                            Log.d("파일을 다운로드 합니다", imageFile.toString());
                            //다운로드용 쓰레드

                            if (dialog == null) {
                                dialog = ProgressDialog.show(ListAct.this, "", "처리중입니다...", false);
                                dialog.show();
                                DownloadThread downThread = new DownloadThread(roomCodes.get(i), imagePath);
                                downThread.start();
                            }
                        } else {
                            //파일이 존재할시..
                            //있는 파일을 imageView에 붙여준다.
                            Log.d("파일이 존재합니다. ", imageFile.toString());
//                                System.out.println("파일존재 " + path);
                            try {
                                bitmap = BitmapFactory.decodeFile(imagePath);
                                mkListView();

                            } catch (OutOfMemoryError e) {
                                e.printStackTrace();
                                bitmap = BitmapFactory.decodeFile(imagePath);
                                mkListView();
                            }
                        }
                    }

                    mkListView();

                }

            } else {
                Toast.makeText(getApplicationContext(), "서버측 응답 없음", Toast.LENGTH_LONG).show();
            }
        }
    }

    class DownloadThread extends Thread {

        String host;
        String serverUrl;
//        String localPath;

        public DownloadThread(String roomCode, String imageDownloadPath) {

            host = NetworkConfig.getInstance().getHost();
            serverUrl = host + "/public_html/room/profile/" + roomCode + ".png";
            localPath = imageDownloadPath;
        }

        public void run() {

            URL imgUrl;
            int Read;

            try {
                Log.d(" 서버로 부터 다운로드 받을 url", serverUrl);
                Log.d("이미지 저장소 경로", localPath);
                imgUrl = new URL(serverUrl);
                HttpURLConnection conn = (HttpURLConnection) imgUrl.openConnection();
                int len = conn.getContentLength();

                if (len > 0) {
                    byte[] tmpByte = new byte[len];
                    InputStream is = conn.getInputStream();
                    File file = new File(localPath);
                    FileOutputStream fos = new FileOutputStream(file);

                    for (; ; ) {
                        Read = is.read(tmpByte);
                        if (Read <= 0) {
                            break;
                        }
                        fos.write(tmpByte, 0, Read);
                    }
                    is.close();
                    fos.close();
                } else {
                    localPath = null;
                }
                conn.disconnect();

//                 mAfterDown.sendEmptyMessage(0);

            } catch (MalformedURLException e) {
                Log.e("MalformedURLException", e.getMessage());

            } catch (IOException e) {
                Log.e("IOException", e.getMessage());
                e.printStackTrace();
            }
            dialog.dismiss();
            dialog = null;
        }
    }


    //리스트뷰 만들기
    private void mkListView() {

        layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ListViewAdapter(items);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    //json 배열 추출.
    private void extractJsonArray(JSONArray jsonArray) {

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                // 서버로부터 날라온 Json 리스트를 객체로 나누고 그걸 다시
                // string 파일로 분해해서  그것을 ListData 리스트에 담는 과정
                items.add(new ListData(jsonArray.getJSONObject(i).getString("room_name"),
                        jsonArray.getJSONObject(i).getString("reg_date"), jsonArray.getJSONObject(i).getString("room_code")));

                //roomcode 가 있어도 photo가 없으면 안되기 때문에 photo 가 있는 룸코드를 따로 저장
                if (jsonArray.getJSONObject(i).getString("photo") != "null") {
                    roomCodes.add(jsonArray.getJSONObject(i).getString("room_code"));
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        getMenuInflater().inflate(R.menu.chat_list_menu , menu);

//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.chat_list_menu , menu);
//        MenuItem item = menu.findItem(R.id.action_search);
//        SearchView searchView = (SearchView) item.getActionView();

//        searchView = (SearchView)menu.findItem(R.id.action_search).getActionView();
//        searchView.setQueryHint("하하");
//        searchView.setQueryHint("검색할내용");

//        return super.onCreateOptionsMenu(menu);
//    }

    //    @SuppressLint("HandlerLeak")
//    Handler mAfterDown = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            // TODO Auto-generated method stub
//            // 파일 다운로드 종료 후 다운받은 파일을 실행시킨다.
//
//            if (localPath == null) {
//                Toast.makeText(getApplicationContext() , "파일전송 실패" , Toast.LENGTH_LONG).show();
//            } else {
//                try {
//                    bitmap = BitmapFactory.decodeFile(localPath);
//                } catch (OutOfMemoryError e) {
//                    e.printStackTrace();
//                    bitmap = BitmapFactory.decodeFile(localPath);
//                }
//            }
//
//            // Glide.with(getApplicationContext()).load(imagePath).into(img_v);
//            // Picasso.with(getApplicationContext()).load(new
//            // File(imagePath)).into(img_v);
//        }
//    };


    public void hideSoftInputWindow(View edit_view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edit_view.getWindowToken(), 0);
    }
}
