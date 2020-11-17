package com.sonymobile.customizationselector;

import android.app.job.JobInfo;
import android.app.job.JobInfo.Builder;
import android.app.job.JobInfo.TriggerContentUri;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Settings.Global;

public class DSDataSubContentJob extends JobService {

    private static final long CONTENT_MAX_UPDATE_DELAY_MS = 300;
    private static final long CONTENT_MIN_UPDATE_DELAY_MS = 100;

    private static final int JOB_ID = 123;

    private static final JobInfo JOB_INFO;
    private static final Uri MULTI_SIM_DATA_URI = Global.getUriFor("multi_sim_data_call");
    private static final String TAG = DSDataSubContentJob.class.getSimpleName();
    private ConfigurationTask mConfigurationTask;

    private static class ConfigurationTask extends AsyncTask<Void, Context, Void> {
        private final Context mContext;

        public ConfigurationTask(Context context) {
            this.mContext = context;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            CustomizationSelectorService.evaluateCarrierBundle(this.mContext);
            return null;
        }
    }

    static {
        Builder builder = new Builder(JOB_ID, new ComponentName("com.sonymobile.customizationselector", DSDataSubContentJob.class.getName()));
        builder.addTriggerContentUri(new TriggerContentUri(MULTI_SIM_DATA_URI, 1)).setTriggerContentUpdateDelay(CONTENT_MIN_UPDATE_DELAY_MS).setTriggerContentMaxDelay(CONTENT_MAX_UPDATE_DELAY_MS);
        JOB_INFO = builder.build();
    }

    public static void scheduleJob(Context context) {
        int schedule = context.getSystemService(JobScheduler.class).schedule(JOB_INFO);
        CSLog.d(TAG, "Schedule job (" + JOB_INFO.getId() + ") - result:" + schedule);
    }

    public boolean onStartJob(final JobParameters jobParameters) {
        CSLog.d(TAG, "onStartJob");
        this.mConfigurationTask = new ConfigurationTask(this) {
            @Override
            protected void onPostExecute(Void unused) {
                DSDataSubContentJob.this.jobFinished(jobParameters, false);
                DSDataSubContentJob.scheduleJob(DSDataSubContentJob.this);
            }
        };
        this.mConfigurationTask.execute();
        return true;
    }

    public boolean onStopJob(JobParameters jobParameters) {
        CSLog.d(TAG, "onStopJob");
        if (this.mConfigurationTask != null) {
            this.mConfigurationTask.cancel(true);
        }
        return true;
    }
}
