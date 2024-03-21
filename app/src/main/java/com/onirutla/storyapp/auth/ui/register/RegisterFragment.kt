package com.onirutla.storyapp.auth.ui.register

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
import androidx.navigation.navOptions
import com.google.android.material.snackbar.Snackbar
import com.onirutla.storyapp.R
import com.onirutla.storyapp.core.ui.util.asFlow
import com.onirutla.storyapp.databinding.FragmentRegisterBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private val vm: RegisterViewModel by viewModels()

    private var _binding: FragmentRegisterBinding? = null
    private val binding: FragmentRegisterBinding
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
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etName
            .asFlow()
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.RESUMED)
            .onEach { Timber.d("name: $it") }
            .onEach { vm.onEvent(RegisterEvent.OnNameChange(it)) }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        binding.etEmail
            .asFlow()
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.RESUMED)
            .onEach { Timber.d("email: $it") }
            .onEach { vm.onEvent(RegisterEvent.OnEmailChange(it)) }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        binding.etPassword
            .asFlow()
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.RESUMED)
            .onEach { Timber.d("password: $it") }
            .onEach { vm.onEvent(RegisterEvent.OnPasswordChange(it)) }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        binding.tvLogin.setOnClickListener {
            findNavController().navigate(RegisterFragmentDirections.actionFragmentRegisterToFragmentLogin())
        }

        binding.btnRegister.setOnClickListener {
            vm.register()
        }

        vm.state.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.RESUMED)
            .onEach { Timber.d("state: $it") }
            .onEach {
                it.registerState.onSuccess {
                    findNavController().apply {
                        binding.progressCircular.visibility = View.GONE
                        navigate(
                            RegisterFragmentDirections.actionFragmentRegisterToFragmentLogin(),
                            navOptions = navOptions {
                                launchSingleTop = true
                                popUpTo(R.id.fragment_register) {
                                    saveState = true
                                    inclusive = true
                                }
                            }
                        )
                    }
                }.onError {
                    binding.progressCircular.visibility = View.GONE
                    Snackbar.make(
                        requireView(),
                        getString(R.string.register_failed, message),
                        Snackbar.LENGTH_SHORT
                    ).setAction(getString(R.string.dismiss)) {
                        vm.onEvent(RegisterEvent.HandledEvent)
                    }.show()
                }.onLoading {
                    binding.progressCircular.visibility = View.VISIBLE
                }.onInitial {
                    binding.progressCircular.visibility = View.GONE
                }
            }
            .onEach { vm.onEvent(RegisterEvent.HandledEvent) }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}