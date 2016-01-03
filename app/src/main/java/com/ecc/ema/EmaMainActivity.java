package com.ecc.ema;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ecc.ema.model.ConfigData;
import com.att.webrtcsdk.MediaType;
import com.att.webrtcsdk.apicall.Constants;
import com.att.webrtcsdk.apicall.SdkCallbacks;
import com.att.webrtcsdk.model.OAuthToken;
import com.att.webrtcsdk.phone.CallConnectedEvent;
import com.att.webrtcsdk.phone.InvitationEvent;
import com.att.webrtcsdk.phone.Phone;
import com.att.webrtcsdk.phone.PhoneCallbacks;
import com.att.webrtcsdk.phone.PhoneErrorType;
import com.att.webrtcsdk.phone.PhoneEventAdapter;
import com.att.webrtcsdk.phone.PhoneEventListener;


public class EmaMainActivity extends AppCompatActivity {

    private Phone phone;
    private PhoneEventListener phoneEventListener;

    private ApiRequest apiRequest;
    private EditText userId;
    private EditText phone_number;
    private String access_token;
    private InvitationEvent invitationEvent;
    private String domain;
    private String TAG = EmaMainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ema_main);

        // Required - Set, if the DHS is not run in local
        Constants.setBaseURL("https://devsummit-ewebrtc.herokuapp.com");
        Constants.setSocketUrl("ws://devsummit-ewebrtc.herokuapp.com");

        apiRequest = new ApiRequest();


        userId = (EditText) findViewById(R.id.userId);
        phone_number = (EditText) findViewById(R.id.phone_number);


        phoneEventListener = new PhoneEventAdapter() {
            @Override
            public void onError(PhoneErrorType type, String error) {
                super.onError(type, error);
            }

            @Override
            public void onWarning(String warning) {
                super.onWarning(warning);
            }

            @Override
            public void onSuccess() {
                super.onSuccess();
            }

            @Override
            public void onCallStateChange(Phone.CallState callState) {
                super.onCallStateChange(callState);
            }

            @Override
            public void onPhoneStateChange(Phone.PhoneState phoneState) {
                super.onPhoneStateChange(phoneState);
            }

            @Override
            public void onSessionReady() {
                super.onSessionReady();

                final Intent serviceIntent = new Intent(getApplicationContext(), RTCService.class);
                startService(serviceIntent);


                findViewById(R.id.phone_number).setVisibility(View.VISIBLE);
                findViewById(R.id.video_button).setVisibility(View.VISIBLE);
                findViewById(R.id.audio_button).setVisibility(View.VISIBLE);
                findViewById(R.id.logout_button).setVisibility(View.VISIBLE);
            }

            @Override
            public void onSessionDisconnected() {
                super.onSessionDisconnected();
                stopService(new Intent(getApplicationContext(), RTCService.class));
                finish();
            }

            @Override
            public void onSessionExpired() {
                super.onSessionExpired();
            }

            @Override
            public void onInvitationReceived(InvitationEvent event) {
                super.onInvitationReceived(event);

                findViewById(R.id.answer_button).setVisibility(View.VISIBLE);
                findViewById(R.id.reject_button).setVisibility(View.VISIBLE);

                invitationEvent = event;
            }

            @Override
            public void onCallConnecting(MediaType mediaType) {
                super.onCallConnecting(mediaType);
            }

            @Override
            public void onCallCanceled(String callId) {
                super.onCallCanceled(callId);
            }

            @Override
            public void onCallConnected(CallConnectedEvent event) {
                super.onCallConnected(event);
            }

            @Override
            public void onCallRejected(String callId) {
                super.onCallRejected(callId);
            }

            @Override
            public void onCallDisconnected(String callId) {
                findViewById(R.id.answer_button).setVisibility(View.INVISIBLE);
                findViewById(R.id.reject_button).setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCallUserNotFound() {
                super.onCallUserNotFound();
            }
        };

        phone = Phone.getPhone(getApplicationContext());
        phone.registerEventListener(phoneEventListener);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);


    }

    @Override
    protected void onResume() {
        super.onResume();
        findViewById(R.id.answer_button).setVisibility(View.INVISIBLE);
        findViewById(R.id.reject_button).setVisibility(View.INVISIBLE);
    }

    public void answerCall(View v) {
        Intent intent = new Intent(getApplicationContext(), RTCActivity.class);
        if (invitationEvent.getMediaType() == MediaType.AUDIO_VIDEO) {
            intent.putExtra("MEDIATYPE", MediaType.AUDIO_VIDEO);
        } else {
            intent.putExtra("MEDIATYPE", MediaType.AUDIO);
        }
        intent.putExtra("OFFER_SDP", invitationEvent.getSdp());
        intent.putExtra("CALL_ID", invitationEvent.getCallId());
        startActivity(intent);
    }

    public void rejectCall(View v) {
        phone.rejectCall(invitationEvent);
    }

    public void getConfig() {
        apiRequest.getConfig(new SdkCallbacks.SuccessCallback() {
            @Override
            public void onSuccess(Object object) {
                ConfigData gd = (ConfigData) object;
                domain=gd.getEwebrtcDomain();
                getAToken();
            }
        }, new SdkCallbacks.ErrorCallback() {
            @Override
            public void onError(String error) {
                Log.e(TAG, "Error while getting the config " + error);
            }
        });
        getAToken();
    }

    public void getAToken() {
        apiRequest.getOAuthToken(new SdkCallbacks.SuccessCallback() {
            @Override
            public void onSuccess(Object token) {
                OAuthToken ot = (OAuthToken) token;
                access_token = ot.getAccessToken();
                associateUser();

            }
        }, new SdkCallbacks.ErrorCallback() {
            @Override
            public void onError(String error) {
                Log.e(TAG, "Error while getting Access Token " + error);
            }
        });

    }

    public void associateUser() {
        phone.associateAccessToken(userId.getText().toString(), access_token, new PhoneCallbacks.SuccessCallback() {
            @Override
            public void onSuccess() {
                phone.login(access_token);
                Button login_button = (Button) findViewById(R.id.login_button);
                login_button.setVisibility(View.INVISIBLE);
                Button logout_button = (Button) findViewById(R.id.logout_button);
                logout_button.setVisibility(View.VISIBLE);
                Button video_button = (Button) findViewById(R.id.video_button);
                video_button.setVisibility(View.VISIBLE);
                Button audio_button = (Button) findViewById(R.id.audio_button);
                audio_button.setVisibility(View.VISIBLE);
                EditText phone_number= (EditText)findViewById(R.id.phone_number);
                phone_number.setVisibility(View.VISIBLE);

            }
        }, new PhoneCallbacks.ErrorCallback() {
            @Override
            public void onError(String error) {
                Log.e(TAG, "Error while associate User " + error);
            }
        });
    }

    public void login(View v) {
        getConfig();

    }

    public void dial(View v) {
        MediaType mediaType = MediaType.AUDIO;
        Log.d("Button Type ", v.getId() + "");

        if (v.getId() == R.id.audio_button) {
            mediaType = MediaType.AUDIO;
        } else {
            mediaType = MediaType.AUDIO_VIDEO;
        }

        if (0 == phone_number.getText().toString().length()) {
            Toast.makeText(getApplicationContext(), "Enter a valid callee ", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent idCallIntent = new Intent(getApplicationContext(), RTCActivity.class);
        String validatedPhoneNumber = Utility.validatePhoneNumber(phone_number.getText().toString());
        if (null != validatedPhoneNumber) {
            //Valid PhoneNumber
            idCallIntent.putExtra("DESTINATION", "tel:" + validatedPhoneNumber);
            idCallIntent.putExtra("MEDIATYPE", mediaType);
            startActivity(idCallIntent);
        } else {

            //TODO : validate emailID or UserID with domain if needed
            String userId = phone_number.getText().toString();
            idCallIntent.putExtra("DESTINATION", "sip:" + userId + '@' + domain);
            idCallIntent.putExtra("MEDIATYPE", mediaType);
            startActivity(idCallIntent);
        }


    }

    public void logout(View v) {
        phone.logout();
        Button login_button = (Button) findViewById(R.id.login_button);
        login_button.setVisibility(View.VISIBLE);
        Button logout_button = (Button) findViewById(R.id.logout_button);
        logout_button.setVisibility(View.INVISIBLE);
        Button video_button = (Button) findViewById(R.id.video_button);
        video_button.setVisibility(View.INVISIBLE);
        Button audio_button = (Button) findViewById(R.id.audio_button);
        audio_button.setVisibility(View.INVISIBLE);
        EditText phone_number= (EditText)findViewById(R.id.phone_number);
        phone_number.setVisibility(View.INVISIBLE);
    }

}
