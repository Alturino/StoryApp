/*
 * MIT License
 *
 * Copyright (c) 2023 Ricky Alturino
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.onirutla.storyapp

import android.Manifest
import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.google.common.truth.Truth.assertThat
import com.onirutla.storyapp.di.TestLocalModule
import com.onirutla.storyapp.di.TestNetworkModule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val LAUNCH_TIMEOUT = 5000L

@ExperimentalCoroutinesApi
@HiltAndroidTest
@MediumTest
@UninstallModules(TestNetworkModule::class, TestLocalModule::class)
@RunWith(AndroidJUnit4::class)
class AuthTest {

    private lateinit var device: UiDevice
    private lateinit var context: Context
    private lateinit var applicationPackageName: String

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.POST_NOTIFICATIONS,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    @Before
    fun setUp() {

        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).apply {
            pressHome()
            assertThat(launcherPackageName).isNotNull()
            wait(
                Until.hasObject(By.pkg(launcherPackageName)),
                LAUNCH_TIMEOUT
            )
        }

        context = ApplicationProvider.getApplicationContext<Context>().apply {
            applicationPackageName = packageName
            packageManager.getLaunchIntentForPackage(packageName)?.apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    .also { startActivity(it) }
            }
        }

        device.wait(
            Until.hasObject(
                By.pkg(applicationPackageName).depth(0),
            ),
            LAUNCH_TIMEOUT
        )

        hiltRule.inject()
    }

    @Test
    fun givenValidInput_shouldNavigateToStoryListActivity_verifyListNotEmpty() {
        device.apply {
            findObject(
                By.text(context.getString(R.string.email))
                    .hint(context.getString(R.string.email))
            ).apply {
                text = "halobang1234@gmail.com"
            }
            findObject(
                By.text(context.getString(R.string.password))
                    .hint(context.getString(R.string.password))
            ).apply {
                text = "halobang@gmail.com"
            }
            findObject(By.clickable(true).text(context.getString(R.string.sign_in))).apply {
                clickAndWait(Until.newWindow(), LAUNCH_TIMEOUT)
            }
            val listIsNotEmpty = findObject(By.scrollable(true)).childCount > 0
            assertThat(listIsNotEmpty).isTrue()
        }
    }

    @After
    fun tearDown() {
        device.pressHome()
    }
}