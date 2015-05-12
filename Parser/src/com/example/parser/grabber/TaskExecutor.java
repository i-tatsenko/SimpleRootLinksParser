package com.example.parser.grabber;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ForkJoinPool;

public class TaskExecutor {

    public static final int DEFAULT_PARALLELISM = 20;
    private ForkJoinPool pool = new ForkJoinPool(DEFAULT_PARALLELISM);

    public void newTask(URI uri, TaskResultCallback callback) throws URISyntaxException {
        pool.shutdownNow();
        pool = new ForkJoinPool(DEFAULT_PARALLELISM);
        Grabber task = new Grabber(uri, callback);
        System.out.printf("Starting with parallelism %d%n", pool.getParallelism());
        pool.execute(task);
    }
}
