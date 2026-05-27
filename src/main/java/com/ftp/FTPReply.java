/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ftp;

/**
 *
 * @author Admin
 */
public class FTPReply {
    
    //Declare the data types 
    private final int code;
    private final String message;
    
    //Create a new FTP reply object 
    public FTPReply(int code, String message) {
        this.code = code;
        this.message = message;
    }
    
    //Gets the reply code
    public int getCode() {
        return code;
    }
    
    //Get the reply message
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return code + " " + message;
    }

    public boolean hasCode(int expectedCode) {
        return code == expectedCode;
    }

    public boolean hasAnyCode(int code1, int code2) {
        return code == code1 || code == code2;
    }
}


