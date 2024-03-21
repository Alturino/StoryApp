package com.onirutla.storyapp.splash_screen

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.onirutla.storyapp.auth.ui.AuthActivity
import com.onirutla.storyapp.core.util.isOnline
import com.onirutla.storyapp.story.ui.StoryActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {

    private val vm: SplashScreenViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        installSplashScreen().apply {
            setKeepOnScreenCondition { true }
        }

        isOnline().flowWithLifecycle(lifecycle, Lifecycle.State.CREATED)
            .onEach { Timber.d("isOnline: $it") }
            .onEach(vm::setOnline)
            .launchIn(lifecycleScope)

        vm.isLogin.flowWithLifecycle(lifecycle, Lifecycle.State.CREATED)
            .onEach { isLogin ->
                when (isLogin) {
                    true -> {
                        Intent(this@SplashScreenActivity, StoryActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }.also {
                            startActivity(it)
                            finishAndRemoveTask()
                        }
                    }

                    false -> {
                        Intent(this@SplashScreenActivity, AuthActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }.also {
                            startActivity(it)
                            finishAndRemoveTask()
                        }
                    }

                    null -> {}
                }
            }
            .launchIn(lifecycleScope)
    }

}