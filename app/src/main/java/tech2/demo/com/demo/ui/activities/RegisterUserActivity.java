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
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;

import tech2.demo.com.demo.Config;
import tech2.demo.com.demo.R;
import tech2.demo.com.demo.custom.PrefUtils;
import tech2.demo.com.demo.model.Users;

public class RegisterUserActivity extends AppCompatActivity {

    EditText mEmail, mPassword;
    TextView mRegister;

    Users userModel;

    private void register() {
        mEmail.setError(null);
        mPassword.setError(null);

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(userModel.getEmail())) {
            mEmail.setError(getString(R.string.login_activity_error_field_required));
            focusView = mEmail;
            cancel = true;
        } else if (TextUtils.isEmpty(userModel.getPassword())) {
            mPassword.setError(getString(R.string.login_activity_error_field_required));
            focusView = mPassword;
            cancel = true;
        } else if (userModel.getPassword().length() < 8 || userModel.getPassword().length() > 16) {
            mPassword.setError(getResources().getString(R.string.login_activity_password_size));
            focusView = mPassword;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            final Firebase mRef = new Firebase(Config.BASE_URL);
            mRef.createUser(userModel.getEmail(), userModel.getPassword(), new Firebase.ValueResultHandler<Map<String, Object>>() {
                @Override
                public void onSuccess(Map<String, Object> result) {
                    mRef.authWithPassword(userModel.getEmail(), userModel.getPassword(), new Firebase.AuthResultHandler() {
                        @Override
                        public void onAuthenticated(AuthData authData) {

                            Firebase ref2 = new Firebase(Config.BASE_URL + Config.USERS_ENDPOINT);
                            Users user = new Users();
                            user.setId(authData.getUid());
                            user.setFirstName("");
                            user.setLastName("");
                            user.setPermissions(0);
                            user.setEmail(userModel.getEmail());
                            user.setExpectedCalories("0");
                            user.setActive(true);
                            ref2.child(authData.getUid()).setValue(user);

                            PrefUtils.setCurrentUserKey(RegisterUserActivity.this, authData.getUid());
                            PrefUtils.setCurrentUserToken(RegisterUserActivity.this, authData.getToken());
                            Intent intent;
                            intent = new Intent(RegisterUserActivity.this, MealsActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            finish();
                        }

                        @Override
                        public void onAuthenticationError(FirebaseError firebaseError) {
                            //Failed to log in
                            Log.d(Config.LOG_TAG, firebaseError.toString());
                            finish();
                        }
                    });
                }

                @Override
                public void onError(FirebaseError firebaseError) {
                    // there was an error
                    Log.d(Config.LOG_TAG, firebaseError.toString());
                    switch (firebaseError.getCode()) {
                        case FirebaseError.EMAIL_TAKEN:
                            mEmail.setError(getResources().getString(R.string.login_activity_email_inuse));
                            mEmail.requestFocus();
                            break;
                        case FirebaseError.INVALID_EMAIL:
                            mEmail.setError(getResources().getString(R.string.login_activity_invalid_email));
                            mEmail.requestFocus();
                            break;
                        case FirebaseError.INVALID_PASSWORD:
                            mPassword.setError(getResources().getString(R.string.login_activity_invalid_password));
                            mPassword.requestFocus();
                            break;
                        default:
                            mEmail.setError(getResources().getString(R.string.login_activity_other_error));
                            mEmail.requestFocus();
                            break;
                    }
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        //Initialize variables
        mEmail = (EditText) findViewById(R.id.activity_register_username);
        mPassword = (EditText) findViewById(R.id.activity_register_password);

        mRegister = (TextView) findViewById(R.id.activity_register_button_register);

        userModel = new Users();
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
                userModel.setEmail(s.toString());
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
                userModel.setPassword(s.toString());
            }
        });

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}
