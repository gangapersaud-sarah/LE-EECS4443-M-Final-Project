package com.example.drinklistgame;

import android.content.Intent;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.drinklistgame.MainActivity;
import com.example.drinklistgame.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

@RunWith(AndroidJUnit4.class)
public class EspressoTestMainActivity {

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testTextViewDisplayed() {
        // Check if the TextView with the ID tv_TrueResult is displayed
        Espresso.onView(ViewMatchers.withId(R.id.tv_TrueResult))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testRefreshButton() {
        // Check if the Refresh button is displayed and perform click action
        Espresso.onView(ViewMatchers.withId(R.id.refresh)).perform(click());

        // Check if the TextView with the ID tv_TrueResult is displayed after refresh
        Espresso.onView(ViewMatchers.withId(R.id.tv_TrueResult))
                .check(matches(isDisplayed()));
    }
    @Test
    public void testErrorRateText() {
        // Check if the Error Rate text view is displayed
        Espresso.onView(ViewMatchers.withId(R.id.tv_ErrorRate))
                .check(matches(isDisplayed()));

    }
}

