package com.appinventiv.notifiactionsample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    private final String CHANNEL_ID = "xyz";
    private final String TAG = "MainActivity";
    private final int notificationId = 1;
    private NotificationCompat.Builder builder;
    private NotificationManagerCompat managerCompat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnShowBasicNotification = findViewById(R.id.btn_show_basic_notification);
        Button btnShowDownloadingBar = findViewById(R.id.btn_show_donwloading_bar);
        Button btnShowFullScreenNotification = findViewById(R.id.btn_show_full_screen_notification);
        Button btnOpenNotificationSettings = findViewById(R.id.btn_open_notification_settings);

        btnShowBasicNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Snackbar.make(v, R.string.show_notification_setting, 9000)
                        .setAction(R.string.snackbar_action, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                openNotificationSettingsForApp();
                            }
                        })
                        .show();
                createBasicNotification();
            }
        });

        btnShowDownloadingBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDownloadBarOnNotification();
            }
        });
        btnShowFullScreenNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            showFullScreenNotification();
                            Log.d(TAG, "Start thread after 9 seconds");
                        }
                    });
                    thread.sleep(9000);
                    thread.start();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        btnOpenNotificationSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNotificationSettingsForApp();
            }
        });
    }

    private void showFullScreenNotification() {

        Intent fullScreenIntent = new Intent(this, BasicNotificationActivity.class);
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(this, 0,
                fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        builder.setSmallIcon(R.drawable.ic_small_business_pic);
        builder.setContentTitle(getResources().getString(R.string.show_full_screen_notification));
        builder.setContentText(getResources().getString(R.string.notify_content));
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        createNotificationChannel();
        builder.setFullScreenIntent(fullScreenPendingIntent, true);
        managerCompat = NotificationManagerCompat.from(MainActivity.this);
        managerCompat.notify(notificationId, builder.build());
    }

    private void createDownloadBarOnNotification() {
        managerCompat = NotificationManagerCompat.from(this);
        builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_small_business_pic);
        builder.setContentTitle(getResources().getString(R.string.show_basic_notification));
        builder.setContentText(getResources().getString(R.string.notify_content));
        builder.setPriority(NotificationCompat.PRIORITY_LOW);
        createNotificationChannel();
        // Issue the initial notification with zero progress
        int PROGRESS_MAX = 100;
        int PROGRESS_CURRENT = 0;
        builder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);
        managerCompat.notify(notificationId, builder.build());
        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    // When done, update the notification one more time to remove the progress bar
                    builder.setContentText("Download complete")
                            .setProgress(0,0,false);
                    managerCompat.notify(notificationId, builder.build());

                }
            });
            thread.sleep(5000);
            thread.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void createBasicNotification() {

        builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_small_business_pic);
        builder.setContentTitle(getResources().getString(R.string.show_basic_notification));
        builder.setContentText(getResources().getString(R.string.notify_content));
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(
                getResources().getString(R.string.notify_big_text_content)
        ));

        // Create an explicit intent for an Activity in your app
        PendingIntent pendingIntent = performActionOnClickNotification();
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);

        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        createNotificationChannel();
        managerCompat = NotificationManagerCompat.from(MainActivity.this);
        managerCompat.notify(notificationId, builder.build());
       // builder.setAutoCancel(true);


    }

    private PendingIntent performActionOnClickNotification() {
        Intent intent = new Intent(MainActivity.this, BasicNotificationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return PendingIntent.getActivity(this, 0, intent, 0);
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            CharSequence name =  getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void openNotificationSettingsForApp() {
        // Links to this app's notification settings.
        Intent intent = new Intent();
        intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
        intent.putExtra("app_package", getPackageName());
        intent.putExtra("app_uid", getApplicationInfo().uid);
        startActivity(intent);
    }

    private void LogicForShowNotificationOnLoackScreen(){
        // Logic to turn on the screen
       /* PowerManager powerManager = (PowerManager) getApplicationContext().getSystemService(POWER_SERVICE);

        if (!powerManager.isInteractive()){ // if screen is not already on, turn it on (get wake_lock for 10 seconds)
            PowerManager.WakeLock wl = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK |PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE,"MH24_SCREENLOCK");
            wl.acquire(10000);
            PowerManager.WakeLock wl_cpu = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"MH24_SCREENLOCK");
            wl_cpu.acquire(10000);
        }*/
    }
}
