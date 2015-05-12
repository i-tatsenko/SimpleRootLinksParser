package com.example.parser.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;

public class Util {

    private static final int BUFFER_SIZE = 1024;
    private static final int MAX_PAGE_DL_SIZE = 2000 * 1024;

    public static String getDataFromUrl(URI url) throws IOException {
        URLConnection connection = url.toURL().openConnection();
        return downloadFromStream(connection.getInputStream());
    }

    private static String downloadFromStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        StringBuilder result = new StringBuilder();
        int bytesRead;
        int totalByesRead = 0;
        while ((bytesRead = inputStream.read(buffer)) > 0) {
            result.append(new String(buffer, 0, bytesRead));
            totalByesRead += bytesRead;
            if (!checkPageSize(totalByesRead)) {
                System.out.println("Too large file, " + totalByesRead);
                return "";
            }
        }
        return result.toString();
    }

    /**
     * @param size downloaded page size
     * @return false if more than MAX_PAGE_DL_SIZE downloaded per page
     */
    private static boolean checkPageSize(int size) {
        return size < MAX_PAGE_DL_SIZE;
    }

}

