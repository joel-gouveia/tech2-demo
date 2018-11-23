package tech2.demo.com.demo.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import tech2.demo.com.demo.common.DateUtils;
import tech2.demo.com.demo.custom.DateComparator;
import tech2.demo.com.demo.custom.FilterFragment;
import tech2.demo.com.demo.custom.PrefUtils;
import tech2.demo.com.demo.custom.adapters.MealsAdapter;
import tech2.demo.com.demo.model.Meals;
import tech2.demo.com.demo.model.Users;
import tech2.demo.com.demo.ui.activities.MealsActivity;

public class MealsFragment extends Fragment implements FilterFragment.OnDialogConfirmation,
        MealDetailsFragment.OnFragmentInteractions {
    private StickyListHeadersListView mListView;
    private TextView mNoContent;
    private MealsAdapter mAdapter;
    private ActionMode mActionMode;
    private String mCurrentUserKey;
    private int mExpectedCalories;

    public static final String DETAILS_TAG = "MealDetailsFragment";
    private List<Meals> mMeals;

    public MealsFragment() {
        // Required empty public constructor
    }

    public static MealsFragment newInstance() {
        return new MealsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_meals, container, false);
        mListView = (StickyListHeadersListView) rootView.findViewById(R.id.fragment_meals_list);
        mNoContent = (TextView) rootView.findViewById(R.id.fragment_meals_no_content);

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        mCurrentUserKey = PrefUtils.getCurrentUserKey(getActivity());
        if (!mCurrentUserKey.equals("")) {

            //Display the user's meals
            Firebase mRefUser = new Firebase(Config.BASE_URL + Config.USERS_ENDPOINT + mCurrentUserKey);
            mRefUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Users user = dataSnapshot.getValue(Users.class);
                    mExpectedCalories = Integer.parseInt(user.getExpectedCalories());
                    Firebase mRef = new Firebase(Config.BASE_URL
                            + Config.MEALS_ENDPOINT + mCurrentUserKey + "/");
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

                            // Shows a message if there are no meals associated with the user
                            if (mMeals == null) {
                                mListView.setVisibility(View.GONE);
                                mNoContent.setVisibility(View.VISIBLE);
                                mMeals = new ArrayList<>();
                            } else {
                                mAdapter = new MealsAdapter(getActivity().getApplicationContext(), mMeals, mExpectedCalories);
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
    public void onResume() {
        super.onResume();

        MealsActivity.mFab.setVisibility(View.VISIBLE);
        MealsActivity.mToolbar.setTitle(getActivity().getResources().getString(R.string.toolbar_title_meals));


        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
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
                MealDetailsFragment fragment = MealDetailsFragment.newInstance(json, "");
                fragment.setOnFragmentInteractions(MealsFragment.this);
                ft.replace(R.id.container, fragment, DETAILS_TAG);
                ft.addToBackStack(DETAILS_TAG);
                // Start the animated transition.
                ft.commit();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void onDataChange(Meals meal) {
        if (mMeals.size() == 0) {
            mMeals.add(meal);
            mAdapter = new MealsAdapter(getActivity().getApplicationContext(), mMeals, mExpectedCalories);
            checkData();
            mListView.setAdapter(mAdapter);
        } else {
            mMeals.add(meal);
            Collections.sort(mMeals, new DateComparator());
            mAdapter.restore(mMeals);
        }
    }

    //Receives the filtered from the view and checks if it has a date, a time or both
    @Override
    public void onDatesSet(String fromDate, String fromTime, String toDate, String toTime) {
        List<Meals> filteredMeals = new ArrayList<>();

        for (int i = 0; i < mMeals.size(); i++) {
            Meals meal = mMeals.get(i);

            if (!TextUtils.isEmpty(fromDate) && !TextUtils.isEmpty(fromTime)) {
                if (DateUtils.isWithinDateAndTime(meal, fromDate, toDate, fromTime, toTime)) {
                    filteredMeals.add(meal);
                }
                mAdapter.restore(filteredMeals);
            } else if (!TextUtils.isEmpty(fromDate)) {
                if (DateUtils.isWithinDate(meal, fromDate, toDate)) {
                    filteredMeals.add(meal);
                }
                mAdapter.restore(filteredMeals);
            } else if (!TextUtils.isEmpty(fromTime)) {
                if (DateUtils.isWithinTime(meal, fromTime, toTime)) {
                    filteredMeals.add(meal);
                }
                mAdapter.restore(filteredMeals);
            } else {
                mAdapter.restore(mMeals);
            }
        }

        checkData();
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

    private void checkData() {
        if (mListView.getAdapter() != null) {
            if (mAdapter.getCount() == 0) {
                mListView.setVisibility(View.GONE);
                mNoContent.setVisibility(View.VISIBLE);
            } else {
                mListView.setVisibility(View.VISIBLE);
                mNoContent.setVisibility(View.GONE);
            }
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
                                    + mCurrentUserKey + "/" + meal.getId());
                            countRemoval++;

                            mRef.removeValue();

                            final int countToRemove = countRemoval;
                            final Firebase mRef2 = new Firebase(Config.BASE_URL + Config.USERS_ENDPOINT + mCurrentUserKey);
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
                            checkData();
                        }
                    }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.action_filter:
                FilterFragment fragment = new FilterFragment();
                fragment.setOnDialogConfirmation(this);
                fragment.show(getFragmentManager(), "DialogFragment");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_meals_filter, menu);
    }
}
