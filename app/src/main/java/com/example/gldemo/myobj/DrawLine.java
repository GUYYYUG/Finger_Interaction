package com.example.gldemo.myobj;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;

public class DrawLine extends OpenGLUtils{
    private IntBuffer verBuffer;
    private IntBuffer colorBuffer;
    private ByteBuffer indBuffer;

    public DrawLine() {
        //初始化 顶点 颜色 和索引
        init();
    }

    private void init() {
        //设置 绘制的顶点数
        int vCount = 4;
        //设置缩放比
        int UNIT_SIZE = 10000;
        //初始化顶点数据
        int ver[] = new int[]{
                -2 * UNIT_SIZE, 3 * UNIT_SIZE, 0,//第一个点
                UNIT_SIZE, UNIT_SIZE, 0,//第二个
                -1 * UNIT_SIZE, -2 * UNIT_SIZE, 0,//第三个
                2 * UNIT_SIZE, -3 * UNIT_SIZE, 0      //第四个
        };

        //创建顶点数据缓冲
        verBuffer = getIntBuffer(ver);

        //初始化顶点颜色
        int one = 65535;//支持65535色彩通道
        int color[] = new int[]{//RGBA
                one, 0, 0, 0,
                one, 0, 0, 0,
                one, 0, 0, 0,
                one, 0, 0, 0
        };

        //创建 颜色缓冲
        colorBuffer = getIntBuffer(color);

        //创建索引
        byte indices[] = new byte[]{
                0, 3, 2, 1
        };
        //创建索引缓冲
        indBuffer = getByteBuffer(indices);
    }
//绘制

    public void mDraw(GL10 gl) {
        //启用顶点坐标数组
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        //启用顶颜色数组
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        //设置画笔
        //为画笔指定顶点数据
        /**
         * 参数1坐标个数
         * 参数2顶点数据类型
         * 参数3 连续顶点坐标数据的间隔
         * 参数4顶点数缓冲
         */
        gl.glVertexPointer(3, GL10.GL_FIXED, 0, verBuffer);
        //为花大笔指定顶点颜色数据
        //同上
        gl.glColorPointer(4,GL10.GL_FIXED,0,colorBuffer);
        /**
         * 参数1 绘制模型 点 线段 三角形
         * 参数2 索引个数
         * 参数3 数据类型
         * 参数4 索引数据缓冲
         */
        gl.glDrawElements(GL10.GL_LINE_LOOP,4,GL10.GL_UNSIGNED_BYTE,indBuffer);
    }

}
