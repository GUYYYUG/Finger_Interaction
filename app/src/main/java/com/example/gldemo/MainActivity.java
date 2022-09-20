package com.example.gldemo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gldemo.R;
import com.example.gldemo.data;
import com.example.gldemo.myobj.GokuRenderer;
import com.example.gldemo.plane.PlaneGlSurfaceView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private PlaneGlSurfaceView mGLView;
    private PlaneGlSurfaceView otherGLView;
    private data mdata = new data();
    private ArrayList<Long> timearr = new ArrayList<>();
    private boolean flag = false;
    private float ee[][] = new float[][]{
            {mdata.test[190][0],mdata.test[190][1],mdata.test[190][2]},
            {mdata.test[100][0],mdata.test[100][1],mdata.test[100][2]},
            {mdata.test[100][0],mdata.test[100][1],mdata.test[100][2]}
    };
    private float init_pose[] = {30,40,50};
    private float init_yaw = 0.0f;
    private float init_roll_pitch = 0.0f;
    private float delta_yaw = 0.0f;
    private float delta_roll_pitch = 0.0f;
    private int e_cnt = 0;
    private float target[]=new float[]{
            ((float)Math.random()-0.5f)*90,((float)Math.random()-0.5f)*90,((float)Math.random()-0.5f)*90
    };
    private int mode = -1;
    private float last_yaw = 0.0f;
    private float last_pitch = 0.0f;
    private float now_yaw = 0.0f;
    private float now_pitch = 0.0f;
    private float relative_yaw = 0.0f;
    private float relative_pitch = 0.0f;
    private boolean relative_mode = false;
    private boolean relatiive_mode_first_type = true;
    private boolean first_abs = true;
    private boolean first_rela = true;
    private boolean isrun= true;

    private float[] target_pose = {0.0f,0.0f,0.0f}; //target pose : yaw,pitch,roll
    private float[] current_pose = {0.0f,0.0f,0.0f}; // target pose: yaw,pitch,roll
    private float[] temp = {0.0f,0.0f,0.0f};


    private TextView txtRcv;
    //
    private GokuRenderer gokuRenderer;
    private GokuRenderer otherRenderer;
    /**开始计时方法*/
    public int cnt = 0;
    private long startTime , endTime ,runTime ;

    private Timer timer = null;
    private TimerTask task = null;
    private float yaw = 0.0f;
    public void draw_with_ratio(float ratio,float last_yaw,float last_pitch,float yaw,float pitch){
        float delta_yaw = (yaw-last_yaw) * ratio;
        float delta_pitch = (pitch-last_pitch) * ratio;
//        mGLView.MyDraw(gokuRenderer,last_yaw+delta_yaw,0,last_pitch+delta_pitch,0);
    }
    private Handler handler=new Handler(){
        /**重写handleMessage方法*/
        @Override
        public void handleMessage(Message msg) {
//            showTime.setText(msg.arg1+"");
//            try{
////            InputStream in = mgr.open("r20/100000_0.png");
////                int i = 600;
//
////                mGLView.MyDraw(gokuRenderer,mdata.test[cnt][0],mdata.test[cnt][1],mdata.test[cnt][2]);
//                mGLView.MyDraw(gokuRenderer,mdata.test[cnt][2],0,mdata.test[cnt][1]);
////                yaw = yaw + 1.0f;
//
//            }catch (Exception e){
//
//            }
            float[] last_angles = mdata.test[cnt];
            readFile("/Download/mypose.txt");
            if (relative_mode){
                //first type
                if(relatiive_mode_first_type){
//                    relatiive_mode_first_type = false;
                    relative_pitch = last_angles[2];
                    relative_yaw = last_angles[1];
                    now_pitch = last_angles[2];
                    now_yaw = last_angles[1];
//                    mGLView.MyDraw(gokuRenderer, -90 + now_pitch+ 15.0f, 0, -now_yaw); //still
//                    mGLView.MyDraw(gokuRenderer, 0, now_pitch, now_yaw);
                    mGLView.MyDraw(gokuRenderer,  0+now_pitch, 0,90+now_yaw,1);
//                    mGLView.MyDraw(gokuRenderer, 0, delta_roll_pitch,delta_yaw,mode);
                    Log.e("rela_first",String.format("yaw: %.2f,pitch:%.2f",now_yaw,now_pitch));


                }else{
                    float delta_pitch = last_angles[2] - relative_pitch;
                    float delta_yaw = last_angles[1] - relative_yaw;
                    txtRcv.setText(String.format("yaw: %.2f,pitch: %.2f",delta_yaw,delta_pitch));
                    now_pitch = now_pitch + delta_pitch;
                    now_yaw = now_yaw + delta_yaw;
                    relative_pitch = last_angles[2];
                    relative_yaw = last_angles[1];
//                    mGLView.MyDraw(gokuRenderer, -90 + now_pitch+ 15.0f, 0, -now_yaw);
                    //1->pitch 2->roll 3->yaw
                    mGLView.MyDraw(gokuRenderer,  0+now_pitch, 0,90+now_yaw,1);
//                    mGLView.MyDraw(gokuRenderer, 0, delta_roll_pitch,delta_yaw,mode);
                    Log.v("rela",String.format("yaw: %.2f,pitch:%.2f",now_yaw,now_pitch));
                }
            }else {
//                mGLView.MyDraw(gokuRenderer, -90 + last_angles[2] + 15.0f, 0, -last_angles[1]);
                // 1->pitch 2->roll 3->yaw
//                mGLView.MyDraw(gokuRenderer, 0, 0+last_angles[2],0+last_angles[1]);
//                mGLView.MyDraw(gokuRenderer, 45+last_angles[2], 0,90+last_angles[1]);
                //1->pitch 2->roll 3->yaw
//                mGLView.MyDraw(gokuRenderer, 0, 0,last_angles[1],1);

                mGLView.MyDraw(gokuRenderer, 0,delta_roll_pitch,delta_yaw,mode);
//                mGLView.MyDraw(gokuRenderer, 90, 0,0);
                Log.e("delta",String.format("yaw: %.2f,pitch:%.2f,mode:%d",delta_yaw,delta_roll_pitch ,mode));
            }
            startTime();//执行计时方法
            float e1 = (float)Math.abs(target_pose[0]+current_pose[0]);
            float e2 = (float)Math.abs(target_pose[1]+current_pose[1]);
            float e3 = (float)Math.abs(target_pose[2]+current_pose[2]);
            Log.e("error,yaw,pitch,roll",String.format("yaw_error: %.2f,pitch_error:%.2f,roll_errore:%.2f",e1,e2,e3));
            float sum = e1+e2+e3;

            if(sum<=10.0f){
                relative_mode = false;
                if(relatiive_mode_first_type){
                    Toast.makeText(MainActivity.this,"relative control start", Toast.LENGTH_SHORT).show();
                    relatiive_mode_first_type = false;
                }
            }
            else {
                relative_mode = false;
                if (first_abs) {
                    first_abs = false;
                    Toast.makeText(MainActivity.this, "absolute control start", Toast.LENGTH_SHORT).show();
                }
            }
            if(e1<=5f && e2 <=5f && e3 <= 5f){
                stopTime();
                endTime = System.currentTimeMillis(); //结束时间
                timearr.add(endTime - startTime);
                String ss = String.format("total time use:%d ms", endTime - startTime);
                new AlertDialog.Builder(MainActivity.this).setTitle("Info")//设置对话框标题

                        .setMessage(ss+"\n"+"Do you want to change pose？")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {//添加确定按钮

                            @Override
                            public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
//                                    stopTime();
//                                e_cnt ++;
//                                otherGLView.MyDraw(otherRenderer,ee[e_cnt][0],ee[e_cnt][1],ee[e_cnt][2],1);
//                                cnt = 0;
                                // random pose
                                Random r = new Random();
                                target_pose[0] = -90f + r.nextFloat()*180f ;
                                target_pose[1] = -90f + r.nextFloat()*180f ;
                                target_pose[2] = -90f + r.nextFloat()*180f ;
                                mGLView.MyDraw(gokuRenderer,target_pose[0],target_pose[1],target_pose[2],0);
                            }
                        })
                        .setNegativeButton("no", new DialogInterface.OnClickListener() {//添加返回按钮

                    @Override
                    public void onClick(DialogInterface dialog, int which) {//响应事件
                        String mstring = "after "+String.valueOf(e_cnt+1)+"trails\n";
                        String avg_time = "average time use:";
                        long sum = 0;
                        for(int i = 0; i<timearr.size();i++){
                            sum = sum+ timearr.get(i);
                        }
                        long  avg = sum/timearr.size();
                        timearr.clear();
                        e_cnt = 0;
                        new AlertDialog.Builder(MainActivity.this).setTitle("finished!")
                                .setMessage(mstring+String.format("avg time use:%d ms", avg)).show();

                    }

                }).show();//在按键响应事件中显示此对话框
            }
        }
    };

    public void readFile(String path){
        String result;
        String new_path;
        try{

            String sdCardDir = Environment.getExternalStorageDirectory().getAbsolutePath();

            new_path = sdCardDir + path;
            Log.e("path",new_path);
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
            if (result.length() == 0){
                mode = 8;
                return;
            }
            Log.v("content",result);
            fileinput.close();
            String haha = "??????";

            String [] arr = result.split("\\s+");
//            for(int i = 0;i<arr.length;i++){
//                System.out.println(arr[i]);
//                if(i==2||i==3){
//                    float yaw = Float.parseFloat(arr[i]);
//                    System.out.println(yaw);
//                }
//            }
            if(arr.length == 3){
                //yaw,pitch,roll
                if (Float.parseFloat(arr[0])!=0){
                    delta_yaw = Float.parseFloat(arr[0]);
                    temp[0] = delta_yaw;
                    mode = 1;
                }else if(Float.parseFloat(arr[1])!=0){
                    delta_roll_pitch = Float.parseFloat(arr[1]);
                    temp[1] = delta_roll_pitch;
                    mode = 2;
                }else if(Float.parseFloat(arr[2])!=0){
                    delta_roll_pitch = Float.parseFloat(arr[2]);
                    temp[2] = delta_roll_pitch;
                    mode = 3;
                }else if(Float.parseFloat(arr[0]) ==0 && Float.parseFloat(arr[1]) ==0 && Float.parseFloat(arr[2]) ==0){
                    mode = 4;
                    current_pose[0] = current_pose[0] + temp[0];
                    current_pose[1] = current_pose[1] + temp[1];
                    current_pose[2] = current_pose[2] + temp[2];
                    temp[0] = 0.0f;
                    temp[1] = 0.0f;
                    temp[2] = 0.0f;
                    return;
                }else{
                    mode = 7;
                    return;
                }
            }
            else{
                mode = 8;
                return;
            }
//            if(arr[0].equals("init")){
//                if(arr[1].equals("yaw")){
//                    init_yaw = Float.parseFloat(arr[2]);
//                    mode = 1;
//                }else if(arr[1].equals("pitch")){
//                    init_roll_pitch = Float.parseFloat(arr[3]);
//                    mode =2;
//                }else if(arr[1].equals("roll")){
//                    init_roll_pitch = Float.parseFloat(arr[3]);
//                    mode = 3;
//                }else{
//                    ;
//                }
//                isrun = true;
//            }
//            else if(arr[0].equals("null")){
//                if(isrun){
//                    mode = 4;
//                    isrun = false;
//                }
//                else{
//                    mode = 5;
//                }
//
//            }
//            else if(arr[0].length()>0){
//                delta_yaw = Float.parseFloat(arr[0]) - init_yaw;
//                delta_roll_pitch = Float.parseFloat(arr[1]) - init_roll_pitch;
//
//            }



        }catch (FileNotFoundException e){
            Log.w("warning","no find22222");
//            if(isrun){
//                mode = 4;
//                isrun = false;
//            }
//            else{
//                mode = 5;
//            }
            mode = 8;
            return ;

        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
            mode = 8;
            return ;
        } catch (IOException e) {
            mode = 8;
//            e.printStackTrace();
            return ;
        }
    }
    /**开始计时方法*/

    private void startTime(){
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
//                cnt++;
                Message message = handler.obtainMessage();//获取Message对象
                message.arg1 = cnt;//设置Message对象附带的参数
                handler.sendMessage(message);//向主线程发送消息

            }
        };
        timer.schedule(task, 20);//执行计时器事件
    };
    /**停止计时方法*/
    private void stopTime(){
        timer.cancel();//注销计时器事件
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        if (Build.VERSION.SDK_INT>=23&&checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }



        mGLView = (PlaneGlSurfaceView) findViewById(R.id.glsv_plane);
//        GokuRenderer gokuRenderer = new GokuRenderer(mGLView);
        gokuRenderer = new GokuRenderer(mGLView,0);
        mGLView.setRenderer(gokuRenderer);

        // 渲染模式(被动渲染)
        mGLView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
//        mGLView.setOnTouchListener(gokuRenderer.getTouchEventListener());
        // 1->pitch 2->roll 3->yaw
//        mGLView.MyDraw(gokuRenderer,0+init_pose[0],0+init_pose[1],0.1f+init_pose[2],0);
        target_pose[0] = -30; //yaw
        target_pose[1] = 45; //pitch
        target_pose[2] = -45; // row
        mGLView.MyDraw(gokuRenderer,-30,45,-45,0);
        // 对照
        otherGLView = (PlaneGlSurfaceView)findViewById(R.id.glsv_plane2);
        otherRenderer = new GokuRenderer(otherGLView,1);
        otherGLView.setRenderer(otherRenderer);
        otherGLView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
//        otherGLView.MyDraw(otherRenderer,-90f+15f+mdata.test[190][2],mdata.test[190][2],mdata.test[190][1]);
        otherGLView.MyDraw(otherRenderer,0,0,90f,6);
//        otherGLView.MyDraw(otherRenderer,0,0,90f,2);
        Button buttonOpengl = (Button) findViewById(R.id.openglDemo);
        readFile("/Download/mypose.txt");

        txtRcv = (TextView) findViewById(R.id.TextView3);


        buttonOpengl.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                startTime();
                startTime= System.currentTimeMillis(); //起始时间

            }
        });

        Button buttonstop = (Button) findViewById(R.id.stopDemo);
        buttonstop.setVisibility(View.INVISIBLE);
        buttonstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopTime();
            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        mGLView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLView.onPause();
    }
}