package com.getsensibill.spendmanager.demo.subclass

import com.getsensibill.spendmanager.demo.databinding.ActivitySubclassKotlinBinding
import com.getsensibill.web.data.UiFinishReason
import com.getsensibill.web.ui.WebUiActivity
import com.getsensibill.web.ui.WebUiFragment

/**
 * Subclassing the WebUiActivity
 * <p>
 * When you subclass the WebUiActivity, you do not need to override anything for it to work.
 * It will use all the defaults provided by the [WebUiActivity] and the [WebUiFragment].
 *
 * If you are using your own view, then its important to override the [callSetContentView] and
 * the [webContainerId] so that the [WebUiActivity] knows where to place the [WebUiFragment].
 */
class SubclassKotlinActivity : WebUiActivity(), WebUiFragment.Listener {
    private lateinit var binding: ActivitySubclassKotlinBinding

    /**
     * Can be used to override the setContentView, so that it uses your provided
     * activities view, instead of the default [WebUiActivity] view.
     */
    override fun callSetContentView() {
        binding = ActivitySubclassKotlinBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    /**
     * If we are overriding the setContentView, we also need to set the container id
     * which will contain the [WebUiFragment].
     */
    override val webContainerId: Int
        get() = binding.container.id

    /**
     * Not required override.
     *
     * The shouldNavigateBack from the [webFragment] will inform you if your activity
     * should handle the back pressed normally.
     */
    override fun onBackPressed() {
        if (webFragment?.shouldNavigateBack() != false) finish()
    }

    /**
     * Not required override.
     *
     * This will be called when ending the WebUi flow, and will give you a [UiFinishReason]
     * which can be used to identify why the flow is ending.
     */
    override fun onRequestFinish(reason: UiFinishReason) {
        finish()
    }
}
