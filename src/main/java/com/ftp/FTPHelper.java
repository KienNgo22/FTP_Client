/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ftp;

/**
 *
 * @author Admin
 */
import java.io.*;

public class FTPHelper {
    //Close everything
    public static void close(Closeable c) {
    if (c != null) {
        try {
            c.close();
            } catch (IOException e) {}
        }
    }   
    
    //Check for existent of local file
    public static void LocalfileExists(File file) throws IOException {
        if (file == null || !file.exists() || !file.isFile()) {
            throw new IOException("Local file does not exist: " + file);
        }
    }
    
    //This is for upload and download IO stream. 
    public static void copy(InputStream input, OutputStream output) throws IOException {
    byte[] buffer = new byte[4096];
    int bytesRead;

    while ((bytesRead = input.read(buffer)) != -1) {
        output.write(buffer, 0, bytesRead);
    }

    output.flush();
}
}
