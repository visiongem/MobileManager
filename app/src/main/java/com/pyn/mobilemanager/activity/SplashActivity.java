package com.pyn.mobilemanager.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.pyn.mobilemanager.R;
import com.pyn.mobilemanager.domain.UpdateInfo;
import com.pyn.mobilemanager.engine.DownLoadFileTask;
import com.pyn.mobilemanager.engine.UpdateInfoService;

import java.io.File;

/**
 * 欢迎界面
 */
public class SplashActivity extends BasicActivity {

    private TextView mTvVersion;
    private ProgressDialog mPd;
    private String mVersion; // 版本号
    private UpdateInfo mUpdateInfo; // 更新版本信息的实体类
    private SharedPreferences sp;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 判断服务器版本号与客户端版本号是否相同
            if (isNeedUpdate(mVersion)) {
                showUpdateDialog();
            } else {
                Toast.makeText(SplashActivity.this, "未检测到新版本，进入主界面", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // 解决4.0中访问网络不能在主线程中进行代码
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork() // 这里可以替换为detectAll()
                // 就包括了磁盘读写和网络I/O
                .penaltyLog() // 打印logcat，通过文件保存相应的log
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects() // 探测SQLite数据库操作
                .detectLeakedClosableObjects().penaltyLog() // 打印logcat
                .penaltyDeath().build());

        super.onCreate(savedInstanceState);
        // 取消标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash);
        initViews();
        init();
        // 完成窗体的全屏显示
        // 取消掉状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    /**
     * 初始化
     */
    private void init() {
        mPd = new ProgressDialog(this);
        mPd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL); // 设置进度条的类型
        mPd.setMessage("正在下载..."); // 设置进度条显示内容
        mVersion = getVersion();

        // 让当前的activity延时1.5秒钟 检查更新
        new Thread() {
            public void run() {
                super.run();
                try {
                    sleep(1500);
                    mHandler.sendEmptyMessage(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

        mTvVersion.setText(mVersion);
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initViews() {
        mTvVersion = (TextView) findViewById(R.id.splash_tv_version);
    }

    /**
     * 显示更新对话框的方法
     */
    private void showUpdateDialog() {
        AlertDialog.Builder builder = new Builder(this); // 创建一个对话框对象
        builder.setTitle("升级提醒"); // 设置对话框标题
        builder.setMessage(mUpdateInfo.getDescription()); // 设置对话框内容
        builder.setCancelable(false); // 让用户不能取消掉对话框
        builder.setPositiveButton("立即更新", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) { // 确定按钮的监听器
                if (Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)) { // 检测内存卡是否可用，若可用的话
                    DownLoadFileThreadTask task = new DownLoadFileThreadTask(
                            mUpdateInfo.getApkUrl(),
                            "/sdcard/MobileManager.apk"); // 则下载文件
                    mPd.show(); // 使进度条显示出来
                    new Thread(task).start(); // 启动线程
                } else {
                    Toast.makeText(getApplicationContext(), "sd卡不可用", Toast.LENGTH_SHORT).show(); // 内存卡不可用的话，显示一条不可用的消息
                    loadMain(); // 直接 进入主界面
                }
            }
        });

        builder.setNegativeButton("取消", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) { // 取消按钮的监听器
                Toast.makeText(getApplicationContext(), "取消升级, 进入应用主界面", Toast.LENGTH_SHORT).show();
                loadMain(); // 用户点击取消的话直接进入主界面
            }
        });
        builder.create().show(); // 创建对话框并显示出来
    }

    /**
     * @param versionText 当前用户的版本信息
     * @return
     */
    private boolean isNeedUpdate(String versionText) {
        try {
            UpdateInfoService service = new UpdateInfoService(
                    getApplicationContext()); // 创建更新信息服务对象
            mUpdateInfo = service.getUpdateInfo(R.string.updateurl); // 得到服务器中的更新信息
            String version = mUpdateInfo.getVersion(); // 得到服务器更新信息中的版本号
            if (version.equals(versionText)) { // 若服务器中的版本号很客户端的版本号相同，则不需要更新
                loadMain(); // 进入主界面
                return false;
            } else { // 若服务器中的版本号很客户端的版本号不相同，则有更新
                return true;
            }
        } catch (Exception e) { // 　异常处理
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "检测新版本出现异常,进入主界面", Toast.LENGTH_SHORT).show();
            loadMain(); // 进入主界面
            return false;
        }
    }

    /**
     * 获取当前应用程序的版本号
     *
     * @return
     */
    private String getVersion() {
        try {
            // 包管理服务，获取packagemanager的实例
            PackageManager manager = getPackageManager();
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "版本号未知";
        }
    }

    /**
     * 启动主界面
     */
    private void loadMain() {
        Intent intent = new Intent(this, MainActivity.class); // 启动主界面的Intent
        startActivity(intent); // 启动intent
        finish(); // 把当前activity从任务栈里面移除
    }

    /**
     * 安装apk
     */
    private void install(File file) {
        Intent intent = new Intent(); // 创建一个意图
        intent.setAction(Intent.ACTION_VIEW); // 为意图设置action属性
        // 设置打开的文件，设置intent的data和Type属性，这里apk的MIME类型是"application/vnd.android.package-archive"
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        finish(); // 把当前activity从任务栈里面移除
        startActivity(intent); // 启动意图
    }

    private class DownLoadFileThreadTask implements Runnable {

        private String path; // 服务器路径
        private String filePath; // 本地文件路径

        public DownLoadFileThreadTask(String path, String filePath) { // 构造方法
            this.path = path;
            this.filePath = filePath;
        }

        @Override
        public void run() {
            try {
                File file = DownLoadFileTask.getFile(path, filePath, mPd);
                mPd.dismiss(); // 使进度条消失
                install(file); // 　安装
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "下载文件失败", Toast.LENGTH_SHORT).show();
                mPd.dismiss(); // 使进度条消失
                loadMain(); // 进入主界面
            }
        }
    }
}
