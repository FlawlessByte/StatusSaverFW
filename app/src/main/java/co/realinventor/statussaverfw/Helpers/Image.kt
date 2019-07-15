package co.realinventor.statussaverfw.Helpers


import java.io.Serializable
import java.util.*


public class Image : Serializable {
    private var size: String? = null
    private var large: String? = null
    private var timestamp: String? = null
    private var time: Date? = null
    private var isVideo = false
        private set

    public constructor() {}

    public constructor(name: String, large: String) {
        this.size = name
        this.large = large
    }

    public constructor(name: String, large: String, timestamp: String) {
        this.size = name
        this.large = large
        this.timestamp = timestamp
    }

    public fun setIsVideo(isVideo: Boolean?) {
        this.isVideo = isVideo!!
    }

    public fun setTime(time: Date) {
        this.time = time
    }

    public fun getTime(): Date? {
        return time
    }

    public fun getSize(): String? {
        return size
    }

    public fun setSize(name: String) {
        this.size = name
    }

    public fun getLarge(): String?{
        return large
    }

    public fun setLarge(large: String) {
        this.large = large
    }

    public fun getTimestamp(): String? {
        return timestamp
    }

    public fun setTimestamp(timestamp: String) {
        this.timestamp = timestamp
    }

    public fun isVideo(): Boolean {
        return isVideo
    }


    companion object {
        var dateComparator: Comparator<Image> = Comparator { img1, img2 -> img2.time!!.compareTo(img1.time!!) }
    }


}
