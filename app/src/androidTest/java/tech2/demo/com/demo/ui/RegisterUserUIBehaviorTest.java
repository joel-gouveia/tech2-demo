package tech2.demo.com.demo.ui;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import tech2.demo.com.demo.R;
import tech2.demo.com.demo.helpers.HintMatcher;
import tech2.demo.com.demo.ui.activities.RegisterUserActivity;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class RegisterUserUIBehaviorTest {
    public static final String USERNAME = "user@email.com";
    public static final String WRONG_PASSWORD = "small";

    @Before
    public void launchActivity() {
        ActivityScenario.launch(RegisterUserActivity.class);
    }

    @Test
    public void emailEditText_canBeTypedInto() {
        onView(ViewMatchers.withId(R.id.activity_register_username)).perform(typeText(USERNAME), closeSoftKeyboard()).check(matches(withText(USERNAME)));
    }

    @Test
    public void emailEditText_hintIsDisplayed() {
        String hintText = getApplicationContext().getResources().getString(R.string.email);

        onView(withId(R.id.activity_register_username)).check(matches(HintMatcher.withHint(hintText)));
    }

    @Test
    public void emailEditText_errorIsEmpty() {
        String fieldRequired = getApplicationContext().getResources().getString(R.string.login_activity_error_field_required);

        onView(withId(R.id.activity_register_button_register)).perform(click());

        // Check that the field required error is displayed
        onView(withId(R.id.activity_register_username)).check(matches(hasErrorText(fieldRequired)));
    }

    @Test
    public void passwordEditText_errorHasSmallPassword() {
        String wrongPassword = getApplicationContext().getResources().getString(R.string.login_activity_password_size);

        onView(withId(R.id.activity_register_username)).perform(typeText(USERNAME), closeSoftKeyboard());
        onView(withId(R.id.activity_register_password)).perform(typeText(WRONG_PASSWORD), closeSoftKeyboard());
        onView(withId(R.id.activity_register_button_register)).perform(click());

        // Check if the error for size of password is displayed
        onView(withId(R.id.activity_register_password)).check(matches(hasErrorText(wrongPassword)));
    }
}
