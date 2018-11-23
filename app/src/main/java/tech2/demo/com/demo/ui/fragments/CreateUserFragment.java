package tech2.demo.com.demo.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Map;

import tech2.demo.com.demo.Config;
import tech2.demo.com.demo.R;
import tech2.demo.com.demo.custom.PrefUtils;
import tech2.demo.com.demo.model.Users;

public class CreateUserFragment extends Fragment {
    private EditText mFirstName, mLastName, mEmail;
    private RadioButton mUser, mManager, mAdmin;
    private ImageView mConfirm, mCancel;

    private Users mNewUser;

    public CreateUserFragment() {
        // Required empty public constructor
    }

    public static CreateUserFragment newInstance() {
        return new CreateUserFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_create_user, container, false);
        mFirstName = (EditText) rootView.findViewById(R.id.fragment_create_user_first_name);
        mLastName = (EditText) rootView.findViewById(R.id.fragment_create_user_last_name);
        mEmail = (EditText) rootView.findViewById(R.id.fragment_create_user_email);
        mUser = (RadioButton) rootView.findViewById(R.id.fragment_create_user_radio_user);
        mManager = (RadioButton) rootView.findViewById(R.id.fragment_create_user_radio_manager);
        mAdmin = (RadioButton) rootView.findViewById(R.id.fragment_create_user_radio_admin);
        mConfirm = (ImageView) rootView.findViewById(R.id.fragment_create_user_confirm);
        mCancel = (ImageView) rootView.findViewById(R.id.fragment_create_user_cancel);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        String userId = PrefUtils.getCurrentUserKey(getActivity());

        if (!userId.equals("")) {
            Firebase mRef = new Firebase(Config.BASE_URL + Config.USERS_ENDPOINT + userId);
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Users user = dataSnapshot.getValue(Users.class);

                    //A manager can not create an admin
                    if (user.getPermissions() < 2) {
                        mAdmin.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Log.d(Config.LOG_TAG, firebaseError.toString());
                }
            });
        } else {
            getActivity().finish();
        }

        mNewUser = new Users();
    }

    @Override
    public void onResume() {
        super.onResume();

        mFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mNewUser.setFirstName(s.toString());
            }
        });

        mLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mNewUser.setLastName(s.toString());
            }
        });

        mEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mNewUser.setEmail(s.toString());
            }
        });

        mUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNewUser.setPermissions(Config.USER);
            }
        });

        mManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNewUser.setPermissions(Config.MANAGER);
            }
        });

        mAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNewUser.setPermissions(Config.ADMIN);
            }
        });

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.popBackStack();
            }
        });
    }

    private void validate() {
        mEmail.setError(null);

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(mNewUser.getEmail())) {
            mEmail.setError(getActivity().getResources().getString(R.string.login_activity_error_field_required));
            cancel = true;
            focusView = mEmail;
        }

        String password = getActivity().getResources().getString(R.string.default_password);

        if (cancel) {
            focusView.requestFocus();
        } else {
            final Firebase mRef = new Firebase(Config.BASE_URL);
            mRef.createUser(mNewUser.getEmail(), password, new Firebase.ValueResultHandler<Map<String, Object>>() {
                @Override
                public void onSuccess(Map<String, Object> result) {
                    mNewUser.setId(String.valueOf(result.get("uid")));
                    mNewUser.setExpectedCalories("0");
                    mNewUser.setActive(true);
                    mRef.child("users").child(mNewUser.getId()).setValue(mNewUser);

                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    fm.popBackStack();
                }

                @Override
                public void onError(FirebaseError firebaseError) {
                    switch (firebaseError.getCode()) {
                        // If the email was already taken then reactivate the account
                        case FirebaseError.EMAIL_TAKEN:
                            final Firebase mRef2 = new Firebase(Config.BASE_URL + Config.USERS_ENDPOINT);
                            mRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getChildrenCount() > 0) {
                                        Users user;
                                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                            user = postSnapshot.getValue(Users.class);
                                            if (user.getEmail().equals(mNewUser.getEmail()) && !user.isActive()) {
                                                user.setActive(true);
                                                mRef2.child(user.getId()).setValue(user);
                                            }
                                        }
                                        FragmentManager fm = getActivity().getSupportFragmentManager();
                                        fm.popBackStack();
                                    }
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {
                                    Log.d(Config.LOG_TAG, firebaseError.toString());
                                }
                            });
                            break;
                        case FirebaseError.INVALID_EMAIL:
                            mEmail.setError(getResources().getString(R.string.login_activity_invalid_email));
                            mEmail.requestFocus();
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
    public void onDetach() {
        super.onDetach();
    }
}
