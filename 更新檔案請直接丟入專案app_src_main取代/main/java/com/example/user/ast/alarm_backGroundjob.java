package com.example.user.ast;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;

import android.util.Log;

public class alarm_backGroundjob extends JobService {

    private JobParameters myjobp; //變數
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d("mjob","背景執行準備開始");
        myjobp = params;
        mTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d("mjob","背景執行結束");
        return false;
    }
    private AsyncTask<Void, Void, Void> mTask = new AsyncTask<Void, Void, Void>() {

        @Override
        protected Void doInBackground(Void... params) {
            Log.d("mjob","背景執行開始囉");
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            jobFinished(myjobp, true);
            Log.d("mjob","背景執行結束");
            super.onPostExecute(result);
        }
    };

}
