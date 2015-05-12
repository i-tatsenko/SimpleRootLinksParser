package com.example.parser.grabber;


import java.util.HashSet;

class LinksCollector {

    private final HashSet<String> collectedLinks = new HashSet<>();

    public LinksCollector() { }

    public synchronized boolean isLinkUnique(String link) {
        //if set already jas such link false will be returned and no data will be but
        return collectedLinks.add(link);
    }

    public void clear() {
        collectedLinks.clear();
    }

}
