package co.realinventor.statussaverfw

import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import co.realinventor.statussaverfw.Fragments.GridFragment
import co.realinventor.statussaverfw.Fragments.SettingsFragment
import co.realinventor.statussaverfw.Fragments.SlideShowDialogFragment
import co.realinventor.statussaverfw.Helpers.Constants
import com.eftimoff.viewpagertransformers.DefaultTransformer
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.ismaeldivita.chipnavigation.ChipNavigationBar

private const val NUM_PAGES = 4

class HomesActivity :  ChipNavigationBar.OnItemSelectedListener , FragmentActivity(), ViewPager.OnPageChangeListener {
    private lateinit var PAGE_TITLE : String
    private lateinit var mInterstitialAd: InterstitialAd

    companion object{
        public lateinit var menu : ChipNavigationBar
    }

    private lateinit var mPager: ViewPager

    private lateinit var win : Window

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homes)

        // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        MobileAds.initialize(this, "ca-app-pub-4525583199746587~8561261623")

        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = "ca-app-pub-3940256099942544/1033173712"
        mInterstitialAd.loadAd(AdRequest.Builder().build())


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = getWindow()
            win = window
            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            // finally change the color
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorImage))
        }

        try {
            PAGE_TITLE = intent.getStringExtra("title")
        }
        catch (e : Exception){
            PAGE_TITLE = Constants.FRAG_IMAGES
        }

        menu = findViewById(R.id.chipBar) as ChipNavigationBar
        menu.setItemSelected(R.id.menu_images)
        menu.setOnItemSelectedListener(this)

        mPager = findViewById(R.id.viewPagerHome)
        mPager.setPageTransformer(true, DefaultTransformer())

        // The pager adapter, which provides the pages to the view pager widget.
        val pagerAdapter = ScreenSlidePagerAdapter(supportFragmentManager)
        mPager.setOffscreenPageLimit(1)
        mPager.adapter = pagerAdapter

        mPager.setOnPageChangeListener(this)


        when(PAGE_TITLE){
            Constants.FRAG_IMAGES -> {
                mPager.setCurrentItem(0)
            }
            Constants.FRAG_VIDEOS -> {
                mPager.setCurrentItem(1)
            }
            Constants.FRAG_SAVED -> {
                mPager.setCurrentItem(2)
            }
            Constants.FRAG_SETTINGS -> {
                mPager.setCurrentItem(3)
            }
        }
    }


    override fun onItemSelected(id: Int) {
        when (id){
            R.id.menu_images -> {
                Log.d("Menu selected : ","Images")
                mPager.setCurrentItem(0, true)
            }
            R.id.menu_videos -> {
                Log.d("Menu selected : ","Videos")
                mPager.setCurrentItem(1, true)
            }
            R.id.menu_saved -> {
                Log.d("Menu selected : ","Saved")
                mPager.setCurrentItem(2, true)
            }
            R.id.menu_settings -> {
                Log.d("Menu selected : ","Settings")
                mPager.setCurrentItem(3, true)
            }
        }
    }


    override fun onBackPressed() {
        var isFragVisible = false

        var slideShowDialogFragment: SlideShowDialogFragment?
        try {
             slideShowDialogFragment = supportFragmentManager.findFragmentByTag("frag") as? SlideShowDialogFragment
            if (slideShowDialogFragment != null && slideShowDialogFragment.isVisible) {
                Log.e("SSDF", "visible")
                isFragVisible = true
            }

        }
        catch (e : java.lang.Exception){
            Log.e("error",e.message)
        }

        if(isFragVisible){ //SlideShowDialogFragment is Visble, so pop fragment stack
            supportFragmentManager.popBackStack()
        }
        else{
            if (mPager.currentItem == 0) {
                // If the user is currently looking at the first step, allow the system to handle the
                // Back button. This calls finish() on this activity and pops the back stack.
                makeExitAlert()
            } else {
                // Otherwise, select the previous step.
                mPager.currentItem = 0
            }


        }
    }

    private fun makeExitAlert(){
        //Display confirm alert to exit activity
        AlertDialog.Builder(this, R.style.Base_Theme_AppCompat_Dialog)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(resources.getString(R.string.exiting_app))
                .setMessage(resources.getString(R.string.are_you_sure_exit))
                .setPositiveButton(resources.getString(R.string.yes), DialogInterface.OnClickListener { dialog, which ->
                    dialog.cancel()
                    //                        finish();
                    if (mInterstitialAd.isLoaded) {
                        mInterstitialAd.show()
                        mInterstitialAd.adListener = object : AdListener() {
                            override fun onAdLoaded() {
                                // Code to be executed when an ad finishes loading.
                                Log.i("Interstial ad", "Loaded")
                            }

                            override fun onAdFailedToLoad(errorCode: Int) {
                                // Code to be executed when an ad request fails.
                                Log.i("Interstial ad", "Failed to load")
                                finish()
                            }

                            override fun onAdOpened() {
                                // Code to be executed when the ad is displayed.
                                Log.i("Interstial ad", "Ad opened")
                            }

                            override fun onAdLeftApplication() {
                                // Code to be executed when the user has left the app.
                                Log.i("Interstial ad", "User left app")
                                finish()
                            }

                            override fun onAdClosed() {
                                // Code to be executed when when the interstitial ad is closed.
                                Log.i("Interstial ad", "Ad closed")
                                finish()
                            }
                        }

                    } else {
                        Log.d("TAG", "The interstitial wasn't loaded yet.")
                        super.onBackPressed()
                    }
                })
                .setNegativeButton(resources.getString(R.string.no), DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.cancel() })
                .show()
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private inner class ScreenSlidePagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        override fun getCount(): Int = NUM_PAGES

        override fun getItem(position: Int): Fragment {
            when(position){
                0 -> return GridFragment.newInstance(Constants.PATH_WHATSAPP, Constants.GRID_COUNT_IMAGE, Constants.MEDIA_IMAGE, Constants.FRAG_IMAGES)
                1 -> return GridFragment.newInstance(Constants.PATH_WHATSAPP, Constants.GRID_COUNT_IMAGE, Constants.MEDIA_VIDEO, Constants.FRAG_VIDEOS)
                2 -> return GridFragment.newInstance(Constants.PATH_DOWNLOADED, Constants.GRID_COUNT_IMAGE, Constants.MEDIA_SAVED, Constants.FRAG_SAVED)
                3 -> return SettingsFragment()
                else -> return SettingsFragment()
            }
        }

    }


    override fun onPageScrollStateChanged(state: Int) {
        //Do nothing
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        //Do nothing
    }

    override fun onPageSelected(position: Int) {
        when(position){
            0 -> {
                menu.setItemSelected(R.id.menu_images)
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    win.setStatusBarColor(ContextCompat.getColor(this, R.color.colorImage))
                }
            }
            1 -> {
                menu.setItemSelected(R.id.menu_videos)
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    win.setStatusBarColor(ContextCompat.getColor(this, R.color.colorVideo))
                }
            }
            2 -> {
                menu.setItemSelected(R.id.menu_saved)
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    win.setStatusBarColor(ContextCompat.getColor(this, R.color.colorSaved))
                }
            }
            3 -> {
                menu.setItemSelected(R.id.menu_settings)
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    win.setStatusBarColor(ContextCompat.getColor(this, R.color.colorSettings))
                }
            }
        }
    }

}
