package com.longx.intelligent.android.ichat2.activity.settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.OpenSourceLicensesActivity;
import com.longx.intelligent.android.ichat2.activity.VersionActivity;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.bottomsheet.AuthorAccountsBottomSheet;
import com.longx.intelligent.android.ichat2.databinding.ActivityVersionSettingsBinding;
import com.longx.intelligent.android.ichat2.dialog.ConfirmDialog;
import com.longx.intelligent.android.ichat2.dialog.CustomViewMessageDialog;
import com.longx.intelligent.android.ichat2.fragment.settings.BasePreferenceFragmentCompat;
import com.longx.intelligent.android.ichat2.util.AppUtil;
import com.longx.intelligent.android.ichat2.value.Constants;
import com.longx.intelligent.android.lib.materialyoupreference.preferences.Material3Preference;

public class VersionSettingsActivity extends BaseActivity {
    private ActivityVersionSettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVersionSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        setupPreferenceFragment(savedInstanceState);
    }

    private void setupPreferenceFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(binding.settings.getId(), new SettingsFragment())
                    .commit();
        }
    }

    public static class SettingsFragment extends BasePreferenceFragmentCompat implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {
        private Material3Preference preferenceAuthor;
        private Material3Preference preferenceVersionName;
        private Material3Preference preferenceVersionCode;
        private Material3Preference preferenceOpenSourceLicenses;
        private Material3Preference preferenceUserGuide;

        @Override
        protected void init(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences_version, rootKey);
            doDefaultActions();
        }

        @Override
        protected void bindPreferences() {
            preferenceAuthor = findPreference(getString(R.string.preference_key_author));
            preferenceVersionName = findPreference(getString(R.string.preference_key_version_name));
            preferenceVersionCode = findPreference(getString(R.string.preference_key_version_code));
            preferenceOpenSourceLicenses = findPreference(getString(R.string.preference_key_open_source_licenses));
            preferenceUserGuide = findPreference(getString(R.string.preference_key_user_guide));
        }

        @Override
        protected void showInfo() {
            preferenceAuthor.setSummary(Constants.AUTHOR);
            preferenceVersionName.setSummary(AppUtil.getVersionName(requireContext()));
            preferenceVersionCode.setSummary(String.valueOf(AppUtil.getVersionCode(requireContext())));
        }

        @Override
        protected void setupYiers() {
            preferenceAuthor.setOnPreferenceClickListener(this);
            preferenceOpenSourceLicenses.setOnPreferenceClickListener(this);
            preferenceUserGuide.setOnPreferenceClickListener(this);
            preferenceVersionName.setOnPreferenceClickListener(this);
            preferenceVersionCode.setOnPreferenceClickListener(this);
        }

        @Override
        public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
            return false;
        }

        @Override
        public boolean onPreferenceClick(@NonNull Preference preference) {
            if(preference.equals(preferenceAuthor)){
                new ConfirmDialog(getActivity(), R.drawable.default_avatar, null, "作者: LONG", true)
                        .setNeutralButton("账号", (dialog, which) -> {
                            new AuthorAccountsBottomSheet(getActivity()).show();
                        })
                        .setPositiveButton()
                        .create()
                        .show();
            } else if(preference.equals(preferenceOpenSourceLicenses)){
                startActivity(new Intent(requireContext(), OpenSourceLicensesActivity.class));
            }else if(preference.equals(preferenceUserGuide)){
                new CustomViewMessageDialog((AppCompatActivity) requireActivity(), getString(R.string.user_guide_info)).create().show();
            }else if(preference.equals(preferenceVersionCode) || preference.equals(preferenceVersionName)){
                startActivity(new Intent(requireContext(), VersionActivity.class));
            }
            return true;
        }
    }
}