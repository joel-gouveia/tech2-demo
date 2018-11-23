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
import tech2.demo.com.demo.ui.fragments.MealsFragment;
import tech2.demo.com.demo.ui.fragments.ProfileFragment;
import tech2.demo.com.demo.ui.fragments.TermsAndConditionsFragment;

public class MealsActivity extends AppCompatActivity implements ListView.OnItemClickListener,
        CreateMealFragment.OnCreateMealFragmentListener {

    public static FloatingActionButton mFab;
    private static final String MAIN_TAG = "MealsFragment";
    private static final String CREATE_TAG = "CreateMealFragment";
    private static final String PROFILE_TAG = "ProfileFragment";
    private static final String TERMS_TAG = "TermsAndConditionsFragmenet";

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private String[] mDrawerOptions;
    private ActionBarDrawerToggle mDrawerToggle;
    public static Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meals);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(getResources().getString(R.string.toolbar_title_meals));
        mToolbar.setNavigationIcon(R.drawable.ic_navigation_drawer);
        setSupportActionBar(mToolbar);

        mDrawerOptions = getResources().getStringArray(R.array.navigation_drawer_options);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mFab = (FloatingActionButton) findViewById(R.id.fab);

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
        Fragment fragment = MealsFragment.newInstance();
        fragmentManager.beginTransaction().replace(R.id.container, fragment, MAIN_TAG).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right, R.anim.slide_in_left, R.anim.slide_out_left);
                Fragment fragment = CreateMealFragment.newInstance("");
                ft.replace(R.id.container, fragment, CREATE_TAG);
                ft.addToBackStack(MAIN_TAG);
                // Start the animated transition.
                ft.commit();
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
                        ft = getSupportFragmentManager().beginTransaction();
                        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right);
                        fragment = ProfileFragment.newInstance();
                        ft.replace(R.id.container, fragment, PROFILE_TAG);
                        ft.addToBackStack(PROFILE_TAG);
                        // Start the animated transition.
                        ft.commit();
                        break;
                    case 1: //Terms and Conditions
                        ft = getSupportFragmentManager().beginTransaction();
                        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right);
                        fragment = TermsAndConditionsFragment.newInstance();
                        ft.replace(R.id.container, fragment, TERMS_TAG);
                        ft.addToBackStack(TERMS_TAG);
                        // Start the animated transition.
                        ft.commit();
                        break;
                    case 2: //Logout
                        PrefUtils.setCurrentUserKey(MealsActivity.this, "");
                        PrefUtils.setCurrentUserToken(MealsActivity.this, "");
                        Firebase mRef = new Firebase(Config.BASE_URL);
                        mRef.unauth();
                        Intent intent = new Intent(MealsActivity.this, LoginActivity.class);
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
        MealsFragment mealsFragment = (MealsFragment) getSupportFragmentManager().findFragmentByTag(MAIN_TAG);
        mealsFragment.onDataChange(meal);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        mToolbar.setTitle(getResources().getString(R.string.toolbar_title_meals));
    }
}
