package com.getsensibill.spendmanager.demo.fragment;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.getsensibill.spendmanager.demo.databinding.ActivityFragmentJavaBinding;
import com.getsensibill.web.data.UiFinishReason;
import com.getsensibill.web.data.configuration.NavigationIntent;
import com.getsensibill.web.data.configuration.ProgrammaticTheme;
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
public class FragmentJavaActivity extends AppCompatActivity implements WebUiFragment.Listener {
    private ActivityFragmentJavaBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFragmentJavaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if(savedInstanceState == null) {
            WebUiFragment fragment = new WebUiFragment();
            Bundle bundle = new Bundle();

            // Pass in a navigation override. Defaults as .DASHBOARD
            bundle.putParcelable(
                    WebUiFragment.ARG_NAVIGATION_OVERRIDE,
                    NavigationIntent.DASHBOARD.INSTANCE
            );

            // Pass in a custom Theme override. Defaults to null
            ProgrammaticTheme theme = new ProgrammaticTheme(new Brand());
            bundle.putParcelable(WebUiFragment.ARG_PROGRAMMATIC_THEME_OVERRIDE, theme);

            fragment.setArguments(bundle);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(binding.fragmentContainer.getId(), fragment, "FRAGMENT_TAG")
                    .commit();
        }
    }

    /**
     * Required Override when using [WebUiFragment.Listener]
     *
     * Handle when the network error fragment needs to be navigated to. We have provided a
     * [WebUiNetworkErrorFragment] that you can display to the user. Or you can use your
     * own fragment or even an alternative logic flow if desired.
     */
    @Override
    public void onDisplayNetworkError(boolean networkNotAvailable) {
        WebUiNetworkErrorFragment fragment = new WebUiNetworkErrorFragment();
        Bundle bundle = new Bundle();

        // If not provided, the fragment assumes that there is no network connection (default: true)
        bundle.putBoolean(WebUiNetworkErrorFragment.ARG_NO_NETWORK_ERROR, networkNotAvailable);
        fragment.setArguments(bundle);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(binding.fragmentContainer.getId(), fragment, "NETWORK_ERROR_FRAGMENT_TAG")
                .commit();
    }

    /**
     * Required Override when using [WebUiFragment.Listener]
     *
     * This will be called when ending the WebUi flow, and will give you a [UiFinishReason]
     * which can be used to identify why the flow is ending. When integrating by fragment, you
     * will need to handle what happens when the flow ends here.
     */
    @Override
    public void onRequestFinish(@NotNull UiFinishReason uiFinishReason) {
        finish();
    }
}
