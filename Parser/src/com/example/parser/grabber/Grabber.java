package com.example.parser.grabber;


import com.example.parser.util.Util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.RecursiveTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Grabber extends RecursiveTask<Set<String>> {

    private static final String LINK_PATTERN = "<a[^<]*href\\s*=\\s*\"(/[^\"]*)\"";
    private static final int DEFAULT_DEEP_LEVEL = 3;

    private final LinksCollector linksCollector;

    private final URI url;

    private final String baseUrl;

    private final int deepLevel;

    private TaskResultCallback callback;

    public Grabber(URI url, TaskResultCallback callback)  {
        this(url, DEFAULT_DEEP_LEVEL);
        this.callback = callback;
    }

    public Grabber(URI url, int deepLevel) {
        this(url, deepLevel, new LinksCollector());
    }

    private Grabber(URI url, int deepLevel, LinksCollector linksCollector) {
        this.url = url;
        this.deepLevel = deepLevel;
        baseUrl = url.getHost();
        this.linksCollector = linksCollector;
    }

    public Set<String> compute() {
        try {
            long start = System.nanoTime();
            if (deepLevel == 0 || isCancelled()) {
                return Collections.emptySet();
            }
            String content = Util.getDataFromUrl(url);
            Set<String> links = findLinks(content);

            System.out.printf("Worker: %-25s:: Done with: %s%n", Thread.currentThread().getName(), url);

            List<Grabber> subTasks = startChildThreads(links);
            subTasks.stream().forEach(task -> links.addAll(task.join()));

            if (!isCancelled() && callback != null) {
                long end = System.nanoTime();
                callback.onResult(new TaskResult(baseUrl, links, (long) ((end - start) / 1E6)));
            }
            return links;
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptySet();
        }
    }

    private List<Grabber> startChildThreads(Set<String> links) {
        List<Grabber> subTasks = new ArrayList<>(links.size());
        for (String link : links) {
            try {
                URI uri = new URI("http://" + baseUrl + link);
                Grabber grabber = new Grabber(uri, deepLevel - 1, linksCollector);
                grabber.fork();
                subTasks.add(grabber);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        return subTasks;
    }

    private Set<String> findLinks(String content) {
        Pattern compile = Pattern.compile(LINK_PATTERN);
        Matcher matcher = compile.matcher(content);
        Set<String> result = new TreeSet<>();
        while (matcher.find()) {
            String uri = matcher.group(1);
            boolean linkUnique = linksCollector.isLinkUnique(uri);
            if (linkUnique) {
                result.add(uri);
            }
        }
        return result;
    }


}
