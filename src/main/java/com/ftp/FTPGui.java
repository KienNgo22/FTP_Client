/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.ftp;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 *
 * @author Admin
 */
public class FTPGui extends javax.swing.JFrame {
    
    private FTPClient client = new FTPClient();

    // Connection fields
    private JTextField hostField = new JTextField("127.0.0.1", 14);
    private JTextField portField = new JTextField("2121", 5);
    private JTextField userField = new JTextField("Test", 10);
    private JPasswordField passField = new JPasswordField("123", 14);

    // Log area
    private JTextArea logArea = new JTextArea();

    // Local site
    private JTextField localPathField = new JTextField();
    private DefaultListModel<String> localListModel = new DefaultListModel<String>();
    private JList<String> localList = new JList<String>(localListModel);
    private File currentLocalDir = new File(System.getProperty("user.home"));

    // Remote site
    private JTextField remotePathField = new JTextField("/");
    private DefaultListModel<String> remoteListModel = new DefaultListModel<String>();
    private JList<String> remoteList = new JList<String>(remoteListModel);
    
    /**
     * Creates new form FTPGui
     */
    public FTPGui() {
        setTitle("Java FTP Client");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        add(top(), BorderLayout.NORTH);
        add(center(), BorderLayout.CENTER);
        add(bottom(), BorderLayout.SOUTH);

        loadLocalFiles(currentLocalDir);

        log("FTP GUI started.");
        log("Local directory: " + currentLocalDir.getAbsolutePath()); 
    }
    
    //GUI LAYOUT
    private JPanel top() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton connectbtn = new JButton("Connect");
        JButton loginbtn = new JButton("Login");
        JButton quitbtn = new JButton("Quit");

        topPanel.add(new JLabel("Host:"));
        topPanel.add(hostField);

        topPanel.add(new JLabel("Port:"));
        topPanel.add(portField);

        topPanel.add(new JLabel("User:"));
        topPanel.add(userField);

        topPanel.add(new JLabel("Password:"));
        topPanel.add(passField);

        topPanel.add(connectbtn);
        topPanel.add(loginbtn);
        topPanel.add(quitbtn);

        connectbtn.addActionListener(e -> connect());
        loginbtn.addActionListener(e -> login());
        quitbtn.addActionListener(e -> quit());

        return topPanel;
    }
    private JPanel center() {
    JPanel centerPanel = new JPanel(new BorderLayout(8, 8));

    logArea.setEditable(false);
    logArea.setLineWrap(true);
    logArea.setWrapStyleWord(true);

    JScrollPane logScroll = new JScrollPane(logArea);
    logScroll.setBorder(BorderFactory.createTitledBorder("Log"));

    JSplitPane fileSplitPane = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            local(),
            remote()
    );

    fileSplitPane.setResizeWeight(0.5);

    JSplitPane mainSplitPane = new JSplitPane(
            JSplitPane.VERTICAL_SPLIT,
            logScroll,
            fileSplitPane
    );

    mainSplitPane.setResizeWeight(0.32);

    centerPanel.add(mainSplitPane, BorderLayout.CENTER);

    return centerPanel;
    }
    
    private JPanel local() {
        JPanel localPanel = new JPanel(new BorderLayout(5, 5));
        localPanel.setBorder(BorderFactory.createTitledBorder("Local Site"));

        JPanel pathPanel = new JPanel(new BorderLayout(5, 5));
        
        JButton browseBtn = new JButton("Browse");
        JButton localUpBtn = new JButton("Up");
        JButton localRefreshBtn = new JButton("Refresh");

        localPathField.setEditable(false);

        pathPanel.add(new JLabel("Local Path:"), BorderLayout.WEST);
        pathPanel.add(localPathField, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(browseBtn);
        buttonPanel.add(localUpBtn);
        buttonPanel.add(localRefreshBtn);

        pathPanel.add(buttonPanel, BorderLayout.EAST);

        localPanel.add(pathPanel, BorderLayout.NORTH);
        localPanel.add(new JScrollPane(localList), BorderLayout.CENTER);

        browseBtn.addActionListener(e -> chooseLocalFolder());
        localUpBtn.addActionListener(e -> goLocalUp());
        localRefreshBtn.addActionListener(e -> loadLocalFiles(currentLocalDir));

        localList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    openSelectedLocalItem();
                }
            }
        });

        return localPanel;
    }
    
    private JPanel remote() {
    JPanel remotePanel = new JPanel(new BorderLayout(5, 5));
    remotePanel.setBorder(BorderFactory.createTitledBorder("Remote Site / Server Site"));

    JPanel pathPanel = new JPanel(new BorderLayout(5, 5));

    JButton remoteUpBtn = new JButton("Up");
    JButton remoteCdBtn = new JButton("CD");
    JButton remoteRefreshBtn = new JButton("Refresh");
    JButton pwdBtn = new JButton("PWD");

    pathPanel.add(new JLabel("Remote Path:"), BorderLayout.WEST);
    pathPanel.add(remotePathField, BorderLayout.CENTER);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonPanel.add(pwdBtn);
    buttonPanel.add(remoteUpBtn);
    buttonPanel.add(remoteCdBtn);
    buttonPanel.add(remoteRefreshBtn);

    pathPanel.add(buttonPanel, BorderLayout.EAST);

    remotePanel.add(pathPanel, BorderLayout.NORTH);
    remotePanel.add(new JScrollPane(remoteList), BorderLayout.CENTER);

    pwdBtn.addActionListener(e -> pwd());
    remoteUpBtn.addActionListener(e -> goRemoteUp());
    remoteCdBtn.addActionListener(e -> changeRemoteDirectory());
    remoteRefreshBtn.addActionListener(e -> refreshRemoteFiles());

    remoteList.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                String selected = remoteList.getSelectedValue();

                if (selected == null || selected.equals("(empty directory)")) {
                    return;
                }

                String name = extractRemoteName(selected);

                if (name.length() == 0) {
                    return;
                }

                remotePathField.setText(name);
                changeRemoteDirectory();
            }
        }
    });

    return remotePanel;
}
    
    private JPanel bottom() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton uploadBtn = new JButton("Upload >>");
        JButton downloadBtn = new JButton("<< Download");
        JButton deleteBtn = new JButton("Delete Remote File");
        JButton mkdirBtn = new JButton("Make Remote Folder");
        JButton rmdirBtn = new JButton("Remove Remote Folder");

        bottomPanel.add(uploadBtn);
        bottomPanel.add(downloadBtn);
        bottomPanel.add(deleteBtn);
        bottomPanel.add(mkdirBtn);
        bottomPanel.add(rmdirBtn);

        uploadBtn.addActionListener(e -> uploadSelectedFile());
        downloadBtn.addActionListener(e -> downloadSelectedFile());
        deleteBtn.addActionListener(e -> deleteRemoteFile());
        mkdirBtn.addActionListener(e -> makeRemoteDirectory());
        rmdirBtn.addActionListener(e -> removeRemoteDirectory());

        return bottomPanel;
    }
     
     //FTP FUNCTIONALITIES. 
    private void connect() {
        try {
            String host = hostField.getText().trim();
            int port = Integer.parseInt(portField.getText().trim());

            log("Connecting to " + host + ":" + port + " ...");

            FTPReply reply = client.connect(host, port);
            log(reply);
        } catch (Exception ex) {
            log("Connect error: " + ex.getMessage());
        }
    }

    private void login() {
        try {
            String user = userField.getText().trim();
            String pass = new String(passField.getPassword());

            log("Logging in as " + user + " ...");

            FTPReply reply = client.login(user, pass);
            log(reply);

            if (client.isLoggedIn()){
                refreshRemoteFiles();
                pwd();
            }
        } catch (Exception ex) {
            log("Login error: " + ex.getMessage());
        }
    }
    
    private void quit() {
        try {
            FTPReply reply = client.quit();
            log(reply);
            remoteListModel.clear();
        } catch (Exception ex) {
            log("Quit error: " + ex.getMessage());
        }
    }
    
    private void pwd() {
        try {
            FTPReply reply = client.pwd();
            log(reply);

            String path = extractPathFromPwdReply(reply.getMessage());
            if (path != null) {
                remotePathField.setText(path);
            }
        } catch (Exception ex) {
            log("PWD error: " + ex.getMessage());
        }
    }

    private void changeRemoteDirectory() {
        try {
            String path = remotePathField.getText().trim();

            if (path.length() == 0) {
                log("Remote path is empty.");
                return;
            }

            FTPReply reply = client.cwd(path);
            log(reply);

            pwd();
            refreshRemoteFiles();
        } catch (Exception ex) {
            log("CD error: " + ex.getMessage());
        }
    }
    private void goRemoteUp() {
        try {
            log("Changing remote directory to parent folder...");

            FTPReply reply = client.cwd("..");
            log(reply);

            pwd();
            refreshRemoteFiles();
        } catch (Exception ex) {
            log("Remote Up error: " + ex.getMessage());
        }
    }
    
    private void refreshRemoteFiles() {
        try {
            remoteListModel.clear();

            log("Listing remote files...");

            String listing = client.list(null);

            if (listing.trim().length() == 0) {
                remoteListModel.addElement("(empty directory)");
                return;
            }

            String[] lines = listing.split("\\r?\\n");

            for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();

            if (line.length() > 0) {
                String fileName = extractRemoteName(line);

                if (fileName.length() > 0) {
                    remoteListModel.addElement(fileName);
                }
            }
        }

            log("Remote list refreshed.");
        } catch (Exception ex) {
            log("Remote refresh error: " + ex.getMessage());
        }
    }

    private void uploadSelectedFile() {
        try {
            File selectedFile = getSelectedLocalFile();

            if (selectedFile == null) {
                log("Please select a local file to upload.");
                return;
            }

            if (!selectedFile.isFile()) {
                log("You can only upload files, not folders.");
                return;
            }

            String localPath = selectedFile.getAbsolutePath();
            String remoteName = selectedFile.getName();

            log("Uploading: " + localPath + " -> " + remoteName);

            client.uploadFile(localPath, remoteName);

            log("Upload success: " + remoteName);
            refreshRemoteFiles();
        } catch (Exception ex) {
            log("Upload error: " + ex.getMessage());
        }
    }
    
    private void downloadSelectedFile() {
        try {
            String selectedRemote = remoteList.getSelectedValue();

            if (selectedRemote == null) {
                log("Please select a remote file to download.");
                return;
            }

            if (selectedRemote.equals("(empty directory)")) {
                log("No file selected.");
                return;
            }

            String remoteName = extractRemoteName(selectedRemote);

            if (remoteName == null || remoteName.trim().length() == 0) {
                log("Cannot detect remote file name.");
                return;
            }

            File localFile = new File(currentLocalDir, remoteName);

            log("Selected remote item: " + selectedRemote);
            log("Detected remote filename: " + remoteName);
            log("Downloading: " + remoteName + " -> " + localFile.getAbsolutePath());

            client.downloadFile(remoteName, localFile.getAbsolutePath());

            log("Download success: " + localFile.getAbsolutePath());
            loadLocalFiles(currentLocalDir);
        } catch (Exception ex) {
            log("Download error: " + ex.getMessage());
        }
    }

    private void deleteRemoteFile() {
        try {
            String selectedRemote = remoteList.getSelectedValue();

            if (selectedRemote == null) {
                log("Please select a remote file to delete.");
                return;
            }

            String remoteName = extractRemoteName(selectedRemote);

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Delete remote file?\n" + remoteName,
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            FTPReply reply = client.delete(remoteName);
            log(reply);

            refreshRemoteFiles();
        } catch (Exception ex) {
            log("Delete error: " + ex.getMessage());
        }
    }

    private void makeRemoteDirectory() {
        try {
            String dirName = JOptionPane.showInputDialog(
                    this,
                    "Enter new remote folder name:"
            );

            if (dirName == null || dirName.trim().length() == 0) {
                return;
            }

            FTPReply reply = client.mkdir(dirName.trim());
            log(reply);

            refreshRemoteFiles();
        } catch (Exception ex) {
            log("MKDIR error: " + ex.getMessage());
        }
    }

    private void removeRemoteDirectory() {
        try {
            String selectedRemote = remoteList.getSelectedValue();

            String dirName;

            if (selectedRemote != null) {
                dirName = extractRemoteName(selectedRemote);
            } else {
                dirName = JOptionPane.showInputDialog(
                        this,
                        "Enter remote folder name to remove:"
                );
            }

            if (dirName == null || dirName.trim().length() == 0) {
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Remove remote folder?\n" + dirName,
                    "Confirm Remove Folder",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            FTPReply reply = client.rmdir(dirName.trim());
            log(reply);

            refreshRemoteFiles();
        } catch (Exception ex) {
            log("RMDIR error: " + ex.getMessage());
        }
    }
    
    // ========================= LOCAL FILE ACTIONS =========================
    
    private void loadLocalFiles(File dir) {
        try {
            if (dir == null || !dir.exists() || !dir.isDirectory()) {
                log("Invalid local directory.");
                return;
            }

            currentLocalDir = dir;
            localPathField.setText(currentLocalDir.getAbsolutePath());
            localListModel.clear();

            File[] files = currentLocalDir.listFiles();

            if (files == null || files.length == 0) {
                localListModel.addElement("(empty directory)");
                return;
            }

            for (int i = 0; i < files.length; i++) {
                File f = files[i];

                if (f.isDirectory()) {
                    localListModel.addElement("[DIR] " + f.getName());
                } else {
                    localListModel.addElement(f.getName());
                }
            }
        } catch (Exception ex) {
            log("Local refresh error: " + ex.getMessage());
        }
    }
    
    private void chooseLocalFolder() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Choose Local Folder");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setCurrentDirectory(currentLocalDir);

        int result = chooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFolder = chooser.getSelectedFile();

            if (selectedFolder != null && selectedFolder.isDirectory()) {
                loadLocalFiles(selectedFolder);
                log("Local folder changed to: " + selectedFolder.getAbsolutePath());
            } else {
                log("Invalid local folder selected.");
            }
        }
    }

    private void goLocalUp() {
        File parent = currentLocalDir.getParentFile();

        if (parent != null) {
            loadLocalFiles(parent);
        } else {
            log("Already at root directory.");
        }
    }

    private void openSelectedLocalItem() {
        File selected = getSelectedLocalFile();

        if (selected == null) {
            return;
        }

        if (selected.isDirectory()) {
            loadLocalFiles(selected);
        }
    }

    private File getSelectedLocalFile() {
        String selected = localList.getSelectedValue();

        if (selected == null) {
            return null;
        }

        if (selected.equals("(empty directory)")) {
            return null;
        }

        String name = selected;

        if (name.startsWith("[DIR] ")) {
            name = name.substring(6);
        }

        return new File(currentLocalDir, name);
    }
    
    // ========================= HELPER METHODS =========================

    private String extractRemoteName(String listLine) {
        if (listLine == null) {
            return "";
        }

        listLine = listLine.trim();

        if (listLine.equals("(empty directory)")) {
            return "";
        }
        
        if (!listLine.startsWith("-") && !listLine.startsWith("d")) {
        return listLine;
    }

        String[] parts = listLine.split("\\s+", 9);

        if (parts.length >= 9) {
        return parts[8];
        }

        return listLine;
    }
    
    private String extractPathFromPwdReply(String message) {
        if (message == null) {
            return null;
        }

        int firstQuote = message.indexOf('"');
        int secondQuote = message.indexOf('"', firstQuote + 1);

        if (firstQuote >= 0 && secondQuote > firstQuote) {
            return message.substring(firstQuote + 1, secondQuote);
        }

        return null;
    }

    private void log(Object msg) {
        logArea.append(String.valueOf(msg) + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

// ========================= MAIN METHOD =========================

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        EventQueue.invokeLater(() -> {
            new FTPGui().setVisible(true);
        });
    }
     
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
}
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

