/*
 * Copyright 2015 AT&T
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ecc.ema;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ecc.ema.R;
import com.att.webrtcsdk.MediaType;
import com.att.webrtcsdk.SdpTransformer;
import com.att.webrtcsdk.WebRTCActivity;
import com.att.webrtcsdk.phone.CallConnectedEvent;
import com.att.webrtcsdk.phone.Phone;
import com.att.webrtcsdk.phone.PhoneErrorType;

import org.webrtc.VideoRenderer;
import org.webrtc.VideoRendererGui;

public class RTCActivity extends WebRTCActivity {
    private static final String TAG = RTCActivity.class.getSimpleName();

    private TextView statusText;
    private ProgressDialog progressDialog;

    private TextView callee;

    private GLSurfaceView videoView;
    private VideoRenderer.Callbacks localRender;
    private VideoRenderer.Callbacks remoteRender;
    private AudioManager audioManager;
    private DeviceSelectionFragment fragmentDevice;


    @Override
    protected void onStart() {
        super.onStart();

        //Removes the notification when a call is rejected
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancelAll();

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        fragmentDevice = new DeviceSelectionFragment();
        IntentFilter filter = new IntentFilter();
        filter.addAction("WEBRTC_ACTION");
    }

    public void changeDevice(int which) {
        int backgroundId;

        fragmentDevice.selectedPosition = which;
        Log.d(TAG, "Which value " + which);
        switch (which) {
            case 0:
                backgroundId = R.drawable.speaker_on;
                speakerOn();
                Log.d(TAG, "Speak On clicked");
                break;
            case 1:
                if (audioManager.isWiredHeadsetOn()) {
                    backgroundId = R.drawable.headset;
                } else {
                    backgroundId = R.drawable.speaker_off;
                }
                speakerOff();
                Log.d(TAG, "Speak Off clicked");
                break;
            default:
                backgroundId = R.drawable.speaker_on;
                speakerOn();
                Log.d(TAG, "Speak ON");
        }


        ImageButton speaker = (ImageButton) findViewById(R.id.speaker_button);
        speaker.setTag(backgroundId);
        speaker.setImageResource(backgroundId);
    }

    public void speakerButtonClicked(View view) {
        fragmentDevice.show(getFragmentManager(), "Device");
    }

    @Override
    protected void close() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isFinishing()) {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    finish();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
        setContentView(R.layout.activity_rtc);

        final Fragment callActivityFragment = new OverlayRTCFragment();
        getFragmentManager().beginTransaction().add(
                R.id.sample_content_fragment, callActivityFragment
        ).commit();
        videoView = (GLSurfaceView) findViewById(R.id.glview_call);
        VideoRendererGui.setView(videoView, new Runnable() {
            @Override
            public void run() {
                eglContextReady();
            }
        });
        remoteRender = createRemoteRender();
        localRender = createLocalRender();


        videoView.setOnClickListener(new View.OnClickListener() {
            private boolean visible = true;

            @Override
            public void onClick(View view) {
                Log.d(TAG, "videoView onClick() called");
                if (!callActivityFragment.isAdded()) {
                    Log.d(TAG, "videoView onClick() called but video fragment not yet added");
                    return;
                }
                final FragmentTransaction transaction = getFragmentManager().beginTransaction();
                if (visible) {
                    transaction.hide(callActivityFragment);
                    visible = false;
                } else {
                    transaction.show(callActivityFragment);
                    visible = true;
                }
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
            }
        });
        statusText = (TextView) findViewById(R.id.dialling);
        callee = (TextView) findViewById(R.id.calleeId);
        if (null != getDestination()) {
            final String[] calleeFragments = getDestination().split(":");
            String calleeText = null;
            if (calleeFragments.length > 1) {
                calleeText = calleeFragments[1];
            }
            callee.setText(calleeText == null ? "Unknown" : calleeText);
        } else {
            statusText.setText("Answering...");
            callee.setVisibility(View.GONE);
        }
    }

    public void muteButtonClicked(View view) {
        Object tag = view.getTag();
        int backgroundId = R.drawable.microphone_on;
        if (tag != null && ((Integer) tag).intValue() == backgroundId) {
            backgroundId = R.drawable.microphone_off;
            muteAudio();
        } else {
            backgroundId = R.drawable.microphone_on;
            unmuteAudio();
        }
        view.setTag(backgroundId);
        ((ImageButton) view).setImageResource(backgroundId);
    }

    public void onHangupButtonClick(View view) {
        endCall();
    }

    public void endCall() {
        showProgressBar("Ending", "Ending call...");
        final Phone phone = getPhone();
        synchronized (phone) {
            if (phone.isConnected()) {
                phone.hangup();
            } else {
                phone.cancel();
            }
        }
    }

    public void notYetImplemented(View view) {
        Toast.makeText(getApplicationContext(), "Not yet implemented.", Toast.LENGTH_LONG).show();
    }

    public void dismissProgressBar() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    public void showProgressBar(String title, String message) {
        progressDialog = ProgressDialog.show(RTCActivity.this, title,
                message, false, false);
    }

    @Override
    public void onPause() {
        super.onPause();
        videoView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCallConnecting(MediaType mediaType) {
        if (mediaType.equals(MediaType.AUDIO)) {
            speakerOff();
            ImageButton speakerButton = (ImageButton) findViewById(R.id.speaker_button);
            speakerButton.setTag(R.drawable.speaker_off);
            speakerButton.setImageResource(R.drawable.speaker_off);
        }
    }

    @Override
    public void onCallConnected(final CallConnectedEvent event) {
        super.onCallConnected(event);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                boolean hasVideo = false;
                if (event.getSdp() != null && SdpTransformer.containsVideo(event.getSdp())) {
                    hasVideo = true;
                } else if (getOfferSdp() != null && SdpTransformer.containsVideo(getOfferSdp().description)) {
                    hasVideo = true;
                }


                if (hasVideo) {
                    statusText.setVisibility(View.GONE);
                    callee.setVisibility(View.GONE);
                } else {
                    statusText.setText("Audio Only");
                }
            }
        });
    }

    @Override
    public void onError(final PhoneErrorType type, final String error) {
        super.onError(type, error);
        String message = "error type " + type;
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCallDisconnected(String disconnectedCallId) {
        super.onCallDisconnected(disconnectedCallId);
        dismissProgressBar();
    }

    @Override
    protected VideoRenderer.Callbacks getLocalRender() {
        return localRender;
    }

    @Override
    protected VideoRenderer.Callbacks getRemoteRender() {
        return remoteRender;
    }

    public static class DeviceSelectionFragment extends DialogFragment {
        int selectedPosition = 0;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            int resourceId = R.array.devices;

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Audio Devices")
                    .setSingleChoiceItems(resourceId, selectedPosition, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // The 'which' argument contains the index position
                            // of the selected item
                            ((RTCActivity) getActivity()).changeDevice(which);

                            dialog.cancel();

                        }
                    });
            return builder.create();


        }

        @Override
        public void onStart() {
            super.onStart();
        }
    }
}

/* vim: set expandtab tabstop=4 shiftwidth=4 softtabstop=4: */
