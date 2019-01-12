package com.tv.tcl;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 文 件 名: SerVer
 * 创 建 人: 何庆
 * 创建日期: 2018/12/30 16:36
 * 修改备注：
 */

public class Server {
    private boolean started = false;
    private ServerSocket ss = null;
    private List<Client> clients = new ArrayList<Client>();
    private List<TcpListener> listeners = new LinkedList<>();

    public void addListener(TcpListener listener) {
        this.listeners.add(listener);
    }

    public void start() {
        this.start(null);
    }

    public void start(TcpListener listener) {
        if (listener != null)
            this.listeners.add(listener);
        try {
            ss = new ServerSocket(Constants.TCP_PORT);
            started = true;
        } catch (BindException e) {//端口被占用
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            while (started) {
                Socket s = ss.accept();
                Client c = new Client(s);
                for (int i = 0; i < listeners.size(); i++) {
                    if (listeners.get(i) != null) {
                        listeners.get(i).onClientJoin(s.getInetAddress().getHostAddress());
                    }
                }
                new Thread(c).start();
                clients.add(c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                ss.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class Client implements Runnable {
        private Socket s;
        private DataInputStream dis = null;
        private DataOutputStream dos = null;
        private boolean bConnected = false;
        private String ip;

        public Client(Socket s) {
            this.s = s;
            this.ip = s.getInetAddress().getHostAddress();
            try {
                dis = new DataInputStream(s.getInputStream());
                dos = new DataOutputStream(s.getOutputStream());
                bConnected = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void send(String str) {
            try {
                dos.writeUTF(str);
            } catch (IOException e) {
                clients.remove(this);
                for (int i = 0; i < listeners.size(); i++) {
                    if (listeners.get(i) != null) {
                        listeners.get(i).onClientQuit(ip);
                    }
                }
            }
        }

        public void run() {
            try {
                while (bConnected) {
                    String str = dis.readUTF();//读到客服端的数据
                    for (int i = 0; i < listeners.size(); i++) {
                        if (listeners.get(i) != null) {
                            listeners.get(i).onClientMsg(this, str);
                        }
                    }
                }
            } catch (Exception e) {
                clients.remove(this);
                for (int i = 0; i < listeners.size(); i++) {
                    if (listeners.get(i) != null) {
                        listeners.get(i).onClientQuit(ip);
                    }
                }
            } finally {
                try {
                    if (dis != null)
                        dis.close();
                    if (dos != null)
                        dos.close();
                    if (s != null) {
                        s.close();
                    }

                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }
        }
    }
}
