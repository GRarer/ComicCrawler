package edu.gatech.grarer3.crawler;


import com.beust.jcommander.JCommander;

public class Main {
    public static void main(String[] args) {

        if(args.length==0 || args[0].equals("help") || args[0].equals("-help")) {
            printHelpText();
            System.exit(0);
        }

        ArgParser parser = new ArgParser();
        JCommander.newBuilder()
                .addObject(parser)
                .build()
                .parse(args);

        parser.run();
    }

    static void printHelpText() {
        String[] help = {
                "This is a utility for making offline archives of webcomics.",
                "Usage: java -jar crawler.jar <first_page_url> [-d <directory_name>] [-p <file_prefix>] [-s <comic_url_substring>] [-a] [-n <number>]",
                "",
                "-d or -directory or -folder : the directory where images will be saved.",
                "The default is <user_name>/Desktop/comics_output",
                "",
                "-p or -prefix : optional prefix for output file names",
                "",
                "-s or -substring : the substring used to identify comic images.",
                "The crawler looks for this substring to determine which element in the page is the comic image.",
                "The default is '/comics/', which is used in the comic image URLs on most webcomic sites.",
                "",
                "-a or -alt : Identify comic images by looking for the comic substring in the image alt-text.",
                "The default is False (look for substring in the image's URL rather than the Alt-text).",
                "",
                "-n or -number: The filenames for each image count up from this number.",
                "The default is 1. Use a different value if you are downloading new images to be added to an existing archive.",
                "",
                "The offline archives created by this software are intended for personal and archival use only.",
                "Please do not use this software to violate copyright laws."
        };

        for (String s : help) {
            System.out.println(s);
        }
    }
}
