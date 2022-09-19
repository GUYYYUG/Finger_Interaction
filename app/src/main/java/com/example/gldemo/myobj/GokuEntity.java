package com.example.gldemo.myobj;


import static android.opengl.GLES10.glVertexPointer;
import static android.opengl.GLES20.glDrawElements;
import static android.opengl.GLES20.glLineWidth;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.opengl.GLES10;
import android.opengl.GLES20;
import android.util.Log;


import com.example.gldemo.base.GLEntity;
import com.example.gldemo.base.MatrixState;
import com.example.gldemo.base.ShaderUtil;
import com.example.gldemo.base.TextureUtil;
import com.example.gldemo.plane.PlaneGlSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * 加载后的物体
 */
public class GokuEntity extends GLEntity {
    private static final int UNIT_SIZE = 1;
    //自定义渲染管线着色器程序id
    int mProgram;
    //总变换矩阵引用
    int muMVPMatrixHandle;
    //位置、旋转变换矩阵
    int muMMatrixHandle;
    //顶点位置属性引用
    int maPositionHandle;
    //顶点法向量属性引用
    int maNormalHandle;
    //光源位置属性引用
    int maLightLocationHandle;
    //摄像机位置属性引用
    int maCameraHandle;
    //顶点纹理坐标属性引用
    int maTexCoorHandle;
    // 顶点颜色
    int muColorHandle;
    // 材质中透明度
    int muOpacityHandle;
    //顶点着色器代码脚本
    String mVertexShader;
    //片元着色器代码脚本
    String mFragmentShader;

    //顶点坐标数据缓冲
    FloatBuffer mVertexBuffer;
    //顶点法向量数据缓冲
    FloatBuffer mNormalBuffer;
    //顶点纹理坐标数据缓冲
    FloatBuffer mTexCoorBuffer;

    // 材质中alpha
    protected float mAlpha;
    // 需转化为纹理的图片
    protected Bitmap mBmp;
    //
    int vCount = 0;
    /**
     *
     */
    // 纹理是否已加载
    protected boolean isInintFinsh = false;
    // 纹理id
    protected int textureId;
    private int mvCount = 4; //因为是4个顶点
    private float vertexArray[] = new float[]
            {
                    4 * UNIT_SIZE, 0, 0,
                    0, 4 * UNIT_SIZE, 0,
                    0, 0, 4 * UNIT_SIZE,
                    4 * UNIT_SIZE, 4 * UNIT_SIZE, 4 * UNIT_SIZE
            };
    private float colors[] = new float[]
            {
                    0, 0, 0, 0,
                    0, 0, 0, 0,
                    0, 0, 0, 0,
                    0, 0, 0, 0
            };
    private static final int BYTES_PER_FLOAT = 4;
    private  final ByteBuffer XFacetsBuffer, YFacetsBuffer, ZFacetsBuffer;
    private  final FloatBuffer xyzVertexData;
    private  final FloatBuffer mColorData;
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

    private float[] mColorPoints = {
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, 0.5f
    };

    //定义XYZ坐标和显示的字
    public float xyzVertices[] = new float[]{
            -0.6f, 0f, 0f,//0 x起点，画坐标轴的
            0.6f, 0f, 0f,//1 X轴的终点
            0.5f,0.1f,0f,//2 X轴箭头1
            0.5f,-0.1f,0f,//3 X轴箭头2

            0f, -0.6f, 0f,//4 Y轴起点
            0f, 0.6f, 0f,//5 Y轴终点
            0.1f ,0.5f ,0f,//6 Y轴箭头1
            -0.1f ,0.5f ,0f,//7 Y轴箭头2

            0f, 0f, -0.6f,//8 Z轴起点
            0f, 0f, 0.6f,//9 Z轴终点
            0f ,0.1f ,0.5f,//10 Z轴箭头1
            0f ,-0.1f ,0.5f,//11 Z轴箭头2

            0.8f,0f,0f,//12 绘制字X
            0.85f,0.1f,0f,//13
            0.75f,0.1f,0f,//14
            0.75f,-0.1f,0f,//15
            0.85f,-0.1f,0f,//16

            0f,0.7f,0f,//17 绘制字Y
            0f,0.65f,0f,//18
            0.05f,0.75f,0f,//19
            -0.05f,0.75f,0f,//20

            -0.05f ,0.05f ,0.7f,//21 绘制字Z
            0.05f,0.05f,0.7f,//22
            -0.05f,-0.05f,0.7f,//23
            0.05f,-0.05f,0.7f,//24

//刻度X轴刻度
            0.3f,0f,0f,//25
            0.3f,0.05f,0f,//26
            -0.3f,0f,0f,//27
            -0.3f,0.05f,0f,//28
//刻度y轴刻度
            0f,0.3f,0f,//29
            -0.05f,0.3f,0f,//30
            0f,-0.3f,0f,//31
            -0.05f,-0.3f,0f,//32
//刻度Z轴刻度
            0f,0f,0.3f,//33
            0f,0.05f,0.3f,//34
            0f,0f,-0.3f,//35
            0f,0.05f,-0.3f//36



    };
    //X坐标及其箭头
    byte[] XFacets = new byte[] {
//起终点
            0,1,
//箭头
            1,2,
            1,3,
//X
            12,13,
            12,14,
            12,15,
            12,16,
//X坐标
            25,26,
            27,28

    };
    //Y坐标及其箭头
    byte[] YFacets = new byte[] {
//起终点
            4,5,
//箭头
            5,6,
            5,7,
//字Y
            17,18,
            17,19,
            17,20,
//Y轴刻度
            29,30,
            31,32

    };
    //Z坐标及其箭头
    byte[] ZFacets = new byte[] {
//起终点
            8,9,
//箭头
            9,10,
            9,11,
//字Z
            21,22,
            22,23,
            23,24,
//Z轴刻度
            33,34,
            35,36
    };

    private String mVertexShaderCode =
            "uniform mat4 u_Matrix;        \n" +
                    "attribute vec4 a_Position;     \n" +
                    "attribute vec4 a_Color;     \n" +
                    "varying vec4 v_Color;     \n" +
                    "void main()                    \n" +
                    "{                              \n" +
                    "    v_Color =  a_Color;  \n" +
                    "    gl_Position =  u_Matrix * a_Position;  \n" +
                    "}   \n";
    private String mFragmentShaderCode =
            "precision mediump float; \n" +
                    "varying vec4 v_Color;     \n" +
                    "void main()                    \n" +
                    "{                              \n" +
                    "    gl_FragColor = v_Color;    \n" +
                    "}";



    public GokuEntity(PlaneGlSurfaceView scene, float[] vertices, float[] normals, float texCoors[], float alpha, Bitmap bmp) {
        //初始化顶点坐标与着色数据
        initVertexData(vertices, normals, texCoors, alpha, bmp);
        //初始化shader
        initShader(scene.getResources());
        //
        mColorData = ByteBuffer
                .allocateDirect(mColorPoints.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mColorData.put(mColorPoints);
        xyzVertexData = ByteBuffer
                .allocateDirect(xyzVertices.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        xyzVertexData.put(xyzVertices);
        xyzVertexData.position(0);

        // 将直线的数组包装成ByteBuffer
        XFacetsBuffer = ByteBuffer.wrap(XFacets);
        YFacetsBuffer = ByteBuffer.wrap(YFacets);
        ZFacetsBuffer = ByteBuffer.wrap(ZFacets);
    }

    //初始化顶点坐标与着色数据的方法
    public void initVertexData(float[] vertices, float[] normals, float texCoors[], float alpha, Bitmap bmp) {
        this.mAlpha = alpha;
        this.mBmp = bmp;
        //顶点坐标数据的初始化================begin============================
        vCount = vertices.length / 3;

        //创建顶点坐标数据缓冲
        //vertices.length*4是因为一个整数四个字节
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mVertexBuffer = vbb.asFloatBuffer();//转换为Float型缓冲
        mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);//设置缓冲区起始位置
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
        //顶点坐标数据的初始化================end============================

        //顶点法向量数据的初始化================begin============================
        ByteBuffer cbb = ByteBuffer.allocateDirect(normals.length * 4);
        cbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mNormalBuffer = cbb.asFloatBuffer();//转换为Float型缓冲
        mNormalBuffer.put(normals);//向缓冲区中放入顶点法向量数据
        mNormalBuffer.position(0);//设置缓冲区起始位置
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
        //顶点着色数据的初始化================end============================

        //顶点纹理坐标数据的初始化================begin============================
        ByteBuffer tbb = ByteBuffer.allocateDirect(texCoors.length * 4);
        tbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mTexCoorBuffer = tbb.asFloatBuffer();//转换为Float型缓冲
        mTexCoorBuffer.put(texCoors);//向缓冲区中放入顶点纹理坐标数据
        mTexCoorBuffer.position(0);//设置缓冲区起始位置
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
        //顶点纹理坐标数据的初始化================end============================
    }

    //初始化shader
    public void initShader(Resources res) {
        //加载顶点着色器的脚本内容
        mVertexShader = ShaderUtil.loadFromAssetsFile("shader/texture_vertex.sh", res);
        //加载片元着色器的脚本内容
        mFragmentShader = ShaderUtil.loadFromAssetsFile("shader/texture_frag.sh", res);
        //基于顶点着色器与片元着色器创建程序
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
        //获取程序中顶点位置属性引用
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中顶点颜色属性引用
        maNormalHandle = GLES20.glGetAttribLocation(mProgram, "aNormal");
        //获取程序中总变换矩阵引用
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        //获取位置、旋转变换矩阵引用
        muMMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMMatrix");
        //获取程序中光源位置引用
        maLightLocationHandle = GLES20.glGetUniformLocation(mProgram, "uLightLocation");
        //获取程序中顶点纹理坐标属性引用
        maTexCoorHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoor");
        //获取程序中摄像机位置引用
        maCameraHandle = GLES20.glGetUniformLocation(mProgram, "uCamera");
        // 顶点颜色
        muColorHandle = GLES20.glGetUniformLocation(mProgram, "uColor");
        // alpha
        muOpacityHandle = GLES20.glGetUniformLocation(mProgram, "uOpacity");
    }

    /**
     * 初始化纹理
     */
    private void initTexture() {
        // 两球之间连线的纹理图片
        if (mBmp != null) {
            textureId = TextureUtil.getTextureIdByBitmap(mBmp);
        }
    }


    @Override
    public void onDraw(MatrixState matrixState) {
        // 加载纹理
        if (isInintFinsh == false) {
            initTexture();
            isInintFinsh = true;
        }

        //制定使用某套着色器程序
        GLES20.glUseProgram(mProgram);
        //将最终变换矩阵传入着色器程序
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, matrixState.getFinalMatrix(), 0);
        Log.v("muMVPMatrixHandle",String.valueOf(muMVPMatrixHandle));
        //将位置、旋转变换矩阵传入着色器程序
        GLES20.glUniformMatrix4fv(muMMatrixHandle, 1, false, matrixState.getMMatrix(), 0);
        Log.v("muMMatrixHandle",String.valueOf(muMMatrixHandle));
        //将光源位置传入着色器程序
        GLES20.glUniform3fv(maLightLocationHandle, 1, matrixState.lightPositionFB);
        Log.v("maLightLocationHandle",String.valueOf(maLightLocationHandle));
        //将摄像机位置传入着色器程序
        GLES20.glUniform3fv(maCameraHandle, 1, matrixState.cameraFB);
        // 将顶点位置数据传入渲染管线
        GLES20.glVertexAttribPointer
                (
                        maPositionHandle,
                        3,
                        GLES20.GL_FLOAT,
                        false,
                        3 * 4,
                        mVertexBuffer
                );
        //将顶点法向量数据传入渲染管线
        GLES20.glVertexAttribPointer
                (
                        maNormalHandle,
                        3,
                        GLES20.GL_FLOAT,
                        false,
                        3 * 4,
                        mNormalBuffer
                );
        // 颜色相关

        //为画笔指定顶点纹理坐标数据
        GLES20.glVertexAttribPointer
                (
                        maTexCoorHandle,
                        2,
                        GLES20.GL_FLOAT,
                        false,
                        2 * 4,
                        mTexCoorBuffer
                );
        // 材质alpha
        GLES20.glUniform1f(muOpacityHandle, mAlpha);
        // 启用顶点纹理数组
        GLES20.glEnableVertexAttribArray(maTexCoorHandle);
        //启用顶点位置、法向量、纹理坐标数据
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glEnableVertexAttribArray(maNormalHandle);


        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);

        //绘制加载的物体
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount);

        //
//        ByteBuffer vbb = ByteBuffer.allocateDirect(vertexArray.length*4);
//        vbb.order(ByteOrder.nativeOrder());
//        FloatBuffer vertex = vbb.asFloatBuffer();
//        vertex.put(vertexArray);
//        vertex.position(0);
//        GLES10.glEnableClientState(GL10.GL_VERTEX_ARRAY);
//        GLES10.glVertexPointer(3, GL10.GL_FLOAT, 0, vertex);
//        GLES10.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
//        GLES20.glDrawArrays(GLES20.GL_LINES, 0, mvCount);
//
//
//
//        GLES20.glLineWidth(3.0f);//直线宽度
//        GLES10.glVertexPointer(3, GL10.GL_FLOAT, 0, xyzVertexData);//设置XYZ的顶点
//
//        // 设置顶点的颜色数据
//        //glColor4f(0.0f, 1.0f, 0.0f, 1.0f);//设置绘笔颜色
//        GLES20.glDrawElements(GL10.GL_LINES, XFacetsBuffer.remaining(),
//                GL10.GL_UNSIGNED_BYTE, XFacetsBuffer);//X
//
//        //glColor4f(1.0f, 1.0f, 0.0f, 1.0f);
//        GLES20.glDrawElements(GL10.GL_LINES, YFacetsBuffer.remaining(),
//                GL10.GL_UNSIGNED_BYTE, YFacetsBuffer);//Y
//
//        //glColor4f(1.0f, 0.0f, 1.0f, 1.0f);
//        GLES20.glDrawElements(GL10.GL_LINES, ZFacetsBuffer.remaining(),
//                GL10.GL_UNSIGNED_BYTE, ZFacetsBuffer);//Z
    }

}
