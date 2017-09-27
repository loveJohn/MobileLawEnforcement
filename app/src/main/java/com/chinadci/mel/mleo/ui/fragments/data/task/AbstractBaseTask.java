package com.chinadci.mel.mleo.ui.fragments.data.task;

import android.content.Context;
import android.os.AsyncTask;

public abstract class AbstractBaseTask<Params, Progress, Result> extends
        AsyncTask<Params, Progress, Result> {

    private TaskResultHandler<Result> taskResultHandler;

    private Context mContext;

    public AbstractBaseTask(Context context, TaskResultHandler<Result> taskResultHandler) {
        this.taskResultHandler = taskResultHandler;
        this.mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Result result) {
        taskResultHandler.resultHander(result);
    }

    public interface TaskResultHandler<Result> {
        void resultHander(Result result);
    }
}
