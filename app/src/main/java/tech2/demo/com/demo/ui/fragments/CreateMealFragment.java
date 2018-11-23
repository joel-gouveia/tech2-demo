package tech2.demo.com.demo.ui.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Calendar;

import tech2.demo.com.demo.Config;
import tech2.demo.com.demo.R;
import tech2.demo.com.demo.common.DateUtils;
import tech2.demo.com.demo.custom.PrefUtils;
import tech2.demo.com.demo.model.Meals;
import tech2.demo.com.demo.model.Users;
import tech2.demo.com.demo.ui.activities.AdminActivity;
import tech2.demo.com.demo.ui.activities.MealsActivity;

public class CreateMealFragment extends Fragment {
    private OnCreateMealFragmentListener mListener;
    private ImageView mConfirmation, mCancel;
    private EditText mDescription, mNumberCalories;
    private TextView mDate, mTime;

    private String id;
    private boolean isAdmin;

    Meals mMeal;

    public CreateMealFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (OnCreateMealFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnCreateMealFragmentListener");
        }
    }

    public static CreateMealFragment newInstance(String id) {
        CreateMealFragment fragment = new CreateMealFragment();
        if (!TextUtils.isEmpty(id)) {
            Bundle args = new Bundle();
            args.putString("user", id);
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            id = getArguments().getString("user");
            isAdmin = true;
        }

        //Define if the fab icon appears or not and the title on the toolbar
        if (MealsActivity.mFab != null) {
            MealsActivity.mFab.setVisibility(View.GONE);
            MealsActivity.mToolbar.setTitle(getResources().getString(R.string.fragment_create_meal_title));
        }
        else {
            AdminActivity.mFab.setVisibility(View.GONE);
            AdminActivity.mToolbar.setTitle(getResources().getString(R.string.fragment_create_meal_title));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_create_meal, container, false);

        mConfirmation = (ImageView) rootView.findViewById(R.id.fragment_create_meal_confirm);
        mCancel = (ImageView) rootView.findViewById(R.id.fragment_create_meal_cancel);
        mDate = (TextView) rootView.findViewById(R.id.fragment_create_meal_date);
        mTime = (TextView) rootView.findViewById(R.id.fragment_create_meal_time);
        mDescription = (EditText) rootView.findViewById(R.id.fragment_create_meal_description);
        mNumberCalories = (EditText) rootView.findViewById(R.id.fragment_create_meal_number_calories);

        mDate.setText(DateUtils.getCurrentDate());
        mTime.setText(DateUtils.getCurrentHour());
        mNumberCalories.setText("0");

        mMeal = new Meals();
        mMeal.setDate(DateUtils.getCurrentDate());
        mMeal.setTime(DateUtils.getCurrentHour());
        mMeal.setNumberCalories(0);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        mConfirmation.setOnClickListener(new View.OnClickListener() {
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

        mDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mMeal.setDescription(s.toString());
            }
        });

        mNumberCalories.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty() && !s.toString().contains("."))
                    mMeal.setNumberCalories(Integer.parseInt(s.toString()));
                else
                    mMeal.setNumberCalories(0);
            }
        });

        mDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                mMeal.setDate(DateUtils.formatDate(dayOfMonth, monthOfYear, year));
                                mDate.setText(mMeal.getDate());
                                mDate.setTextColor(getResources().getColor(android.R.color.black));
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        mTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Time
                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                mMeal.setTime(DateUtils.formatTime(hourOfDay, minute));
                                mTime.setText(mMeal.getTime());
                                mTime.setTextColor(getResources().getColor(android.R.color.black));
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });
    }

    private void validate() {

        mDescription.setError(null);

        if (TextUtils.isEmpty(mMeal.getDescription())) {
            mDescription.setError(getActivity().getResources().getString(R.string.login_activity_error_field_required));
            mDescription.requestFocus();
        } else {
            request();
        }
    }


    private void request() {

        String userId;
        //If it is the user inserting a new meal
        if (!isAdmin)
            userId = PrefUtils.getCurrentUserKey(getActivity());
        else
        //If it comes from an admin then we set another user not the current one
            userId = id;

        if(!userId.equals("")) {
            Firebase mRef = new Firebase(Config.BASE_URL
                    + Config.MEALS_ENDPOINT
                    + userId);
            Firebase mNewRef = mRef.push();

            String id = mNewRef.getKey();
            mMeal.setId(id);
            mNewRef.setValue(mMeal);

            final Firebase mRef2 = new Firebase(Config.BASE_URL + Config.USERS_ENDPOINT + userId);
            mRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Users user = dataSnapshot.getValue(Users.class);
                    user.setNumberMeals(user.getNumberMeals() + 1);
                    mRef2.setValue(user);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Log.d(Config.LOG_TAG, firebaseError.toString());
                }
            });

            mListener.onNewMealCreated(mMeal);

            FragmentManager fm = getActivity().getSupportFragmentManager();
            fm.popBackStack();
        } else {
            getActivity().finish();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnCreateMealFragmentListener {
        void onNewMealCreated(Meals meal);
    }
}
