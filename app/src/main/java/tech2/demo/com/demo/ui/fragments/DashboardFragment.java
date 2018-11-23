package tech2.demo.com.demo.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import tech2.demo.com.demo.Config;
import tech2.demo.com.demo.R;
import tech2.demo.com.demo.custom.PrefUtils;
import tech2.demo.com.demo.custom.adapters.UsersAdapter;
import tech2.demo.com.demo.model.Users;
import tech2.demo.com.demo.ui.activities.AdminActivity;

public class DashboardFragment extends Fragment {
    private static final String VIEW_TAG = "User_Details_Fragment";

    private TextView mNoContent;
    private ListView mListView;
    private UsersAdapter mAdapter;

    private List<Users> mUsers;
    private ActionMode mActionMode;

    public DashboardFragment() {
        // Required empty public constructor
    }

    public static DashboardFragment newInstance() {
        return new DashboardFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        mNoContent = (TextView) rootView.findViewById(R.id.fragment_dashboard_admin_no_content);
        mListView = (ListView) rootView.findViewById(R.id.fragment_dashboard_admin_list);

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        final String userId = PrefUtils.getCurrentUserKey(getActivity());
        if(!userId.equals("")) {
            Firebase mRef = new Firebase(Config.BASE_URL + Config.USERS_ENDPOINT);
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mUsers = new ArrayList<>();
                    Users user;

                    if (dataSnapshot.getChildrenCount() > 0) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            user = postSnapshot.getValue(Users.class);
                            if (!user.getId().equals(userId) && user.isActive()) {
                                mUsers.add(user);
                            }
                        }
                    } else {
                        mUsers = null;
                    }

                    if (mUsers == null) {
                        mListView.setVisibility(View.GONE);
                        mNoContent.setVisibility(View.VISIBLE);
                    } else {
                        mListView.setVisibility(View.VISIBLE);
                        mNoContent.setVisibility(View.GONE);
                        mAdapter = new UsersAdapter(getActivity().getApplicationContext(), mUsers);
                        mListView.setAdapter(mAdapter);
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
    }

    @Override
    public void onResume() {
        super.onResume();

        AdminActivity.mFab.setVisibility(View.VISIBLE);
        AdminActivity.mToolbar.setTitle(getResources().getString(R.string.toolbar_title_dashboard));

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Opens a detailed view of the user with all the information
                String json = new Gson().toJson(mAdapter.getItem(position), Users.class);
                String meals = null;

                if (mAdapter.getItem(position).getMeals() != null) {
                    meals = new Gson().toJson(mAdapter.getItem(position));
                }

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right);
                UserDetailsFragment fragment = UserDetailsFragment.newInstance(json, meals);
                ft.replace(R.id.container, fragment, VIEW_TAG);
                ft.addToBackStack(VIEW_TAG);
                // Start the animated transition.
                ft.commit();
            }
        });

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

                if (mActionMode != null)
                    mActionMode.setTitle(String.valueOf(mAdapter
                            .getSelectedCount()) + " selected");
                return false;
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
                    for (int i = (selected.size() - 1); i >= 0; i--) {
                        if (selected.valueAt(i)) {
                            Firebase mRef = new Firebase(Config.BASE_URL + Config.USERS_ENDPOINT + mUsers.get(selected.keyAt(i)).getId());
                            mRef.child("active").setValue(false);

                            mAdapter.removeItem(selected.keyAt(i));
                        }
                    }
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
        }
    }
}
