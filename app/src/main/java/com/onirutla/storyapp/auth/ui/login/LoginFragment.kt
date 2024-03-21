package com.onirutla.storyapp.auth.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.onirutla.storyapp.R
import com.onirutla.storyapp.core.ui.util.asFlow
import com.onirutla.storyapp.databinding.FragmentLoginBinding
import com.onirutla.storyapp.story.ui.StoryActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val vm: LoginViewModel by viewModels()

    private var _binding: FragmentLoginBinding? = null
    private val binding: FragmentLoginBinding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            owner = this,
            onBackPressedCallback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (!findNavController().navigateUp()) {
                        requireActivity().finishAndRemoveTask()
                    }
                }
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etEmail
            .asFlow()
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.RESUMED)
            .onEach { vm.onEvent(LoginEvent.OnEmailChange(it)) }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        binding.etPassword
            .asFlow()
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.RESUMED)
            .onEach { vm.onEvent(LoginEvent.OnPasswordChange(it)) }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        binding.btnLogin.setOnClickListener {
            vm.login()
        }

        binding.tvRegister.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionFragmentLoginToFragmentRegister())
        }

        vm.state.flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
            .onEach { Timber.d("$it") }
            .onEach { state ->
                state.loginState.onSuccess {
                    with(requireActivity()) {
                        Intent(this, StoryActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }.also {
                            startActivity(it)
                            finishAndRemoveTask()
                        }
                    }
                    vm.onEvent(LoginEvent.HandledEvent)
                }.onError {
                    Snackbar.make(
                        requireView(),
                        getString(R.string.login_failed, message),
                        Snackbar.LENGTH_SHORT
                    ).setAction("Dismiss") {
                        vm.onEvent(LoginEvent.HandledEvent)
                    }.show()
                    vm.onEvent(LoginEvent.HandledEvent)
                }.onLoading {
                    binding.progressCircular.visibility = View.VISIBLE
                }.onInitial {
                    binding.progressCircular.visibility = View.GONE
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}