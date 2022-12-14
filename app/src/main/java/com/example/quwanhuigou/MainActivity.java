package com.example.quwanhuigou;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.Settings;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.quwanhuigou.bean.BuyerNum;
import com.example.quwanhuigou.bean.Task;
import com.example.quwanhuigou.service.KeepAliveService;
import com.example.quwanhuigou.util.HttpClient;
import com.example.quwanhuigou.util.NotificationSetUtil;
import com.example.quwanhuigou.util.UpdateApk;
import com.example.quwanhuigou.util.WindowPermissionCheck;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.PostRequest;
import com.lzy.okgo.request.base.Request;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 停止接单取消所有网络请求
 * 远程公告、频率等
 * try catch
 * 多买号情况下，不选择买号接单的问题
 *------------------------------------
 * 这个模式都用这套代码
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText etUname,etPaw,etYj1;
    private TextView tvStart,tvStop,tvLog,tvBrow,tvAppDown,tvAppOpen,tvTitle;
    private Handler mHandler;
    private String Authorization;
    private String yqAuth;
    private List<String> yqList;
    private String tbId;
    /*
    接单成功音乐提示播放次数（3次）
    播放的次数是count+1次
     */
    private int count;
    private SharedPreferences userInfo;
    private int minPl;
    private int tbIndex;
    private Double minYj;
    List<Task> taskList;
    private int taskIndex;
    private SimpleDateFormat simpleDateFormat;
    private MyBroadcast myBroadcast;
    private AlertDialog dialog;
    private List<BuyerNum> buyerNumList;
    private String[] tbNameArr;
    private AlertDialog alertDialog2;
    private boolean isAuth = false;
    private Spinner sp1;
    private static String LOGIN_URL = "";
    private static String DOWNLOAD = "";
    private static int TQ;
    String[] ptAddress = new String[]{};
    private static String OPENURL = "";



    /**
     * 需要更改的地方：
     * 1、MainActivity
     * 2、build.gradle配置文件
     * 3、AndroidMainfest.xml文件
     * 4、Update文件
     * 5、改权限密码，趣玩去除了徒弟验证，招财没去除
     * 6、KeepAlive文件
     *
     */
    private static String PT_URL = "quWanHuiGou";
    private static String APK_PACKAGE = "com.lzm.qwhg";
    private static String TITLE = "趣玩惠购助手";
    private static String TI_SHI = "趣玩App未安装";
    private static String CHANNELID = "qwhgSuccess";
    private static String SUCCESS_TI_SHI = "趣玩惠购接单成功";
    private static int JIE_DAN_SUCCESS = R.raw.qw_success;
    private static int JIE_DAN_FAIL = R.raw.qw_fail;
    private static int ICON = R.mipmap.qw;
    private static String GUANG_BO = "com.gb.qwhg";
    private static String QUAN_XIAN_LOGIN = "/buyer/login/bingBuyersMobileForWx";
    private static String YAO_QING = "/buyer/invite/invite_list";
    private static String CHECK_ACCOUNT = "/buyer/taskrenew/findBuyerByBuyerId";
    private static String GET_TB = "/buyer/Improvenew/account_grab";
    private static String GET_TASK_LIST = "/buyer/grab_task/grabTaskList";
    private static String GET_TASK = "/buyer/grab_task/grabTask";
    private static String SETTING_ACCOUNT = "/buyer/Improvenew/set_default";
    private static String GET_TASK_ID = "/buyer/taskrenew/findTaskInfo";
    private static String GET_GUAN_JIAN_CI = "/buyer/grab_task/findNextInfo?taskId=";




//    private static String PT_URL = "zhaoCaiJinBao";
//    private static String APK_PACKAGE = "com.lzm.zcjb";
//    private static String TITLE = "招财进宝助手";
//    private static String TI_SHI = "招财进宝App未安装";
//    private static String CHANNELID = "zcjbSuccess";
//    private static String SUCCESS_TI_SHI = "招财进宝接单成功";
//    private static int JIE_DAN_SUCCESS = R.raw.zcjb_success;
//    private static int JIE_DAN_FAIL = R.raw.zcjb_fail;
//    private static int ICON = R.mipmap.zcjb;
//    private static String GUANG_BO = "com.gb.zcjb";
//    private static String QUAN_XIAN_LOGIN = "/fans/login";
//    private static String YAO_QING = "/fans/inviteInformation";
//    private static String CHECK_ACCOUNT = "/fans/fansInformation";
//    private static String GET_TB = "/fans/taskAccountListPage";
//    private static String GET_TASK_LIST = "/fans/formalTaskList";
//    private static String GET_TASK = "/fans/grabFormalTask";
//    private static String SETTING_ACCOUNT = "/fans/toggleDefaultInformation";
//    private static String GET_TASK_ID = "/fans/taskListPage";
//    private static String GET_GUAN_JIAN_CI = "/fans/taskStepsInformation?taskId=";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去掉标题栏
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, KeepAliveService.class);
        //启动保活服务
        startService(intent);
        ignoreBatteryOptimization();//忽略电池优化

        if(!checkFloatPermission(this)){
            //权限请求方法
            requestSettingCanDrawOverlays();
        }
        //获取平台登录地址
        getPtAddress(this);

        initView();
    }

    private void initView(){
        //检查更新
        UpdateApk.update(MainActivity.this);
        //是否开启通知权限
        openNotification();
        //是否开启悬浮窗权限
        WindowPermissionCheck.checkPermission(this);

        sp1 = findViewById(R.id.sp_1);
        etYj1 = findViewById(R.id.et_yj1);
        mHandler = new Handler();
        tvBrow = findViewById(R.id.tv_brow);
        etUname = findViewById(R.id.et_username);
        tvAppDown = findViewById(R.id.tv_appDown);
        tvAppOpen = findViewById(R.id.tv_appOpen);
        tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(TITLE);
        etPaw = findViewById(R.id.et_password);
        tvStart = findViewById(R.id.tv_start);
        tvStop = findViewById(R.id.tv_stop);
        tvLog = findViewById(R.id.tv_log);
        //设置textView为可滚动方式
        tvLog.setMovementMethod(ScrollingMovementMethod.getInstance());
        tvLog.setTextIsSelectable(true);
        buyerNumList = new ArrayList<>();

        simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        //读取用户信息
        getUserInfo();
        taskList = new ArrayList<>();
        tvStart.setOnClickListener(this);
        tvStop.setOnClickListener(this);
        tvBrow.setOnClickListener(this);
        tvAppOpen.setOnClickListener(this);
        tvAppDown.setOnClickListener(this);
        //注册一个广播   第一步
        myBroadcast = new MyBroadcast();
        //筛选Intent，我这个页面要接收什么广播，我可以筛选，只处理哪些
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(GUANG_BO);  //只接收这个广播
        //注册广播，接收广播
        registerReceiver(myBroadcast,intentFilter);
        yqList = new ArrayList<>();

        tvLog.setText("不建议卡佣金"+"\n");
        tvLog.append("----------------------------------"+"\n");
        tvLog.append("找不到商品怎么办?"+"\n");
        tvLog.append("通过任务全标题或者店铺名找到该商品，浏览1分钟左右，返回淘宝重新按照任务关键字搜索，商品就会显示在前面"+"\n");

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_start:

                /**
                 * 不设置为null的话，第一次接单停止后，第二次接单，tbID有值
                 */
                tbId = null;
                isAuth = false;
                String yj1 = etYj1.getText().toString().trim();

                if("".equals(yj1)){
                    etYj1.setText("1");
                }

                if(Integer.parseInt(etYj1.getText().toString().trim()) > 4){
                    etYj1.setText("4");
                }

                minYj = Double.parseDouble(yj1);

                /*
                先清除掉之前的Handler中的Runnable，不然会和之前的任务一起执行多个
                 */
                mHandler.removeCallbacksAndMessages(null);

                if(ptAddress.length == 0){
                    sendLog("获取最新网址中...");
                }else {
//                    if(yqList.size() == 0){
//                        getRegUser();
//                    }else {
//                        login(etUname.getText().toString().trim(),etPaw.getText().toString().trim());
//                    }
                    login(etUname.getText().toString().trim(),etPaw.getText().toString().trim());
                }
                break;
            case R.id.tv_stop:
                stop();
                break;
            case R.id.tv_appDown:
                Uri uri = Uri.parse(DOWNLOAD);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case R.id.tv_appOpen:
                openApp(APK_PACKAGE);
                break;
            case R.id.tv_brow:
                browOpen();
                break;
        }

    }

    private void browOpen(){
        if(LOGIN_URL == ""){
            sendLog("获取最新网址中...");
        }else {
            Uri uri = Uri.parse(LOGIN_URL);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }

    /**
     * 重写activity的onKeyDown方法，点击返回键后不销毁activity
     * 可参考：https://blog.csdn.net/qq_36713816/article/details/71511860
     * 另外一种解决办法：重写onBackPressed方法，里面不加任务内容，屏蔽返回按钮
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }





    /**
     * 登录--权限
     */
    private void getRegUser(){

        tvLog.setText("权限获取中，请勿重复点击..."+"\n");

        HttpClient.getInstance().post(QUAN_XIAN_LOGIN, LOGIN_URL)
                .params("mobile","15610701514")
//                .params("password","lzm112233")
                .params("password","Lzm112233..")
                .headers("Content-Type","application/json;charset=UTF-8")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject loginJsonObj = JSONObject.parseObject(response.body());
                            if("200".equals(loginJsonObj.getString("code"))) {
                                //获取token
                                yqAuth = loginJsonObj.getJSONObject("data").getString("Authorization");
                                getYq1();
                                return;
                            }
                            sendLog(loginJsonObj.getString("message"));
                        }catch (Exception e){
                            sendLog(e.getMessage());
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        sendBroadcast("平台出错啦~");
                    }
                });


    }


    /**
     * 直接邀请
     */
    private void getYq1(){
        HttpClient.getInstance().post(YAO_QING, LOGIN_URL)
                .params("px","1")
                .params("pz","700")
                .params("type","1")
                .headers("Authorization",yqAuth)
                .headers("Content-Type","application/json;charset=UTF-8")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        JSONObject obj = JSONObject.parseObject(response.body());
                        JSONArray userList = obj.getJSONObject("data").getJSONArray("res");
                        for (int i = 0;i<userList.size();i++){
                            yqList.add(userList.getJSONObject(i).getString("buyers_mobile"));
                        }
                        System.out.println("一级邀请"+yqList.size());
                        getYq2();
                    }
                });
    }

    /**
     * 间接邀请
     */
    private void getYq2(){
        HttpClient.getInstance().post(YAO_QING, LOGIN_URL)
                .params("px","1")
                .params("pz","900")
                .params("type","2")
                .headers("Authorization",yqAuth)
                .headers("Content-Type","application/json;charset=UTF-8")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        JSONObject obj = JSONObject.parseObject(response.body());
                        JSONArray userList = obj.getJSONObject("data").getJSONArray("res");
                        for (int i = 0;i<userList.size();i++){
                            yqList.add(userList.getJSONObject(i).getString("buyers_mobile"));
                        }
//                        System.out.println("总邀请"+yqList.size());
                        login(etUname.getText().toString().trim(),etPaw.getText().toString().trim());
                    }
                });
    }

    /**
     * 登录平台
     * @param username
     * @param password
     */
    private void login(String username, String password){
        tvLog.setText(new SimpleDateFormat("HH:mm:ss").format(new Date()) + ": 正在登陆中..."+"\n");

        HttpClient.getInstance().post(QUAN_XIAN_LOGIN, LOGIN_URL)
                .params("mobile",username)
                .params("password",password)
                .headers("Content-Type","application/json;charset=UTF-8")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        JSONObject loginJsonObj = JSONObject.parseObject(response.body());
                        if("200".equals(loginJsonObj.getString("code"))){
//                            if(!yqList.contains(username)){
//                                sendLog("无权限使用~");
//                                return;
//                            }
                            //保存账号和密码
                            saveUserInfo(username,password,
                                    etYj1.getText().toString().trim()
                            );
                            sendBroadcast("登录成功");
                            //获取token
                            Authorization = loginJsonObj.getJSONObject("data").getString("Authorization");
                            //检查账号是否拉黑
                            checkAccout();
                            return;
                        }
                        sendBroadcast(loginJsonObj.getString("message"));
                    }
                });
    }



    /**
     * 检查账号是否被拉黑
     */
    private void checkAccout() {
        HttpClient.getInstance().post(CHECK_ACCOUNT,LOGIN_URL)
                .headers("Content-Type","application/json;charset=UTF-8")
                .headers("Authorization",Authorization)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        JSONObject checkObj = JSONObject.parseObject(response.body());
                        int userStatus = checkObj.getJSONObject("data").getInteger("buyerStatus");
                        if(1 == userStatus){
                            sendBroadcast("请联系师傅！");
                            return;
                        }else if(0 == userStatus){
                            //正常
                            sendBroadcast("账号状态正常");
                        }
                        getTbInfo();
                    }
                });
    }



    private void getTbInfo() {
        HttpClient.getInstance().post(GET_TB,LOGIN_URL)
                .headers("Content-Type","application/json;charset=UTF-8")
                .headers("Authorization",Authorization)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        JSONObject obj = JSONObject.parseObject(response.body());
                        JSONArray tbArr = obj.getJSONArray("data");
                        if(tbArr.size() == 0){
                            sendLog("无可用淘宝号");
                            return;
                        }
                        buyerNumList.clear();
                        tbNameArr = new String[tbArr.size()+1];
                        tbNameArr[0] = "自动切换买号";
                        for (int i = 0; i < tbArr.size(); i++) {
                            JSONObject tbInfo = tbArr.getJSONObject(i);
                            String tbId = tbInfo.getString("examine_id");
                            String tbName = tbInfo.getString("examine_accounts");
                            tbNameArr[i+1] = tbName;
                            buyerNumList.add(new BuyerNum(tbId,tbName));
                        }
                        sendLog("获取到"+tbArr.size()+"个可用淘宝号");
                        showSingleAlertDialog();
                    }
                });
    }


    public void showSingleAlertDialog(){

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("请选择接单淘宝号");
        alertBuilder.setCancelable(false); //触摸窗口边界以外是否关闭窗口，设置 false
        alertBuilder.setSingleChoiceItems( tbNameArr, -1, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(DialogInterface arg0, int index) {
                if("自动切换买号".equals(tbNameArr[index])){
                    isAuth = true;
                    sendLog("将使用 "+tbNameArr[index]+" 进行接单");
                    //默认用第一个
                    tbId = buyerNumList.get(0).getId();
                }else {
                    isAuth = false;
                    //根据选择的淘宝名获取淘宝id
                    List<BuyerNum> buyerNum = buyerNumList.stream().
                            filter(p -> p.getName().equals(tbNameArr[index])).collect(Collectors.toList());
                    tbId = buyerNum.get(0).getId();
                    sendLog("将使用 "+buyerNum.get(0).getName()+" 进行接单");
                }
            }
        });
        alertBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                //TODO 业务逻辑代码
                if(!isAuth && tbId == null){
                    sendBroadcast("未选择接单淘宝号");
                    return;
                }
                setDefault();
                // 关闭提示框
                alertDialog2.dismiss();
            }
        });
        alertDialog2 = alertBuilder.create();
        alertDialog2.show();
    }


    /**
     * 获取任务列表
     */
    private void getTaskList() {
        taskIndex = 0;
        HttpClient.getInstance().post(GET_TASK_LIST,LOGIN_URL)
                .headers("Content-Type","application/json;charset=UTF-8")
                .headers("Authorization",Authorization)
                .execute(new StringCallback() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onSuccess(Response<String> response) {
                        JSONObject taskListObj = JSONObject.parseObject(response.body());
                        /**
                         * {"code":500,"msg":"您还有待处理的收货任务，请先完成！","data":[],"message":"您还有待处理的收货任务，请先完成！"}
                         */
                        if("200".equals(taskListObj.getString("code"))){
                            JSONArray taskListArr = taskListObj.getJSONArray("data");
                            sendBroadcast("获取到"+taskListArr.size()+"个任务");
                            if(taskListArr.size() == 0){
                                jieDan();
                                return;
                            }
                            taskList.clear();
                            for (int i = 0; i < taskListArr.size(); i++) {
                                //获取任务编号
                                JSONObject taskObj = taskListArr.getJSONObject(i);
                                // 佣金
                                Double comm = Double.parseDouble(taskObj.getString("commissionBuyers"));
                                if(comm >= minYj){
                                    Task task = new Task();
                                    task.setTaskNo(taskObj.getString("taskNo"));
                                    task.setStartTime(taskObj.getString("startTime"));
                                    task.setCommissionBuyers(comm);
                                    task.setGoodsName(taskObj.getString("goodsName"));
                                    taskList.add(task);
                                }
                            }
                            sendBroadcast("符合筛选条件的任务"+taskList.size()+"个");

                            if(taskList.size() == 0){
                                jieDan();
                                return;
                            }
                            //按照时间排序  不管是Date、String、Long类型的日期都可以排序，无需转换    升序
                            taskList.sort((t1, t2) -> t1.getStartTime().compareTo(t2.getStartTime()));
                            System.out.println("排序后"+taskList);
                            jieXi();
                        }else if("请再次刷新当前页面！".equals(taskListObj.getString("msg")) || "操作繁忙，请稍后重试！".equals(taskListObj.getString("msg"))){
                            sendBroadcast(taskListObj.getString("msg"));
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getTaskList();
                                }
                            }, 30000);
                        }else if("您今日接到的任务已经达到上限!".equals(taskListObj.getString("msg"))){

                            for (int i = 0; i < buyerNumList.size(); i++) {
                                if(tbId.equals(buyerNumList.get(i).getId())){
                                    if(isAuth){
                                        sendBroadcast(buyerNumList.get(i).getName()+"：日已接满,已自动过滤!");
                                        buyerNumList.remove(i);
                                        break;
                                    }else {
                                        sendBroadcast(buyerNumList.get(i).getName()+"：日已接满!");
                                        playMusic(JIE_DAN_FAIL,3000,0);
                                        return;
                                    }
                                }
                            }
                            if(buyerNumList.size() ==0){
                                sendLog("恭喜您，全部买号已接满");
                                playMusic(JIE_DAN_FAIL,3000,0);
                                return;
                            }
                            //能走到这里来一定是auth
                            tbId = buyerNumList.get(0).getId();
                            setDefault();
                        } else {
                            sendBroadcast(taskListObj.getString("msg"));
                            playMusic(JIE_DAN_FAIL,3000,0);
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        sendBroadcast("getTaskList出错啦,正在重试~");
                        sendBroadcast(response.getException().toString());

                        mHandler.postDelayed(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void run() {
                                getTaskList();
                            }
                        }, 10000);
                    }
                });
    }


    /**
     * 走到这里说明一定有任务存在
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void jieXi(){
        //说明任务列表已经全部轮回了一遍
        if(taskIndex == taskList.size()){
            jieDan();
            return;
        }

        /**
         * 如果任务时间在当前系统时间之前，则直接领取即可
         */
        Task task = taskList.get(taskIndex);
        if(!DateCompare(task.getStartTime())){
            sendBroadcast("订单开始时间： "+task.getStartTime().substring(11)+" ,请耐心等待...");
            if(task.getStartTime().contains("00:00")){
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getTask(task.getTaskNo(),task.getGoodsName());
                    }
                }, 1500);
                return;
            }

            getTask(task.getTaskNo(),task.getGoodsName());
        }else {
            //计算任务开始时间到当前时间的毫秒数，倒计时结束后执行接取任务功能
            long systime = new Date().getTime();//当前系统时间

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long taskDate = 0;
            try {
                sendBroadcast("订单开始时间： "+task.getStartTime().substring(11)+" ,请耐心等待...");
                taskDate = format.parse(task.getStartTime()).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long c = taskDate-systime-TQ ;
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getTask(task.getTaskNo(),task.getGoodsName());
                }
            }, c);
        }

    }



    public void jieDan(){
        //没可用的号一定走不到这里来
        if(isAuth){
            //选择了一个买号  or  多个买号中只有一个可接的账号
            if(buyerNumList.size() == 1){
                mHandler.postDelayed(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void run() {
                        getTaskList();
                    }
                }, minPl);
            }else {
                if(tbIndex < buyerNumList.size()){
                    tbId = buyerNumList.get(tbIndex).getId();
                }else {
                    tbIndex = 0;
                    tbId = buyerNumList.get(tbIndex).getId();
                }
                tbIndex++;

                mHandler.postDelayed(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void run() {
                        setDefault();
                    }
                },minPl);
            }
        }else {
            mHandler.postDelayed(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void run() {
                    setDefault();
                }
            }, minPl);
        }
    }



    /**
     * 日期比较
     * @param startDate
     * @return
     */
    public boolean DateCompare(String startDate){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();

        Date sd1 = null;
        Date sd2 = null;
        try {
            sd1 = df.parse(df.format(date));
            sd2=df.parse(startDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return sd2.after(sd1);  //sd2在sd1之后
    }



    /**
     * 领取任务
     */
    public void getTask(String taskNo,String taskTitle){

        HttpClient.getInstance().post(GET_TASK,LOGIN_URL)
                .params("taskNo",taskNo)
                .headers("Content-Type","application/json;charset=UTF-8")
                .headers("Authorization",Authorization)
                .execute(new StringCallback() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onSuccess(Response<String> response) {

                        JSONObject taskObj = JSONObject.parseObject(response.body());
                        if("200".equals(taskObj.getString("code"))){
                            sendBroadcast("恭喜您，接单成功！");
                            getTaskId(taskTitle);
                            playMusic(JIE_DAN_SUCCESS,3000,2);
                            //停止接单
                            mHandler.removeCallbacksAndMessages(null);
                            return;
                        }
                        //操作进行中，请勿重复点击
                        sendBroadcast(taskObj.getString("message"));
                        taskIndex++;
                        jieXi();
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        sendBroadcast("接取任务出错啦,正在重试~");
                        sendBroadcast(response.getException().toString());

                        mHandler.postDelayed(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void run() {
                                jieXi();
                            }
                        }, 5000);
                    }
                });
    }



    /**
     * 设置默认淘宝号
     */
    private void setDefault() {
        HttpClient.getInstance().post(SETTING_ACCOUNT,LOGIN_URL)
                .params("examine_id",tbId)
                .headers("Content-Type","application/json;charset=UTF-8")
                .headers("Authorization",Authorization)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        JSONObject obj = JSONObject.parseObject(response.body());
                        if("200".equals(obj.getString("code"))){
                            getTaskList();
                        }
                    }
                });
    }



    private void getTaskId(String taskTitle){
        HttpClient.getInstance().post(GET_TASK_ID, LOGIN_URL)
                .params("_index","1")
                .params("_size","10000")
                .params("status","1")
                .headers("Authorization",Authorization)
                .headers("Content-Type","application/json;charset=UTF-8")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        JSONObject obj = JSONObject.parseObject(response.body());
                        String taskId = obj.getJSONObject("data").getJSONArray("records").getJSONObject(0).getString("taskId");
                        getGuanJianCi(taskId,taskTitle);
                    }
                });
    }




    private void getGuanJianCi(String taskId,String taskTitle){
        HttpClient.getInstance().get(GET_GUAN_JIAN_CI+taskId, LOGIN_URL)
                .headers("Authorization",Authorization)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        JSONObject obj = JSONObject.parseObject(response.body());
                        if("ok".equals(obj.getJSONObject("data").getString("errorCode"))){
                            String dianPuName = obj.getJSONObject("data").getString("shopTrueName");
                            String guanJianZi = obj.getJSONObject("data").getJSONObject("taskInfo").getString("keyWord");
                            String zhaoCha = obj.getJSONObject("data").getJSONObject("taskInfo").getString("goodsHideWord");
                            sendBroadcast("-------------------------");
                            sendBroadcast("搜索关键字："+guanJianZi);
                            sendBroadcast("-------------------------");
                            sendBroadcast("商品找茬提示："+zhaoCha);
                            sendBroadcast("-------------------------");
                            sendBroadcast("店铺名："+dianPuName);
                            sendBroadcast("-------------------------");
                            sendBroadcast("任务全标题："+taskTitle);
                            receiveSuccess(dianPuName);
                        }else {
                            receiveSuccess("接到一个审核任务!");
                        }
                    }
                });
    }




    /**
     * 停止接单
     */
    public void stop(){
        OkGo.getInstance().cancelAll();
        //Handler中已经提供了一个removeCallbacksAndMessages去清除Message和Runnable
        mHandler.removeCallbacksAndMessages(null);
        //  sendLog(tvLog,"已停止接单");
        sendBroadcast("已停止接单");
    }


    /**
     * 接单成功后通知铃声
     * @param voiceResId 音频文件
     * @param milliseconds 需要震动的毫秒数
     */
    private void playMusic(int voiceResId, long milliseconds,int total){

        count = total;//不然会循环播放

        //播放语音
        MediaPlayer player = MediaPlayer.create(MainActivity.this, voiceResId);
        player.start();
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                //播放完成事件
                if(count != 0){
                    player.start();
                }
                count --;
            }
        });

        //震动
        Vibrator vib = (Vibrator) this.getSystemService(Service.VIBRATOR_SERVICE);
        //延迟的毫秒数
        vib.vibrate(milliseconds);
    }


    /**
     * 发送广播
     * @param log
     */
    public void sendBroadcast(String log){
        //发送一个广播，广播行为名字叫com.lzm.update
        Intent intent = new Intent(GUANG_BO);
        intent.putExtra("log",log);
        sendBroadcast(intent);
    }



    /**
     * 弹窗公告
     */
    public void announcementDialog(String[] lesson){

        // String[] lesson = new String[]{"此助手针对平台专属定制优化，接单效率比其他接单助手效率都要强，建议使用此助手接单","","接单成功后,会显示搜索关键字，商品全标题，店铺名，找茬提示","","只提供参考作用，禁止直接搜索店铺和商品全标题购买商品，客服发现会拉黑"};

        dialog = new AlertDialog
                .Builder(this)
                .setTitle("公告")
                .setCancelable(false) //触摸窗口边界以外是否关闭窗口，设置 false
                .setPositiveButton("我知道了", null)
                //.setMessage("")
                .setItems(lesson,null)
                .create();
        dialog.show();
    }




    /**
     * 日志更新
     * @param log
     */
    public void sendLog(String log){
        tvLog.append(simpleDateFormat.format(new Date()) + ": "+log);
        tvLog.append("\n");
        int offset = tvLog.getLineCount() * tvLog.getLineHeight();
        if (offset > tvLog.getHeight()) {
            tvLog.scrollTo(0, offset - tvLog.getHeight());
        }
    }

    private void openNotification(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //判断是否需要开启通知栏功能
            NotificationSetUtil.OpenNotificationSetting(this);
        }
    }


    /**
     * 忽略电池优化
     */
    public void ignoreBatteryOptimization() {
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        boolean hasIgnored = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            hasIgnored = powerManager.isIgnoringBatteryOptimizations(getPackageName());
            //  判断当前APP是否有加入电池优化的白名单，如果没有，弹出加入电池优化的白名单的设置对话框。
            if(!hasIgnored) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:"+getPackageName()));
                startActivity(intent);
            }
        }
    }


    private void openApp(String packName){
        PackageManager packageManager = this.getPackageManager();
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packName);
        List<ResolveInfo> apps = packageManager.queryIntentActivities(resolveIntent, 0);
        if (apps.size() == 0) {
            Toast.makeText(this, TI_SHI, Toast.LENGTH_LONG).show();
            return;
        }
        ResolveInfo resolveInfo = apps.iterator().next();
        if (resolveInfo != null) {
            String className = resolveInfo.activityInfo.name;
            Intent intent2 = new Intent(Intent.ACTION_MAIN);
            intent2.addCategory(Intent.CATEGORY_LAUNCHER);
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName cn = new ComponentName(packName, className);
            intent2.setComponent(cn);
            this.startActivity(intent2);
        }
    }


    //判断是否开启悬浮窗权限   context可以用你的Activity.或者tiis
    public static boolean checkFloatPermission(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            return true;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            try {
                Class cls = Class.forName("android.content.Context");
                Field declaredField = cls.getDeclaredField("APP_OPS_SERVICE");
                declaredField.setAccessible(true);
                Object obj = declaredField.get(cls);
                if (!(obj instanceof String)) {
                    return false;
                }
                String str2 = (String) obj;
                obj = cls.getMethod("getSystemService", String.class).invoke(context, str2);
                cls = Class.forName("android.app.AppOpsManager");
                Field declaredField2 = cls.getDeclaredField("MODE_ALLOWED");
                declaredField2.setAccessible(true);
                Method checkOp = cls.getMethod("checkOp", Integer.TYPE, Integer.TYPE, String.class);
                int result = (Integer) checkOp.invoke(obj, 24, Binder.getCallingUid(), context.getPackageName());
                return result == declaredField2.getInt(cls);
            } catch (Exception e) {
                return false;
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                AppOpsManager appOpsMgr = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                if (appOpsMgr == null)
                    return false;
                int mode = appOpsMgr.checkOpNoThrow("android:system_alert_window", android.os.Process.myUid(), context
                        .getPackageName());
                return mode == AppOpsManager.MODE_ALLOWED || mode == AppOpsManager.MODE_IGNORED;
            } else {
                return Settings.canDrawOverlays(context);
            }
        }
    }



    public void getPtAddress(Context context){

        HttpClient.getInstance().get("/ptVersion/checkUpdate","http://47.94.255.103")
                .params("ptName",PT_URL)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        JSONObject ptAddrObj = JSONObject.parseObject(response.body());
                        String url = ptAddrObj.getString("ptUrl");
                        if(url == null){
                            tvLog.setText("请联系软件开发者更新平台网址信息~"+"\n");
                            return;
                        }
                        ptAddress = url.split(",");

                        //也可以自定义填充后的字体样式
                        ArrayAdapter<String> adapter2 =
                                new ArrayAdapter<String>(context,R.layout.support_simple_spinner_dropdown_item,ptAddress);
                        sp1.setAdapter(adapter2);

                        sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                //view：spinner内填充的样式，R.layout.support_simple_spinner_dropdown_item
                                //id是值所在行的位置，一般来说与positin一致
                                //根据索引位置获取选择的值   position：索引位置
                                LOGIN_URL = parent.getItemAtPosition(position).toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                        //公告弹窗
                        String[] gongGao = ptAddrObj.getString("ptAnnoun").split(";");
                        announcementDialog(gongGao);

                        TQ = ptAddrObj.getInteger("tq");
                        DOWNLOAD = ptAddrObj.getString("apkDownload");
                        minPl = Integer.parseInt(ptAddrObj.getString("pinLv"));
                        OPENURL = ptAddrObj.getString("openUrl");
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        sendLog("服务器出现问题啦~");
                    }
                });
    }





    /**
     * 接单成功执行逻辑
     */
    @SuppressLint("WrongConstant")
    protected void receiveSuccess(String dianPuName){
        //前台通知的id名，任意
        String channelId = CHANNELID;
        //前台通知的名称，任意
        String channelName = "接单成功状态栏通知";
        //发送通知的等级，此处为高，根据业务情况而定
        int importance = NotificationManager.IMPORTANCE_HIGH;

        // 2. 获取系统的通知管理器
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        // 3. 创建NotificationChannel(这里传入的channelId要和创建的通知channelId一致，才能为指定通知建立通知渠道)
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(channelId,channelName, importance);
            channel.setLightColor(Color.BLUE);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(channel);
        }
        //点击通知时可进入的Activity
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0);
        // 1. 创建一个通知(必须设置channelId)
        Notification notification = new NotificationCompat.Builder(this,channelId)
                .setContentTitle(SUCCESS_TI_SHI)
                .setContentText("店铺名:"+dianPuName)
                .setSmallIcon(ICON)
                .setContentIntent(pendingIntent)//点击通知进入Activity
                .setPriority(NotificationCompat.PRIORITY_MAX) //设置通知的优先级为最大
                .setCategory(Notification.CATEGORY_TRANSPORT) //设置通知类别
                .setVisibility(Notification.VISIBILITY_PUBLIC)  //控制锁定屏幕中通知的可见详情级别
                .build();

        // 4. 发送通知
        notificationManager.notify(2, notification);
    }


    //权限打开
    private void requestSettingCanDrawOverlays() {
        int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt >= Build.VERSION_CODES.O) {//8.0以上
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            startActivityForResult(intent, 1);
        } else if (sdkInt >= Build.VERSION_CODES.M) {//6.0-8.0
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 1);
        } else {//4.4-6.0以下
            //无需处理了
        }
    }


    public void onResume() {
        super.onResume();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //移除标记为id的通知 (只是针对当前Context下的所有Notification)
        notificationManager.cancel(2);
        //移除所有通知
        //notificationManager.cancelAll();

    }



    /**
     * 保存用户信息
     */
    private void saveUserInfo(String username,String password,String yj1){

        userInfo = getSharedPreferences("userData", MODE_PRIVATE);
        SharedPreferences.Editor editor = userInfo.edit();//获取Editor
        //得到Editor后，写入需要保存的数据
        editor.putString("username",username);
        editor.putString("password", password);
        editor.putString("yj1", yj1);
        editor.commit();//提交修改

    }

    /**
     * 读取用户信息
     */
    private void getUserInfo(){
        userInfo = getSharedPreferences("userData", MODE_PRIVATE);
        String username = userInfo.getString("username", null);//读取username
        String passwrod = userInfo.getString("password", null);//读取password
        String yj1 = userInfo.getString("yj1",null);
        if(username!=null && passwrod!=null){
            etUname.setText(username);
            etPaw.setText(passwrod);
            etYj1.setText(yj1);
        }
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁广播，避免内存泄露
        unregisterReceiver(myBroadcast);
        //关闭弹窗，不然会 报错（虽然不影响使用）
        dialog.dismiss();
    }


    /**
     * 广播接收者  用来接收所有的广播
     * 第二步：第一步接收到之后怎么处理
     */
    private class MyBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String log = intent.getStringExtra("log");
            //方法的作用，广播接收者，接收到广播后要干啥
            switch (intent.getAction().equals(GUANG_BO)?1:0){  //判断action是什么,当action是com.lzm.update的时候
//                case "com.lzm.update":  //如果是这个广播
                case 1:  //如果是这个广播
                    //sendLog(log);
                    System.out.println("日志更新"+log);
                    tvLog.append(simpleDateFormat.format(new Date()) + ": "+log);
                    tvLog.append("\n");
                    int offset = tvLog.getLineCount() * tvLog.getLineHeight();
                    if (offset > tvLog.getHeight()) {
                        tvLog.scrollTo(0, offset - tvLog.getHeight());
                    }
                    break;
            }
        }
    }

}