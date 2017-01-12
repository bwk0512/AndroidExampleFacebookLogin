package org.bwandroid.logintest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by hyo99 on 2016-08-10.
 */

public class NextActivity extends Activity {

    TextView textviewName;
    ImageView imageviewPhoto;
    Button buttonLogout;

    String fbId, fbName;
    Bitmap myBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);

        textviewName = (TextView) findViewById(R.id.textView_Name);
        imageviewPhoto = (ImageView) findViewById(R.id.imageView_Photo);

        fbId = getIntent().getStringExtra("id");
        fbName = getIntent().getStringExtra("name");
        Log.d("tag", "id : " + fbId);
        Log.d("tag", "name : " + fbName);

        //  안드로이드에서 네트워크 관련 작업을 할 때는
        //  반드시 메인 스레드가 아닌 별도의 작업 스레드에서 작업해야 합니다.
        Thread mThread = new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://graph.facebook.com/" + fbId + "/picture?type=large"); // URL 주소를 이용해서 URL 객체 생성

                    //  아래 코드는 웹에서 이미지를 가져온 뒤
                    //  이미지 뷰에 지정할 Bitmap을 생성하는 과정
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    myBitmap = BitmapFactory.decodeStream(is);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        mThread.start(); // 웹에서 이미지를 가져오는 작업 스레드 실행.
        try {
            //  메인 스레드는 작업 스레드가 이미지 작업을 가져올 때까지
            //  대기해야 하므로 작업스레드의 join() 메소드를 호출해서
            //  메인 스레드가 작업 스레드가 종료될 까지 기다리도록 합니다.

            mThread.join();

            fbId = getIntent().getStringExtra("id");
            fbName = getIntent().getStringExtra("name");

            //  이제 작업 스레드에서 이미지를 불러오는 작업을 완료했기에
            //  UI 작업을 할 수 있는 메인스레드에서 이미지뷰에 이미지를 지정합니다.
            textviewName.setText(fbName);
            imageviewPhoto.setImageBitmap(myBitmap);
            //mHandler.sendMessage(mHandler.obtainMessage());

        } catch (InterruptedException e) {
        }

        // 로그아웃 버튼 리스너
        buttonLogout = (Button) findViewById(R.id.button_Logout);
        buttonLogout.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Logout", Toast.LENGTH_SHORT).show();
                LoginManager.getInstance().logOut();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    /**
     * 뒤로가기 키를 눌렀을 때 동작하는 오버라이딩 함수
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK :
                finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
