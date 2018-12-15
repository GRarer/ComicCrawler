import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

public class ImageProcessor {

    private int counter = 0;
    private String destinationFolder;

    public ImageProcessor(String directory) {
        this.destinationFolder = directory;
        this.counter = 0;

        File folder = new File(destinationFolder);
        folder.mkdirs();

    }

    public ImageProcessor() {
        this(System.getProperty("user.home") + "/Desktop/Comic_Output/");
    }

    public String getDestinationFolder() {
        return this.destinationFolder;
    }



    public  void saveImage(URL imgURL) throws IOException {

        System.out.println("Saving image from: " + imgURL.toString());

        counter++;

        String path = destinationFolder + Integer.toString(counter) + getFileExtension(imgURL);

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
