package com.example.pcdalao.transfun;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.example.pcdalao.transfun.base.BaseActivity;


public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected int getContentView(){
        return R.layout.activity_main;
    }
}
