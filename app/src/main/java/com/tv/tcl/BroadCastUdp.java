package com.tv.tcl;

import android.os.SystemClock;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * 文 件 名: BroadCastUdp
 * 创 建 人: 何庆
 * 创建日期: 2018/12/30 23:59
 * 修改备注：
 */

public class BroadCastUdp implements Runnable {
    @Override
    public void run() {
        try {
            DatagramPacket packet = null;
            byte[] bytes = null;
            DatagramSocket socket = new DatagramSocket();
            InetAddress address = InetAddress.getByName("255.255.255.255");
            while (true) {
                SystemClock.sleep(2000);
                bytes = Utils.getLocalIPAddress().getBytes();
                System.out.println(Utils.getLocalIPAddress());
                packet = new DatagramPacket(bytes, bytes.length, address, Constants.UDP_PORT);
                socket.send(packet);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
