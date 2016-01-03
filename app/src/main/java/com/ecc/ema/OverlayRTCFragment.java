package com.ecc.ema;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.ecc.ema.R;


/**
 * A placeholder fragment containing a simple view.
 */
public class OverlayRTCFragment extends Fragment {

    private final static String TAG = RTCActivity.class.getSimpleName();
    private boolean viewGroupIsVisible = true;
    private RelativeLayout rl;
    private Context context;

    public OverlayRTCFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View controlView = inflater.inflate(R.layout.overlay_rtc, container, false);

        //context might me null here so check before use
        context = this.getActivity().getApplicationContext();
        return controlView;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        rl = (RelativeLayout) view.findViewById(R.id.fragment_buttons);
        final Fragment fragment = this;

        (view.findViewById(R.id.call_mute)).setTag(R.drawable.microphone_on);
        rl.setOnClickListener(new View.OnClickListener() {
            private boolean visible = false;

            @Override
            public void onClick(View view) {
                final FragmentTransaction transaction = getFragmentManager().beginTransaction();
                if (visible) {
                    transaction.hide(fragment);
                    visible = false;
                } else {
                    transaction.show(fragment);
                    visible = true;
                }
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();

                viewGroupIsVisible = !viewGroupIsVisible;
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
    }

}

/* vim: set expandtab tabstop=4 shiftwidth=4 softtabstop=4: */
