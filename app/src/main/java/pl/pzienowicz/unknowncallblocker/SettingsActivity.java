package pl.pzienowicz.unknowncallblocker;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;

public class SettingsActivity extends AppCompatPreferenceActivity {

    private static final String GP_URL = "https://play.google.com/store/apps/developer?id=Pawel+Zienowicz";

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            Context context = preference.getContext();

            if(preference.getClass() == SwitchPreference.class) {
                stringValue = Boolean.parseBoolean(stringValue) ? context.getString(R.string.enabled) : context.getString(R.string.disabled);
            }

            preference.setSummary(stringValue);
            return true;
        }
    };

    private static void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        if(preference.getClass() == SwitchPreference.class) {
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getBoolean(preference.getKey(), false));
        } else {
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), ""));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_general);

        bindPreferenceSummaryToValue(findPreference("serviceEnabled"));

        findPreference("otherApps").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent i2 = new Intent(Intent.ACTION_VIEW);
                i2.setData(Uri.parse(GP_URL));
                startActivity(i2);

                return true;
            }
        });
    }
}
