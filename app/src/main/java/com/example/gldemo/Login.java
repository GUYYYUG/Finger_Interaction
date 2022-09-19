package com.example.gldemo;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Login extends AppCompatActivity {
    private String nn[]={"lct","pzs"};
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        Button login = (Button) findViewById(R.id.Sign_in);
        Button register = (Button) findViewById(R.id.Sign_up);
        EditText name = (EditText) findViewById(R.id.Username);
        EditText password = (EditText) findViewById(R.id.Password);
        TextView forget = (TextView) findViewById(R.id.Forgetpassword);
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        //取消标题
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
////取消状态栏
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        ImageButton imbtn1 = (ImageButton)findViewById(R.id.imbtn1);
        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if (view.getId()==R.id.Sign_in){
                    String uname = name.getText().toString();
                    String upass = password.getText().toString();
                    int ff = 0;
                    if(uname.equals("lct")){
                        ff = 1;
                    }
                    if(ff==0){
                        Toast.makeText(Login.this, "username not registered！", Toast.LENGTH_SHORT).show();
                    }
                    else if((!upass.equals("123456"))){
                        Toast.makeText(Login.this, "wrong password！", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Intent intent = new Intent();
                        intent.setClass(Login.this, MainActivity.class);
                        intent.putExtra("flag", 3);
                        startActivity(intent);

                        Login.this.finish();
                    }
                    Log.v("name",String.valueOf(uname.length()));
//                    if(ff==0){
//                        Toast.makeText(Login.this, "用户名错误！", Toast.LENGTH_SHORT).show();
//                    }
//                    else if((upass!="1")){
//                        Toast.makeText(Login.this, "密码错误！", Toast.LENGTH_SHORT).show();
//                    }
//                    else {
//                        imbtn1.setBackgroundResource(R.drawable.run2);
//                    ((ImageButton)view).setImageDrawable(getResources().getDrawable(R.drawable.run2));
//                        Intent intent = new Intent();
//                        intent.setClass(Login.this, MainActivity.class);
//                        intent.putExtra("flag", 3);
//                        startActivity(intent);
//
//                        Login.this.finish();
//                    }
                }
            }
        });
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("username: ", s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("password: ", s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }
}
