package com.tv.tcl;

/**
 * 文 件 名: TcpListener
 * 创 建 人: 何庆
 * 创建日期: 2018/12/30 17:09
 * 修改备注：
 */

public interface TcpListener {
    void onClientJoin(String ip);
    void onClientMsg(Server.Client client, String msg);
    void onClientQuit(String ip);
}
