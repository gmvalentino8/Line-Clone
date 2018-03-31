package com.valentino.line
import android.content.Context
import android.support.v4.view.PagerAdapter
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout

/**
 * Created by Valentino on 3/31/18.
 */



class StartPagerAdapter : PagerAdapter {

    var context : Context
    var images : IntArray
    lateinit var inflater : LayoutInflater

    constructor(context: Context, images: IntArray):super() {
        this.context = context
        this.images = images
    }

    override fun isViewFromObject(view: View?, `object`: Any?): Boolean {
        return view == `object` as LinearLayout
    }

    override fun getCount(): Int {
        return images.size
    }

    override fun instantiateItem(container: ViewGroup?, position: Int): Any {
        var image : ImageView
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var view = inflater.inflate(R.layout.item_start_pager, container, false)
        image = view.findViewById(R.id.startPagerImageView)
        image.setBackgroundResource(images[position])
        container?.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
        container?.removeView(`object` as LinearLayout)
    }


}