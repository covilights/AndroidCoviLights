/*
 * Copyright 2020 CoviLights GbR
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.covilights.view.onboarding

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.covilights.R
import com.covilights.databinding.OnboardingFragmentBinding
import com.covilights.user.UserStatus
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules

/**
 * This fragment contains all steps to inform user how app works, ask for the permission required at runtime, and collect user status.
 */
class OnboardingFragment : Fragment() {

    private lateinit var binding: OnboardingFragmentBinding

    private val viewModel: OnboardingViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadKoinModules(onboardingModule)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = OnboardingFragmentBinding.inflate(layoutInflater)
        val view = binding.root
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.navigate.observe(viewLifecycleOwner, Observer { direction ->
            view.findNavController().navigate(direction)
        })

        viewModel.permission.observe(viewLifecycleOwner, Observer { requestPermissions ->
            if (requestPermissions) requestPermissions()
        })

        viewModel.requirePermissionToast.observe(viewLifecycleOwner, Observer { requirePermission ->
            if (requirePermission) Toast.makeText(requireContext(), R.string.onboarding_permission_required_toast, Toast.LENGTH_SHORT).show()
        })

        viewModel.requirePermissionSnackbar.observe(viewLifecycleOwner, Observer { permissionDenied ->
            if (permissionDenied) {
                Snackbar.make(view, getString(R.string.onboarding_permission_required_snackbar), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.onboarding_permission_required_snackbar_action)) {
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri = Uri.fromParts("package", requireContext().packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    }
                    .show()
            }
        })

        viewModel.statusDialog.observe(viewLifecycleOwner, Observer { showStatus ->
            if (showStatus) showStatusDialog()
        })

        return view
    }

    private fun requestPermissions() {
        val permissions: Array<String> = arrayOf(
            Manifest.permission.INTERNET,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        val notGranted = permissions.any { permission ->
            ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED
        }

        if (notGranted) {
            requestPermissions(permissions, PERMISSIONS_REQUEST)
        } else {
            viewModel.onPermissionsGranted()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST -> {
                if (grantResults.isEmpty()) viewModel.onPermissionsNotGranted()

                val isNotGranted = grantResults.any {
                    it != PackageManager.PERMISSION_GRANTED
                }

                if (isNotGranted) {
                    val shouldAskAgain = permissions.any { permission ->
                        shouldShowRequestPermissionRationale(permission)
                    }

                    if (shouldAskAgain) {
                        viewModel.onPermissionsNotGranted()
                    } else {
                        viewModel.onPermissionsDenied()
                    }
                } else {
                    viewModel.onPermissionsGranted()
                }
            }
        }
    }

    private fun showStatusDialog() {
        val customLayout = layoutInflater.inflate(R.layout.onboarding_fragment_content_status_dialog, null)

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.onboarding_status_dialog_title)
            .setPositiveButton(R.string.change) { _, _ ->
                val positive: RadioButton = customLayout.findViewById(R.id.status_positive)
                val exposed: RadioButton = customLayout.findViewById(R.id.status_exposed)
                val negative: RadioButton = customLayout.findViewById(R.id.status_negative)

                val status = when {
                    positive.isChecked -> UserStatus.POSITIVE
                    exposed.isChecked -> UserStatus.EXPOSED_WITHOUT_SYMPTOMS
                    negative.isChecked -> UserStatus.NEGATIVE
                    else -> UserStatus.NEGATIVE
                }

                viewModel.onStatusSelected(status)
            }
            .setNegativeButton(R.string.cancel, null)
            .setCancelable(true)
            .setView(customLayout)
            .show()
    }

    override fun onDestroy() {
        unloadKoinModules(onboardingModule)
        super.onDestroy()
    }

    companion object {
        private const val PERMISSIONS_REQUEST = 1010
    }
}
