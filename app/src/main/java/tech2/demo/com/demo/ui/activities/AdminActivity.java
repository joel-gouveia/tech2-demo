package tech2.demo.com.demo.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.firebase.client.Firebase;

import tech2.demo.com.demo.Config;
import tech2.demo.com.demo.R;
import tech2.demo.com.demo.custom.PrefUtils;
import tech2.demo.com.demo.model.Meals;
import tech2.demo.com.demo.ui.fragments.CreateMealFragment;
import tech2.demo.com.demo.ui.fragments.CreateUserFragment;
import tech2.demo.com.demo.ui.fragments.DashboardFragment;
import tech2.demo.com.demo.ui.fragments.ProfileFragment;
import tech2.demo.com.demo.ui.fragments.TermsAndConditionsFragment;
import tech2.demo.com.demo.ui.fragments.UserDetailsFragment;

public class AdminActivity extends AppCompatActivity implements ListView.OnItemClickListener,
        CreateMealFragment.OnCreateMealFragmentListener{

    public static FloatingActionButton mFab;
    private static final String DASH_TAG = "Dashboard_Fragment";
    private static final String PROFILE_TAG = "ProfileFragment";
    private static final String TERMS_TAG = "TermsAndConditionsFragmenet";
    private static final String CREATE_USER_TAG = "CreateUserFragment";
    private static final String VIEW_TAG = "User_Details_Fragment";

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private String[] mDrawerOptions;
    private ActionBarDrawerToggle mDrawerToggle;
    public static Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(getResources().getString(R.string.toolbar_title_dashboard));
        mToolbar.setNavigationIcon(R.drawable.ic_navigation_drawer);
        setSupportActionBar(mToolbar);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mDrawerOptions = getResources().getStringArray(R.array.navigation_drawer_options);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<>(this,
                R.layout.drawer_list_item, mDrawerOptions));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(this);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                mToolbar, R.string.drawer_open, R.string.drawer_close) {

            // Called when a drawer has settled in a completely closed state.
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            // Called when a drawer has settled in a completely open state.
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = DashboardFragment.newInstance();
        fragmentManager.beginTransaction().replace(R.id.container, fragment, DASH_TAG).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft;
                Fragment fragment;
                FragmentManager fm = getSupportFragmentManager();

                Fragment myFragment = fm.findFragmentByTag(DASH_TAG);
                if (myFragment != null && myFragment.isVisible()) {
                    mToolbar.setTitle(getResources().getString(R.string.fragment_create_user_title));
                    ft = getSupportFragmentManager().beginTransaction();
                    ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right, R.anim.slide_in_left, R.anim.slide_out_left);
                    fragment = CreateUserFragment.newInstance();
                    ft.replace(R.id.container, fragment, CREATE_USER_TAG);
                    ft.addToBackStack(CREATE_USER_TAG);
                    ft.commit();
                } else {
                    UserDetailsFragment frag = (UserDetailsFragment) fm.findFragmentByTag(VIEW_TAG);
                    frag.addMealUser();
                }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        mDrawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                FragmentTransaction ft;
                Fragment fragment;
                FragmentManager fm = getSupportFragmentManager();
                fm.popBackStack();

                switch (position) {
                    case 0: //User Profile
                        mToolbar.setTitle(getResources().getString(R.string.fragment_profile_toolbar_title));
                        ft = getSupportFragmentManager().beginTransaction();
                        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right);
                        fragment = ProfileFragment.newInstance();
                        ft.replace(R.id.container, fragment, PROFILE_TAG);
                        ft.addToBackStack(PROFILE_TAG);
                        // Start the animated transition.
                        ft.commit();
                        break;
                    case 1: //Terms and Conditions
                        mToolbar.setTitle(getResources().getString(R.string.fragment_terms_toolbar_title));
                        ft = getSupportFragmentManager().beginTransaction();
                        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right);
                        fragment = TermsAndConditionsFragment.newInstance();
                        ft.replace(R.id.container, fragment, TERMS_TAG);
                        ft.addToBackStack(TERMS_TAG);
                        // Start the animated transition.
                        ft.commit();
                        break;
                    case 2: //Logout
                        PrefUtils.setCurrentUserKey(AdminActivity.this, "");
                        PrefUtils.setCurrentUserToken(AdminActivity.this, "");
                        Firebase mRef = new Firebase(Config.BASE_URL);
                        mRef.unauth();
                        Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                        finish();
                }
            }
        });

        mDrawerLayout.closeDrawers();
    }

    // Called whenever we call invalidateOptionsMenu()
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onNewMealCreated(Meals meal) {
        if (meal != null) {
            UserDetailsFragment userDetailsFragment = (UserDetailsFragment) getSupportFragmentManager().findFragmentByTag(VIEW_TAG);
            userDetailsFragment.onDataChange(meal);
        }
        //Do nothing
    }
}
