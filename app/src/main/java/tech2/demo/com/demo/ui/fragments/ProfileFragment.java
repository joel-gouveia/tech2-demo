package tech2.demo.com.demo.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import tech2.demo.com.demo.Config;
import tech2.demo.com.demo.R;
import tech2.demo.com.demo.custom.PrefUtils;
import tech2.demo.com.demo.model.Meals;
import tech2.demo.com.demo.model.Users;
import tech2.demo.com.demo.ui.activities.AdminActivity;
import tech2.demo.com.demo.ui.activities.MealsActivity;

public class ProfileFragment extends Fragment {

    private EditText mFirstName, mLastName, mEmail, mMaxCalories;
    private ImageView mConfirm, mCancel;
    private RelativeLayout relativeLayout;

    private Users mUser;
    private int mTotalCalories;

    private Firebase mRef;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        mFirstName = (EditText) rootView.findViewById(R.id.fragment_profile_first_name);
        mLastName = (EditText) rootView.findViewById(R.id.fragment_profile_last_name);
        mEmail = (EditText) rootView.findViewById(R.id.fragment_profile_email);
        mMaxCalories = (EditText) rootView.findViewById(R.id.fragment_profile_expected_calories);
        relativeLayout = (RelativeLayout) rootView.findViewById(R.id.fragment_profile_calories_layout);
        mConfirm = (ImageView) rootView.findViewById(R.id.fragment_profile_confirm);
        mCancel = (ImageView) rootView.findViewById(R.id.fragment_profile_cancel);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        //Fetchs the user and all his meals
        final String userId = PrefUtils.getCurrentUserKey(getActivity());

        if(!userId.equals("")) {
            mRef = new Firebase(Config.BASE_URL + Config.USERS_ENDPOINT + userId);
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mUser = dataSnapshot.getValue(Users.class);

                    if (mUser.getFirstName() != null)
                        mFirstName.setText(mUser.getFirstName());
                    if (mUser.getLastName() != null)
                        mLastName.setText(mUser.getLastName());
                    mEmail.setText(mUser.getEmail());

                    Firebase mRef2 = new Firebase(Config.BASE_URL + Config.MEALS_ENDPOINT + userId);
                    mRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            mTotalCalories = 0;
                            if (dataSnapshot.getValue() != null) {
                                for (DataSnapshot data : dataSnapshot.getChildren()) {
                                    Meals meal = data.getValue(Meals.class);
                                    mTotalCalories += meal.getNumberCalories();
                                }
                            }

                            if (mUser.getPermissions() == 0) {
                                mMaxCalories.setText(mUser.getExpectedCalories());

                                if (mTotalCalories <= Integer.parseInt(mUser.getExpectedCalories())) {
                                    mMaxCalories.setTextColor(getActivity().getResources().getColor(R.color.foodie_green));
                                } else {
                                    mMaxCalories.setTextColor(getActivity().getResources().getColor(R.color.foodie_red));
                                }
                            } else {
                                relativeLayout.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                            Log.d(Config.LOG_TAG, firebaseError.toString());
                        }
                    });
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Log.d(Config.LOG_TAG, firebaseError.toString());
                }
            });
        } else {
            getActivity().finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(MealsActivity.mFab != null){
            MealsActivity.mToolbar.setTitle(getActivity().getResources().getString(R.string.fragment_profile_toolbar_title));
            MealsActivity.mFab.setVisibility(View.GONE);
        } else {
            AdminActivity.mToolbar.setTitle(getActivity().getResources().getString(R.string.fragment_profile_toolbar_title));
            AdminActivity.mFab.setVisibility(View.GONE);
        }

        mFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mUser.setFirstName(s.toString());
                mConfirm.setVisibility(View.VISIBLE);
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
                mUser.setLastName(s.toString());
                mConfirm.setVisibility(View.VISIBLE);
            }
        });

        mMaxCalories.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().equals(""))
                    mUser.setExpectedCalories(s.toString());
                mConfirm.setVisibility(View.VISIBLE);
            }
        });

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRef.setValue(mUser);

                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.popBackStack();
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

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
