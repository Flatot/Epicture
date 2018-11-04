package com.epitech.flatot.epicture.Adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.epitech.flatot.epicture.Views.FragmentBottom.TabLayoutHome.AlbumsFragment
import com.epitech.flatot.epicture.Views.FragmentBottom.TabLayoutHome.GalleryFragment
import java.lang.reflect.Array

class TabLayoutAdapter(val access_token: String, val galFrag: GalleryFragment, fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                galFrag.getAlbums()
                return galFrag
            }
            else -> {
                AlbumsFragment()
            }
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence {
        return if (position == 0) {
            "Gallery"
        } else {
            "Albums"
        }
    }
}