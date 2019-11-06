package com.example.dawn.friendsintheworld;


import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class TCPConnection {
        private RunOnThread thread;
        private Receive receive;
        private ReceiveListener listener;
        private Socket socket;
        private DataInputStream input;
        private DataOutputStream output;
        private InetAddress address;
        private int connectionPort;
        private String ip;
        private Exception exception;

        public TCPConnection(String ip, int connectionPort) {
            this.ip = ip;
            this.connectionPort = connectionPort;
            thread = new RunOnThread();

        }

        public void connect() {
            thread.start();
            thread.execute(new Connect());
        }

        public void setListener(ReceiveListener listener) {
            this.listener = listener;
        }

        public void disconnect() {
            thread.execute(new Disconnect());
        }

        public void send(Expression expression) {
            thread.execute(new Send(expression));
        }

        private class Receive extends Thread {
            public void run() {
                String result;
                try {
                    while (receive != null) {
                        result = (String) input.readUTF(); //
                        System.out.println(result);
                        listener.newMessage(result);
                    }
                } catch (Exception e) { // IOException, ClassNotFoundException
                    receive = null;
                }
            }
        }

        public Exception getException() {
            Exception result = exception;
            exception = null;
            return result;
        }

        private class Connect implements Runnable {
            public void run() {
                try {
                    Log.d("TCPConnection","Connect-run");
                    address = InetAddress.getByName(ip);
                    Log.d("TCPConnection-Connect","Skapar socket");
                    socket = new Socket(address, connectionPort);
                    input = new DataInputStream(socket.getInputStream());
                    output = new DataOutputStream(socket.getOutputStream());
                    output.flush();
                    Log.d("TCPConnection-Connect","Strömmar klara");
                    listener.newMessage("CONNECTED");
                    receive = new Receive();
                    receive.start();
                } catch (Exception e) { // SocketException, UnknownHostException
                    Log.d("TCPConnection-Connect",e.toString());
                    exception = e;
                    listener.newMessage("EXCEPTION");
                }
            }
        }

        public class Disconnect implements Runnable {
            public void run() {
                try {
                    if (socket != null)
                        socket.close();
                    if (input != null)
                        input.close();
                    if (output != null)
                        output.close();
                    thread.stop();
                    listener.newMessage("CLOSED");
                } catch(IOException e) {
                    exception = e;
                    listener.newMessage("EXCEPTION");
                }
            }
        }

        public class Send implements Runnable {
            private Expression exp;

            public Send(Expression exp) {
                this.exp = exp;
            }

            public void run() {
                try {
                    output.writeUTF(exp.message);
                    output.flush();
                } catch (IOException e) {
                    exception = e;
                    listener.newMessage("EXCEPTION");
                }
            }
        }

    }
