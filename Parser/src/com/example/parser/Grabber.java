package com.example.parser;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.RecursiveTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Grabber extends RecursiveTask<Set<String>> {

    private static final int BUFFER_SIZE = 1024;
    //    private static final String LINK_PATTERN = "<a.*href\\s*=\\s*(\"/([^\"]*\")|/'[^']*'|/([^'\">\\s]+))";
    private static final String LINK_PATTERN = "<a[^<]*href\\s*=\\s*\"(/[^\"]*)\"";
    private static final int DEFAULT_DEEP_LEVEL = 3;


    private final String url;

    private final String baseUrl;

    private final int deepLevel;

    public Grabber(String url) throws URISyntaxException {
        this(url, DEFAULT_DEEP_LEVEL);
    }

    public Grabber(String url, int deepLevel) throws URISyntaxException {
        this.url = url;
        this.deepLevel = deepLevel;
        baseUrl = new URI(url).getHost();
    }

    public Set<String> compute() {
        try {
            if (deepLevel == 0) {
                return Collections.emptySet();
            }
            String content = grab(url);
            Set<String> links = findLinks(content);
            List<Grabber> subTasks = new ArrayList<>(links.size());
            for (String link : links) {
                try {
                    Grabber grabber = new Grabber("http://" + baseUrl + link, deepLevel - 1);
                    grabber.fork();
                    subTasks.add(grabber);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
            subTasks.stream().forEach(task -> links.addAll(task.join()));
            return links;
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptySet();
        }
    }

    private Set<String> findLinks(String content) {
        Pattern compile = Pattern.compile(LINK_PATTERN);
        Matcher matcher = compile.matcher(content);
        Set<String> result = new TreeSet<>();
        while (matcher.find()) {
            String uri = matcher.group(1);
            boolean linkUnique = LinksCollector.getInstance().isLinkUnique(uri);
            if (linkUnique) {
                result.add(uri);
            }
        }
        return result;
    }

    private String grab(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        return downloadFromStream(connection.getInputStream());
    }

    private String downloadFromStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        StringBuilder result = new StringBuilder();
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) > 0) {
            result.append(new String(buffer, 0, bytesRead));
        }
        return result.toString();
    }

}
