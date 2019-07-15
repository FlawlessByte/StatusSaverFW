package co.realinventor.statussaverfw.Fragments

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import co.realinventor.statussaverfw.ConfigureActivity
import co.realinventor.statussaverfw.Helpers.Constants
import co.realinventor.statussaverfw.R

class SettingsFragment() : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener {

    internal val FEEDBACK_KEY = "key_feedback"
    internal val RATE_APP_KEY = "key_rate_app"
    internal val GRID_COL_KEY = "key_grid_column"
    internal val PRIVACY_KEY = "key_privacy"
    internal val WHATS_SEL_KEY = "key_whats_sel"
    internal var gridCount : Int = 3
    internal lateinit var sharedPref :SharedPreferences

    //settings : Select whatsapp, business, gb


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(co.realinventor.statussaverfw.R.xml.preferences, rootKey)

        sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        gridCount = sharedPref!!.getInt("IMAGE_GRID", 3)

        val feedPref = findPreference(FEEDBACK_KEY)
        val ratePref = findPreference(RATE_APP_KEY)
        val privacyPref = findPreference(PRIVACY_KEY)
        val whatsSelPref = findPreference(WHATS_SEL_KEY)
        val gridPref = findPreference(GRID_COL_KEY) as ListPreference
        gridPref.setValueIndex(gridCount-1)
        feedPref.setOnPreferenceClickListener(Preference.OnPreferenceClickListener {
            Log.d("Pref", "Feedback")
            sendFeedback(activity!!)
            false
        })

        whatsSelPref.setOnPreferenceClickListener {
            Log.d("Pref", "whats sel")
            startActivity(Intent(context, ConfigureActivity::class.java))
            activity!!.finish()
            false
        }

        ratePref.setOnPreferenceClickListener(Preference.OnPreferenceClickListener {
            Log.d("Pref", "Rate")
            rateApp(activity!!)
            false
        })

        privacyPref.setOnPreferenceClickListener{
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://realinventor.github.io/StatusSaver/privacy.html"))
            startActivity(browserIntent)
            false
        }

        gridPref.setOnPreferenceChangeListener(this)

    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        if(gridCount != newValue) {
            with(sharedPref.edit()){
                putInt("IMAGE_GRID",newValue.toString().toInt())
                Log.e("Grid val",newValue.toString())
                commit()
                Constants.GRID_COUNT_IMAGE = newValue.toString().toInt()
            }
        }

        return true
    }

    fun sendFeedback(context: Context) {
        var body: String? = null
        try {
            body = context.packageManager.getPackageInfo(context.packageName, 0).versionName
            body = "\n\n-----------------------------\nPlease don't remove this information\n Device OS: Android \n Device OS version: " +
                    Build.VERSION.RELEASE + "\n App Version: 1.0" + body + "\n Device Brand: " + Build.BRAND +
                    "\n Device Model: " + Build.MODEL + "\n Device Manufacturer: " + Build.MANUFACTURER
        } catch (e: PackageManager.NameNotFoundException) {
        }

        val intent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "jimmyjose.mec@gmail.com", null))
        //        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("jimmyjose.mec@gmail.com"))
        intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback from android app [SSFW]")
        intent.putExtra(Intent.EXTRA_TEXT, body)
        context.startActivity(Intent.createChooser(intent, context.resources.getString(R.string.choose_email_client)))
    }


    fun rateApp(context: Context) {
        val uri = Uri.parse("market://details?id=" + context.packageName)
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        try {
            context.startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            context.startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + context.packageName)))
        }

    }

}