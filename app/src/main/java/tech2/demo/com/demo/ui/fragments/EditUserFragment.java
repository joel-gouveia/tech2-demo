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
import android.widget.RelativeLayout;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.gson.Gson;

import tech2.demo.com.demo.Config;
import tech2.demo.com.demo.R;
import tech2.demo.com.demo.custom.PrefUtils;
import tech2.demo.com.demo.model.Users;
import tech2.demo.com.demo.ui.activities.AdminActivity;

public class EditUserFragment extends Fragment {
    private OnEditUserInteraction mListener;
    private Users mEditedUser;

    private EditText mFirstName, mLastName, mEmail, mExpectedCalories;
    private RadioButton mAdmin, mManager, mUser;
    private ImageView mConfirm, mCancel;
    private RelativeLayout mCaloriesLayout;

    public EditUserFragment() {
        // Required empty public constructor
    }

    public static EditUserFragment newInstance(String json) {
        EditUserFragment fragment = new EditUserFragment();
        Bundle args = new Bundle();
        args.putString("user", json);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mEditedUser = new Gson().fromJson(getArguments().getString("user"), Users.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_edit_user, container, false);

        mFirstName = (EditText) rootView.findViewById(R.id.fragment_edit_user_first_name);
        mLastName = (EditText) rootView.findViewById(R.id.fragment_edit_user_last_name);
        mEmail = (EditText) rootView.findViewById(R.id.fragment_edit_user_email);
        mExpectedCalories = (EditText) rootView.findViewById(R.id.fragment_edit_user_expected_calories);
        mAdmin = (RadioButton) rootView.findViewById(R.id.fragment_edit_user_admin);
        mManager = (RadioButton) rootView.findViewById(R.id.fragment_edit_user_manager);
        mUser = (RadioButton) rootView.findViewById(R.id.fragment_edit_user_user);
        mConfirm = (ImageView) rootView.findViewById(R.id.fragment_edit_user_confirm);
        mCancel = (ImageView) rootView.findViewById(R.id.fragment_edit_user_cancel);
        mCaloriesLayout = (RelativeLayout) rootView.findViewById(R.id.fragment_edit_user_expected_calories_layout);

        AdminActivity.mFab.setVisibility(View.GONE);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        AdminActivity.mToolbar.setTitle(getActivity().getResources().getString(R.string.fragment_edit_user_title));

        if(mEditedUser.getFirstName() != null){
            mFirstName.setText(mEditedUser.getFirstName());
        }

        if(mEditedUser.getLastName() != null){
            mLastName.setText(mEditedUser.getLastName());
        }

        if(mEditedUser.getPermissions() == 0){
            mCaloriesLayout.setVisibility(View.VISIBLE);
            if(!TextUtils.isEmpty(mEditedUser.getExpectedCalories())){
                mExpectedCalories.setText(mEditedUser.getExpectedCalories());
            } else {
                mExpectedCalories.setText(0);
            }
        } else {
            mCaloriesLayout.setVisibility(View.GONE);
        }

        mEmail.setText(mEditedUser.getEmail());

        // If the user editing the profile is only a manager it can't upgrade an account to admin
        String id = PrefUtils.getCurrentUserKey(getActivity());

        if(!id.equals("")) {
            Firebase mRef = new Firebase(Config.BASE_URL + Config.USERS_ENDPOINT + id);
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Users user = dataSnapshot.getValue(Users.class);
                    if (user.getPermissions() == 1)
                        mAdmin.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Log.d(Config.LOG_TAG, firebaseError.toString());
                }
            });
        } else {
            getActivity().finish();
        }

        if(mEditedUser.getPermissions() == Config.USER)
            mUser.setChecked(true);
        else if (mEditedUser.getPermissions() == Config.MANAGER)
            mManager.setChecked(true);
        else
            mAdmin.setChecked(true);
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
                mEditedUser.setFirstName(s.toString());
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
                mEditedUser.setLastName(s.toString());
            }
        });

        mExpectedCalories.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().equals(""))
                    mEditedUser.setExpectedCalories(s.toString());
            }
        });

        mAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditedUser.setPermissions(Config.ADMIN);
            }
        });

        mManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditedUser.setPermissions(Config.MANAGER);
            }
        });

        mUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditedUser.setPermissions(Config.USER);
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

    public void setOnEditUserInteraction(OnEditUserInteraction mListener) {
        this.mListener = mListener;
    }

    public interface OnEditUserInteraction{
        void onEdit(Users user);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void validate(){
        Firebase mRef = new Firebase(Config.BASE_URL + Config.USERS_ENDPOINT + mEditedUser.getId());
        mRef.setValue(mEditedUser);

        mListener.onEdit(mEditedUser);

        FragmentManager fm = getActivity().getSupportFragmentManager();
        fm.popBackStack();
    }
}
