package edu.gatech.grarer3.crawler;

import com.beust.jcommander.Parameter;

import java.net.MalformedURLException;
import java.net.URL;

public class ArgParser {

    @Parameter(description = "Start URL")
    private String inputStartURL = null;

    @Parameter(names = {"-s", "-substring"}, description = "URL substring used to identify comic images")
    private String comicSubstring = "/comics/";

    @Parameter(names = {"-a", "-alt"}, description = "Look for images with the specified substring in their alt-text rather than in their URL")
    private Boolean useAltText = false;

    @Parameter(names = {"-d", "-directory", "-folder"}, description = "URL substring used to identify comic images")
    private String directory = System.getProperty("user.home") + "/Desktop/Comic_Output/";

    @Parameter(names = {"-p", "-prefix"}, description = "Prefix to be included in file names")
    private String prefix = "";

    @Parameter(names = {"-n", "-number"}, description = "Number from which to start numbering file names")
    private Integer number = 1;

    public void run() {


        //validate url input
        if(inputStartURL==null) {
            System.out.println("You must provide a first-page URL.");
            System.exit(0);
        }
        String http = "http://";
        String https = "https://";
        if ((inputStartURL.length() < 6)
                ||((!inputStartURL.substring(0,7).equals(http)
                &&(!inputStartURL.substring(0,8).equals(https))))) {
            inputStartURL = http + inputStartURL;
        }

        //add slash to directory
        String slash = "\\";
        if (!directory.substring(directory.length()-1, directory.length()).equals(slash) && !directory.substring(directory.length()-1, directory.length()).equals("/")) {
            directory = directory + slash;
        }

        //verify that starting number is not negative (because i'm not sure how that would interact with file system alphabetization)
        int n = number;
        if (n < 0) {
            System.out.println("Initial file number cannot be negative.");
            System.exit(0);
        }


        System.out.println();
        System.out.println("Starting from url: " + inputStartURL);
        System.out.println("Using comic url substring: " + comicSubstring);
        System.out.println("Outputting to: " + directory);
        if (n != 0) {
            System.out.println("Numbering output starting from " + n);
        }
        System.out.println(useAltText ? "Identifying images using alt-text substring" : "Identifying images using URL substring");
        System.out.println();

        try {
            URL startURL = new URL(inputStartURL);

            ImageProcessor ip = new ImageProcessor(directory, prefix, n);
            Crawler crawler = new Crawler(startURL, comicSubstring, useAltText, ip);

            crawler.readComic();

        } catch (MalformedURLException ex) {
            System.out.println("Malformed start url: " + inputStartURL);
            System.exit(0);
        }
    }
}