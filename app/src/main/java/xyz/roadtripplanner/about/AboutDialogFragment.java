package xyz.roadtripplanner.about;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import xyz.roadtripplanner.R;

/**
 * Dialog About this app
 *
 * @author xyz
 */
public final class AboutDialogFragment extends DialogFragment{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.about_fragment, container, false);
        TextView aboutText = (TextView)fragmentView.findViewById(R.id.about_app);
        if (aboutText != null) {
            aboutText.setMovementMethod(LinkMovementMethod.getInstance());
        }

        return fragmentView;
    }

}
