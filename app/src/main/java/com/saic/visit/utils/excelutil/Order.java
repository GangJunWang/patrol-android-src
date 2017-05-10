package com.saic.visit.utils.excelutil;

import java.io.Serializable;

/**
 * Created by 1 on 2017/3/17.
 */

public class Order implements Serializable {

    public String xuhao;
    public String id;
    public String restPhone;
    public String istrfa;
    public String restName;
    public String receiverAddr;
    public String beizhu;


    public Order(String xuhao,String id, String restPhone, String istrfa, String restName, String receiverAddr, String beizhu) {
        this.xuhao = xuhao;
        this.id = id;
        this.restPhone = restPhone;
        this.istrfa = istrfa;
        this.restName = restName;
        this.receiverAddr = receiverAddr;
        this.beizhu = beizhu;
    }

   /* public Order(JSONObject obj) {
        this.id = obj.optString("order_number");
        this.restPhone = obj.optString("Rphone");
        this.restName = obj.optString("Rname");
        this.receiverAddr = obj.optString("receiver_address");
    }*/
}
