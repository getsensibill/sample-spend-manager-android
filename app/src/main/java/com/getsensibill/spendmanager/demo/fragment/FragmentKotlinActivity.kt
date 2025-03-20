package com.getsensibill.spendmanager.demo.fragment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.commit
import com.getsensibill.spendmanager.demo.R
import com.getsensibill.spendmanager.demo.databinding.ActivityFragmentKotlinBinding
import com.getsensibill.web.data.UiFinishReason
import com.getsensibill.web.data.configuration.NavigationIntent
import com.getsensibill.web.data.configuration.WebTheme
import com.getsensibill.web.data.models.Brand
import com.getsensibill.web.ui.WebUiActivity
import com.getsensibill.web.ui.WebUiFragment
import com.getsensibill.web.ui.WebUiFragment.Listener
import com.getsensibill.web.ui.WebUiNetworkErrorFragment

/**
 * Directly using the [WebUiFragment]
 * <p>
 * When using [WebUiFragment] directly, please ensure to properly implement the [Listener] methods.
 * Also, please use, or see [WebUiNetworkErrorFragment] as reference for what to display when
 * [Listener.onDisplayNetworkError] is called.
 */
class FragmentKotlinActivity : AppCompatActivity(),
    WebUiFragment.Listener, WebUiNetworkErrorFragment.Listener {

    private lateinit var binding: ActivityFragmentKotlinBinding

    @IdRes
    private val webContainerId: Int = R.id.fragment_container

    /** The instance of [WebUiFragment] that is being displayed by the activity */
    private val webFragment: WebUiFragment?
        get() = supportFragmentManager.findFragmentByTag(TAG_WEB_FRAGMENT) as? WebUiFragment

    private val errorFragment: WebUiNetworkErrorFragment?
        get() = supportFragmentManager.findFragmentByTag(TAG_WEB_NETWORK_ERROR_FRAGMENT) as? WebUiNetworkErrorFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFragmentKotlinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            loadWebUi()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (webFragment?.shouldNavigateBack() != false) super.onBackPressed()
    }

    /**
     * Required Override when using [WebUiFragment.Listener]
     *
     * Handle when the network error fragment needs to be navigated to. We have provided a
     * [WebUiNetworkErrorFragment] that you can display to the user. Or you can use your
     * own fragment or even an alternative logic flow if desired.
     */
    override fun onDisplayNetworkError(networkNotAvailable: Boolean) {
        if (isFinishing) return
        supportFragmentManager.commit(true) {
            val fragment = errorFragment ?: WebUiNetworkErrorFragment()
            fragment.apply {
                arguments = Bundle().apply {
                    // If not provided, the fragment assumes that there is no network connection (default: true)
                    putBoolean(WebUiNetworkErrorFragment.ARG_NO_NETWORK_ERROR, networkNotAvailable)
                }
            }
            replace(webContainerId, fragment, TAG_WEB_NETWORK_ERROR_FRAGMENT)
        }
    }

    /**
     * Required Override when using [WebUiFragment.Listener]
     *
     * This will be called when ending the WebUi flow, and will give you a [UiFinishReason]
     * which can be used to identify why the flow is ending. When integrating by fragment, you
     * will need to handle what happens when the flow ends here.
     */
    override fun onRequestFinish(reason: UiFinishReason) {
        finish()
    }

    // -----WebUiNetworkErrorFragment.Listener Overrides -----
    /**
     * Required Override when using [WebUiNetworkErrorFragment.Listener]
     *
     * This is the returning call from the [WebUiNetworkErrorFragment] when the retry button is
     * pressed. If using a fragment integration and not subclassing [WebUiActivity], then the
     * error view will not know how to handle the event.
     */
    override fun onNetworkErrorRetry() {
        loadWebUi()
    }

    /**
     * Required Override when using [WebUiNetworkErrorFragment.Listener]
     *
     * This is the returning call from the [WebUiNetworkErrorFragment] when the ok/close button is
     * pressed. If using a fragment integration and not subclassing [WebUiActivity], then the
     * error view will not know how to handle the event.
     */
    override fun onNetworkErrorClose() {
        onRequestFinish(UiFinishReason.UNABLE_TO_LOAD_SPA)
    }


    private fun loadWebUi() {
        if (isFinishing) return
        supportFragmentManager.commit {
            replace(
                webContainerId,
                webFragment ?: createWebUiFragment(),
                TAG_WEB_FRAGMENT
            )
        }
    }

    /**
     * When creating the [WebUiFragment], you can either return the fragment back directly,
     * or override arguments using the apply block. Here is an example of overriding the
     * [WebUiFragment.ARG_NAVIGATION_OVERRIDE] and [WebUiFragment.ARG_WEB_THEME_OVERRIDE] arguments.
     */
    private fun createWebUiFragment(): WebUiFragment = WebUiFragment().apply {
        arguments = Bundle().apply {
            // Pass in a navigation override. Defaults as .DASHBOARD
            putParcelable(WebUiFragment.ARG_NAVIGATION_OVERRIDE, NavigationIntent.DASHBOARD)
            // Pass in a custom Theme override. Defaults to null
            putParcelable(
                WebUiFragment.ARG_WEB_THEME_OVERRIDE,
                WebTheme(Brand())
            )
        }
    }

    companion object {
        private const val TAG_WEB_FRAGMENT = "TAG_WEB_FRAGMENT"
        private const val TAG_WEB_NETWORK_ERROR_FRAGMENT = "TAG_WEB_NETWORK_ERROR_FRAGMENT"
    }
}
