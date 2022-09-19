package com.example.gldemo.myobj;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;


import com.example.gldemo.base.BitmapUtil;
import com.example.gldemo.base.GLEntity;
import com.example.gldemo.base.GLObjColorEntity;
import com.example.gldemo.base.GLGroup;
import com.example.gldemo.base.MatrixState;
import com.example.gldemo.base.ObjInfo;
import com.example.gldemo.base.ObjLoaderUtil;
import com.example.gldemo.plane.PlaneGlSurfaceView;

import java.util.ArrayList;
import java.util.Arrays;


public class GokuGroup extends GLGroup {
    private static final String TAG = GokuGroup.class.getSimpleName();

    private float angley = 0.0f;
    private ArrayList<ObjInfo> objDatas;
    private ArrayList<GLEntity> mObjSprites = new ArrayList<GLEntity>();
    private float rotate_info[] = {0,0,1,0};
    public int mmode = 0;
    public GokuGroup(PlaneGlSurfaceView scene,int type) {
        super(scene);
        try {
            if(type==0) {
                objDatas = ObjLoaderUtil.load("teapot.obj", scene.getResources());
            }
            else{
                objDatas = ObjLoaderUtil.load("teapot3.obj", scene.getResources());
            }
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void rpy_to_axis(float roll_d,float yaw_d,float pitch_d,int mode){
        mmode = mode;
        float roll = (float) Math.toRadians(roll_d);
        float yaw = (float) Math.toRadians(yaw_d);
        float pitch = (float) Math.toRadians(pitch_d);
        float yawM[][] = new float[][]{
            {(float) Math.cos(yaw),-(float)Math.sin(yaw),0},
            {(float)Math.sin(yaw),(float) Math.cos(yaw),0},
            {0,0,1}
        };
        float pitchM[][] = new float[][]{
                {(float) Math.cos(pitch),0,(float)Math.sin(pitch)},
                {0,1,0},
                {-(float)Math.sin(pitch),0,(float) Math.cos(pitch)}

        };
        float rollM[][] = new float[][]{
                {1,0,0},
                {0, (float) Math.cos(roll),-(float)Math.sin(roll)},
                {0, (float) Math.sin(roll),(float) Math.cos(roll)}
        };
        float [][] res1 =  new float[3][3];
        float [][] res =  new float[3][3];
        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++){
                for(int k=0;k<3;k++){
//                    res1[i][j] =res1[i][j] + yawM[i][k] * pitchM[k][j];
                    res1[i][j] =res1[i][j] + yawM[i][k] * rollM[k][j];
                }
            }
        }
        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++){
                for(int k=0;k<3;k++){
//                    res[i][j] =res[i][j] + res1[i][k] * rollM[k][j];
                    res[i][j] =res[i][j] + res1[i][k] * pitchM[k][j];
                }
            }
        }
        float rx,ry,rz,theta;
        rx = res[2][1] - res[1][2];
        ry = res[0][2] - res[2][0];
        rz = res[1][0] - res[0][1];
        theta = (float)Math.toDegrees(Math.acos(((res[0][0]+res[1][1]+res[2][2])-1)/2));
        rotate_info[0] = theta;
//        rotate_info[1] = rx;
//        rotate_info[2] = ry;
//        rotate_info[3] = rz;
        rotate_info[1] = roll_d;
        rotate_info[2] = yaw_d;
        rotate_info[3] = pitch_d;

    }
    public void initObjs() {
        mObjSprites.clear();
        if (objDatas != null) {
            for (int i = 0; i < objDatas.size(); i++) {
                ObjInfo data = objDatas.get(i);
                //
                int diffuseColor = data.mtlData != null ? data.mtlData.Kd_Color : 0xffffffff;
                float alpha = data.mtlData != null ? data.mtlData.alpha : 1.0f;
                String texturePath = data.mtlData != null ? data.mtlData.Kd_Texture : "";

                // 构造对象
                if (data.aTexCoords != null && data.aTexCoords.length != 0 && TextUtils.isEmpty(texturePath) == false) {
                    Bitmap bmp = BitmapUtil.getBitmapFromAsset(getBaseScene().getContext(), texturePath);
                    GLEntity spirit = new GokuEntity(getBaseScene(), data.aVertices, data.aNormals, data.aTexCoords, alpha, bmp);
                    mObjSprites.add(spirit);
                } else {
                    GLEntity spirit = new GLObjColorEntity(getBaseScene(), data.aVertices, data.aNormals, diffuseColor, alpha);
                    mObjSprites.add(spirit);
                }
            }
        }
    }

    private void init() {
        mSpriteScale = 5f;

        // alpha数值
        mSpriteAlpha = 1;
        // 旋转
        mSpriteAngleX = 0.01f; //-90
        mSpriteAngleY = 0;
        mSpriteAngleZ = 0;
    }


    @Override
    public void onDraw(MatrixState matrixState) {
        super.onDraw(matrixState);
        matrixState.scale(getSpriteScale(), getSpriteScale(), getSpriteScale());
//         旋转
//        matrixState.rotate(this.getSpriteAngleY(), 0, 1, 0); //y axis
//        matrixState.translate(0,0,angley);
//        matrixState.rotate(this.getSpriteAngleX(), 1, 0, 0);//x axis
//        matrixState.rotate(this.getSpriteAngleZ(), 0, 0, 1);//z axis
        //rotate
        if(mmode==0){
            matrixState.rotate(rotate_info[3],0,1,0);
            matrixState.rotate(rotate_info[3],0,0,1);
            matrixState.rotate(rotate_info[3],1,0,0);
        }
        else if(mmode == 1) //yaw
        {
            matrixState.rotate(rotate_info[3],0,1,0);
//            Log.i("PrintDemo", Arrays.toString(matrixState.getMMatrix()));
//            matrixState.rotate(90,0,0,1);
//            matrixState.rotate(90,1,0,0);
//            matrixState.rotate(90,0,0,1);
//            matrixState.rotate(90,1,0,0);
        }
        else if(mmode == 2) //pitch
        {
            matrixState.rotate(rotate_info[2],0,0,1);
        }
        else if(mmode == 3)
        {
            matrixState.rotate(rotate_info[1],1,0,0);
        }
        else
        {
            matrixState.rotate2(rotate_info[3],0,1,0);
//        matrixState.rotate2(rotate_info[2],0,0,1);
//        matrixState.rotate2(rotate_info[1],1,0,0);
        }


        Log.v("value",String.valueOf(mObjSprites.size()));
        // 绘制
        for (int i = 0; i < mObjSprites.size(); i++) {

            GLEntity sprite = mObjSprites.get(i);
            sprite.onDraw(matrixState);
        }
    }

}