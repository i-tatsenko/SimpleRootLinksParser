package com.example.parser;


import java.util.HashSet;

public class LinksCollector {

    private static final LinksCollector INSTANCE = new LinksCollector();

    private LinksCollector() { }

    public static LinksCollector getInstance() {
        return INSTANCE;
    }

    private final HashSet<String> collectedLinks = new HashSet<>();

    public synchronized boolean isLinkUnique(String link) {
        //if set already jas such link false will be returned and no data will be but
        return collectedLinks.add(link);
    }

    public void clear() {
        collectedLinks.clear();
    }



}
