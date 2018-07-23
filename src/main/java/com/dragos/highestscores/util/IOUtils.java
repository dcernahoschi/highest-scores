package com.dragos.highestscores.util;

import java.io.*;
import java.nio.charset.Charset;

/**
 * Created by dragos on 22.07.2018.
 */
public class IOUtils {

    private static final String DEFAULT_CHARSET = "ASCII";

    public static String readFromStream(InputStream inputStream) throws IOException {

        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

        byte[] messageBytes = new byte[]{};
        byte[] bytes = new byte[128];
        int read;
        while ((read = bufferedInputStream.read(bytes)) != -1) {

            int prevLength = messageBytes.length;
            messageBytes = new byte[prevLength + read];
            System.arraycopy(bytes, 0, messageBytes, prevLength, read);
            bytes = new byte[128];
        }

        return new String(messageBytes, Charset.forName(DEFAULT_CHARSET));
    }

    public static void writeToStream(OutputStream outputStream, String content) throws IOException {

        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
        bufferedOutputStream.write(content.getBytes(DEFAULT_CHARSET));
        bufferedOutputStream.flush();
    }
}
