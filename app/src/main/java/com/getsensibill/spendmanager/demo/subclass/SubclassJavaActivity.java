package com.getsensibill.spendmanager.demo.subclass;

import android.os.Bundle;

import com.getsensibill.spendmanager.demo.databinding.ActivitySubclassJavaBinding;
import com.getsensibill.web.data.UiFinishReason;
import com.getsensibill.web.data.configuration.NavigationIntent;
import com.getsensibill.web.data.configuration.ProgrammaticTheme;
import com.getsensibill.web.data.models.Brand;
import com.getsensibill.web.ui.WebUiActivity;
import com.getsensibill.web.ui.WebUiFragment;
import com.getsensibill.web.ui.WebUiNetworkErrorFragment;

import org.jetbrains.annotations.NotNull;

import timber.log.Timber;

/**
 * Subclassing the WebUiActivity
 * <p>
 * When you subclass the WebUiActivity, you do not need to override anything for it to work.
 * It will use all the defaults provided by the [WebUiActivity] and the [WebUiFragment].
 *
 * If you are using your own layout, then its important to override the [callSetContentView] and
 * the [webContainerId] so that the [WebUiActivity] knows where to place the [WebUiFragment].
 */
public class SubclassJavaActivity extends WebUiActivity implements WebUiFragment.Listener {

    private ActivitySubclassJavaBinding binding;

    /**
     * Not required override.
     *
     * Can be used to override the setContentView, so that it uses your provided
     * activities view, instead of the default [WebUiActivity] view.
     */
    @Override
    protected void callSetContentView() {
        binding = ActivitySubclassJavaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    /**
     * Not required override, unless you are overriding [callSetContentView]
     *
     * If we are overriding the setContentView, we also need to set the container id
     * which will contain the [WebUiFragment].
     */
    @Override
    protected int getWebContainerId() {
        return binding.webContainer.getId();
    }

    /**
     * Not required override.
     *
     * If you want to change the default navigation when first loading into Spend Manager,
     * you can override this and provide a different [NavigationIntent]. Alternatively,
     * you can also apply this as an argument when overriding [createWebUiFragment].
     */
    @Override
    protected @NotNull NavigationIntent getWebInitialNavigation() {
        return NavigationIntent.DASHBOARD.INSTANCE;
    }

    /**
     * Not required override.
     *
     * Used to create the [WebUiFragment] held in the Activity. Common reason for overriding
     * this would be if there are arguments that are needed to be supplied to the fragment.
     */
    @Override
    protected @NotNull WebUiFragment createWebUiFragment() {
        WebUiFragment fragment = super.createWebUiFragment();
        Bundle bundle = new Bundle();

        // Pass in a navigation override. Defaults as .DASHBOARD
        bundle.putParcelable(WebUiFragment.ARG_NAVIGATION_OVERRIDE, NavigationIntent.DASHBOARD.INSTANCE);

        // Pass in a custom Theme override. Defaults to null
        ProgrammaticTheme theme = new ProgrammaticTheme(new Brand());
        bundle.putParcelable(WebUiFragment.ARG_PROGRAMMATIC_THEME_OVERRIDE, theme);

        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * Not required override.
     *
     * The shouldNavigateBack from the [webFragment] will inform you if your activity
     * should handle the back pressed normally. Also note, that if you need direct access to
     * activity.onBackPressed, we have [activityOnBackPressed] convenience method you can call.
     */
    @Override
    public void onBackPressed() {
        final WebUiFragment webFragment = getWebFragment();
        final boolean shouldNavigateBack = webFragment == null || webFragment.shouldNavigateBack();
        if (shouldNavigateBack) {
            finish();
        }
    }

    /**
     * Not required override.
     *
     * This will be called when ending the WebUi flow, and will give you a [UiFinishReason]
     * which can be used to identify why the flow is ending. Default behaviour is to try and
     * reload the Spend Manager. If you do not want it to attempt an immediate reload of
     * Spend Manager, you can override this call.
     */
    @Override
    public void onRequestFinish(@NotNull UiFinishReason reason) {
        super.onRequestFinish(reason);
        Timber.i(reason.name());
    }

    /**
     * Not required override.
     *
     * By overriding [onDisplayNetworkError], handle when the network error fragment needs to be
     * navigated to. If you do not override it, then by default Spend Manager will handle when and
     * how to display the [WebUiNetworkErrorFragment].
     */
    @Override
    public void onDisplayNetworkError(boolean networkNotAvailable) {
        super.onDisplayNetworkError(networkNotAvailable);
        Timber.i(networkNotAvailable ? "true" : "false");
    }

    /**
     * Not required override.
     *
     * By overriding this, you can supply additional arguments to the [WebUiNetworkErrorFragment]
     * to change
     * Common reason for overriding this would be in case any arguments need to be supplied to the fragment.
     */
    @Override
    protected @NotNull WebUiNetworkErrorFragment createWebUiNetworkErrorFragment() {
        WebUiNetworkErrorFragment fragment = super.createWebUiNetworkErrorFragment();
        Bundle bundle = new Bundle();

        // If not provided, the fragment assumes that there is no network connection (default: true)
        bundle.putBoolean(WebUiNetworkErrorFragment.ARG_NO_NETWORK_ERROR, true);

        fragment.setArguments(bundle);
        return fragment;
    }
}
