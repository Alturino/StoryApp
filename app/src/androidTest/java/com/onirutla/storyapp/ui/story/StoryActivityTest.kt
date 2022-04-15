package com.onirutla.storyapp.ui.story

import JsonConverter
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.filters.MediumTest
import com.onirutla.storyapp.R
import com.onirutla.storyapp.di.ApiServiceModule
import com.onirutla.storyapp.util.EspressoIdlingResource
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
@HiltAndroidTest
@MediumTest
@UninstallModules(ApiServiceModule::class)
class StoryActivityTest {

    private val mockWebServer = MockWebServer()

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var activityScenarioRule = activityScenarioRule<StoryActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
        mockWebServer.start(8080)
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)

    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun test() {
        val mockResponse = MockResponse().setResponseCode(200)
            .setBody(JsonConverter.readStringFromFile("story.json"))
        mockWebServer.enqueue(mockResponse)

        onView(withId(R.id.story_list)).check(matches(isDisplayed()))
        onView(withText("lastgan")).check(matches(isDisplayed()))
        onView(withText("ngantuk bre")).check(matches(isDisplayed()))
        onView(withId(R.id.story_list)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                0,
                click()
            )
        )
        pressBack()
    }
}

