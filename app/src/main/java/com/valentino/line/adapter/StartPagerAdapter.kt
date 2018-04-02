package com.valentino.line.adapter
import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.valentino.line.R

/**
 * Created by Valentino on 3/31/18.
 */



class StartPagerAdapter(var context: Context, private var images: IntArray) : PagerAdapter() {
    lateinit var inflater : LayoutInflater

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object` as LinearLayout
    }

    override fun getCount(): Int {
        return images.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var image : ImageView
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var view = inflater.inflate(R.layout.item_start_pager, container, false)
        image = view.findViewById(R.id.startPagerImageView)
        image.setBackgroundResource(images[position])
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }

}