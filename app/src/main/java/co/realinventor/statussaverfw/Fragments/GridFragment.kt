package co.realinventor.statussaverfw.Fragments

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import co.realinventor.statussaverfw.Helpers.Constants
import co.realinventor.statussaverfw.Helpers.GalleryAdapter
import co.realinventor.statussaverfw.Helpers.Image
import co.realinventor.statussaverfw.Helpers.MediaFiles
import co.realinventor.statussaverfw.HomesActivity
import co.realinventor.statussaverfw.R
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.util.*

class GridFragment : Fragment() {
    private var images: ArrayList<Image>? = null
    private var mAdapter: GalleryAdapter? = null
    private var recyclerView: RecyclerView? = null
    private var allObjects: java.util.ArrayList<Any>? = null
    private var GRID_COUNT : Int = 3
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private lateinit var MEDIA_TYPE : String
    private lateinit var MEDIA_PATH : String
    private lateinit var TAG : String


    companion object{
        fun newInstance(path : String, gridCount : Int, mediaType : String, tag :String) : GridFragment{
            val args = Bundle()
            args.putString("path", path)
            args.putInt("grid_count", gridCount)
            args.putString("media_type", mediaType)
            args.putString("tag",tag)
            val fragment = GridFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        GRID_COUNT = arguments!!.getInt("grid_count")
        MEDIA_PATH = arguments!!.getString("path")
        MEDIA_TYPE = arguments!!.getString("media_type")
        TAG = arguments!!.getString("tag")

    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?): View {

        val rootView = inflater.inflate(co.realinventor.statussaverfw.R.layout.layout_fragment_grid, container, false)


        images = java.util.ArrayList()

        recyclerView = rootView.findViewById(co.realinventor.statussaverfw.R.id.recycler_view) as RecyclerView
        swipeRefreshLayout = rootView.findViewById(co.realinventor.statussaverfw.R.id.swipe_refresh_layout) as SwipeRefreshLayout

        mAdapter = GalleryAdapter(rootView.context, images!!)

        var mLayoutManager: RecyclerView.LayoutManager = GridLayoutManager(activity, GRID_COUNT)

        recyclerView!!.setLayoutManager(mLayoutManager)
        recyclerView!!.setItemAnimator(DefaultItemAnimator())
        recyclerView!!.setAdapter(mAdapter)

        swipeRefreshLayout!!.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                Log.i("SwipeRefreshLayout ", "onRefresh called from SwipeRefreshLayout")
                processImages()

                Handler().postDelayed({
                    mAdapter!!.notifyDataSetChanged()
                    swipeRefreshLayout!!.setRefreshing(false)
                }, 600)
                Toast.makeText(activity, activity!!.getResources().getString(R.string.refreshed), Toast.LENGTH_SHORT).show()
            }
        })


        recyclerView!!.addOnItemTouchListener(GalleryAdapter.RecyclerTouchListener(rootView.context, recyclerView!!, object : GalleryAdapter.ClickListener {
            override fun onClick(view: View, position: Int) {

                Log.e("onClick", "triggered")
                Log.e("CHild pos", ""+position)
                val fragment = SlideShowDialogFragment.newInstance(images!!,position,MEDIA_TYPE)
//                val ft = fragmentManager?.beginTransaction()
//                ft?.show(fragment)
                HomesActivity.menu.visibility = View.GONE
                activity!!.supportFragmentManager.beginTransaction().replace(R.id.containerViewRel,fragment, "frag").addToBackStack(TAG).commit()


            }

            override fun onLongClick(view: View?, position: Int) {
                Log.e("onLongClick", "triggered")
                Snackbar.make(rootView, "You pressed it long!", Snackbar.LENGTH_SHORT).show()
            }
        }))


        processImages()

        return rootView
    }


    fun processImages() {
        MediaFiles.initMediaFiles()
        MediaFiles.initSavedFiles()

        val filePaths : ArrayList<String>

        when(MEDIA_TYPE){
            Constants.MEDIA_IMAGE -> filePaths = MediaFiles.getImageFiles()
            Constants.MEDIA_VIDEO -> filePaths = MediaFiles.getVideoFiles()
            Constants.MEDIA_SAVED -> filePaths = MediaFiles.getSavedFiles()
            else -> filePaths = MediaFiles.getSavedFiles()
        }

        images?.clear()

        //DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getContext());
        val df : android.text.format.DateFormat = android.text.format.DateFormat()

        for (i in filePaths) {
            val image = Image()
            val file = File(MEDIA_PATH + i)
            val date = Date(file.lastModified())

            val DATA_TAG = "KB"
            val FILE_SIZE = String.format("%.02f", file.length() / 1024.0)

            image.setTimestamp(date.toString())
            image.setSize("$FILE_SIZE $DATA_TAG")
            image.setLarge(MEDIA_PATH + i)
            image.setTime(Date(file.lastModified()))
            if(i.endsWith(".mp4"))
                image.setIsVideo(true)

            images?.add(image)
        }

        Collections.sort(images, Image.dateComparator)

        mAdapter!!.notifyDataSetChanged()
    }

    override fun onStart() {
        super.onStart()
        mAdapter?.notifyDataSetChanged()
    }

    override fun onPause() {
        super.onPause()
        Log.e("Fragment", "Paused")
    }

    override fun onResume() {
        super.onResume()
        Log.e("Fragment", "Resumed")
        var mLayoutManager: RecyclerView.LayoutManager = GridLayoutManager(activity, Constants.GRID_COUNT_IMAGE)
        recyclerView!!.setLayoutManager(mLayoutManager)

    }
}