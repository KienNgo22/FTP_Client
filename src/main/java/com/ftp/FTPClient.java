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

public class FTPClient {
    private final FTPConnection connection;// Handles the control connection: connect, send command, read reply, and close
    private final FTPTransfer transfer;// Handles data transfer operations such as LIST, RETR, and STOR.
    private boolean login = false; // Set the startup value for current login state.

    // Create the FTP client and prepare helper parts.
    public FTPClient() {
        connection = new FTPConnection();
        transfer = new FTPTransfer(connection);
    }

    // Part 1: Connect to FTP server.
    public FTPReply connect(String host, int port) throws IOException {
        if (connection.isConnected()) {throw new IOException("Already connected.");}
        login = false;// Reset login state when connecting to a new server.
        return connection.connect(host, port);// Open the control connection and return the welcome reply.
    }

    // Returns true if connected to a server.
    public boolean isConnected() {
        return connection.isConnected();
    }

    // Returns true if the user is logged in.
    public boolean isLoggedIn() {
        return login;
    }

    // Part 2: Login.
    public FTPReply login(String username, String password) throws IOException {
        connection.connectionConfirm(); // Make sure that client is connected to server.
        FTPReply userReply = connection.sendCommand("USER " + username); // Send the username.

        // 230: login success without password needed.
        if (userReply.getCode() == 230) {
            login = true;
            return userReply;
        }

        // 331: server is asking for password.
        if (userReply.getCode() == 331) {
            FTPReply passReply = connection.sendCommand("PASS " + password);

            // If the password is accepted, mark login as successful.
            if (passReply.getCode() == 230) {
                login = true;
            }

            return passReply;
        }

        // If reply was neither 230 nor 331, return the USER reply as-is.
        return userReply;
    }

    // Part 3: Method for anonymous login.
    public FTPReply loginAnonymous() throws IOException {
        return login("anonymous", "anonymous@example.com"); // Many public FTP servers allow username "anonymous".
    }

    // Part 4: PWD - show the current remote working directory.
    public FTPReply pwd() throws IOException {
        loginConfirm();
        return connection.sendCommand("PWD");
    }

    // Part 5: CWD - change the remote working directory.
    public FTPReply cwd(String path) throws IOException {
        loginConfirm();
        return connection.sendCommand("CWD " + path);
    }

    // Part 6: Files/directories listing from the server using PASV + LIST.
    public String list(String path) throws IOException {
        loginConfirm();
        return transfer.list(path);
    }

    // Part 7: Download a file from the FTP server using PASV + RETR.
    public void downloadFile(String remoteFile, String localFile) throws IOException {
        loginConfirm();
        setBinaryMode(); // Use binary mode for safe file transfer.
        transfer.download(remoteFile, localFile);// Transfer class handles PASV, RETR, data socket, and final reply.
    }

    // Part 8: Upload a local file to the FTP server using PASV + STOR.
    public void uploadFile(String localFile, String remoteFile) throws IOException {
        loginConfirm();
        setBinaryMode();// Use binary mode for safe upload.
        transfer.upload(localFile, remoteFile);// Transfer class handles PASV, STOR, data socket, and final reply.
    }

    // Part 9: DELE - Deletes a remote file.
    public FTPReply delete(String remoteFile) throws IOException {
        loginConfirm();
        return connection.sendCommand("DELE " + remoteFile);
    }

    // Part 10: MKD - make a directory in server.
    public FTPReply mkdir(String dirName) throws IOException {
        loginConfirm();
        return connection.sendCommand("MKD " + dirName);
    }

    // Part 11: RMD - remove a directory in server.
    public FTPReply rmdir(String dirName) throws IOException {
        loginConfirm();
        return connection.sendCommand("RMD " + dirName);
    }

    // Part 12: QUIT - disconnect gracefully.
    public FTPReply quit() throws IOException {
        // If not connected, return a simple reply instead of failing.
        if (!connection.isConnected()) {
            return new FTPReply(0, "Not connected.");
        }

        FTPReply reply;

        try {
            // Ask the server to close the session.
            reply = connection.sendCommand("QUIT");
        } finally {
            // Always close local resources even if QUIT fails.
            close();
        }

        return reply;
    }

    // Make sure that the user is logged in.
    private void loginConfirm() throws IOException {
        connection.connectionConfirm();

        if (!login) {
            throw new IOException("Not logged in.");
        }
    }

    // Telling server to use binary mode for file transfer.
    private void setBinaryMode() throws IOException {
        FTPReply reply = connection.sendCommand("TYPE I"); // TYPE I should normally return 200 if successful.

        // Error handling.
        if (reply.getCode() != 200) {
            throw new IOException("Failed to set binary mode: " + reply);
        }
    }

    // Close every socket, reader, writer, etc. and reset all current state.
    public void close() {
        login = false;

        // Close control connection resources safely.
        connection.close();
    }
}