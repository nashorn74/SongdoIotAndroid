package net.iot.helloworld;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class NaverOpenAPIActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_naver_open_api);
    }

    public void sendRequest(View view) {
        EditText keywordText = (EditText)findViewById(R.id.keyword);
        new LoadNaverBlogInfo().execute(keywordText.getText().toString());
    }

    class LoadNaverBlogInfo extends AsyncTask<String,String,String> {
        ProgressDialog dialog = new ProgressDialog(NaverOpenAPIActivity.this);
        @Override
        protected String doInBackground(String... params) {
            String clientId = "MW2x6sD9lTqKIk9ZGlXj";
            String clientSecret = "S1YmSsDJOa";
            StringBuffer response = new StringBuffer();
            try {
                String text = URLEncoder.encode(params[0], "UTF-8");
                String apiURL = "https://openapi.naver.com/v1/search/blog.json?query="+ text;
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("X-Naver-Client-Id", clientId);
                con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
                int responseCode = con.getResponseCode();
                BufferedReader br;
                if(responseCode==200) { // 정상 호출
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                } else {  // 에러 발생
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                }
                String inputLine;
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response.toString();
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("네이버 블로그 정보 로딩 중...");
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            //Toast.makeText(NaverOpenAPIActivity.this, s, Toast.LENGTH_LONG).show();
            try {
                //JSON 문자열 -> JSON 객체로 변환
                JSONObject json = new JSONObject(s);
                //JSON 객체에서 items 키값의 배열을 추출 --> 여기에만 해당
                JSONArray items = json.getJSONArray("items");
                for (int i = 0; i < items.length(); i++) {//items 배열 안의 객체 정보 개별 추출
                    JSONObject obj = items.getJSONObject(i);
                    String title = obj.getString("title");
                    String link = obj.getString("link");
                    String description = obj.getString("description");
                    String bloggername = obj.getString("bloggername");
                    String postdate = obj.getString("postdate");
                    Log.i("title", title);
                }
                int display = json.getInt("display");
                //Toast.makeText(NaverOpenAPIActivity.this, items.length()+"", Toast.LENGTH_LONG).show();
            } catch (Exception e) { e.printStackTrace(); }
        }
    }
}
