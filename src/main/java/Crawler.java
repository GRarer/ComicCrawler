

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import java.util.*;

import org.apache.commons.io.FileUtils;

public class Crawler {

    private static final int timeoutMillis = 10000; //timeout when fetching html

    public static boolean debugMode = false;

    private URL startURL;
    //url substring used to identify which image is the comic, usually "/comics/"
    private String comicSubstring;

    private ImageProcessor imageProcessor;


    public Crawler(URL startURL, String comicSubstring) {

        //TODO find correct first page when startURL is a homepage

        this.startURL = startURL;
        this.comicSubstring = comicSubstring;
        this.imageProcessor = new ImageProcessor();
    }

    public void startReading() {

        URL nextURL = startURL;



        while (nextURL!=null) {
            nextURL = readPage(nextURL);
        }

        /*
         * Remove duplicate pages from end.
         * The crawler visits the last page of some comics (e.g. XKCD) twice before it realizes it's in a loop.
         */
        try {
            File folder = new File(imageProcessor.getDestinationFolder());

            File[] fileArray = folder.listFiles();

            if(fileArray.length>1) {

                System.out.println("Testing for duplicates");

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
                        System.out.println();
                        System.out.println("Failed to delete duplicate image.");
                        System.out.println("Please check whether the last panel has been saved twice.");
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println();
            System.out.println("There was an exception when checking for duplicate panels.");
            System.out.println("Please check whether the last panel has been saved twice.");
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

        if(debugMode) {
            System.out.println("Page media:");
            for (Element element : media) {
                System.out.println(element.attr("abs:src"));
                System.out.println();
            }
        }

        for (Element element : media) {
            if (element.attr("abs:src").contains(comicSubstring)) {
                imageSrc = element.attr("abs:src");

                if(debugMode) {
                    System.out.println("Found image url: " + imageSrc);
                }

                break;
            }
        }

        if (imageSrc==null) {
            System.out.println("No valid comic found");
        } else {
            try {
                imageProcessor.saveImage(new URL(imageSrc));
            } catch (MalformedURLException ex) {
                System.out.println("Malformed image URL. Skipping to next page.");
            } catch (IOException ex) {
                System.out.println("Failed to write image to file. Skipping to next page.");
            }
        }


        //find link to next page
        Elements links = doc.select("a[href]");
        String nextURLString = "";

        if(debugMode) {
            System.out.println("Links:");
            for(Element link : links) {
                System.out.println("link: "+ link.attr("abs:href"));
                System.out.println("rel: " + link.attr("rel"));
                System.out.println("Text: " + link.text());
                System.out.println();
            }
        }

        for (Element link : links) {
            if(link.attr("rel").equals("next")) {
                nextURLString = link.attr("abs:href");
                break;
            }
        }

        //alternate link-finding for sites that don't implement the 'rel' attribute
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
