package kr.co.dunet.app.goodallbeta;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.co.dunet.app.goodallbeta.adapter.holder.ListData;

import static android.R.attr.name;

public class RoomCreateAct extends AppCompatActivity {

    @BindView(R.id.switch1)
    SwitchCompat switch1;
    @BindView(R.id.check_password)
    EditText checkPassword;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.chat_room_name)
    EditText chatRoomName;
    @BindView(R.id.btn_duplicate)
    TextView btn_duplicate;
    @BindView(R.id.btn_photo_add)
    TextView btn_photo_add;
    @BindView(R.id.imgView)
    ImageView imageView;

    //    private SwitchCompat switchCompat;
    private HttpService http;
    private String msg;
    private SendHttp mSendHttp;
    private SendHttpToPhoto mSendPhoto;
    private static final int PICK_FROM_ALBUM = 101;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 102;
    private ArrayList<ListData> roomDataFromListAct;
    String folderName = "GoodallTemp";
    private boolean isRoomName = false;
    private String nickname;
    private String userId;
    private String jpegImagePath;
    private ProgressDialog progressDialog = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setCustomView(R.layout.actionbar_create_room);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        setContentView(R.layout.act_room_create);
        ButterKnife.bind(this);

        DBManager mDbManager = DBManager.getInstance(this);
        Cursor c = mDbManager.selectMember(new String[]{"id", "nickname"}, null, null, null, null, null);

        while (c.moveToNext()) {
            userId = c.getString(0);
            nickname = c.getString(1);
        }
        c.close();

        roomDataFromListAct = (ArrayList<ListData>) getIntent().getSerializableExtra("list");
        Log.d("리스트 act 로 부터 넘어온 데이터", roomDataFromListAct.get(0).toString());

        chatRoomName = (EditText) findViewById(R.id.chat_room_name);
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        //방 생성시 비밀번호 필요한지 안한지 정하는 버튼
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    password.setEnabled(false);
                    checkPassword.setEnabled(false);
//                    Toast.makeText(getApplicationContext() , "true" , Toast.LENGTH_LONG).show();
                } else {
                    password.setEnabled(true);
                    checkPassword.setEnabled(true);
//                    Toast.makeText(getApplicationContext() , "false" , Toast.LENGTH_LONG).show();
                }
            }
        });

    }


    //중복확인 버튼쪽 로직
    //ListAct 에서 넘어온 방이름을 검사해서 있으면 true 없으면 false 상태값만 넘겨줌.
    @OnClick(R.id.btn_duplicate)
    public void BtnCheckDuplicate() {

        String roomName = chatRoomName.getText().toString().trim();

        if (roomName.equals("")) {
            Toast.makeText(getApplicationContext(), "방 이름은 공백일 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        } else {
            for (ListData list : roomDataFromListAct) {
                if (list.getRoomName().equals(roomName)) {
                    Log.d("데이터 체크 ", list.toString());
                    Toast.makeText(getApplicationContext(), "중복된 이름의 채팅방이 있습니다.", Toast.LENGTH_SHORT).show();
                    isRoomName = false;
                    return;
                }
            }
        }
        Toast.makeText(getApplicationContext(), "해당 이름으로 방을 생성할 수 있습니다.", Toast.LENGTH_SHORT).show();
        isRoomName = true;
    }


    @OnClick(R.id.btn_photo_add)
    public void BtnPhotoAdd() {

        //마시멜로부턴 내부 메모리 접근시 권한을 받아야한다 . 권한 체크쪽 로직
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "무권한", Toast.LENGTH_LONG).show();

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);

            return;

        } else {
            Toast.makeText(getApplicationContext(), "권한있음", Toast.LENGTH_LONG).show();
        }
//        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);

//        startActivityForResult(intent, PICK_FROM_ALBUM);

    }

    // 내부 메모리 권한을 받은 후에 리스너
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Toast.makeText(getApplicationContext(), "권한받았어요", Toast.LENGTH_SHORT).show();
                    startActivityForResult(Intent.createChooser(new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"), "choose an image"), PICK_FROM_ALBUM);
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getApplicationContext(), "권한을 안받으시면 해당작업을 할수 없습니다.", Toast.LENGTH_LONG).show();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }

    }

    //방만들기 위해 서버에 접근 후 DB에 기록하기
    private class SendHttp extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {

            http = HttpService.getInstance();
            http.setUrlString("/room/create");

            List<BasicNameValuePair> nameValuePairs = new ArrayList<>(4);

            nameValuePairs.add(new BasicNameValuePair("id", params[0]));
            nameValuePairs.add(new BasicNameValuePair("password", params[1]));
            nameValuePairs.add(new BasicNameValuePair("name", params[2]));
            nameValuePairs.add(new BasicNameValuePair("nickname", params[3]));

            http.setNameValuePairs(nameValuePairs);

            return http.sendData();

        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            String roomCode = "";

            if (result) {
                Log.d("json data", http.getJson());

                JSONObject jsonObj = null;

                if (http.getJson() != null) {

                    try {
                        jsonObj = new JSONObject(http.getJson());
                        String resultMsg = jsonObj.getString("result");
                        msg = jsonObj.getString("msg");
                        if (resultMsg.equals("0000")) {

                            roomCode = jsonObj.getString("code");
                            Log.d("룸코드 뭐임? ", roomCode);

                            //방만들때 이미지를 삽입안할경우.
                            if (jpegImagePath != null) {
                                if (progressDialog == null) {
                                    progressDialog = ProgressDialog.show(RoomCreateAct.this, "", "처리중입니다...", false);
                                    mSendPhoto = new SendHttpToPhoto();
                                    mSendPhoto.execute(roomCode, jpegImagePath);
                                    progressDialog.show();
                                }

                            } else {
                                Toast.makeText(getApplicationContext(), "채팅방!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), ChatRoomAct.class));
                            }
                        } else {
//                            Toast.makeText(getApplicationContext(), "중복된 룸 이름 존재.", Toast.LENGTH_LONG).show();
                            Log.d("틀린 항목  :   ", msg);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            } else {
                Toast.makeText(getApplicationContext(), "서버측 응답 없음", Toast.LENGTH_LONG).show();
            }
        }
    }

    //사진을 서버로 전송하기 위해 사용되는 AsyncTask
    private class SendHttpToPhoto extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {

            http = HttpService.getInstance();
            http.setUrlString("/room/photo");

            return http.sendPhoto(params);

        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            progressDialog = null;

            if (result) {
                Log.d("json data", http.getJson());

                JSONObject jsonObj;

                if (http.getJson() != null) {

                    try {
                        jsonObj = new JSONObject(http.getJson());
                        String resultMsg = jsonObj.getString("result");
                        msg = jsonObj.getString("msg");
                        if (resultMsg.equals("0000")) {

                            Toast.makeText(getApplicationContext(), "사진전송 완료", Toast.LENGTH_SHORT).show();
                            Log.d("RoomCreateAct", "사진전송 완료");

                            startActivity(new Intent(getApplicationContext(), ChatRoomAct.class));
                        } else {
//                            Toast.makeText(getApplicationContext(), "중복된 룸 이름 존재.", Toast.LENGTH_LONG).show();
                            Log.d("틀린 항목  :   ", msg);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            } else {
                Toast.makeText(getApplicationContext(), "서버측 응답 없음", Toast.LENGTH_LONG).show();
            }
        }
    }

    //이미지 앨범에서 클릭하고 난후에 로직
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap bitmap = null;

        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == PICK_FROM_ALBUM) {

            Uri uri = data.getData();

            try {
                //uri 로 이미지 경로 받아와서 bitmap 이미지 파일을 imageView 에 set 하고 서버에 넘기기 위해
                // bitmap - > jpeg 변환 작업
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                imageView.setImageBitmap(bitmap);

                mkImageBitmapToJpeg(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //bitmap - > jpeg
    private void mkImageBitmapToJpeg(Bitmap bitmap) {

        String ex_storage = Environment.getExternalStorageDirectory().getAbsolutePath();
        Log.d("절대 경로 ~", ex_storage);
        // Get Absolute Path in External Sdcard
        String folder_name = "/" + folderName + "/";
        String file_name = name + ".jpg";
        String image_path = ex_storage + folder_name;
        jpegImagePath = image_path + file_name;
        Log.d("jpegImage_path", jpegImagePath);

        File file_path;
        try {
            file_path = new File(image_path);
            if (!file_path.isDirectory()) {
                file_path.mkdirs();
            }
            FileOutputStream out = new FileOutputStream(image_path + file_name);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();

        } catch (FileNotFoundException exception) {
            Log.e("FileNotFoundException", exception.getMessage());
        } catch (IOException exception) {
            Log.e("IOException", exception.getMessage());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.room_create_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.create_room) {

            createRoom();
        }
        return super.onOptionsItemSelected(item);
    }

    private void createRoom() {

        String txtRoomName = chatRoomName.getText().toString().trim();
        String txtPassword = password.getText().toString().trim();
        String txtCheckPassword = checkPassword.getText().toString().trim();

        //중복확인에서 받아온 상태변수 방이 중복된 정보가 없다는 가정하에 수행
        if (isRoomName) {

            //비공개방으로 만들때(비밀번호가 있는방 만들때)
            if (switch1.isChecked()) {

                //비번 안비어있을때
                if (!txtPassword.equals("")) {
                    //룸 이름은 중복되지 않았으나 비밀번호가 재확인 비밀번호랑 일치하지않을경우.
                    if (!txtPassword.equals(txtCheckPassword)) {
                        Toast.makeText(getApplicationContext(), "비밀번호랑 확인란 값 다름", Toast.LENGTH_SHORT).show();
                    } else {

                        Log.d("id", userId);
                        Log.d("password", txtPassword);
                        Log.d("name", txtRoomName);
                        Log.d("nickname", nickname);
                        mSendHttp = new SendHttp();
                        mSendHttp.execute(userId, txtPassword, txtRoomName, nickname);

                    }

                } else {
                    Toast.makeText(getApplicationContext(), "비밀번호 공백 ㄴㄴ", Toast.LENGTH_SHORT).show();
                }
                //공개방으로 만들때(비밀번호x)
            } else {
                Toast.makeText(getApplicationContext(), "공개방입니다", Toast.LENGTH_SHORT).show();
            }

        } else if (!isRoomName) {
            Toast.makeText(getApplicationContext(), "중복 확인 먼저 해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public void hideSoftInputWindow(View edit_view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edit_view.getWindowToken(), 0);
    }
}


