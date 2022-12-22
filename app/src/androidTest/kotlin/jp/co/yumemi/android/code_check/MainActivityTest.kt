package jp.co.yumemi.android.code_check

import androidx.test.core.app.ApplicationProvider.*
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import jp.co.yumemi.android.code_check.ui.adapter.ResultListAdapter
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    
    @get:Rule
    var activityScenarioRule = activityScenarioRule<MainActivity>()

    @Test
    fun testSearch() {
        onView(withId(R.id.searchInputText))
            .perform(click())
            .perform(typeTextIntoFocusedView("java"))
            .perform(pressImeActionButton())
            .check(matches(isDisplayed()))

        Thread.sleep(3000)

        onView(withId(R.id.recyclerView))
            .check(matches(isDisplayed()))
            .perform(swipeUp())
            .perform(swipeDown())
            .perform(scrollToPosition<ResultListAdapter.RepoInfoViewHolder>(0))
            .perform(actionOnItemAtPosition<ResultListAdapter.RepoInfoViewHolder>(2, click()))

        Thread.sleep(2000)

        onView(withId(R.id.result_scroll_view))
            .check(matches(isDisplayed()))
            .perform(swipeUp())
            .perform(swipeDown())
            .perform(pressBack())

        Thread.sleep(1000)
    }

}