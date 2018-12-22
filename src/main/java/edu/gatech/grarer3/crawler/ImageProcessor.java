package edu.gatech.grarer3.crawler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Objects;

public class ImageProcessor {

    private int counter;
    private String destinationFolder;
    private String fileNamePrefix;


    //TODO better naming for multiple images from same page
    public ImageProcessor(String directory, String fileNamePrefix) {
        this.destinationFolder = directory;
        this.counter = 0;

        if(fileNamePrefix.length()>0) {
            fileNamePrefix = fileNamePrefix + " ";
        }

        this.fileNamePrefix = fileNamePrefix;

        File folder = new File(destinationFolder);

        if (!(folder.mkdirs())) {
            if(Objects.requireNonNull(folder.listFiles()).length != 0) {
                System.out.println();
                System.out.println(destinationFolder +" already contains files.");
                System.out.println("Please move those files or select a different directory, then try again. ");
                System.exit(0);
            }
        }
    }

    public String getDestinationFolder() {
        return this.destinationFolder;
    }



    public  void saveImage(URL imgURL) throws IOException {

        counter++;

        String path = destinationFolder + fileNamePrefix + Integer.toString(counter) + getFileExtension(imgURL);

        URLConnection connection = imgURL.openConnection();
        connection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36");
        ReadableByteChannel readChannel = Channels.newChannel(connection.getInputStream());

        FileOutputStream outputStream = new FileOutputStream(path);
        FileChannel writeChannel = outputStream.getChannel();
        writeChannel.transferFrom(readChannel,0,Long.MAX_VALUE);
        writeChannel.close();
    }

    private String getFileExtension(URL url) {
        String urlString = url.toString();

        if (!urlString.contains(".")) {
            return null;
        }

        return urlString.substring(urlString.lastIndexOf('.'));
    }
}
