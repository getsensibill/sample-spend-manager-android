package com.getsensibill.spendmanager.demo.subclass

import android.os.Bundle
import com.getsensibill.spendmanager.demo.databinding.ActivitySubclassKotlinBinding
import com.getsensibill.web.data.UiFinishReason
import com.getsensibill.web.data.configuration.NavigationIntent
import com.getsensibill.web.data.configuration.ProgrammaticTheme
import com.getsensibill.web.data.models.Brand
import com.getsensibill.web.ui.WebUiActivity
import com.getsensibill.web.ui.WebUiFragment
import com.getsensibill.web.ui.WebUiNetworkErrorFragment
import timber.log.Timber

/**
 * Subclassing the WebUiActivity
 * <p>
 * When you subclass the WebUiActivity, you do not need to override anything for it to work.
 * It will use all the defaults provided by the [WebUiActivity] and the [WebUiFragment].
 *
 * If you are using your own layout, then its important to override the [callSetContentView] and
 * the [webContainerId] so that the [WebUiActivity] knows where to place the [WebUiFragment].
 */
class SubclassKotlinActivity : WebUiActivity(), WebUiFragment.Listener {
    private lateinit var binding: ActivitySubclassKotlinBinding

    /**
     * Not required override.
     *
     * Can be used to override the setContentView, so that it uses your provided
     * activities view, instead of the default [WebUiActivity] view.
     */
    override fun callSetContentView() {
        binding = ActivitySubclassKotlinBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    /**
     * Not required override, unless you are overriding [callSetContentView]
     *
     * If we are overriding the setContentView, we also need to set the container id
     * which will contain the [WebUiFragment].
     */
    override val webContainerId: Int
        get() = binding.webContainer.id

    /**
     * Not required override.
     *
     * If you want to change the default navigation when first loading into Spend Manager,
     * you can override this and provide a different [NavigationIntent]. Alternatively,
     * you can also apply this as an argument when overriding [createWebUiFragment].
     */
    override val webInitialNavigation: NavigationIntent
        get() = NavigationIntent.DASHBOARD

    /**
     * Not required override.
     *
     * Used to create the [WebUiFragment] held in the Activity. Common reason for overriding
     * this would be if there are arguments that are needed to be supplied to the fragment.
     */
    override fun createWebUiFragment(): WebUiFragment = super.createWebUiFragment().apply {
        arguments = Bundle().apply {
            // Pass in a navigation override. Defaults as .DASHBOARD
            putParcelable(WebUiFragment.ARG_NAVIGATION_OVERRIDE, NavigationIntent.DASHBOARD)
            // Pass in a custom Theme override. Defaults to null
            putParcelable(WebUiFragment.ARG_PROGRAMMATIC_THEME_OVERRIDE, ProgrammaticTheme(Brand()))
        }
    }

    /**
     * Not required override.
     *
     * The shouldNavigateBack from the [webFragment] will inform you if your activity
     * should handle the back pressed normally. Also note, that if you need direct access to
     * activity.onBackPressed, we have [activityOnBackPressed] convenience method you can call.
     */
    override fun onBackPressed() {
        if (webFragment?.shouldNavigateBack() != false) activityOnBackPressed()
    }


    /**
     * Not required override.
     *
     * This will be called when ending the WebUi flow, and will give you a [UiFinishReason]
     * which can be used to identify why the flow is ending. Default behaviour is to try and
     * reload the Spend Manager. If you do not want it to attempt an immediate reload of
     * Spend Manager, you can override this call.
     */
    override fun onRequestFinish(reason: UiFinishReason) {
        super.onRequestFinish(reason)
        Timber.i(reason.name)
    }

    /**
     * Not required override.
     *
     * By overriding [onDisplayNetworkError], handle when the network error fragment needs to be
     * navigated to. If you do not override it, then by default Spend Manager will handle when and
     * how to display the [WebUiNetworkErrorFragment].
     */
    override fun onDisplayNetworkError(networkNotAvailable: Boolean) {
        super.onDisplayNetworkError(networkNotAvailable)
        Timber.i(networkNotAvailable.toString())
    }

    /**
     * Not required override.
     *
     * By overriding this, you can supply additional arguments to the [WebUiNetworkErrorFragment]
     * to change
     * Common reason for overriding this would be in case any arguments need to be supplied to the fragment.
     */
    override fun createWebUiNetworkErrorFragment(): WebUiNetworkErrorFragment =
        super.createWebUiNetworkErrorFragment().apply {
            arguments = Bundle().apply {
                // If not provided, the fragment assumes that there is no network connection (default: true)
                putBoolean(WebUiNetworkErrorFragment.ARG_NO_NETWORK_ERROR, true)
            }
        }
}
