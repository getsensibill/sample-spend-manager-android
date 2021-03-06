package com.getsensibill.spendmanager.demo.fragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.getsensibill.internal.baseui.fragment.SbBaseFragment;
import com.getsensibill.spendmanager.demo.R;
import com.getsensibill.spendmanager.demo.databinding.ActivityFragmentJavaBinding;
import com.getsensibill.web.data.UiFinishReason;
import com.getsensibill.web.data.configuration.NavigationIntent;
import com.getsensibill.web.data.configuration.WebTheme;
import com.getsensibill.web.data.models.Brand;
import com.getsensibill.web.ui.WebUiFragment;
import com.getsensibill.web.ui.WebUiNetworkErrorFragment;

import org.jetbrains.annotations.NotNull;

/**
 * Directly using the [WebUiFragment]
 * <p>
 * When using [WebUiFragment] directly, please ensure to properly implement the [Listener] methods.
 * Also, please use, or see [WebUiNetworkErrorFragment] as reference for what to display when
 * [Listener.onDisplayNetworkError] is called.
 */
public class FragmentJavaActivity extends AppCompatActivity
        implements WebUiFragment.Listener, WebUiNetworkErrorFragment.Listener {

    private static final String TAG_WEB_FRAGMENT = "TAG_WEB_FRAGMENT";
    private static final String TAG_WEB_NETWORK_ERROR_FRAGMENT = "TAG_WEB_NETWORK_ERROR_FRAGMENT";

    private ActivityFragmentJavaBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFragmentJavaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (savedInstanceState == null) {
            loadWebUi();
        }
    }

    /**
     * Required Override when using [WebUiFragment.Listener]
     * <p>
     * Handle when the network error fragment needs to be navigated to. We have provided a
     * [WebUiNetworkErrorFragment] that you can display to the user. Or you can use your
     * own fragment or even an alternative logic flow if desired.
     */
    @Override
    public void onDisplayNetworkError(boolean networkNotAvailable) {
        // If the fragment manager already has reference to an error fragment, reuse that instance.
        // Otherwise, create a fresh one.
        Fragment existingFragment = findFragmentByTag(TAG_WEB_NETWORK_ERROR_FRAGMENT);
        WebUiNetworkErrorFragment fragment = existingFragment instanceof WebUiNetworkErrorFragment ?
                (WebUiNetworkErrorFragment) existingFragment :
                new WebUiNetworkErrorFragment();

        Bundle args = new Bundle();

        // If not provided, the fragment assumes that there is no network connection (default: true)
        args.putBoolean(WebUiNetworkErrorFragment.ARG_NO_NETWORK_ERROR, networkNotAvailable);
        fragment.setArguments(args);

        showFragmentInContainer(fragment, TAG_WEB_NETWORK_ERROR_FRAGMENT);
    }

    @Override
    public void onBackPressed() {
        Fragment existingFragment = findFragmentByTag(TAG_WEB_FRAGMENT);
        if (existingFragment == null
                || (existingFragment instanceof WebUiFragment
                && ((WebUiFragment) existingFragment).shouldNavigateBack())) {
            super.onBackPressed();
        }
    }

    /**
     * Required Override when using [WebUiFragment.Listener]
     * <p>
     * This will be called when ending the WebUi flow, and will give you a [UiFinishReason]
     * which can be used to identify why the flow is ending. When integrating by fragment, you
     * will need to handle what happens when the flow ends here.
     */
    @Override
    public void onRequestFinish(@NotNull UiFinishReason uiFinishReason) {
        finish();
    }

    /**
     * Required Override when using [WebUiNetworkErrorFragment.Listener]
     * <p>
     * This is the returning call from the [WebUiNetworkErrorFragment] when the ok/close button is
     * pressed. If using a fragment integration and not subclassing [WebUiActivity], then the
     * error view will not know how to handle the event.
     */
    @Override
    public void onNetworkErrorClose() {
        onRequestFinish(UiFinishReason.UNABLE_TO_LOAD_SPA);
    }

    // -----WebUiNetworkErrorFragment.Listener Overrides -----

    /**
     * Required Override when using [WebUiNetworkErrorFragment.Listener]
     * <p>
     * This is the returning call from the [WebUiNetworkErrorFragment] when the retry button is
     * pressed. If using a fragment integration and not subclassing [WebUiActivity], then the
     * error view will not know how to handle the event.
     */
    @Override
    public void onNetworkErrorRetry() {
        loadWebUi();
    }

    private void loadWebUi() {
        if (isFinishing()) return;

        Fragment existingFragment = findFragmentByTag(TAG_WEB_FRAGMENT);
        WebUiFragment fragment = existingFragment instanceof WebUiFragment ?
                (WebUiFragment) existingFragment :
                new WebUiFragment();

        if (existingFragment == null) {
            Bundle bundle = new Bundle();
            // Pass in a navigation override. Defaults as .DASHBOARD
            bundle.putParcelable(
                    WebUiFragment.ARG_NAVIGATION_OVERRIDE,
                    NavigationIntent.DASHBOARD.INSTANCE
            );

            // Pass in a custom Theme override. Defaults to null
            WebTheme theme = new WebTheme(new Brand());
            bundle.putParcelable(WebUiFragment.ARG_WEB_THEME_OVERRIDE, theme);

            fragment.setArguments(bundle);
        }

        showFragmentInContainer(fragment, TAG_WEB_FRAGMENT);
    }

    private void showFragmentInContainer(SbBaseFragment fragment, String tag) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment, tag)
                .commit();
    }

    private Fragment findFragmentByTag(String tag) {
        return getSupportFragmentManager().findFragmentByTag(tag);
    }
}
