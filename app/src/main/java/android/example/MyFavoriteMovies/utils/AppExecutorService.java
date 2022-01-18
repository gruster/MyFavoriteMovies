package android.example.MyFavoriteMovies.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public abstract class AppExecutorService {

    private static ExecutorService executorService;
    private static final int THREADS = 2;

    public static ExecutorService getInstance() {
        if (executorService == null) {
            synchronized (AppExecutorService.class) {
                if (executorService == null) {
                    executorService = Executors.newFixedThreadPool(THREADS, new SetDaemonThread());
                }
            }
        }
        return executorService;
    }

    private static class SetDaemonThread implements ThreadFactory {
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        }
    }

}
