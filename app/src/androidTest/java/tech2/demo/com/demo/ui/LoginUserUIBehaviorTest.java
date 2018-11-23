package tech2.demo.com.demo.ui;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import tech2.demo.com.demo.R;
import tech2.demo.com.demo.helpers.DrawableMatcher;
import tech2.demo.com.demo.ui.activities.LoginActivity;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class LoginUserUIBehaviorTest {

    @Before
    public void launchActivity() {
        ActivityScenario.launch(LoginActivity.class);
    }
    @Test
    public void backgroundImageView_exists() {
        onView(ViewMatchers.withId(R.id.activity_login_background_image)).check(matches(DrawableMatcher.withDrawable(R.drawable.sushi_wallpaper)));
    }

    // Register button from RegisterUserActivity should not exist in login activity
    @Test
    public void registerButton_doesntExist() {
        onView(withId(R.id.activity_register_button_register)).check(doesNotExist());
    }

    @Test
    public void registerNowButton_isClickable() {
        // Checks that the register now button is clickable
        onView(withId(R.id.activity_login_button_register)).check(matches(isClickable()));
    }

    @Test
    public void registerNowButton_moveActivity() {
        String register = getApplicationContext().getResources().getString(R.string.register);

        onView(withId(R.id.activity_login_button_register)).perform(click());

        // Check if the register button is now visible
        onView(withText(register)).check(matches(isDisplayed()));
    }
}
