package com.st.TCP;


        import android.util.Log;
        import java.io.*;
        import java.net.InetAddress;
        import java.net.Socket;


public class TCPClient {
        private String serverMessage;
        private OnMessageReceived mMessageListener = null;
        private boolean mRun = false;
        private String mIp;
        private int    mPort;
        PrintWriter    out;
        BufferedReader in;
        Socket socket;

        public TCPClient(String ip, int port, OnMessageReceived listener) {
                mMessageListener = listener;
                mIp   = ip;//ip
                mPort = port;//port
        }

        public void sendMessage(String message){
                Log.d("sendMessage", "TCPClient: envoie en cours...");
                if (out != null && !out.checkError()) {
                        out.print(message);
                        out.flush();
                        Log.d("sendMessage", "TCPClient: message Envoyer");
                }
                else {
                        Log.d("sendMessage", "TCPClient: Echec de l'envoi du message");
                }
        }

        public void stopClient(){
                mRun = false;
        }

        public void run() {

                mRun = true;

                try {
                        //here you must put your computer's IP address.
                        InetAddress serverAddr = InetAddress.getByName(mIp);

                        Log.e("TCP Client", "C: Connecting...");

                        //create a socket to make the connection with the server
                        socket = new Socket(serverAddr, mPort);

                        try {
                                //send the message to the server
                                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                                Log.e("TCP Client", "C: Sent.");

                                 //receive the message which the server sends back
                                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                                //in this while the client listens for the messages sent by the server
                                while (mRun) {
                                        serverMessage = in.readLine();

                                        if (serverMessage != null && mMessageListener != null) {
                                                //call the method messageReceived from MyActivity class
                                                mMessageListener.messageReceived(serverMessage);
                                        }
                                        serverMessage = null;

                                }

                                Log.e("RESPONSE FROM SERVER", "S: Received Message: '" + serverMessage + "'");

                        } catch (Exception e) {

                                Log.e("TCP", "S: Error", e);

                        } finally {
                                socket.close();
                        }

                } catch (Exception e) {

                        Log.e("TCP", "C: Error", e);
                }

        }
        //Declare the interface. The method messageReceived(String message) will must be implemented in the MyActivity
        //class at on asynckTask doInBackground
        public interface OnMessageReceived {
                public void messageReceived(String message);
        }
}
