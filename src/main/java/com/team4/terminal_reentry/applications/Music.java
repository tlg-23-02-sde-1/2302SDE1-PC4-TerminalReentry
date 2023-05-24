package com.team4.terminal_reentry.applications;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

class Music {
    String url = "https://www.vgmusic.com/music/console/nintendo/nes/zeldaund.mid";
    byte[] midiBytes = null;

    public Music() {
//        this.url = url;
        setMidiBytes(url);
    }

    public Music(String url) {
        this.url = url;
        setMidiBytes(url);
    }

    public byte[] getMidiBytes() {
        return midiBytes;
    }

    public int setMidiBytes(String url) {
        // Open a connection to the URL
        URLConnection connection = null;
        try {
            connection = new URL(url).openConnection();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        // Get content length of the file
        int fileLength = connection.getContentLength();

        // Open the input stream
        try {
            InputStream in = new BufferedInputStream(connection.getInputStream());

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            // Create buffer to read the data in chunks
            byte[] dataBuffer = new byte[1024];
            int bytesRead;

            // Read the file and write it to the output stream
            while ((bytesRead = in.read(dataBuffer, 0, dataBuffer.length)) != -1) {
                out.write(dataBuffer, 0, bytesRead);
            }
            midiBytes = out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileLength;
    }
}