package com.sruthi.floodalert;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

    public class MyFirebaseMessagingService
            extends FirebaseMessagingService {

        @Override
        public void onMessageReceived(RemoteMessage remoteMessage) {

            if (remoteMessage.getNotification() != null) {

                String title = remoteMessage.getNotification().getTitle();
                String body = remoteMessage.getNotification().getBody();

                NotificationManager manager =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel =
                            new NotificationChannel("flood_channel",
                                    "Flash Flood Alerts",
                                    NotificationManager.IMPORTANCE_HIGH);
                    manager.createNotificationChannel(channel);
                }

                NotificationCompat.Builder builder =
                        new NotificationCompat.Builder(this, "flood_channel")
                                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                                .setContentTitle(title)
                                .setContentText(body)
                                .setPriority(NotificationCompat.PRIORITY_HIGH);

                manager.notify(1, builder.build());
            }
        }
    }

