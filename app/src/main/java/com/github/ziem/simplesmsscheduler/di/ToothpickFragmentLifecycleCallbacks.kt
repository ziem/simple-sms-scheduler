package com.github.ziem.simplesmsscheduler.di

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import toothpick.Toothpick

class ToothpickFragmentLifecycleCallbacks : FragmentManager.FragmentLifecycleCallbacks() {
    override fun onFragmentPreAttached(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        context: Context
    ) {
        Toothpick.inject(fragment, Toothpick.openScopes(context, fragment))
    }

    override fun onFragmentDetached(fragmentManager: FragmentManager, fragment: Fragment) {
        Toothpick.closeScope(fragment)
    }
}