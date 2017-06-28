package kr.co.dunet.app.goodallbeta;

import android.net.http.AndroidHttpClient;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by hanbit on 2017-05-23.
 */

public class HttpService {


    private static final String TAG = "HttpService";
    private String protocol = "http";
    private String dns = "";
    private String port = "";
    private String urlString = null;
    private String json = null;
    private static HttpService httpInstance = null;
    private List<BasicNameValuePair> nameValuePairs = null;

    public static synchronized HttpService getInstance() {
        if (httpInstance == null) {
            httpInstance = new HttpService();
        }
        return httpInstance;
    }

    public HttpService() {

        setProtocal(NetworkConfig.getInstance().getProtocal());
        setDns(NetworkConfig.getInstance().getDns());
        setPort(NetworkConfig.getInstance().getPort());

    }

    public void setProtocal(String protocol) {
        this.protocol = protocol;
    }

    public void setDns(String dns) {
        this.dns = dns;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setUrlString(String urlString) {
        this.urlString = urlString;
    }

    public void setNameValuePairs(List<BasicNameValuePair> nameValuePairs) {
        this.nameValuePairs = nameValuePairs;
    }

    public String getJson() {
        return json;
    }

    public boolean sendData() {

        try {

            json = null;
            Log.d("url 뭐 들엄?" , urlString);
            //해당 protocal , dns  , port , urlString 을 모두 받은 url 객체 생성
            URL url = new URL(protocol + "://" + dns + ":" + port + urlString);

            //커넥션..
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            //각종 옵션을 가진 con 객체 구성
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            con.setDefaultUseCaches(false);
            con.setConnectTimeout(6000);
            con.setReadTimeout(6000);

            try {
                con.connect();
                Log.d(TAG, "접속됨");
            } catch (SocketTimeoutException e) {
                Log.d(TAG, "소켓 접속 타임아웃 발생");
                //접속 해제
                con.disconnect();

                return false;
            }

            //해당 http인스턴스를 받는쪽에서 set 했을때 분기처리
            //nameValuePairs(키 , 벨류) 로 값을 저장해 http 로 보낼수 있는 객체
            if (nameValuePairs != null) {
                OutputStream outputStream = con.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                //로그인창에서 받아온 id 비번 값을 가진 nameValuePairs 를 id=?&password=? 이런식으로 파싱된 String을 버퍼로 실어 보냄
                bufferedWriter.write(makeUrlQuery(nameValuePairs));
                //다 보내고 닫기
                bufferedWriter.close();
                outputStream.close();
            }

            //응답으로 200번대 들어왔을떄.
            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Log.d(TAG, "통신 잘됬슴용");

                json = getText(con.getInputStream());
                con.disconnect();

                //AsyncTask 에 마무리 작업  result에 들어가서 거기서 또 분기처리
                return true;

            } else {

                Integer resultCode = con.getResponseCode();
                Log.d(TAG, "sendData 메소드의 result 코드 : " + resultCode.toString());

                con.disconnect();

                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }

    }

    public boolean getRoomList() {

        json = null;

        try {
            URL url = new URL(protocol + "://" + dns + ":" + port + urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            con.setDefaultUseCaches(false);
            con.setConnectTimeout(6000);
            con.setReadTimeout(6000);

            try {
                con.connect();
                Log.d(TAG, "접속성공");
            } catch (SocketTimeoutException e) {
                Log.d(TAG, "리스트 가져오기 소켓 타임아웃");
                con.disconnect();

                return false;
            }

            OutputStream outputStream = con.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            bufferedWriter.write(makeUrlQuery(nameValuePairs));
            //다 보내고 닫기
            bufferedWriter.close();
            outputStream.close();

            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Log.d(TAG, "통신 잘됬슴용");

                json = getText(con.getInputStream());
                con.disconnect();

                //AsyncTask 에 마무리 작업  result에 들어가서 거기서 또 분기처리
                return true;

            } else {

                Integer resultCode = con.getResponseCode();
                Log.d(TAG, "sendData 메소드의 result 코드 : " + resultCode.toString());

                con.disconnect();

                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean sendPhoto(String...postData){

        try {
            json = null;

            String imagePath = postData[1];
            if(imagePath != null){

            Log.d("FILE HTTP", imagePath);
            }

            // MultipartEntityBuilder 생성
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);


            // 문자열 및 데이터 추가

            //("photo", path, myId);
            builder.addTextBody("roomCode", postData[0],
                    ContentType.create("Multipart/related", "UTF-8"));
            builder.addPart("photo", new FileBody(new File(imagePath)));

            HttpEntity entity = builder.build();

            // 전송
            InputStream inputStream = null;
            AndroidHttpClient httpClient = AndroidHttpClient.newInstance("Android");

            HttpPost httpPost = new HttpPost(protocol + "://" + dns + ":" + port + urlString);

            httpPost.setEntity(entity);

            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity httpEntity = response.getEntity();

            inputStream = httpEntity.getContent();

            // 응답
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(inputStream, "UTF-8"));
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }
            inputStream.close();

            // 응답 결과
            json = stringBuilder.toString();
            Log.i("File Response", json);

            File file = new File(imagePath);
            file.delete();

            httpClient.close();

            return true;

        } catch (IOException e) {
            e.printStackTrace();

            return false;
        }
    }

    //로그인창에서 id 랑 비밀번호를 NameValuePair 객체로 받아서 그걸 분해해서 다시 StringBuilder 로 append해줌(urlQuery 제작)
    private String makeUrlQuery(List<BasicNameValuePair> param) {

        StringBuilder builder = new StringBuilder();

        boolean first = true;

        for (BasicNameValuePair pair : param) {
            if (first) {
                first = false;
            } else {
                builder.append("&");
            }

            try {
                builder.append(URLEncoder.encode(pair.getName(), "UTF-8"));
                builder.append("=");
                builder.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }

        Log.d(TAG, "보낼 url값  : " + builder.toString());

        return builder.toString();
    }

    private String getText(InputStream in) {

        StringBuilder builder = new StringBuilder();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            while (true) {
                String line = reader.readLine();
                if (null == line) {
                    break;
                }
                builder.append(line + "\n");
            }

            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "서버로 부터 받은 json 객체를 String 으로 가공 : " + builder.toString());
        return builder.toString();

    }

}
