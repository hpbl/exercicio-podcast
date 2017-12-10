package br.ufpe.cin.if710.podcast;

import android.content.Context;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;

import org.hamcrest.Description;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.ufpe.cin.if710.podcast.domain.ItemFeed;
import br.ufpe.cin.if710.podcast.ui.MainActivity;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class UITest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("br.ufpe.cin.if710.podcast", appContext.getPackageName());
    }

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule(MainActivity.class);

    @Test
    public void firstPodcast_isCiencia() {
        String expected = "Ciência e Pseudociência";

        SystemClock.sleep(35000);

        onData(anything())
                .inAdapterView(withId(R.id.items))
                .atPosition(0)
                .onChildView(withId(R.id.item_title))
                .check(matches(withText(expected)));
    }

    @Test
    public void lastPodcast_hasRightDate() {
        String title = "Frontdaciência - T08E29 - Mario Bunge I";
        String expectedDate = "Mon, 18 Sep 2017 12:00:00 GMT";

        SystemClock.sleep(35000);

        onData(withItemTitle(title))
                .inAdapterView(withId(R.id.items))
                .onChildView(withId(R.id.item_date))
                .check(matches(withText(expectedDate)));
    }


    private static FeatureMatcher<ItemFeed, String> withItemTitle(final String itemTitle) {
        return new FeatureMatcher<ItemFeed, String>(equalTo(itemTitle), "with itemTitle", "itemTitle") {
            @Override
            protected String featureValueOf(ItemFeed actual) {
                return actual.getTitle();
            }
        };
    }


}
