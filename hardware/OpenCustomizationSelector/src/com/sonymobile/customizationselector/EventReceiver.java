package com.sonymobile.customizationselector;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.Settings;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import androidx.core.app.NotificationCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

public class EventReceiver extends BroadcastReceiver {

    private static final String TAG = EventReceiver.class.getSimpleName();
    private static final String CHANNEL_ID = "Sony Modem";

    private int getSubId(Context context, Intent intent) {
        int intExtra = intent.getIntExtra("subscription", SubscriptionManager.INVALID_SUBSCRIPTION_ID);
        CSLog.d(TAG, "Event received for subscription: " + intExtra);
        return (intExtra == -1 || !CommonUtil.isMandatorySimParamsAvailable(context, intExtra)) ? -1 : intExtra;
    }

    public void onReceive(Context context, Intent intent) {
        if (context == null || intent == null) {
            CSLog.d(TAG, "Context or Intent was null.");
            return;
        }

        String action = intent.getAction();
        if ("android.telephony.action.CARRIER_CONFIG_CHANGED".equals(action)) {
            CSLog.d(TAG, "Carrier config changed received");

            if (CommonUtil.isDefaultDataSlot(context, getSubId(context, intent))) {
                CSLog.d(TAG, "Default data SIM loaded");
                Intent service = new Intent(context, CustomizationSelectorService.class);
                service.setAction("evaluate_action");
                context.startService(service);
            }
        } else if ("android.intent.action.BOOT_COMPLETED".equals(action)) {
            if (CommonUtil.isDualSim(context)) {
                DSDataSubContentJob.scheduleJob(context);
            }

            notifyStatus(context);
        }
    }

    private void notifyStatus(Context context) {
        String[] status = readModemFile();

        int subID = SubscriptionManager.getDefaultSubscriptionId();
        List<SubscriptionInfo> list = SubscriptionManager.from(context).getActiveSubscriptionInfoList();
        if (list.size() >= 1) {
            subID = list.get(0).getSubscriptionId();
        }

        String ims = context.getSystemService(TelephonyManager.class).createForSubscriptionId(subID).isImsRegistered(subID)
                ? "Registered" : "Not yet registered";
        ims = isModemDefault(status[1]) ? ims.replace("yet ", "") + " (default modem)" : ims;

        NotificationManager manager = context.getSystemService(NotificationManager.class);
        createChannel(manager);

        if (Settings.System.getInt(context.getContentResolver(), "cs_notification", 1) == 1) {
            manager.notify(1, new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle(CHANNEL_ID)
                    .setContentText("Status: " + status[0] + "\nConfig: " + status[1] + "\nIMS: " + ims)
                    .setSmallIcon(R.drawable.ic_baseline_sim_card_24)
                    .setOngoing(false)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setSound(null)
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

    public boolean isModemDefault(String modem) {
        String[] DEFAULT_MODEMS = {"amss_fsg_lilac_tar.mbn",
                "amss_fsg_poplar_tar.mbn", "amss_fsg_poplar_dsds_tar.mbn",
                "amss_fsg_maple_tar.mbn", "amss_fsg_maple_dsds_tar.mbn"};
        for (String m : DEFAULT_MODEMS) {
            if (m.equals(modem)) {
                return true;
            }
        }
        return !modem.contains("ims") && !modem.contains("volte")
                && !modem.contains("vilte") && !modem.contains("vowifi");
    }
}
