package com.example.gldemo;

import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

//import com.example.mytcp.FuncTcpServer;

/**
 * Created by Jason Zhu on 2017-04-25.
 * Email: cloud_happy@163.com
 */

public class TcpClient implements Runnable{
    private String TAG = "TcpClient";
//    private String  serverIP = "192.168.88.141";
    private String  serverIP = "183.173.55.227";
    private int serverPort = 8080;
    private PrintWriter pw;
    private InputStream is;
    private FileInputStream fin;
    private DataInputStream dis;
    private boolean isRun = true;
    private Socket socket = null;
    byte buff[]  = new byte[4096];
    private String rcvMsg;
    private int rcvLen;
    public String mypose;


    public TcpClient(String ip , int port){
        this.serverIP = ip;
        this.serverPort = port;
    }

    public void closeSelf(){
        isRun = false;
    }

    public void send(String msg){
        pw.println(msg);
        pw.flush();
    }
    public void sendFile(String filename,String path){
        String result;
        String new_path;
        try{

            String sdCardDir = Environment.getExternalStorageDirectory().getAbsolutePath();

            new_path = sdCardDir + path;
            File file = new File(new_path);
            if(!file.exists()){
                Log.w("warning","no find");
            }
            Log.v("pathpath",new_path);
            int length = (int)file.length();
            Log.w("length",String.valueOf(length));
            byte[] buff = new byte[length];
            FileInputStream fileinput = new FileInputStream(file);
            fileinput.read(buff);
            result = new String(buff,"UTF-8");
            fileinput.close();
            String haha = "??????";
            pw.println(result);
            pw.flush();
//            OutputStream outputData = socket.getOutputStream();
//            FileInputStream fileInput = new FileInputStream(path);
//            int size = -1;

//            byte[] buffer = new byte[1024];
//            while((size = fileInput.read(buffer, 0, 1024)) != -1){
//                outputData.write(buffer, 0, size);
//            }
//            outputData.close();
//            fileInput.close();

        }catch (FileNotFoundException e){
            Log.w("warning","no find22222");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            socket = new Socket(serverIP,serverPort);
            socket.setSoTimeout(5000);
            pw = new PrintWriter(socket.getOutputStream(),true);
            is = socket.getInputStream();

            dis = new DataInputStream(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (isRun){
            try {
                rcvLen = dis.read(buff);
                rcvMsg = new String(buff,0,rcvLen,"utf-8");
                Log.i(TAG, "run: 收到消息:"+ rcvMsg);
                Intent intent =new Intent();
                intent.setAction("tcpClientReceiver");
                intent.putExtra("tcpClientReceiver",rcvMsg);
//                mypose = rcvMsg;
                MainActivity.context.sendBroadcast(intent);//将消息发送给主界面
                if (rcvMsg.equals("QuitClient")){   //服务器要求客户端结束
                    isRun = false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        try {
            pw.close();
            is.close();
            dis.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
