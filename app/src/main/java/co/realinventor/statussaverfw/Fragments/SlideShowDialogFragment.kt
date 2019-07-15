package co.realinventor.statussaverfw.Fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import androidx.core.os.postDelayed
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import co.realinventor.statussaverfw.Helpers.Constants
import co.realinventor.statussaverfw.Helpers.Image
import co.realinventor.statussaverfw.Helpers.MediaFiles
import co.realinventor.statussaverfw.HomesActivity
import co.realinventor.statussaverfw.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.eftimoff.viewpagertransformers.CubeOutTransformer
import com.eftimoff.viewpagertransformers.DefaultTransformer
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.lang.Exception

class SlideShowDialogFragment : Fragment(){
    private lateinit var images : ArrayList<Image>
    private var position : Int = 0
    private lateinit var mediaType : String
    private lateinit var viewPager : ViewPager
    private lateinit var mPageAdapter : MyViewPagerAdapter
    private lateinit var floatingActionBarDownload : FloatingActionButton
    private lateinit var floatingActionBarShare : FloatingActionButton
    private lateinit var floatingActionBarDelete : FloatingActionButton
    private lateinit var rootView : View


    companion object{
        fun newInstance(images : ArrayList<Image>, position : Int, mediaType : String) : SlideShowDialogFragment{
            val args = Bundle()
            args.putInt("position", position)
            args.putSerializable("images",images)
            args.putString("media_type",mediaType)
            val fragment = SlideShowDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        images = arguments!!.getSerializable("images") as ArrayList<Image>
        position = arguments!!.getInt("position")
        mediaType = arguments!!.getString("media_type")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        rootView = inflater.inflate(co.realinventor.statussaverfw.R.layout.layout_slideshow_fragment, container, false)

        floatingActionBarDelete = rootView.findViewById(R.id.floating_action_button_delete)
        floatingActionBarShare = rootView.findViewById(R.id.floating_action_button_share)
        floatingActionBarDownload = rootView.findViewById(R.id.floating_action_button_download)
        viewPager = rootView.findViewById(R.id.slideShowViewPager) as ViewPager
        mPageAdapter = MyViewPagerAdapter(images,position)
        viewPager.adapter = mPageAdapter
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener)
        if(mediaType.equals(Constants.MEDIA_IMAGE))
            viewPager.setPageTransformer(true, CubeOutTransformer())
        else
            viewPager.setPageTransformer(true, DefaultTransformer())
        setListeners()
        manageVisibility()

        setCurrentItem(position)

        return rootView
    }

    private fun manageVisibility(){
        when(mediaType){
            Constants.MEDIA_IMAGE,Constants.MEDIA_VIDEO -> {
                floatingActionBarDelete.visibility = View.GONE
                floatingActionBarDownload.animate().translationY(-"350.0".toFloat())
            }
            Constants.MEDIA_SAVED -> {
                floatingActionBarDownload.visibility = View.GONE
                floatingActionBarDelete.animate().translationY(-"350.0".toFloat())
            }
        }
    }

    private fun setListeners(){
        floatingActionBarDownload.setOnClickListener{
            Log.e("DownloadButton","Pressed")
            val filepath = images[viewPager.currentItem].getLarge()
            Log.d("File to be downloaded", filepath)
            MediaFiles.copyToDownload(filepath!!)
            Snackbar.make(rootView, "File saved!",Snackbar.LENGTH_SHORT).show()

            //Notifies MediaScanner about this new file
            val file = File(filepath.replace(MediaFiles.WHATSAPP_STATUS_FOLDER_PATH, MediaFiles.DOWNLOADED_IMAGE_PATH))
            Log.d("MediaScanner ", file.getPath())
            val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            intent.data = Uri.fromFile(file)
            activity!!.sendBroadcast(intent)
        }
        floatingActionBarDelete.setOnClickListener{
            Log.e("DeleteButton","Pressed")
            val builder = AlertDialog.Builder(activity, R.style.AlertDialogCustom)
            builder.setMessage(activity!!.getResources().getString(R.string.are_u_sure_del))
                    .setTitle(activity!!.getResources().getString(R.string.alert))
                    .setPositiveButton(activity!!.getResources().getString(R.string.delete)) { dialog, id ->
                        // FIRE ZE MISSILES!

                        val file = File(images[viewPager.currentItem].getLarge())
                        Log.d("File path", file.path)
                        val deleted = file.delete()
                        //                setCurrentItem(position+1);
                        if (deleted) {
                            Toast.makeText(activity, activity!!.getResources().getString(R.string.deleted_file), Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(activity, activity!!.getResources().getString(R.string.could_not_delete), Toast.LENGTH_SHORT).show()
                        }
                        val intent = Intent(activity, HomesActivity::class.java)
                        intent.putExtra("title", Constants.FRAG_SAVED)
                        activity!!.finish()
                        startActivity(intent)
                    }
                    .setNegativeButton(activity!!.getResources().getString(R.string.cancel)) { dialog, id ->
                        // User cancelled the dialog
                    }
            builder.create()
            builder.show()
        }
        floatingActionBarShare.setOnClickListener{
            Log.e("ShareButton","Pressed")
            Toast.makeText(activity, "Preparing for sharing", Toast.LENGTH_SHORT).show()
            val filepath = images[viewPager.currentItem].getLarge()
            //Log.d("Intent share file", filepath);
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(filepath))
            shareIntent.type = "image/jpeg"
            startActivity(Intent.createChooser(shareIntent, "Choose an app to share"))
        }
    }

    //  page change listener
    internal var viewPagerPageChangeListener: ViewPager.OnPageChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageSelected(position: Int) {
//            displayMetaInfo(position)
//            setListeners()
            manageVisibility()
        }

        override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}

        override fun onPageScrollStateChanged(arg0: Int) {}
    }

    private fun setCurrentItem(pos: Int){
        viewPager.setCurrentItem(pos,false)
    }

    override fun onDestroy() {
        HomesActivity.menu.visibility = View.VISIBLE
        super.onDestroy()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }

    class MyViewPagerAdapter : PagerAdapter{
        private lateinit var images : ArrayList<Image>
        private var position = 0
        private var layoutInflater: LayoutInflater? = null

        private var mc: MediaController? = null


        public constructor(images : ArrayList<Image>, position: Int) : super(){
            this.images = images
            this.position = position
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object` as View
        }

        override fun getCount(): Int {
            return images.size
        }

        public override fun instantiateItem(container: ViewGroup, position: Int): Any {
            layoutInflater = container.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
            val view = layoutInflater!!.inflate(R.layout.image_fullscreen_layout, container, false)

            if (!images[position].isVideo()) run {
                //The content is an image
                val photoView = view.findViewById(R.id.image_preview) as PhotoView
                photoView.visibility = View.VISIBLE

                val image = images[position]

                Glide.with(container.context).load(image.getLarge())
                        .thumbnail(0.5f)
//                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(photoView)

                container.addView(view)
            }

            else{

                class CustomMC(context: Context?) : MediaController(context) {
                    public override fun hide() {
                        visibility = View.INVISIBLE
                        super.hide()
                    }

                    public override fun show() {
                        visibility = View.VISIBLE
                        super.show()
                    }
//                    public override fun dispatchKeyEvent(event : KeyEvent) : Boolean{
//                        Log.e("KeyEvent", "Triggered")
//                        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
//                            (context as FragmentActivity).onBackPressed()
//                            Log.e("Back", "Press code")
//                        }
//
//                        return super.dispatchKeyEvent(event);
//                    }
                }


                //The content is video
                val video = images[position]

                mc = CustomMC(container.context)
//                mc = MediaController(container.context)
                val videoView = view.findViewById(R.id.video_preview) as VideoView
                videoView.setMediaController(mc)

                videoView.visibility = View.VISIBLE
                Log.d("Video to be played ", video.getLarge())
                val large = video.getLarge()
                videoView.setVideoURI(Uri.parse(large))

                //Set MediaController anchored to Framelayour
                val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
                lp.gravity = Gravity.BOTTOM
                mc!!.setLayoutParams(lp)

                (mc!!.getParent() as ViewGroup).removeView(mc)

                (view.findViewById(R.id.frameLayout) as FrameLayout).addView(mc)
                (view.findViewById(R.id.frameLayout) as FrameLayout).visibility = View.VISIBLE

                videoView.requestFocus()
                videoView.seekTo(10)

                videoView.setOnPreparedListener {
                    Log.d("On prepared ", "video prepared")
                    mc!!.setAnchorView(videoView)
                    videoView.requestFocus()
//                    mc!!.show(videoView.duration)
                    mc!!.show()
                }



                videoView.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
                    if (hasFocus) {
                        videoView.start()
                    } else {
                        videoView.pause()
                    }
                }

                container.addView(view)
            }
            return view
        }


        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }
    }
}