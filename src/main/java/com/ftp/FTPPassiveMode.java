/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ftp;

/**
 *
 * @author Admin
 */
import java.io.IOException;
import java.net.Socket;

/**
 * Handles FTP passive mode.
 * It sends PASV, parses the server reply, and opens the data socket.
 */
public class FTPPassiveMode {

    public Socket openDataSocket(FTPConnection connection) throws IOException {
        FTPReply pasvReply = connection.sendCommand("PASV");        // Ask server to enter passive mode.
        // Passive mode error handling.
        if (pasvReply.getCode() != 227) {
            throw new IOException("PASV failed: " + pasvReply);
        }
        String message = pasvReply.getMessage();// Example PASV reply: 227 Entering Passive Mode (192,168,1,2,195,80)

        // identify the two parentheses in the pasive reply for parsing
        int start = message.indexOf('(');
        int end = message.indexOf(')', start + 1);

        // If mising parentheses, the reply is malformed.
        if (start == -1 || end == -1) {
            throw new IOException("Invalid PASV reply format: " + message);
        }

        // Extract the comma-separated numbers inside parentheses.
        String data = message.substring(start + 1, end);
        String[] parts = data.split(",");

        // PASV must provide exactly 6 numbers: h1,h2,h3,h4,p1,p2.
        if (parts.length != 6) {
            throw new IOException("Invalid PASV address format: " + message);
        }

        try {
            // Build the IP address from the first 4 parts.
            String host = parts[0].trim() + "."
                    + parts[1].trim() + "."
                    + parts[2].trim() + "."
                    + parts[3].trim();

            // Build the port from the last 2 parts.
            int p1 = Integer.parseInt(parts[4].trim());
            int p2 = Integer.parseInt(parts[5].trim());

            int port = p1 * 256 + p2;

            // Open and return the new data socket.
            return new Socket(host, port);

        } catch (NumberFormatException e) {
            throw new IOException("Invalid PASV number format: " + message);
        }
    }
}