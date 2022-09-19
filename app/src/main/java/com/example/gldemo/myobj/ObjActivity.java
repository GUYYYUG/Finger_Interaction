package com.example.gldemo.myobj;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gldemo.R;
import com.example.gldemo.plane.PlaneGlSurfaceView;

public class ObjActivity extends AppCompatActivity {

    private PlaneGlSurfaceView mGLView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGLView = (PlaneGlSurfaceView) findViewById(R.id.glsv_plane);
        GokuRenderer gokuRenderer = new GokuRenderer(mGLView,0);
        mGLView.setRenderer(gokuRenderer);
        // 渲染模式(被动渲染)
        mGLView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mGLView.setOnTouchListener(gokuRenderer.getTouchEventListener());
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
