package kr.co.dunet.app.goodallbeta;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;

import butterknife.BindView;
import butterknife.OnClick;

public class MainAct extends AppCompatActivity {

    private static String CLIENT_ID = "Fb3lxrhhepgCSF8Qt2Y5";
    private static String CLIENT_NAME = "네이버 로그인";
    private static String CLIENT_SECRET = "2wDpi5Vb3A";
    private static final String TAG = "MainAct";
//    Button btn_logout;
//    private SignInButton signInButton;
    private boolean btnOnOff = false;
    @BindView(R.id.btn_google)
    Button BtnGoogle;

    private FirebaseAuth mAuth;
    private FirebaseUser mfFirebaseUser;
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 1004;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private static OAuthLogin mOAuthLoginInstance;
    private static Context mContext;
    private static OAuthLoginButton btn_naver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ChatApplication app = (ChatApplication) getApplicationContext();
        Log.d("main " , app.getUserId());

        super.onCreate(savedInstanceState);
        try {
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setCustomView(R.layout.actionbar_login);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        setContentView(R.layout.act_main);
        mContext = this;

//        btn_logout = (Button) findViewById(R.id.btn_logout);

        init();

    }


    public void btnLogin(View v) {

        startActivity(new Intent(mContext, LoginAct.class));
    }

    public void btnJoin(View v) {
        startActivity(new Intent(mContext, JoinAct.class));
    }

//    public void btnLogout(View v) {
//
//        mAuth.signOut();
//
//        if (mOAuthLoginInstance.getAccessToken(mContext) != null) {
//            mOAuthLoginInstance.logout(mContext);
//
//            btn_logout.setText("LogIn");
//        }
//
//        Toast.makeText(mContext, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
//
//    }

    @OnClick(R.id.btn_google)
    public void BtnOnClicktoGoogle() {

        Toast.makeText(getApplicationContext() , "놀림" , Toast.LENGTH_LONG).show();

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);

        if (mfFirebaseUser != null) {
            Toast.makeText(getApplicationContext(), "환영합니다 ~  " + mfFirebaseUser.getDisplayName() + "님 ", Toast.LENGTH_SHORT).show();
        }
    }


    public void btnGuestClick(View v) {
        startActivity(new Intent(mContext, GuestAct.class));
    }

    public void init() {

        Log.d(TAG, "init 시작");

        naverSignIn();
        googleSignIn();
    }

    private void naverSignIn() {

        mOAuthLoginInstance = OAuthLogin.getInstance();

        mOAuthLoginInstance.init(mContext, CLIENT_ID, CLIENT_SECRET, CLIENT_NAME);

        btn_naver = (OAuthLoginButton) findViewById(R.id.btn_naver);
        btn_naver.setOAuthLoginHandler(new OAuthLoginHandler() {
            @Override
            public void run(boolean success) {
                if (success) {
                    String accessToken = mOAuthLoginInstance.getAccessToken(mContext);
                    String refreshToken = mOAuthLoginInstance.getRefreshToken(mContext);
                    long expiresAt = mOAuthLoginInstance.getExpiresAt(mContext);
                    String tokenType = mOAuthLoginInstance.getTokenType(mContext);
                    Log.d(TAG, "--------------------------" + tokenType.toString());
                    startActivity(new Intent(mContext, ListAct.class));
                    btnOnOff = true;

                } else {
                    String errorCode = mOAuthLoginInstance.getLastErrorCode(mContext).getCode();
                    String errorDesc = mOAuthLoginInstance.getLastErrorDesc(mContext);
                    Toast.makeText(mContext, "errorCode:" + errorCode + ", errorDesc:" + errorDesc, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void googleSignIn() {

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
//                     User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                        Toast.makeText(getApplicationContext(), "You got an Error", Toast.LENGTH_LONG).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        mfFirebaseUser = mAuth.getCurrentUser();

//        signInButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
//                startActivityForResult(signInIntent, RC_SIGN_IN);
//
//                if (mfFirebaseUser != null) {
//                    Toast.makeText(getApplicationContext(), "환영합니다 ~  " + mfFirebaseUser.getDisplayName() + "님 ", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

        Log.d(TAG, mAuth.toString());
    }

    public void facebookSignIn() {

//        callbackManager = CallbackManager.Factory.create();
//
//
//        facebookLoginBtn = (LoginButton) findViewById(R.id.btn_facebook);
//        facebookLoginBtn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                FacebookSdk.sdkInitialize(getApplicationContext());
//                Log.i(TAG , "UserId :  " + loginResult.getAccessToken().getUserId());
//                Log.i(TAG , "Auth Token :  " + loginResult.getAccessToken().getToken());
//
//            }
//
//            @Override
//            public void onCancel() {
//
//                Log.w(TAG, "cancel");
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//                Log.e(TAG ,"Error " , error);
//            }
//        });
//        if(mOAuthLoginInstance.getAccessToken(mContext) !=null){
//            startActivity(new Intent(mContext , ListAct.class));
//        }
    }

    public void hideSoftInputWindow(View edit_view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edit_view.getWindowToken(), 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Log.d(TAG, "드롱::::::::::::::::::::::::::::::::::::::::::::::::?");
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                firebaseAuthWithGoogle(acct);

                Log.d(TAG, "result.isSuccess ::::::::::::::::::::");

                // Get account information
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(MainAct.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // ...
                    }
                });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}



