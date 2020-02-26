package com.appnexus.opensdk.mocks;

import android.os.Process;

import java.util.concurrent.ThreadFactory;

public class MockPriorityThreadFactory implements ThreadFactory {

    private final int mThreadPriority;

    public MockPriorityThreadFactory(int threadPriority) {
        mThreadPriority = threadPriority;
    }

    @Override
    public Thread newThread(final Runnable runnable) {
        Runnable wrapperRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Process.setThreadPriority(mThreadPriority);
                } catch (Throwable t) {

                }
                runnable.run();
            }
        };
        return new Thread(wrapperRunnable);
    }

}
