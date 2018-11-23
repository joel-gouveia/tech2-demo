package tech2.demo.com.demo.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import tech2.demo.com.demo.Config;
import tech2.demo.com.demo.R;
import tech2.demo.com.demo.custom.PrefUtils;
import tech2.demo.com.demo.model.Users;

public class LoginActivity extends AppCompatActivity {

    EditText mEmail, mPassword;
    TextView mLogin, mRegister;
    ImageView mImage;

    Users currentUser;

    private void login() {
        mEmail.setError(null);
        mPassword.setError(null);

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(currentUser.getEmail())) {
            mEmail.setError(getString(R.string.login_activity_error_field_required));
            focusView = mEmail;
            cancel = true;
        }

        if (TextUtils.isEmpty(currentUser.getPassword())) {
            mPassword.setError(getString(R.string.login_activity_error_field_required));
            focusView = mPassword;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            Firebase ref = new Firebase(Config.BASE_URL);
            ref.authWithPassword(currentUser.getEmail(), currentUser.getPassword(), new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData authData) {
                    PrefUtils.setCurrentUserKey(LoginActivity.this, authData.getUid());
                    PrefUtils.setCurrentUserToken(LoginActivity.this, authData.getToken());

                    checkPermissionsToLogin();
                }
                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    Log.d(Config.LOG_TAG, firebaseError.toString());
                    // there was an error
                    switch (firebaseError.getCode()) {
                        case FirebaseError.USER_DOES_NOT_EXIST:
                            mEmail.setError(getResources().getString(R.string.login_activity_no_such_user));
                            mEmail.requestFocus();
                            break;
                        case FirebaseError.INVALID_PASSWORD:
                            mPassword.setError(getResources().getString(R.string.login_activity_wrong_password));
                            mPassword.requestFocus();
                            break;
                        case FirebaseError.LIMITS_EXCEEDED:
                            mEmail.setError(getResources().getString(R.string.login_activity_max_entries));
                            mEmail.requestFocus();
                            break;
                        case FirebaseError.NETWORK_ERROR:
                            mEmail.setError(getResources().getString(R.string.login_activity_other_error));
                            break;
                        default:
                            // handle other errors
                            mEmail.setError(getResources().getString(R.string.login_activity_other_error));
                            break;
                    }
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Initialize variables
        mEmail = (EditText) findViewById(R.id.activity_login_username);
        mPassword = (EditText) findViewById(R.id.activity_login_password);
        mLogin = (TextView) findViewById(R.id.activity_login_button_login);
        mRegister = (TextView) findViewById(R.id.activity_login_button_register);
        mImage = (ImageView) findViewById(R.id.activity_login_background_image);

        currentUser = new Users();
        currentUser.setEmail("");
        currentUser.setPassword("");
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check if the current user id saved can be logged in
        String currentUserToken = PrefUtils.getCurrentUserToken(this);

        if(!currentUserToken.equals("")) {
            Firebase ref = new Firebase(Config.BASE_URL);
            ref.authWithCustomToken(currentUserToken, new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData authData) {
                    PrefUtils.setCurrentUserKey(LoginActivity.this, authData.getUid());
                    PrefUtils.setCurrentUserToken(LoginActivity.this, authData.getToken());
                    checkPermissionsToLogin();
                }

                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    //there was an error so the token has most likely experienced
                    switch (firebaseError.getCode()) {
                        case FirebaseError.INVALID_TOKEN:
                            PrefUtils.setCurrentUserToken(LoginActivity.this, "");
                            PrefUtils.setCurrentUserKey(LoginActivity.this, "");
                            break;
                    }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                currentUser.setEmail(s.toString());
            }
        });

        mPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                currentUser.setPassword(s.toString());
            }
        });

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch the register user activity
                Intent intent = new Intent(LoginActivity.this, RegisterUserActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });
    }

    private void checkPermissionsToLogin(){
        //When we need to reset the login tokens
/*        PrefUtils.setCurrentUserKey(this, "");
        PrefUtils.setCurrentUserToken(this, "");*/

        // Check the permissions so we can decide which path on the activity we go to.
        // Check if the user is active or not
        String currentUserKey = PrefUtils.getCurrentUserKey(this);
        if(!currentUserKey.equals("")) {
            Firebase mRef = new Firebase(Config.BASE_URL + Config.USERS_ENDPOINT + currentUserKey);
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Intent intent;
                    Users user = dataSnapshot.getValue(Users.class);

                    if(!user.isActive()){
                        mEmail.setError(getResources().getString(R.string.login_activity_no_such_user));
                        mEmail.requestFocus();
                        PrefUtils.setCurrentUserKey(LoginActivity.this, "");
                        PrefUtils.setCurrentUserToken(LoginActivity.this, "");
                    } else {
                        if (user.getPermissions() == 0) {
                            intent = new Intent(LoginActivity.this, MealsActivity.class);
                        } else {
                            intent = new Intent(LoginActivity.this, AdminActivity.class);
                        }
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Log.d(Config.LOG_TAG, firebaseError.toString());
                }
            });
        }
    }
}
