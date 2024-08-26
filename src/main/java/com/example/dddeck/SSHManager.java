package com.example.dddeck;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.Session;

public class SSHManager {

    Session session;

    String host;
    String user;
    String password;
    int port = 22;

    SSHManager sshManager;

    public SSHManager(String host, String user, String password, int port){
        this.host = host;
        this.user = user;
        this.password = password;
        this.port = port;
    }

    public SSHManager(){
    }

    public Session connect(){
        System.out.println(App.timestamp() + "SSHManager connect()");
        System.out.println(App.timestamp() + "\nUser: " + this.user + "\nPassword: " + this.password + "\nHost: " + this.host + "\nPort: " + this.port);
        try {
        JSch jsch = new JSch();
        Session session = jsch.getSession(this.user, this.host, this.port);
        session.setPassword(this.password);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();
        this.session = session;
        System.out.println(App.timestamp() + "Session connected." + session);
        
        return session;

        } catch (Exception e){
            e.printStackTrace();
            System.out.println(App.timestamp() + "\nUser: " + this.user + "\nPassword: " + this.password + "\nHost: " + this.host + "\nPort: " + this.port);
            System.out.println(App.timestamp() + "[e] Session: " + e);
            return null;
        }
    }

    public void disconnect(Session session) {
        if (session != null && session.isConnected()) {
            session.disconnect();
            System.out.println(App.timestamp() + "Session disconnected.");
        } else {
            System.out.println(App.timestamp() + "Session is already null or not connected.");
        }
    }

    public Session getSession(){
        return this.session;
    }


    public String sshExec(Session session, String command){
        System.out.println(App.timestamp() + "SSHManager sshExec()");

        try {
            connect();
            ChannelExec channelExec = (ChannelExec)
                session.openChannel("exec");
            channelExec.setCommand(command);

            InputStream in = channelExec.getInputStream();
            channelExec.connect();

            // Use StringBuilder to accumulate the output
            StringBuilder outputBuffer = new StringBuilder();
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    outputBuffer.append(new String(tmp, 0, i));
                }
                if (channelExec.isClosed()) {
                    if (in.available() > 0) continue;
                    // System.out.println("Exit status: " + channelExec.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                    ee.printStackTrace();
                    return null;
                }
            }
            
            channelExec.disconnect();

            String commandOutput = outputBuffer.toString();
            System.out.println(App.timestamp() + "[SteamDeck] " + commandOutput);
            return commandOutput;

            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void getRemoteDir(String remoteDir, String localDir, String host, String user, String password){
        JSch jsch = new JSch();
        Session session = null;
        ChannelSftp channelSftp = null;

        System.out.println(App.timestamp() + "Downloading save from SD...");

        File directory = new File(localDir);
        if (!directory.exists()) {
            directory.mkdirs(); 
        }
    

        try{
            session = jsch.getSession(user, host, 22);
            session.setPassword(password);

            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();

            downloadFolder(channelSftp, remoteDir, localDir);
            System.out.println(App.timestamp() + "Backup created!");
        }   catch (Exception e){
            e.printStackTrace();
        } finally {
            if (channelSftp != null && channelSftp.isConnected()){
                channelSftp.disconnect();
            }
            if (session != null && session.isConnected()){
                session.disconnect();
            }
        }
    }

        private static void downloadFolder(ChannelSftp channelSftp, String remoteDir, String localDir) throws SftpException, IOException {
        Vector<ChannelSftp.LsEntry> fileAndFolderList = channelSftp.ls(remoteDir); // List source directory structure.

        for (ChannelSftp.LsEntry item : fileAndFolderList) { // Iterate objects in the list to get file/folder names.
            String fileName = item.getFilename();

            // Skip ".", ".." entries
            if (fileName.equals(".") || fileName.equals("..")) {
                continue;
            }

            String remoteFilePath = remoteDir + "/" + fileName;
            String localFilePath = localDir + File.separator + fileName;

            if (item.getAttrs().isDir()) { // If it is a directory, recursively copy it
                new File(localFilePath).mkdirs(); // Create the local directory.
                downloadFolder(channelSftp, remoteFilePath, localFilePath); // Recursively download subdirectories.
            } else { // If it is a file, download it.
                try (FileOutputStream fos = new FileOutputStream(new File(localFilePath))) {
                    channelSftp.get(remoteFilePath, fos);
                }
            }
        }
    }

}

