package com.example.dddeck;
import java.io.IOException;

import com.jcraft.jsch.*;

import java.io.InputStream;
import java.nio.file.*;

public class SSHManager {
    public String sshExec(String host, String user, String password, String port, String command){
        System.out.println(App.timestamp() + "SSHManager sshStatus()");
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(user, host, 22); //todo: add port
            session.setPassword(password);

            System.out.println(App.timestamp() + "\nUser: " + user + "\nPassword: " + password + "\nHost: " + host + "\nPort: " + port);

            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

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
            session.disconnect();

            String commandOutput = outputBuffer.toString();

            // Now you can use commandOutput as needed
            // System.out.println("SSH Command Output: ");
            System.out.println(App.timestamp() + "[SteamDeck] " + commandOutput);
            return commandOutput;

            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
    }
}

