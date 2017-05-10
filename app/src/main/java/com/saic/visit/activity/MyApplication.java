package com.saic.visit.activity;

import android.app.Application;

import com.saic.visit.utils.excelutil.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 1 on 2017/3/17.
 */

public class MyApplication extends Application {

    public static String path ="/sdcard/凯迪拉克/凯迪拉克评估/";
    public static String path1 = "";
    public static String path2 ="/sdcard/凯迪拉克/";
    public static String path3 ="/sdcard/凯迪拉克缩略图/";
    public static String JingXiaoShang = "";
    public static String JingXiaoCode = "";
    public static List<Order> excelList = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
