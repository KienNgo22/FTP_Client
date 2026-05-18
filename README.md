# Java FTP Client

## 1. Project Overview

The project is a FTP Client that can communicate iwht FTP server using FTP protocals using TCP sockets. Implemented using Java. It allow control connection commands and seperate passive mode data connections for file listing and file transfer. 

The project includes:
* FTP Client core functionalities developments 
* FTP reply message parsing support
* A simple Graphical User Interface (GUI) 

The application can connect and interact to both anonymously available public FTP server and local test FTP servers. 

---

## 2. Implemented Features 

The project implements the following FTP operations: 

| Operartions                   | Implementation                                            |
| ----------------------------- | --------------------------------------------------------- |
| Connect to FTP server         | `connect(host, port)`                                     |
| Anonymous login               | `loginAnonymous()`                                        |
| Custom login                  | `login(username, password)`                               |
| Show current remote directory | `pwd()` using `PWD`                                       |
| Change remote directory       | `cwd(path)` using `CWD`                                   |
| List remote files             | `list(path)` using `PASV + LIST`                          |
| Download remote file          | `downloadFile(remoteFile, localFile)` using `PASV + RETR` |
| Upload local file             | `uploadFile(localFile, remoteFile)` using `PASV + STOR`   |
| Delete remote file            | `delete(remoteFile)` using `DELE`                         |
| Create remote folder          | `mkdir(dirName)` using `MKD`                              |
| Remove remote folder          | `rmdir(dirName)` using `RMD`                              |
| Quit session                  | `quit()` using `QUIT`                                     |

The project also includes:

* FTP reply code parsing
* Multiline FTP reply support
* Proper socket and stream cleanup
* Error handling using exceptions and GUI log messages
* A simple Swing-based GUI for interacting with the FTP client

---

## 3. Source Files

The project contains the following Java source files:

### `FTPClient.java`

Contains the main FTP client logic, including:

* Control connection management
* Login commands
* FTP command sending
* FTP response reading
* Passive data connection handling
* File listing, upload, and download
* Remote file and folder operations
* Resource cleanup

### `FTPReply.java`

Represents a server reply from the FTP server. It stores:

* FTP reply code
* FTP reply message

It is used by `FTPClient` to return structured command responses.

### `FTPGui.java`

Provides a simple Swing graphical interface that allows the user to:

* Enter host, port, username, and password
* Connect and log in
* View local file
* View remote file
* Upload file
* Download file
* Delete remote file
* Create and remove remote folder
* View FTP command replies and error messages in a log box 

---

## 4. Libraries Used

### Core FTP Client

The FTP protocol implementation uses only the allowed Java packages:

java.io.*
java.net.*
java.util.*

### GUI

The graphical user interface uses Swing-based GUI components. Supporting AWT layout and event classes are used together with Swing for arranging components and handling mouse events.

Examples include:

javax.swing.*
java.awt.*
java.awt.event.*

These GUI packages are used only for the interface layer and do not replace the manual FTP protocol implementation.

---

## 5. How to Build and Run in NetBeans

### Step 1: Open the Project

1. Launch NetBeans.
2. Select **File → Open Project**.
3. Choose the FTP Client project folder.
4. Click **Open Project**.

### Step 2: Confirm the Source Files

Inside the project, confirm that the package contains:

```text
com.ftp
├── FTPClient.java
├── FTPReply.java
└── FTPGui.java
```

### Step 3: Run the GUI

1. Open `FTPGui.java`.
2. Right-click inside the file.
3. Choose **Run File**.

Or:

1. Right-click the project name.
2. Choose **Properties**.
3. Under **Run**, set the main class to:

```text
com.ftp.FTPGui
```

4. Click **OK**.
5. Press the green **Run Project** button.

---

## 6. How to Use the GUI

### 6.1 Connect to an FTP Server

1. Enter the FTP server host.
2. Enter the FTP port.

   * Standard FTP port: `21`
3. Click **Connect**.
4. The server welcome reply should appear in the log area.

Example:

```text
Host: ftp.gnu.org
Port: 21
```

---

### 6.2 Log In

#### Custom Login

1. Enter a username.
2. Enter a password.
3. Click **Login**.

#### Anonymous Login

If using a server that allows anonymous FTP, enter:

```text
Username: anonymous
Password: anonymous@example.com
```

Then click **Login**.

After login succeeds, the remote file list will refresh and the current remote directory will be shown.

---

### 6.3 View Current Remote Directory

Click **PWD** to send the FTP `PWD` command.

The returned path is shown in:

* The log area
* The remote path text field

---

### 6.4 Change Remote Directory

To change directories:

1. Type the remote path into the **Remote Path** field.
2. Click **CD**.

To move to the parent directory:

* Click **Up**.

---

### 6.5 Refresh Remote File List

Click **Refresh** in the Remote Site panel.

The program uses:

```text
PASV + LIST
```

to retrieve the current server directory listing.

---

### 6.6 Choose a Local Folder

Click **Browse** in the Local Site panel and select a folder.

This folder becomes the working local folder used for:

* Upload source files
* Download destination files

You can also:

* Click **Up** to move to the parent local directory
* Click **Refresh** to reload the current local folder
* Double-click a local folder to open it

---

### 6.7 Upload a File

1. Select a local file from the Local Site list.
2. Click **Upload >>**.
3. The file is uploaded to the current remote FTP directory.

The client uses:

```text
TYPE I
PASV
STOR
```

for binary upload.

---

### 6.8 Download a File

1. Select a remote file from the Remote Site list.
2. Click **<< Download**.
3. The file is downloaded into the currently selected local folder.

The client uses:

```text
TYPE I
PASV
RETR
```

for binary download.

---

### 6.9 Delete a Remote File

1. Select a remote file.
2. Click **Delete Remote File**.
3. Confirm the deletion in the dialog box.

This uses:

```text
DELE
```

---

### 6.10 Create a Remote Folder

1. Click **Make Remote Folder**.
2. Enter the folder name.
3. Confirm the input.

This uses:

```text
MKD
```

---

### 6.11 Remove a Remote Folder

1. Select a remote folder or provide its name manually.
2. Click **Remove Remote Folder**.
3. Confirm the removal.

This uses:

```text
RMD
```

---

### 6.12 Quit the FTP Session

Click **Quit**.

This sends:

```text
QUIT
```

and closes the control connection and local resources.

---

## 7. FTP Protocol Flow Summary

### Control Connection

The control connection is opened when the client connects to the server. It is used for sending textual FTP commands and receiving FTP replies.

Examples:

```text
USER
PASS
PWD
CWD
PASV
LIST
RETR
STOR
DELE
MKD
RMD
QUIT
```

### Data Connection

A separate data connection is created for:

* Directory listing
* File download
* File upload

The client uses passive mode:

```text
PASV
```

The server returns an IP address and port, and the client opens a data socket to that address.

---

## 8. Example Test Scenario

A typical test session may follow these steps:

1. Connect to the FTP server.
2. Log in using anonymous or valid custom credentials.
3. Click **PWD**.
4. Refresh the remote listing.
5. Change remote directory using **CD**.
6. Download a file using **<< Download**.
7. Upload a local file using **Upload >>**, if the server permits writing.
8. Create a folder using **Make Remote Folder**, if the server permits it.
9. Remove the folder using **Remove Remote Folder**.
10. Click **Quit**.

Note: Some public FTP servers allow downloading and listing but may not allow upload, deletion, or directory creation. Those operations should be tested on a writable FTP test server when needed.

---

## 9. Error Handling and Robustness

The client handles common error situations such as:

* Trying to send commands without connecting first
* Trying to use remote commands before login
* Invalid or unexpected FTP reply formats
* Malformed passive mode replies
* Missing local upload file
* Failed file transfer start or completion replies
* Socket or stream shutdown problems

The GUI catches exceptions and shows user-friendly messages in the log panel.

---

## 10. Submission Contents

The final ZIP file submitted to Moodle should include:

```text
FTPClient.java
FTPReply.java
FTPGui.java
README.md
Short_Report.pdf or Short_Report.docx
```

The short report should explain:

* Program architecture
* Control connection vs. data connection
* FTP protocol command flow
* Reply parsing, including multiline replies
* Passive mode transfer process
* Testing screenshots or test command examples

---

## 11. Notes

* This project manually implements FTP communication using Java sockets.
* No third-party FTP library is used.
* File transfers use binary mode for correctness.
* Public FTP server permissions may vary, so upload/delete/mkdir/rmdir should be tested on a writable FTP server if necessary.
#   F T P _ C l i e n t  
 