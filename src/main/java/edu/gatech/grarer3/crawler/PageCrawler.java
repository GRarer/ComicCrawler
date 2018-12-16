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

public class PageCrawler {

    private static final int timeoutMillis = 10000; //timeout when fetching html

    private URL startURL;
    //url substring used to identify which image is the comic, usually "/comics/"
    private String comicSubstring;

    private ImageProcessor imageProcessor;

    List<String> eventLog;


    public PageCrawler(URL startURL, String comicSubstring, ImageProcessor ip) {
        this.startURL = startURL;
        this.comicSubstring = comicSubstring;
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
            System.out.println("\n");
            System.out.println("There was an exception when finishing.");
            System.out.println("The last panel may have been erroneously saved twice. Please check manually.");
        }


        System.out.println("\nPress 'Enter' to print event log");
        try {
            System.in.read();
        } catch (IOException ex) {
            //do nothing
        }
        System.out.println();
        for(String event : eventLog) {
            System.out.println();
            System.out.println(event);

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
            System.out.println("Page Not Found. Exiting crawler loop.");
            return null;
        }

        //find image
        Elements media = doc.select("[src]");
        String imageSrc = null;

        for (Element element : media) {
            if (element.attr("abs:src").contains(comicSubstring)) {
                imageSrc = element.attr("abs:src");

                break;
            }
        }

        if (imageSrc==null) {
            System.out.println("No valid comic found");
            eventLog.add("No valid comic found on " + url.toString());
        } else {
            try {
                imageProcessor.saveImage(new URL(imageSrc));
            } catch (MalformedURLException ex) {
                System.out.println("Malformed image URL: " + imageSrc);
                eventLog.add("Malformed image URL: " + imageSrc + " on " + url.toString());
            } catch (IOException ex) {
                System.out.println("Failed to write image to file. Skipping to next page.");
                eventLog.add(imageSrc + " from " + url.toString() + " failed to write to file");
            }
        }


        //find link to next page using 'rel' attribute
        Elements links = doc.select("a[href]");
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
            System.out.println("No 'next' link found. Exiting crawler loop.");
            return null;
        }

        try {
            URL nextURL = new URL(nextURLString);

            if (nextURL.equals(url)) {
                System.out.println("Next link leads back to the same page. Exiting crawler loop.");
                return null;
            }

            return nextURL;
        } catch (MalformedURLException e) {
            System.out.println("Malformed 'next' link URL. Exiting crawler loop.");
            return null;
        }
    }

}
