# Java FTP Client

A Java-based FTP client that communicates with FTP servers over TCP sockets. The project implements core FTP commands manually, including control-channel communication, passive-mode data connections, binary file transfer, response parsing, and a simple Swing GUI.

## Features

* Connect to an FTP server
* Login with custom credentials
* Anonymous login support
* Show current remote directory with `PWD`
* Change remote directory with `CWD`
* List remote files using `PASV + LIST`
* Download files using `PASV + RETR`
* Upload files using `PASV + STOR`
* Delete remote files with `DELE`
* Create remote directories with `MKD`
* Remove remote directories with `RMD`
* Gracefully end sessions with `QUIT`
* Parse FTP reply codes, including multiline replies
* Manage control and data sockets separately
* Handle errors and resource cleanup safely
* Provide a simple Swing-based graphical interface

## Project Structure

```text
com.ftp
├── FTPClient.java
├── FTPReply.java
└── FTPGui.java
```

### `FTPClient.java`

Implements the FTP protocol logic, including:

* Control connection management
* Login handling
* FTP command sending
* FTP response parsing
* Passive-mode data connection setup
* Remote listing, upload, and download operations
* Remote file/folder management
* Stream and socket cleanup

### `FTPReply.java`

Represents an FTP server reply and stores:

* Reply code
* Reply message

### `FTPGui.java`

Provides a simple desktop interface for:

* Connecting to a server
* Logging in
* Browsing local files
* Viewing remote files
* Uploading and downloading files
* Deleting files
* Creating and removing folders
* Viewing FTP replies and error messages in a log panel

## Implemented FTP Operations

| Operation               | Method                                | FTP Command               |
| ----------------------- | ------------------------------------- | ------------------------- |
| Connect                 | `connect(host, port)`                 | Control socket connection |
| Anonymous login         | `loginAnonymous()`                    | `USER`, `PASS`            |
| Custom login            | `login(username, password)`           | `USER`, `PASS`            |
| Print working directory | `pwd()`                               | `PWD`                     |
| Change directory        | `cwd(path)`                           | `CWD`                     |
| List files              | `list(path)`                          | `PASV + LIST`             |
| Download file           | `downloadFile(remoteFile, localFile)` | `TYPE I + PASV + RETR`    |
| Upload file             | `uploadFile(localFile, remoteFile)`   | `TYPE I + PASV + STOR`    |
| Delete file             | `delete(remoteFile)`                  | `DELE`                    |
| Make directory          | `mkdir(dirName)`                      | `MKD`                     |
| Remove directory        | `rmdir(dirName)`                      | `RMD`                     |
| Quit session            | `quit()`                              | `QUIT`                    |

## Libraries Used

### Core FTP Implementation

The protocol implementation uses only standard Java packages:

```java
java.io.*
java.net.*
java.util.*
```

### GUI Layer

The GUI uses Swing, together with standard AWT layout and event helpers commonly used in Swing interfaces:

```java
javax.swing.*
java.awt.*
java.awt.event.*
```

These GUI packages are used only for presentation and interaction. The FTP protocol itself is implemented manually.

## Build and Run in NetBeans

### 1. Open the Project

1. Launch NetBeans.
2. Select **File → Open Project**.
3. Choose the FTP Client project folder.
4. Click **Open Project**.

### 2. Verify the Source Files

Make sure the `com.ftp` package contains:

```text
FTPClient.java
FTPReply.java
FTPGui.java
```

### 3. Set the Main Class

1. Right-click the project name.
2. Select **Properties**.
3. Open the **Run** category.
4. Set **Main Class** to:

```text
com.ftp.FTPGui
```

5. Click **OK**.

### 4. Run the Application

Press the green **Run Project** button in NetBeans.

You can also open `FTPGui.java`, right-click inside the file, and choose **Run File**.

## Using the GUI

### Connect to a Server

1. Enter the FTP host.
2. Enter the FTP port.
3. Click **Connect**.

Example:

```text
Host: ftp.gnu.org
Port: 21
```

### Log In

For custom login:

1. Enter a username.
2. Enter a password.
3. Click **Login**.

For anonymous login, use:

```text
Username: anonymous
Password: anonymous@example.com
```

After a successful login, the remote directory listing is refreshed automatically.

### Remote Navigation

* Click **PWD** to show the current remote directory.
* Enter a path and click **CD** to change directories.
* Click **Up** to move to the parent remote directory.
* Click **Refresh** to reload the current remote file list.

### Local Navigation

* Click **Browse** to choose a local working folder.
* Click **Up** to move to the parent local directory.
* Click **Refresh** to reload the local file list.
* Double-click a local folder to open it.

### Upload a File

1. Select a local file.
2. Click **Upload >>**.
3. The file is uploaded to the current remote directory.

Protocol flow:

```text
TYPE I
PASV
STOR
```

### Download a File

1. Select a remote file.
2. Click **<< Download**.
3. The file is saved into the current local working folder.

Protocol flow:

```text
TYPE I
PASV
RETR
```

### Remote File Operations

* **Delete Remote File** → sends `DELE`
* **Make Remote Folder** → sends `MKD`
* **Remove Remote Folder** → sends `RMD`

### Quit

Click **Quit** to send:

```text
QUIT
```

The application then closes the FTP session and releases local resources.

## FTP Protocol Design

### Control Connection

The control connection is used for commands and replies, such as:

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

A separate data connection is opened in passive mode for:

* Directory listing
* File download
* File upload

The general flow is:

```text
Client sends PASV
Server returns IP address and port
Client opens a data socket
Client sends LIST / RETR / STOR
Server transfers data through the data socket
Server sends a final completion reply
```

## Example Test Workflow

1. Connect to an FTP server.
2. Log in with valid credentials or anonymous access.
3. Click **PWD**.
4. Refresh the remote listing.
5. Change directories with **CD**.
6. Download a file.
7. Upload a file, if the server permits writing.
8. Create and remove a folder, if the server permits it.
9. Click **Quit**.

> Note: Many public FTP servers allow file listing and downloads, but may reject upload, delete, and folder-management commands. Use a writable FTP test server when verifying those operations.

## Error Handling

The client is designed to handle common failure cases, including:

* Sending commands before connecting
* Performing remote actions before login
* Invalid FTP reply formats
* Malformed passive-mode replies
* Missing local upload files
* Failed transfer start replies
* Failed transfer completion replies
* Stream or socket cleanup issues

Errors are reported through exceptions in the core client and displayed in the GUI log panel.

## Submission Files

For the course submission, include the following in the ZIP file:

```text
FTPClient.java
FTPReply.java
FTPGui.java
README.md
Short_Report.pdf or Short_Report.docx
```

## Notes

* The FTP protocol is implemented manually using Java sockets.
* No third-party FTP library is used.
* Binary mode is enabled for upload and download operations.
* Public FTP server permissions may vary by server.
