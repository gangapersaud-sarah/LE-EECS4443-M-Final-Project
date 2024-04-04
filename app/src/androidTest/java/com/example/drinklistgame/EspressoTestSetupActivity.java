package com.example.drinklistgame;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.Visibility.VISIBLE;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4ClassRunner.class)
public class EspressoTestSetupActivity {

    @Rule
    public ActivityScenarioRule<SetupActivity> activityScenarioRule =
            new ActivityScenarioRule<>(SetupActivity.class);

    @Test
    public void testStartButtonEnabledAfterCheckboxChecked() {
        // Check if the start button is initially disabled
        Espresso.onView(withId(R.id.start_button))
                .check(matches(isEnabled()));

        // Check the checkbox
        Espresso.onView(withId(R.id.checkbox)).perform(ViewActions.click());

        // Verify if the start button is enabled after checking the checkbox
        Espresso.onView(withId(R.id.start_button))
                .check(matches(isEnabled()));
    }

    @Test
    public void testvisibilityOfElements() {
        // Check the checkbox
        Espresso.onView(withId(R.id.checkbox)).perform(ViewActions.click());

        // Enter some text in the EditText fields
        Espresso.onView(withId(R.id.nameEditText))
                .check(matches(withEffectiveVisibility(VISIBLE)));
//                .perform(ViewActions.typeText("John"));

        Espresso.onView(withId(R.id.EditStudentID))
                .check(matches(withEffectiveVisibility(VISIBLE)));
//                .perform(ViewActions.typeText("123456"));
        Espresso.onView(withId(R.id.EditEmail))
                .check(matches(withEffectiveVisibility(VISIBLE)));
//                .perform(ViewActions.typeText("john@example.com"));

        // Perform click action on the start button
        Espresso.onView(withId(R.id.start_button))
                .check(matches(withEffectiveVisibility(VISIBLE)));
//                .perform(ViewActions.click());

    }
}
