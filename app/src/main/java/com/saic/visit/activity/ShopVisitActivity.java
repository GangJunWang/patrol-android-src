package com.saic.visit.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.google.gson.Gson;
import com.saic.visit.R;
import com.saic.visit.common.BaseActivity;
import com.saic.visit.constant.Constants;
import com.saic.visit.http.ResponseSupport;
import com.saic.visit.http.VolleyRequestManager;
import com.saic.visit.model.CatalogVo;
import com.saic.visit.model.LogRequest;
import com.saic.visit.model.RequestVoRequest;
import com.saic.visit.model.TaskDetailResponse;
import com.saic.visit.utils.DialogUtil;
import com.saic.visit.utils.ModelAdapter;
import com.saic.visit.utils.NetWorkUtil;
import com.saic.visit.utils.SharePreferenceUtil;
import com.saic.visit.utils.ToastUtil;
import com.saic.visit.utils.excelutil.ExcelUtil;
import com.saic.visit.utils.excelutil.Order;
import com.saic.visit.utils.superme.WeiboDialogUtils;

import org.xutils.DbManager;
import org.xutils.ex.DbException;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by liuhui on 2016/5/17.
 */

/**
 * 2017/3/14  展示图片信息界面
 */
public class ShopVisitActivity extends BaseActivity {

    @Bind(R.id.rela_back)
    RelativeLayout relaBack;
    @Bind(R.id.txt_title)
    TextView txtTitle;
    @Bind(R.id.img_right)
    ImageView imgRight;
    @Bind(R.id.rela_right)
    RelativeLayout relaRight;
    @Bind(R.id.list_visit)
    ListView listVisit;
    @Bind(R.id.visit_no)
    TextView visitNo;
    @Bind(R.id.report)
    Button report;
    @Bind(R.id.confirm)
    Button confirm;
    @Bind(R.id.layout_visit)
    LinearLayout layoutVisit;
    private ModelAdapter adapter;
    private TaskDetailResponse taskListResponse;
    private TaskDetailResponse taskType;
    private boolean flag;
    private String contactPhone;
    public static final String INTENT_EXTRA_PHONE = "contactPhone";
    private String taskId;
    private Gson gson;
    private List<CatalogVo> delItems, addItems, modifyItems;
    private Handler hand;
    private Dialog loadingDialog;
    private DbManager db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_shop_visit);
        ButterKnife.bind(this);
        db = MyApplication.initDbSqlite();
    }


    @Override
    protected void init(Bundle savedInstanceState) {
        txtTitle.setText(getResources().getText(R.string.visit_title));
        imgRight.setImageResource(R.drawable.shop_visit_add);

        initData();
    }

    @OnClick({R.id.rela_back, R.id.txt_title, R.id.rela_right, R.id.report, R.id.confirm})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.rela_back:
                MyApplication.excelList.clear();
                MyApplication.excelList2.clear();
                try {
                    db.delete(Order.class);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                setResult(20);
                finish();
                ShopVisitActivity.this.overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
      /*          if (null == MyApplication.excelList || 0 == MyApplication.excelList.size()) {
                    setResult(20);
                    finish();
                    ShopVisitActivity.this.overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                } else {
                    ToastUtil.show(ShopVisitActivity.this, "请先导出数据");
                }*/
                break;
            case R.id.txt_title:
                break;
            /*
             */
            case R.id.rela_right:
                intent = new Intent(ShopVisitActivity.this, AddVisitActivity.class);
                startActivityForResult(intent, 100);
                ShopVisitActivity.this.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            /**
             * 一键导出
             */
            case R.id.report:

                new AlertDialog.Builder(this).setTitle("确认导出吗？")
                        .setIcon(R.mipmap.app_icon)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 点击“确认”后的操作
                                if (null == MyApplication.excelList || 0 == MyApplication.excelList.size()) {
                                    ToastUtil.show(ShopVisitActivity.this, "暂无数据");
                                } else {
                                    report.setBackgroundResource(R.drawable.enble_button);
                                    report.setEnabled(false);
                                    loadingDialog = WeiboDialogUtils.createLoadingDialog(ShopVisitActivity.this, "正在导出.....");
                                    /**
                                     * 应该先创建Exel表
                                     */
                                    new Thread() {

                                        private List<Order> all;


                                        @Override
                                        public void run() {
                                            super.run();
                                            try {
                                                all = db.findAll(Order.class);
                                                if (null != all) {
                                                    String yyyyMMdd = new DateFormat().format("yyyyMMdd", Calendar.getInstance(Locale.CHINA)) + "";
                                                    ExcelUtil.writeExcel(ShopVisitActivity.this, all, MyApplication.JingXiaoCode + "_" + MyApplication.JingXiaoShang + "_" + yyyyMMdd);
                                                } else {
                                                    String yyyyMMdd = new DateFormat().format("yyyyMMdd", Calendar.getInstance(Locale.CHINA)) + "";
                                                    ExcelUtil.writeExcel(ShopVisitActivity.this, MyApplication.excelList, MyApplication.JingXiaoCode + "_" + MyApplication.JingXiaoShang + "_" + yyyyMMdd);
                                                }


                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            /**
                                             * 在这里压缩文件导出
                                             */

                        /*    if (!"".equals(MyApplication.path2) && null != MyApplication.path2){
                                CompressUtil.zip(MyApplication.path2);
                            }else {
                                ToastUtil.show(ShopVisitActivity.this,"请重新取证");
                            }*/

                                            //  MyApplication.excelList = null;
                           /* for (int i = 0; i < MyApplication.excelList.size();) {
                                MyApplication.excelList.remove(i);
                            }*/
                                            MyApplication.excelList.clear();
                                            MyApplication.excelList2.clear();
                                            hand.sendEmptyMessage(0);
                                            try {
                                                db.delete(Order.class);
                                            } catch (DbException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    }.start();


                                    hand = new Handler() {

                                        @Override
                                        public void handleMessage(Message msg) {
                                            if (!NetWorkUtil.isNetworkConnected(ShopVisitActivity.this)) {
                                                ToastUtil.show(ShopVisitActivity.this, "网络连接不可用");
                                                return;
                                            }
                                            DialogUtil.showLoading(ShopVisitActivity.this);
                                            RequestVoRequest lur = new RequestVoRequest("Task/commitSurvey");
                                            gson = new Gson();
                                            taskId = SharePreferenceUtil.getStringValue(Constants.TASKID, ShopVisitActivity.this);
                                            lur.setTaskId(Long.parseLong(taskId));
                                            String data = SharePreferenceUtil.getStringValue(Constants.TASKDETAIL + taskId, ShopVisitActivity.this);
                                            String type = SharePreferenceUtil.getStringValue(Constants.RECEPTIONIST + taskId, ShopVisitActivity.this);
                                            taskListResponse = gson.fromJson(data, TaskDetailResponse.class);
                                            taskType = gson.fromJson(type, TaskDetailResponse.class);
                                            lur.setSurveyVo(taskListResponse.survey);
                                            lur.setSupervisorId(SharePreferenceUtil.getLongValue(Constants.SUPERVISORID, ShopVisitActivity.this));

                                            if (taskType == null) {
                                                taskType = new TaskDetailResponse();
                                            }
                                            lur.setReceptionistTypeVos(taskType.receptionistTypes);
                                            gson.toJson(lur);
                                            VolleyRequestManager.getInstance(ShopVisitActivity.this).startHttpPostRequest(this, lur, ResponseSupport.class, new Response.ListenerV2<ResponseSupport>() {
                                                @Override
                                                public void onResponse(ResponseSupport response, Map<String, String> headers) throws Exception {
                                                    if (!VolleyRequestManager.realResponseResultSupport(ShopVisitActivity.this, response, null, true))
                                                        return;
                                                    new Thread() {
                                                        public void run() {
                                                            uploadLog();
                                                        }
                                                    }.start();
                                                }
                                            }, volleyError);
                                            loadingDialog.cancel();
                                        }
                                    };

                                }

                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 点击“返回”后的操作,这里不设置没有任何操作
                            }
                        }).show();


                /**
                 * 总店确认
                 */

                break;
            case R.id.confirm:
                intent = new Intent(ShopVisitActivity.this, TaskConfirmActivity.class);
                intent.putExtra(INTENT_EXTRA_PHONE, contactPhone);
                startActivityForResult(intent, 20);
                ShopVisitActivity.this.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == 100) {
            initData();
        }
        if (requestCode == 10 && resultCode == 10) {
            setResult(10);
            finish();
            ShopVisitActivity.this.overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
        }
        if (requestCode == 20 && resultCode == 20) {
            setResult(10);
            finish();
            ShopVisitActivity.this.overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
        }
    }

    private void initData() {
        String taskId = SharePreferenceUtil.getStringValue(Constants.TASKID, ShopVisitActivity.this);
        Gson gsonss = new Gson();
        String data = SharePreferenceUtil.getStringValue(Constants.TASKDETAIL + taskId, ShopVisitActivity.this);
        taskListResponse = gsonss.fromJson(data, TaskDetailResponse.class);
        int type = SharePreferenceUtil.getIntValue(Constants.TYPE + taskId, ShopVisitActivity.this);
        contactPhone = taskListResponse.dealer.getContactPhone();
        if (type == 1) {
            flag = false;
        } else {
            flag = true;
        }
        adapter = new ModelAdapter(this, taskListResponse.survey.getModuleVos(), !flag);

        listVisit.setAdapter(adapter);
        if (flag) {
            if (taskListResponse.survey.getModuleVos().size() > 0) {
                visitNo.setVisibility(View.GONE);
                layoutVisit.setVisibility(View.VISIBLE);
                if (taskListResponse.survey.isCommit()) {
                    confirm.setBackgroundResource(R.drawable.blue_white_button);
                    confirm.setTextColor(getResources().getColor(R.color.main_blue));
                    confirm.setEnabled(true);
                } else {
                    confirm.setBackgroundResource(R.drawable.gray_white_button);
                    confirm.setTextColor(getResources().getColor(R.color.gray));
                    confirm.setEnabled(false);
                }
            } else {
                visitNo.setVisibility(View.VISIBLE);
                layoutVisit.setVisibility(View.GONE);
            }
        } else {
            visitNo.setVisibility(View.GONE);
            layoutVisit.setVisibility(View.GONE);
            relaRight.setVisibility(View.INVISIBLE);

        }

    }

/*    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(20);
            finish();
            ShopVisitActivity.this.overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
        }
        return false;

    }*/

    public void uploadLog() {

        LogRequest lr = new LogRequest("log/saveOperatorLog");
        lr.setSupervisorId(SharePreferenceUtil.getLongValue(Constants.SUPERVISORID, ShopVisitActivity.this));
        lr.setSurveyId(Long.parseLong(taskId));
        LogRequest saveLog = gson.fromJson(SharePreferenceUtil.getStringValue(Constants.ITEMDETAILS + taskId, ShopVisitActivity.this), LogRequest.class);
        if (saveLog == null || saveLog.getOperatorLogDetails() == null) {
            SharePreferenceUtil.save(Constants.RECEPTIONIST + taskId, "", ShopVisitActivity.this);
            SharePreferenceUtil.save(Constants.RESPONSE + taskId, "", ShopVisitActivity.this);
            SharePreferenceUtil.save(Constants.ADDQUESTION + taskId, "", ShopVisitActivity.this);
            SharePreferenceUtil.save(Constants.TASKDETAIL + taskId, "", ShopVisitActivity.this);
            SharePreferenceUtil.save(Constants.TASKDEL + taskId, "", ShopVisitActivity.this);
            SharePreferenceUtil.save(Constants.ITEMDETAILS + taskId, "", ShopVisitActivity.this);
            Intent intent = new Intent(ShopVisitActivity.this, SuccessActivity.class);
            intent.putExtra(INTENT_EXTRA_PHONE, contactPhone);
            startActivityForResult(intent, 10);
            ShopVisitActivity.this.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            return;
        }
        lr.setOperatorLogDetails(saveLog.getOperatorLogDetails());
        VolleyRequestManager.getInstance(this).startHttpPostRequest(this, lr, ResponseSupport.class, new Response.ListenerV2<ResponseSupport>() {
            @Override
            public void onResponse(ResponseSupport response, Map<String, String> headers) throws Exception {
                if (!VolleyRequestManager.realResponseResultSupport(ShopVisitActivity.this, response, null, true))
                    return;
                SharePreferenceUtil.save(Constants.RECEPTIONIST + taskId, "", ShopVisitActivity.this);
                SharePreferenceUtil.save(Constants.RESPONSE + taskId, "", ShopVisitActivity.this);
                SharePreferenceUtil.save(Constants.ADDQUESTION + taskId, "", ShopVisitActivity.this);
                SharePreferenceUtil.save(Constants.TASKDETAIL + taskId, "", ShopVisitActivity.this);
                SharePreferenceUtil.save(Constants.TASKDEL + taskId, "", ShopVisitActivity.this);
                SharePreferenceUtil.save(Constants.ITEMDETAILS + taskId, "", ShopVisitActivity.this);
                Intent intent = new Intent(ShopVisitActivity.this, SuccessActivity.class);
                intent.putExtra(INTENT_EXTRA_PHONE, contactPhone);
                startActivityForResult(intent, 10);
                ShopVisitActivity.this.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

            }
        }, volleyError);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            MyApplication.excelList.clear();
            MyApplication.excelList2.clear();

        /*    try {
                db.delete(Order.class);
            } catch (DbException e) {
                e.printStackTrace();
            }*/
            setResult(20);
            finish();
            ShopVisitActivity.this.overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
            return false;

        }
        return super.onKeyDown(keyCode, event);
    }

}