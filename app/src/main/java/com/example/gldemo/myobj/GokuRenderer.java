package com.example.gldemo.myobj;

import static android.opengl.GLES10.glColor4f;
import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glDrawElements;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGenBuffers;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetProgramInfoLog;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetShaderInfoLog;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glLineWidth;
import static android.opengl.GLES10.glVertexPointer;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.setLookAtM;

import android.opengl.GLES10;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;


import com.example.gldemo.base.LeGLConfig;
import com.example.gldemo.base.MatrixState;
import com.example.gldemo.base.ObjInfo;
import com.example.gldemo.plane.PlaneGlSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Renderer class
 */
public class GokuRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "GokuRenderer";
    /**
     * UI
     */
    GokuGroup mSpriteGroup = null;
    private int type = 0;
    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;//角度缩放比例
    MatrixState matrixState;

    private static final int BYTES_PER_FLOAT = 4;
    private int mShaderProgram;

    private int aColorLocation;
    private int aPositionLocation;
    private int uMatrixLocation;


    private final float[] modelMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];
    private final float[] invertedViewProjectionMatrix = new float[16];
    private final float[] mMVPMatrix = new float[16];

    private volatile boolean mIsPressed;
    private volatile boolean mHadChanged = false;

    private int mVboBufferId;
    public boolean save_matrix = false;





    public PlaneGlSurfaceView mGLSurfaceView;

    public GokuRenderer(PlaneGlSurfaceView glSurfaceView,int type) {
        /**
         * type = 0 represents red car,type = 1 represents copy car
         */
        this.mGLSurfaceView = glSurfaceView;
        this.type = type;
        matrixState = new MatrixState();
        // 初始化obj+mtl文件
        mSpriteGroup = new GokuGroup(mGLSurfaceView,this.type);
        //

    }


    @Override
    public void onDrawFrame(GL10 gl) {
        // TODO GlThread
        // 清除深度缓冲与颜色缓冲
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        // 设置屏幕背景色RGBA
        /**
         * 绘制物体
         */

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();



        // Assign the matrix
//        glUniformMatrix4fv(uMatrixLocation, 1, false, mMVPMatrix, 0);
//        Log.e("GokuRender", String.valueOf(uMatrixLocation));
        // Draw the table.
        //glDrawArrays(GL_TRIANGLE_STRIP, 0, 10);
//        glLineWidth(3.0f);//直线宽度
//        glVertexPointer(3, GL10.GL_FLOAT, 0, xyzVertexData);//设置XYZ的顶点


        //push and pop
        // remain the original matrix
        if (save_matrix == false) {
            matrixState.pushMatrix();
            // ondraw frame
            mSpriteGroup.onDraw(matrixState);
            Log.i("PrintDemo",Arrays.toString(matrixState.getMMatrix()));

            matrixState.popMatrix();
            Log.e("PrintDemo",Arrays.toString(matrixState.getMMatrix()));
        }else{
            matrixState.pushMatrix();
            mSpriteGroup.onDraw(matrixState);
            Log.i("PrintDemo",Arrays.toString(matrixState.getMMatrix()));
            matrixState.popMatrix();
            Log.e("PrintDemo",Arrays.toString(matrixState.getMMatrix()));
        }
//        Log.i("PrintDemo",Arrays.toString(matrixState.getMMatrix()));


    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // TODO GlThread
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        //开启混合
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
//        // 设置屏幕背景色RGBA
//        //GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
//        // 启用深度测试
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
//        // 设置为打开背面剪裁
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        // 初始化变换矩阵
        matrixState.setInitStack();
        matrixState.setLightLocation(1000, 1000, 1000);
        //
//        int vertexShader = compileShader(GL_VERTEX_SHADER, mVertexShaderCode);
//        int fragmentShader = compileShader(GL_FRAGMENT_SHADER, mFragmentShaderCode);
//        mShaderProgram = linkProgram(vertexShader, fragmentShader);
//        glUseProgram(mShaderProgram);
//
//        aColorLocation = glGetAttribLocation(mShaderProgram, "a_Color");
//
//        aPositionLocation = glGetAttribLocation(mShaderProgram, "a_Position");
//
//        uMatrixLocation = glGetUniformLocation(mShaderProgram, "u_Matrix");
//        Log.v("GokuRender1111", String.valueOf(uMatrixLocation));
//
//        xyzVertexData.position(0);
        //
        initUI();

        //
        glBindBuffer(GL_ARRAY_BUFFER, mVboBufferId);
        glVertexAttribPointer(aPositionLocation, 3, GL_FLOAT,
                false, 3 * BYTES_PER_FLOAT, 0);
//        glVertexAttribPointer(aPositionLocation, 3, GL_FLOAT,
//                false, 0, mVertexData);
        glEnableVertexAttribArray(aPositionLocation);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

//        mColorData.position(0);
//        glVertexAttribPointer(aColorLocation, 3, GL_FLOAT,
//                false, 0, mColorData);
//        glEnableVertexAttribArray(aColorLocation);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // TODO GlThread
        // viewPort
        GLES20.glViewport(0, 0, width, height);
        //

        float ratio = (float) width / height;
        // 平行投影
//		MatrixState.setProjectOrtho(-ratio, ratio, -1, 1,
//				LeGLConfig.PROJECTION_NEAR, LeGLConfig.PROJECTION_FAR);
        matrixState.setProjectFrustum(-ratio, ratio, -1, 1,
                LeGLConfig.PROJECTION_NEAR, LeGLConfig.PROJECTION_FAR);
        // camera
        matrixState.setCamera(LeGLConfig.EYE_X, LeGLConfig.EYE_Y, LeGLConfig.EYE_Z,
                LeGLConfig.VIEW_CENTER_X, LeGLConfig.VIEW_CENTER_Y, LeGLConfig.VIEW_CENTER_Z,
                0f, 1f, 0f);


    }

    /**
     * 初始化场景中的GOKU实体类
     */
    private void initUI() {
        mSpriteGroup.initObjs();
        // Allocate a buffer.
        final int buffers[] = new int[1];
        glGenBuffers(buffers.length, buffers, 0);

        if (buffers[0] == 0) {
            throw new RuntimeException("Could not create a new index buffer object.");
        }


        // Bind to the buffer.
//        glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);

        // Transfer data from native memory to the GPU buffer.
        //glBufferData(GL_ARRAY_BUFFER, mVertexData.capacity() * BYTES_PER_FLOAT, mVertexData, GL_STATIC_DRAW);
//        glBufferData(GL_ARRAY_BUFFER, xyzVertexData.capacity() * BYTES_PER_FLOAT, xyzVertexData, GL_STATIC_DRAW);


        // IMPORTANT: Unbind from the buffer when we're done with it.
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public PlaneGlSurfaceView.OnTouchEventListener getTouchEventListener() {
        return touchEventListener;
    }


    /**
     * draw
     */
    public void DrawWithButton(float roll_d,float yaw_d,float pitch_d,int mode){
        if(mode==0){
            save_matrix = true;
        }else{
            save_matrix = false;
        }
        mSpriteGroup.rpy_to_axis(roll_d,yaw_d,pitch_d,mode);
        mGLSurfaceView.requestRender();//重绘画面
        mGLSurfaceView.invalidate();

    }
    /**
     * draw one frame
     */
    public void DrawOneFrame(){

    }
    /**
     * 触摸回调，don't use
     */
    PlaneGlSurfaceView.OnTouchEventListener touchEventListener = new PlaneGlSurfaceView.OnTouchEventListener() {
        @Override
        public void onTouchEvent(float dx, float dy) {
            float yAngle = mSpriteGroup.getSpriteAngleY();
            yAngle += dx * TOUCH_SCALE_FACTOR;
            mSpriteGroup.setSpriteAngleY(yAngle);

            float xAngle = mSpriteGroup.getSpriteAngleX();
            xAngle += dy * TOUCH_SCALE_FACTOR;
            mSpriteGroup.setSpriteAngleX(xAngle);

            float zAngle = mSpriteGroup.getSpriteAngleZ();




            mGLSurfaceView.requestRender();//重绘画面
            mGLSurfaceView.invalidate();




                }
//            }


//        }
    };



}
