package org.hairyhearts;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.baasbox.android.BaasBox;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;
import com.baasbox.android.RequestToken;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class LoginActivity extends Activity {
    private final static String TAG = "MainActivity";
    private final static String SIGNUP_TOKEN_KEY = "signup_token_key";

    private String mUsername;
    private EditText mUsernameView;

    private String mPassword;
    private EditText mPasswordView;

    private RequestToken mSignupOrLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (savedInstanceState != null) {
            mSignupOrLogin = savedInstanceState.getParcelable(SIGNUP_TOKEN_KEY);
        }

        mUsernameView = (EditText) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);

        findViewById(R.id.loginButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin(false);
            }
        });

        findViewById(R.id.signupButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin(true);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mSignupOrLogin != null) {
            mSignupOrLogin.suspend();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSignupOrLogin != null) {
            mSignupOrLogin.resume(onComplete);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mSignupOrLogin != null) {
            outState.putParcelable(SIGNUP_TOKEN_KEY, mSignupOrLogin);
        }
    }

    private void signupWithBaasBox(boolean newUser) {
        BaasUser user = BaasUser.withUserName(mUsername);
        user.setPassword(mPassword);

        if (newUser) {
            mSignupOrLogin = user.signup(onComplete);
        } else {
            mSignupOrLogin = user.login(onComplete);
        }
    }

    private final BaasHandler<BaasUser> onComplete = new BaasHandler<BaasUser>() {
        @Override
        public void handle(BaasResult<BaasUser> result) {
            mSignupOrLogin = null;

            if (result.isFailed()) {
                Log.d(TAG, "ERROR: " + result.error());
            }

            completeLogin(result.isSuccess());
        }
    };

    private void completeLogin(boolean success) {
        mSignupOrLogin = null;

        if (success) {
            new GetRegistrationId(this).execute();            
        } else {
            mPasswordView.setError(getString(R.string.error_incorrect_password));
            mPasswordView.requestFocus();
        }
    }

    public void attemptLogin(boolean newUser) {
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        mUsername = mUsernameView.getText().toString();
        mPassword = mPasswordView.getText().toString();

        boolean cancel = false;

        View focusView = null;
        if (TextUtils.isEmpty(mPassword)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (mPassword.length() < 4) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(mUsername)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            signupWithBaasBox(newUser);
        }
    }
    
    public void complete() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
    
    private static class GetRegistrationId extends AsyncTask<Void, Void, Void> {
        private LoginActivity context;
        
        public GetRegistrationId(LoginActivity context) {
            this.context = context;
        }
        
        @Override
        protected Void doInBackground(Void... params) {
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);

            try {
                String regId = gcm.register(Config.SENDER_ID);
                BaasBox.getDefault().registerPushSync(regId);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            
            return null;
        }
        
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);       
            
            context.complete();
        }        
    }
}
