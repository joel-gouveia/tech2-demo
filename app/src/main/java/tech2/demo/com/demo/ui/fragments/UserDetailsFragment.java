package tech2.demo.com.demo.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import tech2.demo.com.demo.Config;
import tech2.demo.com.demo.R;
import tech2.demo.com.demo.custom.DateComparator;
import tech2.demo.com.demo.custom.PrefUtils;
import tech2.demo.com.demo.custom.adapters.MealsAdapter;
import tech2.demo.com.demo.model.Meals;
import tech2.demo.com.demo.model.Users;
import tech2.demo.com.demo.ui.activities.AdminActivity;

public class UserDetailsFragment extends Fragment implements MealDetailsFragment.OnFragmentInteractions,
        EditUserFragment.OnEditUserInteraction {
    private static final String MEAL_TAG = "Meal_Details_Fragment";
    private static final String EDIT_TAG = "Edit_User_Fragment";
    private static final String CREATE_MEAL_TAG = "CreateMealFragment";
    private Users mUserDetailed, mCurrentUser;
    private List<Meals> mMeals;

    private TextView mName, mExpectedCalories, mUsername, mNoContent;
    private ImageView mEdit, mDelete;
    private LinearLayout mCaloriesLayout;
    private StickyListHeadersListView mListView;
    private MealsAdapter mAdapter;
    private ActionMode mActionMode;
    private String mCurrentUserKey;

    public UserDetailsFragment() {
        // Required empty public constructor
    }

    public static UserDetailsFragment newInstance(String user, String meals) {
        UserDetailsFragment fragment = new UserDetailsFragment();
        Bundle args = new Bundle();
        args.putString("user", user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle args = getArguments();

            Gson gson = new Gson();
            mUserDetailed = gson.fromJson(args.getString("user"), Users.class);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        mCurrentUserKey = PrefUtils.getCurrentUserKey(getActivity());

        if (!mCurrentUserKey.equals("")) {
            Firebase mRef = new Firebase(Config.BASE_URL + Config.USERS_ENDPOINT + mCurrentUserKey);
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mCurrentUser = dataSnapshot.getValue(Users.class);
                    checkPermissions();
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Log.d(Config.LOG_TAG, firebaseError.toString());
                }
            });


            mRef = new Firebase(Config.BASE_URL + Config.MEALS_ENDPOINT + mUserDetailed.getId());
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        mMeals = new ArrayList<>();
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            Meals meal = data.getValue(Meals.class);
                            mMeals.add(meal);
                        }
                        Collections.sort(mMeals, new DateComparator());
                    } else {
                        mMeals = null;
                    }

                    // Shows a message if there are no collections associated with the user
                    if (mMeals == null || mUserDetailed.getPermissions() > Config.USER) {
                        mListView.setVisibility(View.GONE);
                        mNoContent.setVisibility(View.VISIBLE);
                        mMeals = new ArrayList<>();
                    } else {
                        mAdapter = new MealsAdapter(getActivity().getApplicationContext(), mMeals,
                                Integer.parseInt(mUserDetailed.getExpectedCalories()));
                        mListView.setVisibility(View.VISIBLE);
                        mNoContent.setVisibility(View.GONE);
                        mListView.setAdapter(mAdapter);
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Log.d(Config.LOG_TAG, firebaseError.toString());
                }
            });

            setListViewOnClick();
        } else {
            getActivity().finish();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_user_details, container, false);

        mName = (TextView) rootView.findViewById(R.id.fragment_user_details_name);
        mExpectedCalories = (TextView) rootView.findViewById(R.id.fragment_user_details_number_calories);
        mUsername = (TextView) rootView.findViewById(R.id.fragment_user_details_username);
        mEdit = (ImageView) rootView.findViewById(R.id.fragment_user_details_edit);
        mDelete = (ImageView) rootView.findViewById(R.id.fragment_user_details_delete);
        mListView = (StickyListHeadersListView) rootView.findViewById(R.id.fragment_user_details_meals_list);
        mNoContent = (TextView) rootView.findViewById(R.id.fragment_user_details_no_content);
        mCaloriesLayout = (LinearLayout) rootView.findViewById(R.id.fragment_user_details_calories_layout);

        // Set the variables to the respective name
        mUsername.setText(mUserDetailed.getEmail());
        mExpectedCalories.setText(String.valueOf(mUserDetailed.getExpectedCalories()));
        if (mUserDetailed.getFirstName() != null || mUserDetailed.getLastName() != null) {
            String fullName = mUserDetailed.getFirstName() + " " + mUserDetailed.getLastName();
            mName.setText(fullName);
        } else {
            mName.setText("");
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        AdminActivity.mToolbar.setTitle(getActivity().getResources().getString(R.string.toolbar_title_dashboard));

        mEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Allow the admin to edit the user
                String json = new Gson().toJson(mUserDetailed, Users.class);

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right);
                EditUserFragment fragment = EditUserFragment.newInstance(json);
                fragment.setOnEditUserInteraction(UserDetailsFragment.this);
                ft.replace(R.id.container, fragment, EDIT_TAG);
                ft.addToBackStack(EDIT_TAG);
                // Start the animated transition.
                ft.commit();
            }
        });

        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Firebase mRef = new Firebase(Config.BASE_URL + Config.USERS_ENDPOINT + mUserDetailed.getId());
                mRef.child("active").setValue(false);

                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.popBackStack();
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (mCurrentUser.getPermissions() == Config.ADMIN) {
                    mAdapter.toggleSelection(position);
                    boolean hasCheckedItems = mAdapter.getSelectedCount() > 0;

                    if (hasCheckedItems && mActionMode == null)
                        // there are some selected items, start the actionMode
                        mActionMode = getActivity().startActionMode(new ActionModeCallback());
                    else if (!hasCheckedItems && mActionMode != null)
                        // there no selected items, finish the actionMode
                        mActionMode.finish();

                    if (mActionMode != null) {
                        mActionMode.setTitle(String.valueOf(mAdapter
                                .getSelectedCount()) + " selected");
                        mListView.setOnItemClickListener(null);
                    } else {
                        setListViewOnClick();
                    }
                }
                return false;
            }
        });
    }

    private void setListViewOnClick() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mActionMode != null)
                    mActionMode.finish();

                String json = new Gson().toJson(mAdapter.getItem(position), Meals.class);

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right);
                MealDetailsFragment fragment = MealDetailsFragment.newInstance(json, mUserDetailed.getId());
                fragment.setOnFragmentInteractions(UserDetailsFragment.this);
                ft.replace(R.id.container, fragment, MEAL_TAG);
                ft.addToBackStack(MEAL_TAG);
                // Start the animated transition.
                ft.commit();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDelete(Meals meal) {
        for (int i = 0; i < mMeals.size(); i++) {
            if (mMeals.get(i).getId().equals(meal.getId())) {
                mAdapter.removeItem(i);
                break;
            }
        }
        checkData();
    }

    @Override
    public void onEdit(Meals meal) {
        for (int i = 0; i < mMeals.size(); i++) {
            if (mMeals.get(i).getId().equals(meal.getId())) {
                mMeals.set(i, meal);
                mAdapter.restore(mMeals);
                break;
            }
        }
    }

    private void checkPermissions() {
        if (mUserDetailed.getPermissions() > Config.USER) {
            mCaloriesLayout.setVisibility(View.GONE);
            mListView.setVisibility(View.GONE);
            mNoContent.setVisibility(View.VISIBLE);
        }

        if (mCurrentUser.getPermissions() == Config.MANAGER) {
            AdminActivity.mFab.setVisibility(View.GONE);

            if (mUserDetailed.getPermissions() > Config.MANAGER) {
                mEdit.setVisibility(View.GONE);
                mDelete.setVisibility(View.GONE);
            }
        } else {
            AdminActivity.mFab.setVisibility(View.VISIBLE);
        }
    }

    public void onDataChange(Meals meal) {
        if (mMeals.size() == 0) {
            mMeals.add(meal);
            mAdapter = new MealsAdapter(getActivity().getApplicationContext(),
                    mMeals,
                    Integer.parseInt(mUserDetailed.getExpectedCalories()));
            checkData();
            mListView.setAdapter(mAdapter);
        } else {
            mMeals.add(meal);
            Collections.sort(mMeals, new DateComparator());
            mAdapter.restore(mMeals);
        }
    }

    private void checkData() {
        if (mAdapter.getCount() == 0 || mUserDetailed.getPermissions() > Config.USER) {
            mListView.setVisibility(View.GONE);
            mNoContent.setVisibility(View.VISIBLE);
        } else {
            mListView.setVisibility(View.VISIBLE);
            mNoContent.setVisibility(View.GONE);
        }
    }

    public void addMealUser() {
        FragmentTransaction ft;

        AdminActivity.mToolbar.setTitle(getResources().getString(R.string.fragment_create_meal_title));
        ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right, R.anim.slide_in_left, R.anim.slide_out_left);
        CreateMealFragment fragment = CreateMealFragment.newInstance(mUserDetailed.getId());
        ft.replace(R.id.container, fragment, CREATE_MEAL_TAG);
        ft.addToBackStack(CREATE_MEAL_TAG);
        ft.commit();
    }

    @Override
    public void onEdit(Users user) {
        mUserDetailed = user;
        if (mUserDetailed.getPermissions() == 0)
            if (mListView.getAdapter() != null) {
                mAdapter.restore(new ArrayList<Meals>());
            } else {
                mMeals = new ArrayList<>();
                mAdapter = new MealsAdapter(getActivity(), mMeals, Integer.parseInt(mUserDetailed.getExpectedCalories()));
            }
    }

    private class ActionModeCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // inflate contextual menu
            mode.getMenuInflater().inflate(R.menu.menu_activity_meals_context, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            switch (item.getItemId()) {
                case R.id.delete:
                    // retrieve selected items and delete them out
                    SparseBooleanArray selected = mAdapter
                            .getSelectedIds();
                    int countRemoval = 0;
                    for (int i = (selected.size() - 1); i >= 0; i--) {
                        if (selected.valueAt(i)) {
                            Meals meal = mAdapter.getItem(selected.keyAt(i));
                            mAdapter.removeItem(selected.keyAt(i));

                            Firebase mRef = new Firebase(Config.BASE_URL + Config.MEALS_ENDPOINT
                                    + mUserDetailed.getId() + "/" + meal.getId());
                            mRef.removeValue();
                            countRemoval++;

                            checkData();
                        }
                    }
                    final int countToRemove = countRemoval;
                    final Firebase mRef2 = new Firebase(Config.BASE_URL + Config.USERS_ENDPOINT + mUserDetailed.getId());
                    mRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Users user = dataSnapshot.getValue(Users.class);
                            user.setNumberMeals(user.getNumberMeals() - countToRemove);
                            mRef2.setValue(user);
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                            Log.d(Config.LOG_TAG, firebaseError.toString());
                        }
                    });
                    setListViewOnClick();
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                default:
                    return false;
            }

        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            // remove selection
            mAdapter.removeSelection();
            mActionMode = null;
            setListViewOnClick();
        }
    }
}
