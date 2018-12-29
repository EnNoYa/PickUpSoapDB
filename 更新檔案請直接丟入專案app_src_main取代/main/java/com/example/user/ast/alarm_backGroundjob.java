package com.example.user.ast;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;

public class alarm_backGroundjob extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
