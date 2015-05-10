package com.example.parser;

import javax.swing.*;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;

/**
 * Created by User on 10.05.15.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        long start = System.nanoTime();
        startGui();
        ForkJoinPool forkJoinPool = new ForkJoinPool(10);
        Set<String> links = forkJoinPool.invoke(new Grabber("http://bash.im"));
        long total = System.nanoTime() - start;
        System.out.println(links);
        System.out.printf("Found %d links%n", links.size());
        System.out.printf("It took %.2f ms", total / 1E6);
    }

    public static void startGui() {
        JFrame frame = new JFrame("Parser");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

    }
}
