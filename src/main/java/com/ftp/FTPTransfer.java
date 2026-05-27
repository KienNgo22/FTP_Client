package com.ftp;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Admin
 */
import java.io.*;
import java.net.*;

public class FTPTransfer {

    private final FTPConnection connection;// Control connection is used to send LIST, RETR, STOR and read replies.
    private final FTPPassiveMode passiveMode;// Passive mode helper is used to open temporary data sockets.

    // Create a transfer handler using the current control connection.
    public FTPTransfer(FTPConnection connection) {
        this.connection = connection;
        this.passiveMode = new FTPPassiveMode();
    }

    // Files/directories listing from the server using PASV + LIST.
    // This method needs to use a separate data connection, not just the current control connection.
    public String list(String path) throws IOException {
        Socket datasocket = null; // Open a data socket for list.
        BufferedReader datareader = null; // Reader to read the text from the data connection.

        try {
            // PASV: this line sends PASV to the FTP server and opens the data socket.
            datasocket = passiveMode.openDataSocket(connection);

            FTPReply startReply; // Store the first server reply after LIST.

            // If no path is given, list the current directory.
            if (path == null || path.trim().length() == 0) {
                startReply = connection.sendCommand("LIST");
            } else {
                // Else, list the directory from the path.
                startReply = connection.sendCommand("LIST " + path);
            }

            // Check if the server is ready to send listing data.
            if (!(startReply.getCode() == 125 || startReply.getCode() == 150)) {
                throw new IOException("LIST failed to start: " + startReply);
            }

            // Read the directory listing from the data connection.
            datareader = new BufferedReader(
                    new InputStreamReader(datasocket.getInputStream(), "UTF-8")
            );

            // Store the full listing in a StringBuilder.
            StringBuilder list = new StringBuilder();
            String line;

            // Read until the server closes the data connection.
            while ((line = datareader.readLine()) != null) {
                list.append(line).append(System.lineSeparator());
            }

            // Close the data reader and data socket after receiving the listing.
            FTPHelper.close(datareader);
            FTPHelper.close(datasocket);

            // After data transfer ends, the server sends a final reply, usually 226 or sometimes 250.
            FTPReply finalReply = connection.readReply();

            if (!(finalReply.getCode() == 226 || finalReply.getCode() == 250)) {
                throw new IOException("LIST failed to complete: " + finalReply);
            }

            // Return the full directory listing as a string.
            return list.toString();

        } finally {
            // Try to close resources regarding the error.
            FTPHelper.close(datareader);
            FTPHelper.close(datasocket);
        }
    }

    // Download a file from the FTP server using PASV + RETR.
    public void download(String remoteFile, String localFile) throws IOException {

        Socket dataSocket = null;
        InputStream dataIn = null;
        FileOutputStream fileOut = null;

        try {
            // Open passive data connection first.
            dataSocket = passiveMode.openDataSocket(connection);

            // Request the file from the server.
            FTPReply startReply = connection.sendCommand("RETR " + remoteFile);

            // RETR should begin with reply 125 or 150.
            if (!(startReply.getCode() == 125 || startReply.getCode() == 150)) {
                throw new IOException("RETR failed to start: " + startReply);
            }

            // Read file data from the server.
            dataIn = new BufferedInputStream(dataSocket.getInputStream());

            // Write file data to the local file.
            fileOut = new FileOutputStream(localFile);

            // Transfer file bytes in chunks.
            FTPHelper.copy(dataIn, fileOut);

            // Close transfer resources before reading final FTP reply.
            FTPHelper.close(dataIn);
            FTPHelper.close(fileOut);
            FTPHelper.close(dataSocket);

            // Read final completion reply from the control connection.
            FTPReply finalReply = connection.readReply();

            if (!(finalReply.getCode() == 226 || finalReply.getCode() == 250)) {
                throw new IOException("RETR failed to complete: " + finalReply);
            }

        } finally {
            // Safety cleanup.
            FTPHelper.close(dataIn);
            FTPHelper.close(fileOut);
            FTPHelper.close(dataSocket);
        }
    }

    // Upload a local file to the FTP server using PASV + STOR.
    public void upload(String localFile, String remoteFile) throws IOException {

        // Check whether the local file actually exists.
        File file = new File(localFile);
        FTPHelper.LocalfileExists(file);

        Socket dataSocket = null;
        OutputStream dataOut = null;
        FileInputStream fileIn = null;

        try {
            // Open passive data connection.
            dataSocket = passiveMode.openDataSocket(connection);

            // Tell the server that we want to store/upload a file.
            FTPReply startReply = connection.sendCommand("STOR " + remoteFile);

            // STOR should begin with 125 or 150.
            if (!(startReply.getCode() == 125 || startReply.getCode() == 150)) {
                throw new IOException("STOR failed to start: " + startReply);
            }

            // Output stream to send bytes to the server.
            dataOut = new BufferedOutputStream(dataSocket.getOutputStream());

            // Input stream to read bytes from local file.
            fileIn = new FileInputStream(file);

            // Transfer file bytes in chunks.
            FTPHelper.copy(fileIn, dataOut);

            // Close resources before reading final FTP reply.
            FTPHelper.close(fileIn);
            FTPHelper.close(dataOut);
            FTPHelper.close(dataSocket);

            // Final server reply after upload completes.
            FTPReply finalReply = connection.readReply();

            if (!(finalReply.getCode() == 226 || finalReply.getCode() == 250)) {
                throw new IOException("STOR failed to complete: " + finalReply);
            }

        } finally {
            // Safety cleanup.
            FTPHelper.close(fileIn);
            FTPHelper.close(dataOut);
            FTPHelper.close(dataSocket);
        }
    }
}