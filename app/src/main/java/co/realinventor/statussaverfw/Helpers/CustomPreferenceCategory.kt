package co.realinventor.statussaverfw.Helpers

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.TextView
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceViewHolder




class CustomPreferenceCategory : PreferenceCategory {
    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet,
                defStyle: Int) : super(context, attrs, defStyle) {
    }

    override public fun onBindViewHolder(holder: PreferenceViewHolder?) {
        super.onBindViewHolder(holder)
        val titleView = holder!!.findViewById(android.R.id.title) as TextView
        titleView.setTextColor(Color.BLACK)
        titleView.textSize = "24.0".toFloat()
    }
}