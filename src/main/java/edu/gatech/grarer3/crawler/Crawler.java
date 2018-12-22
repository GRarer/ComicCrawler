package edu.gatech.grarer3.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import java.util.*;
import java.util.List;

import org.apache.commons.io.FileUtils;


public class Crawler {

    private static final int timeoutMillis = 10000; //timeout when fetching html

    private URL startURL;
    //url substring used to identify which image is the comic, usually "/comics/"
    private String comicSubstring;

    private boolean useAltTextSubstring;

    private ImageProcessor imageProcessor;


    List<String> eventLog;


    public Crawler(URL startURL, String comicSubstring, boolean useAltTextSubstring, ImageProcessor ip) {
        this.startURL = startURL;
        this.comicSubstring = comicSubstring;
        this.useAltTextSubstring = useAltTextSubstring;
        this.imageProcessor = ip;
        this.eventLog = new ArrayList<>();
    }



    /**
     * Crawls from this crawler's start URL and saves images in sequence.
     */
    public void readComic() {

        URL nextURL = startURL;

        while (nextURL!=null) {
            nextURL = readPage(nextURL);
        }

        /*
         * Remove duplicate images from end.
         * The crawler visits the last page of some comics (e.g. XKCD) twice before it realizes it's in a loop.
         */
        try {
            File folder = new File(imageProcessor.getDestinationFolder());

            File[] fileArray = folder.listFiles();

            if(fileArray==null) {
                throw new FileNotFoundException();
            }


            if(fileArray.length>1) {

                List<File> images = new ArrayList<>(Arrays.asList(fileArray));

                /*
                 * Sort files by number to find last and second last.
                 * listFiles() doesn't give the correct order.
                 * e.g. it lists 10.png before 9.png
                 */
                images.sort((f1, f2) -> {
                    String name1 = f1.getName();
                    name1 = name1.replaceAll("\\D+","");
                    String name2 = f2.getName();
                    name2 = name2.replaceAll("\\D+","");

                    int i1 = Integer.parseInt(name1);
                    int i2 = Integer.parseInt(name2);

                    if (i1 == i2) {
                        return f1.getName().compareTo(f2.getName());
                    } else {
                        return i1 - i2;
                    }
                });

                File lastImage = images.get(images.size()-1);
                File secondLastImage = images.get(images.size()-2);

                if(FileUtils.contentEquals(lastImage, secondLastImage)) {

                    if(!lastImage.delete()) {
                        System.out.println("\n");
                        System.out.println("Finished.");
                        System.out.println();
                        System.out.println("Failed to delete duplicate image.");
                        System.out.println("Please manually check whether the last panel has been saved twice.");
                    } else {
                        System.out.println("Duplication error detected and correctly resolved.");
                        System.out.println("\n");
                        System.out.println("Finished.");
                    }
                } else {
                    System.out.println("\n");
                    System.out.println("Finished.");
                }
            }
        } catch (Exception ex) {
            eventLog.add("The last panel may have been erroneously saved twice. Please check manually.");
        }

        if(eventLog.size()>0) {
            System.out.println("\n\n\nEvent Log: ");


            for(String event : eventLog) {
                System.out.println();
                System.out.println(event);

            }
        } else {
            System.out.println("No exceptions to report.");
        }



    }


    /**
     * @param url the url of the page (not the URL of the image)
     * @return the URL of the next page, or null if the next page cannot be found
     */
    private URL readPage(URL url) {

        System.out.println(url.toString());

        //get html document
        Document doc;
        try {
            doc = Jsoup.parse(url, timeoutMillis);
        } catch (IOException ex) {
            //try again
            try {
                System.out.println("Page Not Found: " + url.toString());
                System.out.println("Trying to load page again");
                doc = Jsoup.parse(url, timeoutMillis * 10);
            } catch (IOException ex2) {
                System.out.println("Page Not Found. Exiting crawler loop.");
                eventLog.add("Page not found: " + url.toString());
                return null;
            }
        }


        List<URL> comicImageURLs = findComicImageURLs(doc);

        if (comicImageURLs.isEmpty()) {
            System.out.println("No valid comic found");
            eventLog.add("No valid comic found on " + url.toString());
        } else {
            for (URL imgURL : comicImageURLs) {
                try {
                    imageProcessor.saveImage(imgURL);
                } catch (IOException e) {
                    System.out.println("Failed to write image to file. Skipping to next page.");
                    eventLog.add(imgURL.toString() + " from " + url.toString() + " failed to write to file");
                }
            }
        }

        return findNextLink(doc, url);
    }


    private List<URL> findComicImageURLs(Document document) {

        List<URL> imgURLs = new ArrayList<>();

        Elements media = document.select("[src]");


        if (useAltTextSubstring) {
            //look for images with the substring in their alt-text
            for (Element element : media) {
                if (element.attr("abs:alt").contains(comicSubstring)) {
                    try {
                        imgURLs.add(new URL(element.attr("abs:src")));
                    } catch (MalformedURLException e) {
                        eventLog.add("Malformed image url: " + element.attr("abs:src"));
                    }
                }
            }
        } else {
            //look for images with the substring in their URL
            for (Element element : media) {
                if (element.attr("abs:src").contains(comicSubstring)) {
                    try {
                        imgURLs.add(new URL(element.attr("abs:src")));
                    } catch (MalformedURLException e) {
                        eventLog.add("Malformed image url: " + element.attr("abs:src"));
                    }
                }
            }
        }

        return imgURLs;
    }

    private URL findNextLink(Document document, URL docURL) {

        //find link to next page using 'rel' attribute
        Elements links = document.select("a[href]");
        String nextURLString = "";

        for (Element link : links) {
            if(link.attr("rel").equals("next")) {
                nextURLString = link.attr("abs:href");
                break;
            }
        }

        //for sites that don't implement the 'rel' attribute, look for links with 'next' in the link text
        if(nextURLString.equals("")) {
            for (Element link : links) {
                String linkText = link.text();
                if(linkText.toLowerCase().contains("next")) {
                    nextURLString = link.attr("abs:href");
                    break;
                }
            }
        }

        if (nextURLString.equals("")) {
            System.out.println("No 'next' link found.");
            return null;
        }

        try {
            URL nextURL = new URL(nextURLString);

            //check for getting stuck in a loop
            if (nextURL.equals(docURL)) {
                System.out.println("Next link leads back to the same page.");
                return null;
            }

            return nextURL;
        } catch (MalformedURLException e) {
            System.out.println("Malformed 'next' link URL.");
            return null;
        }
    }
}
