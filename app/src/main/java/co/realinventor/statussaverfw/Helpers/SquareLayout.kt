package co.realinventor.statussaverfw.Helpers

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.widget.RelativeLayout

class SquareLayout : RelativeLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs : AttributeSet ) : super(context, attrs)

    public constructor(context: Context, attrs : AttributeSet, defStyleAttr : Int) : super(context, attrs, defStyleAttr)

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public constructor(context: Context, attrs : AttributeSet, defStyleAttr : Int, defStyleRes : Int) : super(context, attrs, defStyleAttr, defStyleRes)

    protected override fun onMeasure(widthMeasureSpec : Int, heightMeasureSpec : Int){
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }

}