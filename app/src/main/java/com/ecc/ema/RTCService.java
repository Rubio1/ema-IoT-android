package com.ecc.ema;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;

import com.ecc.ema.R;
import com.att.webrtcsdk.phone.InvitationEvent;
import com.att.webrtcsdk.phone.Phone;
import com.att.webrtcsdk.phone.PhoneEventAdapter;
import com.att.webrtcsdk.phone.PhoneEventListener;
import com.att.webrtcsdk.service.WebRTCSDKService;

public class RTCService extends Service {

    private final static String TAG = RTCService.class.getSimpleName();
    private Phone phone;
    private InvitationEvent invitationEvent;
    private PhoneEventListener eventListener;
    private NotificationManager notificationManager;
    private final static int FOREGROUND_SERVICE_NOTIFICATION_ID = 5;
    private final static int NOTIFICATION_ID = 6;

    @Override
    public void onCreate() {
        super.onCreate();

        phone = Phone.getPhone(getApplicationContext());

        //Initialize SDK Service
        final Intent serviceSDKIntent = new Intent(getApplicationContext(), WebRTCSDKService.class);
        startService(serviceSDKIntent);

        this.eventListener = new PhoneEventAdapter() {

            @Override
            public void onInvitationReceived(InvitationEvent event) {
                super.onInvitationReceived(event);

                notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                invitationEvent = event;

                // Intent to launch the RTCActivity to answer the call.
                Intent answerIntent = new Intent(getApplicationContext(), RTCActivity.class);
                answerIntent.putExtra("OFFER_SDP", event.getSdp());
                answerIntent.putExtra("CALLER", event.getFrom());
                answerIntent.putExtra("CALL_ID", event.getCallId());
                answerIntent.putExtra("MEDIATYPE", event.getMediaType());

                // Intent to reject the call by WebRTCService itself with phone.rejectCall(callId)
                Intent rejectIntent = new Intent(getApplicationContext(), RTCService.class);
                rejectIntent.putExtra("CALL_ID", event.getCallId());

                //FullScreen Intent - Launch respective activity to handle in your app to handle incoming call
                Intent showTaskIntent = new Intent(
                        getApplicationContext(), EmaMainActivity.class
                );
                showTaskIntent.setAction(Intent.ACTION_MAIN);
                showTaskIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                showTaskIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                PendingIntent contentIntent = PendingIntent.getActivity(
                        getApplicationContext(), 0, showTaskIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

                // Notification for incoming call
                Notification callNotification = new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.app_icon_small)
                        .setContentTitle("Incoming Call")
                        .setContentText(event.getFrom())
                        .setAutoCancel(false)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                        .addAction(new android.support.v4.app.NotificationCompat.Action.Builder(R.drawable.answer, "Answer", PendingIntent.getActivity(
                                getApplicationContext(), 10, new Intent(answerIntent).putExtra("ACTION", "Answer"),
                                PendingIntent.FLAG_UPDATE_CURRENT
                        )).build())
                        .addAction(new android.support.v4.app.NotificationCompat.Action.Builder(R.drawable.reject, "Reject", PendingIntent.getService(
                                getApplicationContext(), 11, new Intent(rejectIntent).putExtra("ACTION", "Reject"),
                                PendingIntent.FLAG_UPDATE_CURRENT
                        )).build())
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE))
                        .setFullScreenIntent(contentIntent, true)
                        .build();

                notificationManager.notify(NOTIFICATION_ID, callNotification);
            }

            @Override
            public void onCallDisconnected(String callId) {
                notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                notificationManager.cancelAll();
            }
        };

        phone.registerEventListener(eventListener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (intent == null || intent.getExtras() == null) {
            Intent showTaskIntent = new Intent(
                    getApplicationContext(), EmaMainActivity.class
            );
            showTaskIntent.setAction(Intent.ACTION_MAIN);
            showTaskIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            showTaskIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            PendingIntent contentIntent = PendingIntent.getActivity(
                    getApplicationContext(), 0, showTaskIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );

            Notification notification = new Notification.Builder(getApplicationContext())
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("Logged into AT&T WebRTC")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setWhen(System.currentTimeMillis())
                    .setContentIntent(contentIntent)
                    .build();
            startForeground(FOREGROUND_SERVICE_NOTIFICATION_ID, notification);
        } else if (intent.getExtras() != null) {
            final Bundle extras = intent.getExtras();
            final String action = extras.getString("ACTION", null);
            phone = Phone.getPhone(getApplicationContext());

            if (action != null) {
                if (action.equalsIgnoreCase("Reject")) {
                    phone.rejectCall(extras.getString("CALL_ID", null));
                    //Removes the notification when a call is rejected
                    NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.cancel(RTCService.NOTIFICATION_ID);
                }
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
