package utils;

import com.jcraft.jsch.*;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * This class establishes a connection to an AWS EC2 instance via SSH, allowing the program to access a private RDS database instance on the same VPC as the EC2.
 */
public class JSChConnection {

    private static Session session = null;
    public static int assignedPort; // will be assigned via setPortForwarding()
    private static String privateKeyName = "key01.pem"; // private key name here

    /**
     * Establishes connection to an AWS EC2 instance. Uses a private key for authentication
     */
    public static void connectSession() {

        final int sshPort = 22;
        // localPort, remotePort, remoteHost for portForwardingL
        final int localPort = 3306;
        final int remotePort = 3306;
        final String remoteHost = "client-schedule.cfb3yfxkufro.us-west-2.rds.amazonaws.com"; // AWS DB Hostname

        String jumpServerUser = "ec2-user"; // SSH username
        String jumpServerHost = "35.85.38.156"; // SSH jumpServerHost/ip
        String privateKey;
        File cwd = new File("."); // cwd

        try {
            // gets the canonical path to the cwd, adds "\", then specifies key file
            privateKey = cwd.getCanonicalPath() + File.separator + privateKeyName;
        } catch (IOException e) {
            System.out.println("Could not find private key file. Aborting!");
            return;
        }

        try {
            JSch jsch = new JSch();

            jsch.addIdentity(privateKey);
            System.out.println("Identity added.");

            session = jsch.getSession(jumpServerUser, jumpServerHost, sshPort);
            session.setConfig("StrictHostKeyChecking", "no");

            System.out.println("Establishing SSH Connection...");
            session.connect();

            assignedPort = session.setPortForwardingL(localPort, remoteHost, remotePort);
            System.out.println("Port forwarding enabled: " + assignedPort);

        } catch (JSchException ex) {
            java.util.logging.Logger.getLogger(JSChConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("SSH session established.");
    }

    public static Session getSession() {
        return session;
    }

    public static void disconnectSession() {
        session.disconnect();
        System.out.println("SSH Session disconnected.");
    }
}
