package id.gits.kade.ibun.schedules

import androidx.test.InstrumentationRegistry
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import id.gits.kade.ibun.Injection
import id.gits.kade.ibun.R
import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest
class SchedulesScreenTest {

    @Rule
    @JvmField
    var schedulesActivityTestRule = object : ActivityTestRule<SchedulesActivity>(SchedulesActivity::class.java) {
        override fun beforeActivityLaunched() {
            super.beforeActivityLaunched()
            Injection.provideSportsRepository(InstrumentationRegistry.getTargetContext()).deleteAllFavorites()
        }
    }

    @Before
    fun registerIdlingResource(){
        IdlingRegistry.getInstance().register(schedulesActivityTestRule.activity.countingIdlingResource)
    }

    private fun <T> first(matcher: Matcher<T>): Matcher<T> {
        return object : BaseMatcher<T>() {
            override fun describeTo(description: Description?) {
                description?.appendText("should return first matching item")
            }

            var isFirst = true

            override fun matches(item: Any): Boolean {
                if (isFirst && matcher.matches(item)) {
                    isFirst = false
                    return true
                }

                return false
            }

        }
    }

    @Test
    fun showPrevMatches() {
        onView(withContentDescription("Prev Match")).perform(click())
        onView(first(withText("vs"))).check(matches(isDisplayed()))
    }

    @Test
    fun showNextMatches() {
        onView(withContentDescription("Next Match")).perform(click())
        onView(first(withText("vs"))).check(matches(isDisplayed()))
    }

    @Test
    fun showDetailAndFavorite() {
        //favorite
        onView(first(withText("vs"))).perform(click())
        onView(first(withId(R.id.add_to_favorite))).perform(click())

        pressBack()

        onView(withContentDescription("Favorites")).perform(click())
        onView(first(withText("vs"))).check(matches(isDisplayed()))

        // delete favorite
        onView(first(withText("vs"))).perform(click())
        onView(first(withId(R.id.add_to_favorite))).perform(click())

        pressBack()

        onView(first(withText("vs"))).check(doesNotExist())
    }
}