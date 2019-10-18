package com.vrgsoft.core.presentation.router

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.vrgsoft.core.presentation.fragment.argumented.Argumented
import com.vrgsoft.core.presentation.fragment.argumented.ArgumentedFragment
import com.vrgsoft.core.presentation.fragment.argumented.BaseArguments
import com.vrgsoft.core.presentation.fragment.BaseFragment
import com.vrgsoft.core.presentation.fragment.IBaseRouter

abstract class FragmentRouter(
    protected val manager: FragmentManager
) : IBaseRouter {

    protected abstract val containerId: Int

    fun replaceWithoutBackStack(
        fragment: androidx.fragment.app.Fragment
    ) {
        manager.beginTransaction()
            .replace(containerId, fragment, fragment::class.java.simpleName)
            .addToBackStack(null)
            .commit()
    }

    fun androidx.fragment.app.Fragment.replace() {
        manager.beginTransaction()
            .replace(containerId, this, this::class.java.simpleName)
            .commit()
    }

    fun addWithBackStack(
        fragment: androidx.fragment.app.Fragment
    ) {
        manager.beginTransaction()
            .replace(containerId, fragment, fragment::class.java.simpleName)
            .addToBackStack(fragment::class.java.simpleName)
            .commit()
    }

    fun androidx.fragment.app.Fragment.replaceWithBackStack() {
        manager.beginTransaction()
            .replace(containerId, this, this::class.java.simpleName)
            .addToBackStack(this::class.java.simpleName)
            .commit()
    }

    fun popBackStack() {
        manager.popBackStack()
    }

    fun currentFragment() = manager.findFragmentById(containerId)

    protected inline fun <reified T : BaseFragment<*>> createInstance(): T {
        val instance = T::class.constructors.first().call()

        instance.arguments = Bundle()

        return instance
    }

    protected inline fun <A : BaseArguments, reified T : Argumented<A>> createArgumentedInstance(args: BaseArguments): T {
        val instance = T::class.constructors.first().call()

        (instance as Fragment).arguments = Bundle().apply {
            putParcelable(ArgumentedFragment.KEY_ARGS, args)
        }

        return instance
    }

    override fun clickBack(): Boolean {
        if (manager.backStackEntryCount > 1) {
            with(currentFragment()) {
                return if (this is Routable) {
                    if (!this.backPressed()) {
                        manager.popBackStack()
                    }
                    true
                } else {
                    manager.popBackStack()
                    true
                }
            }
        } else {
            return false
        }
    }
}