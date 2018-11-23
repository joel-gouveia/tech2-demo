package tech2.demo.com.demo.ui.fragments;

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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.gson.Gson;

import java.util.Calendar;

import tech2.demo.com.demo.Config;
import tech2.demo.com.demo.R;
import tech2.demo.com.demo.common.DateUtils;
import tech2.demo.com.demo.custom.PrefUtils;
import tech2.demo.com.demo.model.Meals;
import tech2.demo.com.demo.model.Users;
import tech2.demo.com.demo.ui.activities.AdminActivity;
import tech2.demo.com.demo.ui.activities.MealsActivity;

public class MealDetailsFragment extends Fragment {
    private OnFragmentInteractions mListener;
    private Meals mMeal, mChangeMeal;
    private EditText mDescription, mNumberCalories;
    private TextView mDate, mTime;
    private ImageView mEdit, mDelete, mConfirm, mCancel;
    private LinearLayout mEditLayout;

    //If it is an admin deleting this we need an id for the current user
    private String id;
    private boolean isAdmin;

    public MealDetailsFragment() {
        // Required empty public constructor
    }

    public static MealDetailsFragment newInstance(String meal, String id) {
        MealDetailsFragment fragment = new MealDetailsFragment();
        Bundle args = new Bundle();
        args.putString("meal", meal);
        if (!TextUtils.isEmpty(id))
            args.putString("user", id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMeal = new Meals();

        if (getArguments() != null) {
            Bundle args = getArguments();

            Gson gson = new Gson();
            mMeal = gson.fromJson(args.getString("meal"), Meals.class);

            if (args.getString("user") != null) {
                id = args.getString("user");
                isAdmin = true;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_meal_details, container, false);

        mEdit = (ImageView) rootView.findViewById(R.id.fragment_meal_details_edit);
        mDelete = (ImageView) rootView.findViewById(R.id.fragment_meal_details_delete);
        mDate = (TextView) rootView.findViewById(R.id.fragment_meal_details_date);
        mTime = (TextView) rootView.findViewById(R.id.fragment_meal_details_time);
        mDescription = (EditText) rootView.findViewById(R.id.fragment_meal_details_description);
        mNumberCalories = (EditText) rootView.findViewById(R.id.fragment_meal_details_number_calories);
        mEditLayout = (LinearLayout) rootView.findViewById(R.id.fragment_meal_details_edit_layout);
        mCancel = (ImageView) rootView.findViewById(R.id.fragment_meal_details_cancel);
        mConfirm = (ImageView) rootView.findViewById(R.id.fragment_meal_details_confirm);

        mDescription.setText(mMeal.getDescription());
        mDate.setText(mMeal.getDate());
        mTime.setText(mMeal.getTime());
        String numberC = mMeal.getNumberCalories() + "";
        mNumberCalories.setText(numberC);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        String id = PrefUtils.getCurrentUserKey(getActivity());

        if(!id.equals("")) {
            // If we are entering this activity from the management side editing and deleting can only be shown to admins
            Firebase mRef = new Firebase(Config.BASE_URL + Config.USERS_ENDPOINT + id);
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Users user = dataSnapshot.getValue(Users.class);
                    if (user.getPermissions() == 1) {
                        mDelete.setVisibility(View.GONE);
                        mEdit.setVisibility(View.GONE);
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


        mEditLayout.setVisibility(View.GONE);
        setEditable();

    }

    @Override
    public void onResume() {
        super.onResume();

        // Can't add a meal while checking another
        if (MealsActivity.mFab != null)
            MealsActivity.mFab.setVisibility(View.GONE);
        else
            AdminActivity.mFab.setVisibility(View.GONE);

        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userId;
                if (!isAdmin)
                    userId = PrefUtils.getCurrentUserKey(getActivity());
                else
                    userId = id;

                // Deleting the meal from firebase and reducing the counter for the number of meals in the user data
                if(!userId.equals("")) {
                    Firebase mRef = new Firebase(Config.BASE_URL + Config.MEALS_ENDPOINT
                            + userId + "/" + mMeal.getId());
                    mRef.removeValue();

                    final Firebase mRef2 = new Firebase(Config.BASE_URL + Config.USERS_ENDPOINT + userId);
                    mRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Users user = dataSnapshot.getValue(Users.class);
                            user.setNumberMeals(user.getNumberMeals() - 1);
                            mRef2.setValue(user);
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                            Log.d(Config.LOG_TAG, firebaseError.toString());
                        }
                    });

                    mListener.onDelete(mMeal);

                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    fm.popBackStack();
                } else {
                    getActivity().finish();
                }
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChangeMeal = new Meals();
                mEditLayout.setVisibility(View.GONE);
                setEditable();
            }
        });

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });

        mEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChangeMeal = new Meals();

                if (mEditLayout.getVisibility() == View.VISIBLE)
                    mEditLayout.setVisibility(View.GONE);
                else
                    mEditLayout.setVisibility(View.VISIBLE);
                setEditable();
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
                mChangeMeal.setDescription(s.toString());
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
                    mChangeMeal.setNumberCalories(Integer.parseInt(s.toString()));
                else
                    mChangeMeal.setNumberCalories(0);
            }
        });
    }

    private void setEditable() {
        if (mEditLayout.getVisibility() == View.VISIBLE) {
            setListeners();
            mDescription.setFocusableInTouchMode(true);
            mNumberCalories.setFocusableInTouchMode(true);
            mDate.setFocusableInTouchMode(true);
            mTime.setFocusableInTouchMode(true);
            mDate.setClickable(true);
            mTime.setClickable(true);
        } else {
            mDescription.setFocusable(false);
            mNumberCalories.setFocusable(false);
            mDate.setFocusable(false);
            mTime.setFocusable(false);
            mDate.setFocusableInTouchMode(false);
            mTime.setFocusableInTouchMode(false);
            mDate.setClickable(false);
            mTime.setClickable(false);
        }
    }

    // Setting the listeners later so clicking on the textviews does not open the dialogs
    private void setListeners() {
        mDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                final int mYear = c.get(Calendar.YEAR);
                final int mMonth = c.get(Calendar.MONTH);
                final int mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                mChangeMeal.setDate(DateUtils.formatDate(dayOfMonth, monthOfYear, year));
                                mDate.setText(mChangeMeal.getDate());
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

                                mChangeMeal.setTime(DateUtils.formatTime(hourOfDay, minute));
                                mTime.setText(mChangeMeal.getTime());
                                mTime.setTextColor(getResources().getColor(android.R.color.black));
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });
    }

    private void validate() {
        mChangeMeal.setId(mMeal.getId());

        if (TextUtils.isEmpty(mChangeMeal.getDescription())) {
            mChangeMeal.setDescription(mMeal.getDescription());
        }
        if (mChangeMeal.getNumberCalories() == 0) {
            mChangeMeal.setNumberCalories(mMeal.getNumberCalories());
        }

        if (TextUtils.isEmpty(mChangeMeal.getDate())) {
            mChangeMeal.setDate(mMeal.getDate());
        }
        if (TextUtils.isEmpty(mChangeMeal.getTime())) {
            mChangeMeal.setTime(mMeal.getTime());
        }

        String userId;
        if (!isAdmin)
            userId = PrefUtils.getCurrentUserKey(getActivity());
        else
            userId = id;

        if(!userId.equals("")) {
            Firebase mRef = new Firebase(Config.BASE_URL
                    + Config.MEALS_ENDPOINT + userId
                    + "/" + mMeal.getId());
            mRef.setValue(mChangeMeal);

            mListener.onEdit(mChangeMeal);
            FragmentManager fm = getActivity().getSupportFragmentManager();
            fm.popBackStack();
        } else {
            getActivity().finish();
        }
    }

    public void setOnFragmentInteractions(OnFragmentInteractions mListener) {
        this.mListener = mListener;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractions {
        void onDelete(Meals meal);

        void onEdit(Meals meal);
    }
}
