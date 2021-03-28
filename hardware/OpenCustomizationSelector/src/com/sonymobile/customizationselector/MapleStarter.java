package com.sonymobile.customizationselector;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.IBinder;
import android.os.SystemProperties;
import android.provider.Settings;
import android.telephony.SubscriptionManager;
import androidx.core.app.NotificationCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class MapleStarter extends Service {

    private static final String TAG = "MapleStarter";
    private static final String CHANNEL_ID = "Sony Modem";

    public static boolean mStarted = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private int mSubID = -1;

    private final BroadcastReceiver carrierConfigReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            CSLog.d(TAG, "Carrier config changed received");
            mSubID = getSubId(context, intent);
        }
    };

    private final BroadcastReceiver bootReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            CSLog.d(TAG, "BOOT COMPLETE changed received");

            if (CommonUtil.isDefaultDataSlot(context, mSubID)) {
                CSLog.d(TAG, "Default data SIM loaded");
                Intent service = new Intent(context, CustomizationSelectorService.class);
                service.setAction("evaluate_action");
                context.startService(service);
            }
            if (CommonUtil.isDualSim(context)) {
                DSDataSubContentJob.scheduleJob(context);
            }
            notifyStatus(context);
            mStarted = false;

            unregisterReceiver(bootReceiver);
            unregisterReceiver(carrierConfigReceiver);
            CSLog.d(TAG, "Stopping...");
            stopSelf();
        }
    };

    @Override
    public void onCreate() {
        CSLog.d(TAG, "Service: onCreate");
        registerReceiver(carrierConfigReceiver, new IntentFilter("android.telephony.action.CARRIER_CONFIG_CHANGED"));
        registerReceiver(bootReceiver, new IntentFilter(Intent.ACTION_BOOT_COMPLETED));
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private int getSubId(Context context, Intent intent) {
        int intExtra = intent.getIntExtra("subscription", SubscriptionManager.INVALID_SUBSCRIPTION_ID);
        CSLog.d(TAG, "Event received for subscription: " + intExtra);
        return (intExtra == -1 || !CommonUtil.isMandatorySimParamsAvailable(context, intExtra)) ? -1 : intExtra;
    }

    private void notifyStatus(Context context) {
        String[] status = readModemFile();

        NotificationManager manager = context.getSystemService(NotificationManager.class);
        createChannel(manager);

        if (Settings.System.getInt(context.getContentResolver(), "cs_notification", 1) == 1) {
            manager.notify(1, new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle(CHANNEL_ID)
                    .setContentText("Status: ...")
                    .setSmallIcon(R.drawable.ic_baseline_sim_card_24)
                    .setOngoing(false)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setSound(null)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText("Status: " + status[0] + "\nConfig: " + status[1] +
                                    "\nCust ID: " + SystemProperties.get(Configurator.PROP_TA_AC_VERSION, "N/A")))
                    .setColorized(true)
                    .addAction(R.drawable.ic_baseline_sim_card_24, "Disable Notification",
                            PendingIntent.getBroadcast(context, 1, new Intent(context, NotificationReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT))
                    .build());
        }
    }

    private void createChannel(NotificationManager manager) {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_HIGH);
        channel.setLightColor(Color.WHITE);
        channel.setSound(null, null);
        channel.enableVibration(false);

        manager.createNotificationChannel(channel);
    }

    private String[] readModemFile() {
        String[] stat = {"N/A", "N/A"};
        try {
            File file = new File("/cache/modem/modem_switcher_status");
            if (file.exists()) {
                String line, data = "";

                BufferedReader br = new BufferedReader(new FileReader(file));
                while ((line = br.readLine()) != null) {
                    data += line;
                }
                data = data.replace("\n", "").replace("\r", "")
                        .replace("{", "").replace("}", "").replace("\"", "").trim();

                return data.equals("") ? stat : new String[]{data.split(",")[0], data.split(",")[1]};
            } else {
                return stat;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return stat;
        }
    }
}
