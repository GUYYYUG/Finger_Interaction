package com.example.gldemo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
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

import java.io.InputStream;
import java.util.ArrayList;
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
    private int e_cnt = 0;
    private float target[]=new float[]{
            ((float)Math.random()-0.5f)*90,((float)Math.random()-0.5f)*90,((float)Math.random()-0.5f)*90
    };
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
                    Log.v("rela",String.format("yaw: %.2f,pitch:%.2f",now_yaw,now_pitch));
                }
            }else {
//                mGLView.MyDraw(gokuRenderer, -90 + last_angles[2] + 15.0f, 0, -last_angles[1]);
                // 1->pitch 2->roll 3->yaw
//                mGLView.MyDraw(gokuRenderer, 0, 0+last_angles[2],0+last_angles[1]);
//                mGLView.MyDraw(gokuRenderer, 45+last_angles[2], 0,90+last_angles[1]);
                //1->pitch 2->roll 3->yaw
                mGLView.MyDraw(gokuRenderer, 0, 0,last_angles[1],1);
//                mGLView.MyDraw(gokuRenderer, 90, 0,0);
                Log.v("abs",String.format("yaw: %.2f,pitch:%.2f",last_angles[1],last_angles[2] ));
            }
            startTime();//执行计时方法
            float e1 = (float)Math.abs(mdata.test[cnt][0]-ee[e_cnt][0]);
            float e2 = (float)Math.abs(mdata.test[cnt][1]-ee[e_cnt][1]);
            float e3 = (float)Math.abs(mdata.test[cnt][2]-ee[e_cnt][2]);
            float sum = e2+e3;

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
            if(sum<=1.0f){
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
                                e_cnt ++;
                                otherGLView.MyDraw(otherRenderer,ee[e_cnt][0],ee[e_cnt][1],ee[e_cnt][2],1);
                                cnt = 0;

                                mGLView.MyDraw(gokuRenderer,mdata.test[cnt][0],mdata.test[cnt][1],mdata.test[cnt][2],1);
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
    /**开始计时方法*/

    private void startTime(){
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                cnt++;
                Message message = handler.obtainMessage();//获取Message对象
                message.arg1 = cnt;//设置Message对象附带的参数
                handler.sendMessage(message);//向主线程发送消息

            }
        };
        timer.schedule(task, 50);//执行计时器事件
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


        mGLView = (PlaneGlSurfaceView) findViewById(R.id.glsv_plane);
//        GokuRenderer gokuRenderer = new GokuRenderer(mGLView);
        gokuRenderer = new GokuRenderer(mGLView,0);
        mGLView.setRenderer(gokuRenderer);

        // 渲染模式(被动渲染)
        mGLView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
//        mGLView.setOnTouchListener(gokuRenderer.getTouchEventListener());
        // 1->pitch 2->roll 3->yaw
//        mGLView.MyDraw(gokuRenderer,0+init_pose[0],0+init_pose[1],0.1f+init_pose[2],0);
        mGLView.MyDraw(gokuRenderer,0,0,90,0);
        // 对照
        otherGLView = (PlaneGlSurfaceView)findViewById(R.id.glsv_plane2);
        otherRenderer = new GokuRenderer(otherGLView,1);
        otherGLView.setRenderer(otherRenderer);
        otherGLView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
//        otherGLView.MyDraw(otherRenderer,-90f+15f+mdata.test[190][2],mdata.test[190][2],mdata.test[190][1]);
        otherGLView.MyDraw(otherRenderer,0,0,90f,0);
//        otherGLView.MyDraw(otherRenderer,0,0,90f,2);
        Button buttonOpengl = (Button) findViewById(R.id.openglDemo);

        txtRcv = (TextView) findViewById(R.id.TextView3);


        buttonOpengl.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                startTime();
                startTime= System.currentTimeMillis(); //起始时间

            }
        });

        Button buttonstop = (Button) findViewById(R.id.stopDemo);
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