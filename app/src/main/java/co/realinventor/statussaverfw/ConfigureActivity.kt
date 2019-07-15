package co.realinventor.statussaverfw

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.ProgressBar
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import co.realinventor.statussaverfw.Helpers.Constants
import com.google.android.material.button.MaterialButton

class ConfigureActivity : AppCompatActivity() {
    private lateinit var radioGroup : RadioGroup
    private lateinit var continueButton : MaterialButton
    private lateinit var sharedPref : SharedPreferences
    private lateinit var progBarConfigure : ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configure)

//        sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        with (sharedPref.edit()) {
            putBoolean("isFirstTime", false)
            putInt("IMAGE_GRID",3)
            putInt("VIDEO_GRID",3)
            putInt("SAVED_GRID",3)
            apply()
        }


        radioGroup = findViewById(R.id.radioGroup)
        continueButton = findViewById(R.id.continueButton)
        progBarConfigure = findViewById(R.id.progBarConfigure)

        continueButton.setOnClickListener {
            continueButton.visibility = View.INVISIBLE
            progBarConfigure.visibility = View.VISIBLE

            val id = radioGroup.checkedRadioButtonId
            when(id){
                R.id.whatsapp_normal ->
                    Constants.PATH_WHATSAPP = Environment.getExternalStorageDirectory().toString() + "/Whatsapp/Media/.Statuses/"
                R.id.whatsapp_business ->
                    Constants.PATH_WHATSAPP = Environment.getExternalStorageDirectory().toString() + "/WhatsApp Business/Media/.Statuses/"
            }
            with (sharedPref.edit()) {
                putString("WHATSAPP_PATH", Constants.PATH_WHATSAPP)
                apply()
            }
            startActivity(Intent(applicationContext, HomesActivity::class.java))
            finish()
        }

    }
}
