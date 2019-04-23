package com.ambrosus.ambviewer

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_asset_details.*
import java.lang.IllegalArgumentException
import kotlin.reflect.KClass

class AssetDetailsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
        = inflater.inflate(R.layout.fragment_asset_details, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tabLayout.setupWithViewPager(viewPager)
        viewPager.adapter = PagerAdapter(arguments, childFragmentManager, context!!)
    }

    class PagerAdapter(
            private val args: Bundle?,
            fm: FragmentManager,
            private val context: Context)
        : FragmentPagerAdapter(fm) {

        private val fragments = arrayOf(
                        Pair(AssetInfoFragment::class.java, R.string.titleInfo),
                        Pair(AssetIdentifiersFragment::class.java, R.string.titleIdentifiers),
                        Pair(AssetEventsFragment::class.java, R.string.titleEvents)
        )

        override fun getCount(): Int = fragments.size

        override fun getItem(position: Int) = instantiate(context, fragments[position].first.name, args)

        override fun getPageTitle(position: Int): CharSequence? = context.getString(fragments[position].second)
    }
}
