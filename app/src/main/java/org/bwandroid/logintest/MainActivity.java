package org.bwandroid.logintest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;
import org.json.JSONException;
import java.util.Arrays;


public class MainActivity extends Activity {//extends AppCompatActivity {

    private String strHash = ""; // KeyHash를 받을 변수

    String fbId; // 페이스북 고유ID
    String fbName; // 페이스북 이름
    String fbEmail; // 페이스북 이메일

    LoginButton loginButton; // 페이스북 로그인 버튼

    CallbackManager callbackManager; // 콜백

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 페이스북 sdk 초기화
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_main);

        // 로그인 응답을 처리할 콜백 관리자를 만듦
        callbackManager = CallbackManager.Factory.create();

        loginButton = (LoginButton) findViewById(R.id.login_button);

        // 페이스북에서 제공할 데이터 권한
        loginButton.setReadPermissions(Arrays.asList("public_profile","email"));

        // 이미 로그인 상태면 loginButton 자동실행
        if(isLogin()) {
            LoginManager.getInstance().logOut();
            loginButton.performClick();
        }

        // loginButton
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(getApplicationContext(), "Login", Toast.LENGTH_SHORT).show();
                loginButton.setVisibility(View.INVISIBLE);

                //GraphRequest 클래스에는 지정된 액세스 토큰의 사용자 데이터를 가져오는 newMeRequest 메서드가 있다
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                                try {
                                    Log.d("tag", "Input Profile Data");
                                    fbId = jsonObject.getString("id");
                                    fbName = jsonObject.getString("name");
                                    fbEmail = jsonObject.getString("email");

                                    Intent intent = new Intent(getApplicationContext(), NextActivity.class);
                                    intent.putExtra("id", fbId);
                                    intent.putExtra("name", fbName);
                                    startActivity(intent);
                                    finish();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,picture.type(large)");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "로그인을 취소 하였습니다!", Toast.LENGTH_SHORT).show();
                Log.d("fb_login_sdk", "callback cancel");
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), "에러가 발생하였습니다", Toast.LENGTH_SHORT).show();
                Log.d("fb_login_sdk", "callback onError");
            }
        });

        /*
         * 페이스북을 쓰기 위해서는 해쉬키가 필요하다
         * KeyHash를 Log로 추출   Hash : yKQywhTgC6fnC8ZTu/fzSV2uU3g=   // ctmVa70LW0WlzjD4UJCd8mNnqMc=
        try
         {
            PackageInfo info = this.getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature sig : info.signatures)
            {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(sig.toByteArray());
                strHash = Base64.encodeToString(md.digest(), Base64.DEFAULT);
            }
        }
        catch (PackageManager.NameNotFoundException e)
        {
            Log.d("UnityFacebookTest", "Error Hashkey not found");
        }
        catch (NoSuchAlgorithmException e)
        {
            Log.d("UnityFacebookTest", "Error Hashkey no such algorithm");
        }
        ///< 해시키를 출력. 이 값을 페이스북 개발자 센터의 등록한 앱에 기입
        Log.d("UnityFacebookTest", "Hash : " + this.strHash);
        */
    }

    /**
     * Facebook SDK 로그인 또는 공유와 통합한 모든 액티비티와 프래그먼트에서
     * onActivityResult를 callbackManager에 전달해야 한다
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 현재 로그인 상태를 검사하는 함수
     * @return 로그인 중이면 1을 아니면 0을 반환
     */
    public boolean isLogin() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }
}

