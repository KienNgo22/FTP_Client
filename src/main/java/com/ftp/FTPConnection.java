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

public class FTPConnection {
    
    private Socket socket;// Main socket for all commands to talk to the server.
    private BufferedReader buffRead;// Read reply from server.
    private BufferedWriter buffWrite; // Send FTP commands to server.
    private boolean connected = false;// Set the startup value for current connection state.

    // Part 1: Connect to FTP server.
    public FTPReply connect(String host, int port) throws IOException { 
        if (connected) {
            throw new IOException("Already connected.");
        }
        
        try {
            socket = new Socket(host, port);// Open the control socket to the FTP server.
            // Create a reader to receive text replies from the server.
            buffRead = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            // Create a writer to send text commands to the server.
            buffWrite = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
            connected = true;// Mark the current state of client to connected.
            // When connection is opened, the server instantly sends a welcome message.
            return readReply();

        } catch (IOException e) {
            // If connection fails, close everything safely.
            close();
            throw e;
        }
    }

    // Sending raw command through current socket and read the reply.
    public FTPReply sendCommand(String command) throws IOException {
        connectionConfirm();
        buffWrite.write(command + "\r\n");  // FTP commands must end with CRLF (\r\n).
        buffWrite.flush();// Force the command to actually be sent now.
        return readReply();// Read and return the server's reply.
    }

    // Reads a server reply from the control connection.
    // This way of development supports both single-line and multi-line FTP replies.
    public FTPReply readReply() throws IOException {
        String first = buffRead.readLine();// Read the first line of the reply.

        // FTP reply error handling.
        if (first == null) {throw new IOException("Server closed the connection.");
        }
        
        if (first.length() < 3) {throw new IOException("Invalid FTP reply: " + first);
        }// A valid FTP reply must begin with at least 3 characters for the code.

        String codeastext = first.substring(0, 3);
        int code;

        try {
            // Convert reply code to integer.
            code = Integer.parseInt(codeastext);
        } catch (NumberFormatException e) {
            throw new IOException("Invalid FTP reply code: " + first);
        }

        // Store the complete reply text.
        StringBuilder fullMessage = new StringBuilder();
        fullMessage.append(first);

        // Multi-line FTP reply format:
        // 230-First line
        // 230-Second line
        // 230 Final line
        // If character 4 is '-', the reply continues.
        if (first.length() > 3 && first.charAt(3) == '-') {
            readMultilineReply(codeastext, fullMessage);
        }

        // Return a reply object containing code and full text.
        return new FTPReply(code, fullMessage.toString());
    }

    // Loop to keep on reading the multi-line FTP reply.
    private void readMultilineReply(String codeastext, StringBuilder fullMessage) throws IOException {
        String line;

        while (true) {
            line = buffRead.readLine();

            // Connection close during multiline error handling.
            if (line == null) {
                throw new IOException("Connection closed during multiline reply.");
            }

            // Add this line to the full message.
            fullMessage.append("\n").append(line);

            // End when we see the same 3-digit code followed by a space.
            if (line.length() >= 4
                    && line.startsWith(codeastext)
                    && line.charAt(3) == ' ') {
                break;
            }
        }
    }

    // Confirming that control connection exists and is opened.
    public void connectionConfirm() throws IOException {
        if (!connected || socket == null || socket.isClosed()) {
            throw new IOException("Not connected to any server.");
        }
    }

    // Returns true if connected to a server.
    public boolean isConnected() {
        return connected && socket != null && !socket.isClosed();
    }

    // Close socket, reader, writer, and reset connection state.
    public void close() {
        connected = false;

        // Close text reader/writer and socket safely.
        FTPHelper.close(buffRead);
        FTPHelper.close(buffWrite);
        FTPHelper.close(socket);

        // Remove references after closing.
        buffRead = null;
        buffWrite = null;
        socket = null;
    }
}