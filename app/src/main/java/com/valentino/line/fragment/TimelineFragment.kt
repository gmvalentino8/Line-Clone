package com.valentino.line.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*

import com.valentino.line.R

class TimelineFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_timeline, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_timeline_options, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

}
