package com.rodneybeede.software.recaptchaimagedownloader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class App {

    public static void main(String[] args) throws MalformedURLException, IOException {
        final URL START_URL = new URL("http://www.google.com/recaptcha/demo/");
        final NumberFormat format = new DecimalFormat("0000");


        System.out.println("Current Working Directory is " + (new File(".").getAbsolutePath()));

        final File imagesFolder = new File("./Images");
        imagesFolder.mkdirs();

        for (int i = 0; i < 1000; i++) {
            Object content;
            try {
                content = START_URL.getContent();
            } catch (IOException ex) {
                ex.printStackTrace();
                System.exit(255);
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader((InputStream) content));

            String nextURL = null;
            for (String cLine = reader.readLine(); null != cLine; cLine = reader.readLine()) {
                final String MARKER = "<noscript><iframe src=";
                final int sIdx = cLine.indexOf(MARKER);
                if (-1 != sIdx) {
                    final int eIdx = cLine.indexOf("\"", sIdx + 1 + MARKER.length());
                    nextURL = cLine.substring(sIdx + MARKER.length() + 1, eIdx);

                    break;
                }
            }

            reader.close();

            if (null != nextURL) {
                System.out.println(nextURL);
                content = (new URL(nextURL)).getContent();

                reader = new BufferedReader(new InputStreamReader((InputStream) content));

                for (String cLine = reader.readLine(); null != cLine; cLine = reader.readLine()) {
                    final String MARKER = "src=\"image?c=";
                    final int sIdx = cLine.indexOf(MARKER);
                    if (-1 != sIdx) {
                        final int eIdx = cLine.indexOf("\"", sIdx + 1 + MARKER.length());
                        nextURL = "http://www.google.com/recaptcha/api/" + cLine.substring(sIdx + 5, eIdx);

                        break;
                    }
                }

                System.out.println(nextURL);

                final InputStream isImage = (InputStream) (new URL(nextURL)).openStream();

                final File imageFile = new File(imagesFolder, format.format(i) + ".jpg");
                final FileOutputStream fos = new FileOutputStream(imageFile);

                for(int b = isImage.read(); -1 != b; b = isImage.read()) {
                    fos.write(b);
                }

                fos.close();
                isImage.close();

                System.out.println(imageFile.getAbsoluteFile());
            }
        }
    }
}
