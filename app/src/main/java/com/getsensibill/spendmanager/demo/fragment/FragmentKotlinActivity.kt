package com.getsensibill.spendmanager.demo.fragment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.commit
import com.getsensibill.spendmanager.demo.databinding.ActivityFragmentKotlinBinding
import com.getsensibill.web.data.UiFinishReason
import com.getsensibill.web.data.configuration.NavigationIntent
import com.getsensibill.web.data.configuration.ProgrammaticTheme
import com.getsensibill.web.data.models.Brand
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
class FragmentKotlinActivity : AppCompatActivity(), WebUiFragment.Listener {
    private lateinit var binding: ActivityFragmentKotlinBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFragmentKotlinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(savedInstanceState == null) {
            val fragment = WebUiFragment().apply {
                arguments = Bundle().apply {
                    // Pass in a navigation override. Defaults as .DASHBOARD
                    putParcelable(WebUiFragment.ARG_NAVIGATION_OVERRIDE, NavigationIntent.DASHBOARD)
                    // Pass in a custom Theme override. Defaults to null
                    putParcelable(
                        WebUiFragment.ARG_PROGRAMMATIC_THEME_OVERRIDE,
                        ProgrammaticTheme(Brand())
                    )
                }
            }
            supportFragmentManager.commit {
                replace(binding.fragmentContainer.id, fragment, "FRAGMENT_TAG")
            }
        }
    }

    /**
     * Required Override when using [WebUiFragment.Listener]
     *
     * Handle when the network error fragment needs to be navigated to. We have provided a
     * [WebUiNetworkErrorFragment] that you can display to the user. Or you can use your
     * own fragment or even an alternative logic flow if desired.
     */
    override fun onDisplayNetworkError(networkNotAvailable: Boolean) {
        supportFragmentManager.commit {
            val fragment = WebUiNetworkErrorFragment().apply {
                arguments = Bundle().apply {
                    // If not provided, the fragment assumes that there is no network connection (default: true)
                    putBoolean(WebUiNetworkErrorFragment.ARG_NO_NETWORK_ERROR, true)
                }
            }
            replace(binding.fragmentContainer.id, fragment, "NETWORK_ERROR_FRAGMENT_TAG")
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
}
