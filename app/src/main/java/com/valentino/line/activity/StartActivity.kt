package com.valentino.line.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.View
import android.widget.ImageView
import com.valentino.line.R
import com.valentino.line.StartPagerAdapter
import kotlinx.android.synthetic.main.activity_start.*
import android.widget.LinearLayout

class StartActivity : AppCompatActivity(), View.OnClickListener, ViewPager.OnPageChangeListener {

    private val imageResources = intArrayOf(R.drawable.start_1, R.drawable.start_2,
    R.drawable.start_3, R.drawable.start_4, R.drawable.start_5)
    private var dotsCount = 0
    lateinit private var dots : Array<ImageView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        startViewPager.adapter = StartPagerAdapter(applicationContext, imageResources)
        setupViewPagerIndicator()
        startViewPager.addOnPageChangeListener(this)
        loginTextView.setOnClickListener(this)
        signupTextView.setOnClickListener(this)
    }

    private fun setupViewPagerIndicator() {
        dotsCount = startViewPager.adapter.getCount()
        dots = Array<ImageView>(dotsCount) {ImageView(this)}

        for (i in 0 until dotsCount) {
            dots[i].setImageResource(R.drawable.shape_pager_deselected)
            val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(20, 0, 20, 40)
            pagerIndicator.addView(dots[i], params)
        }
        dots[0].setImageResource(R.drawable.shape_pager_selected)
    }

    override fun onClick(p0: View?) {
        when (p0) {
            loginTextView -> {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
            signupTextView -> {
                val intent = Intent(this, SignupActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onPageScrollStateChanged(state: Int) {}

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    override fun onPageSelected(position: Int) {
        for (i in 0 until dotsCount) {
            dots[i].setImageResource(R.drawable.shape_pager_deselected)
        }
        dots[position].setImageResource(R.drawable.shape_pager_selected)
    }
}
