package test.connect.geoexploreapp;

import static org.hamcrest.Matchers.not;
import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.AllOf.allOf;

import android.widget.EditText;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;


import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

public class AditiSystemTest {
    private String emailId = "aditin@iastate.edu";
    private String password = "p";
    private static final int SIMULATED_DELAY_MS = 1000;

    @Rule
    public ActivityScenarioRule<LoginSignUpActivity> activityRule = new ActivityScenarioRule<>(LoginSignUpActivity.class);

    @Test
    public void LogInTest()  {
        onView(withId(R.id.login_email)).perform(typeText(emailId));
        onView(withId(R.id.login_password)).perform(typeText(password));
        onView(withId(R.id.login_button)).perform(click());

        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {
        }

        // Verify that volley returned the correct value
        onView(withId(R.id.mainActivity)).check(matches(isDisplayed()));
    }

    @Test
    public void SignInTest(){
        onView(allOf(withText("Signup"), isDescendantOfA(withId(R.id.tab_layout))))
                .perform(click());

        onView(withId(R.id.firstName)).perform(typeText("first name1"));
        onView(withId(R.id.lastName)).perform(typeText("lastName1"));
        onView(withId(R.id.signup_email)).perform(typeText("emailId1"));
        onView(withId(R.id.signup_password)).perform(typeText("password"));
        onView(withId(R.id.signup_confirm)).perform(typeText("password"));
        onView(withId(R.id.SignUpScrollView)).perform(swipeUp());
        onView(withId(R.id.signup_button)).perform(click());

        // Put thread to sleep to allow volley to handle the request
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {
        }

        // Verify that volley returned the correct value
         onView(withId(R.id.bottomNavigationView)).check(matches(isDisplayed()));

    }


    @Test
    public void CreateObservation(){
        onView(withId(R.id.login_email)).perform(typeText(emailId));
        onView(withId(R.id.login_password)).perform(typeText(password));
        onView(withId(R.id.login_button)).perform(click());


        // Verify that volley returned the correct value
        onView(withId(R.id.mainActivity)).check(matches(isDisplayed()));

        onView(withId(R.id.map)).check(matches(isDisplayed()));

        onView(withId(R.id.map)).perform(longClick());
        onView(withText("What do you want to create?"))
                .inRoot(isDialog()) //
                .check(matches(isDisplayed()));

        onView(allOf(withId(android.R.id.text1), withText("Observation")))
                .inRoot(isDialog())
                .perform(click());


        onView(withId(R.id.editTextTitle)).perform(typeText("Lion stopped again"));
        closeSoftKeyboard();
        onView(withId(R.id.editTextDescription)).perform(typeText("Saw a lion. Be careful.33"));
        closeSoftKeyboard();
        onView(withText("Create")).perform(click());

        // Put thread to sleep to allow volley to handle the request
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {
        }

        onView(withId(R.id.statusMessage)).check(matches(withText("Observation created successfully!")));

    }

    @Test
    public void CreateEventFailed(){
        onView(withId(R.id.login_email)).perform(typeText(emailId));
        onView(withId(R.id.login_password)).perform(typeText(password));
        onView(withId(R.id.login_button)).perform(click());


        // Verify that volley returned the correct value
        onView(withId(R.id.map)).check(matches(isDisplayed()));

        onView(withId(R.id.map)).perform(longClick());
        onView(withText("What do you want to create?"))
                .inRoot(isDialog()) //
                .check(matches(isDisplayed()));

        onView(allOf(withId(android.R.id.text1), withText("Event")))
                .inRoot(isDialog())
                .perform(click());


        onView(withId(R.id.editTextTitle)).perform(typeText(""));
        closeSoftKeyboard();

        onView(withText("Create")).perform(click());

        // Put thread to sleep to allow volley to handle the request
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {
        }

        onView(withId(R.id.statusMessage)).check(matches(withText("Need Title for Event.")));

    }


    @Test
    public void GetObservationByIDTest(){
        onView(withId(R.id.login_email)).perform(typeText(emailId));
        onView(withId(R.id.login_password)).perform(typeText(password));
        onView(withId(R.id.login_button)).perform(click());
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {
        }
        onView(withId(R.id.map)).check(matches(isDisplayed()));
        onView(withId(R.id.MarkerOperationsFab)).perform(click());
        onView(withId(R.id.btn_observation_read)).perform(click());
        onView(withText("Enter Observation ID"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
        onView(ViewMatchers.withClassName(Matchers.equalTo(EditText.class.getName())))
                .perform(ViewActions.typeText("2"), ViewActions.closeSoftKeyboard());
        onView(withText("OK")).perform(click());
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {
        }
        onView(withId(R.id.statusMessage)).check(matches(withText("Observation found successfully!")));


    }
    @Test
    public void GetObservationByIDFail(){
        onView(withId(R.id.login_email)).perform(typeText(emailId));
        onView(withId(R.id.login_password)).perform(typeText(password));
        onView(withId(R.id.login_button)).perform(click());
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {
        }
        onView(withId(R.id.map)).check(matches(isDisplayed()));
        onView(withId(R.id.fab_main)).perform(click());
        onView(withId(R.id.btn_observation_read)).perform(click());
        onView(withText("Enter Observation ID"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
        onView(ViewMatchers.withClassName(Matchers.equalTo(EditText.class.getName())))
                .perform(ViewActions.typeText("2000"), ViewActions.closeSoftKeyboard());
        onView(withText("OK")).perform(click());
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {
        }
        onView(withId(R.id.statusMessage)).check(matches(withText("Observation ID Not Found!")));


    }
}


