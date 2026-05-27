# FTP - Java FTP Client

## Introduction

This is a Java-based FTP Client application with a graphical user interface.

The application allows users to connect to an FTP server, log in, browse local files, browse remote server files, upload files, download files, create folders, remove folders, delete files, and disconnect safely.

## Main Features

- FTP server connect
- Login with username and password
- Anonymous login support
- Show current remote directory using `PWD`
- Change remote directory using `CWD`
- List remote files using `PASV + LIST`
- Download files using `PASV + RETR`
- Upload files using `PASV + STOR`
- Delete remote files using `DELE`
- Create remote folders using `MKD`
- Remove remote folders using `RMD`
- Quit and close the FTP connection safely
- Graphical user interface built with Java Swing

## Project Structure
src/main/java/com/ftp/
-FTPGui.java
-FTPClient.java
-FTPConnection.java
-FTPPassiveMode.java
-FTPTransfer.java
-FTPHelper.java
-FTPReply.java

## Requirements
To run this project, you need:
- Java JDK 8 or newer
- NetBeans or another Java IDE
- An FTP server for testing
For function that cannot be use in an anonymous login, you can run a local FileZilla Server for test. 

## How to Run in NetBeans

1. Open NetBeans.
2. Open the FTP project folder.
3. Make sure the main class is: com.ftp.FTPGui
4. Click **Run Project**.
5. The FTP GUI will open.
---
## How to Run from Terminal
Compile the project:
```bash
javac -d out src/main/java/com/ftp/*.java
```
Run the GUI:
```bash
java -cp out com.ftp.FTPGui
```
## How to Use the Program
### Step 1: Start the FTP Server
Start your FTP server before running the client.
Example local server setting:
```text
Host: 127.0.0.1
Port: 2121
Username: Test
Password: 123
```
These values may be different depending on your FTP server configuration.
---
### Step 2: Connect to the Server
In the GUI:
1. Enter the FTP server host.
2. Enter the port number.
3. Click "Connect".
If the connection is successful, the server welcome reply will appear in the log area.
---
### Step 3: Login
In the GUI:
1. Enter the username.
2. Enter the password.
3. Click **Login**.
After login, the program will refresh the remote file list and show the current remote directory.
---
### Step 4: Browse Local Files
The left side of the GUI shows files from your computer.
You can:
- Click "Browse" to choose a local folder.
- Click "Up" to go to the parent folder.
- Click "Refresh" to reload the local file list.
- Double-click a local folder to open it.
---
### Step 5: Browse Remote Files
The right side of the GUI shows files from the FTP server.
You can:
- Click "PWD" to show the current remote directory.
- Click "CD" to change the remote directory with the destination.
- Click "Up" to go to the parent remote folder.
- Click "Refresh" to reload the remote file list.
- Double-click a remote folder to open it.
---
### Step 6: Upload a File
To upload a file from your computer to the FTP server:
1. Select a file from the local file list.
2. Click "Upload ".
3. The file will be uploaded to the current remote directory.
After uploading, the remote file list will refresh.
---
### Step 7: Download a File
To download a file from the FTP server to your computer:
1. Select a file from the remote file list.
2. Click "Download".
3. The file will be saved into the current local folder.
After downloading, the local file list will refresh.
---
### Step 8: Delete a Remote File
To delete a file from the FTP server:
1. Select a remote file.
2. Click "Delete Remote File".
3. Confirm the delete action.

### Step 9: Create a Remote Folder
To create a folder on the FTP server:
1. Click "Make Remote Folder".
2. Enter the new folder name.
3. Click "OK".
---
### Step 10: Remove a Remote Folder
To remove a folder from the FTP server:
1. Select a remote folder, or enter the folder name.
2. Click "Remove Remote Folder".
3. Confirm the action.
Note: The folder usually needs to be empty before it can be removed.
---
### Step 11: Quit the Program
Click "Quit" to disconnect from the FTP server safely.
This sends the `QUIT` command and closes the connection.
---
## Example Test Flow
You can test the program using this simple flow:
1. Start FileZilla Server.
2. Run FTPv2.
3. Enter the host, port, username, and password.
4. Click Connect.
5. Click Login.
6. Click PWD.
7. Click Refresh.
8. Upload a local file.
9. Download a remote file.
10. Create a remote folder.
11. Delete a remote file.
12. Remove a remote folder.
13. Click Quit.
---
## Author.
Name: Ngo Trung Kien
Student ID: 10423065
Course: Computer Network 2
Project: FTPv2 - Java FTP Client
