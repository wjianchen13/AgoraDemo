package com.example.agorademo.test1;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.agorademo.R;

import io.agora.rtc2.ChannelMediaOptions;
import io.agora.rtc2.ClientRoleOptions;
import io.agora.rtc2.Constants;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.RtcEngineConfig;

public class TestActivity1 extends AppCompatActivity {

    private final int PERMISSIONS_CODE = 1; // 权限请求代码
    // 填写声网控制台中获取的 App ID
    private String appId = "823e9c477e9e4803bf7562f7030acc47";
    // 填写频道名
    private String channelName = "10000";
    // 填写声网控制台中生成的临时 Token
    private String token = "007eJxTYHiY0m12dKlrdPqE9X/m/a9rasy54MXftnPHyuMPC6Ptg6coMFgYGadaJpuYm6dapppYGBgnpZmbmhmlmRsYGyQmA8V/VPmlNwQyMmTemsnKyACBID4rg6EBEDAwAAATJiD2";
    private int uid = 20;
//    private String token = "007eJxTYCg7V3177cyHJ4N3u4vPYuY06f2seTOl9ueFSd5dAvx3fyUoMFgYGadaJpuYm6dapppYGBgnpZmbmhmlmRsYGyQmA8Xz2NzTGgIZGVbqf2RkZIBAEJ+VwdAACBgYAKexHvM=";
//    private int uid = 20;
    private EditText edtvUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test1);
        edtvUid = findViewById(R.id.edtv_test);
    }

    /**
     * 声音测试
     * @param v
     */
    public void onTest1(View v) {
        checkPermissions();
    }

    /**
     * 检查权限
     * @param
     * @return void
     */
    private void checkPermissions() {
        boolean flag = checkPermissions(getRequiredPermissions());
        if(!flag) {
            requestPermission(getRequiredPermissions());
        } else {
            Toast.makeText(this, "请求权限成功", Toast.LENGTH_SHORT).show();
        }
    }


    private String[] getRequiredPermissions(){
        // 判断 targetSDKVersion 31 及以上时所需的权限
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            return new String[]{
                    Manifest.permission.RECORD_AUDIO, // 录音权限
                    Manifest.permission.READ_PHONE_STATE, // 读取电话状态权限
                    Manifest.permission.BLUETOOTH_CONNECT // 蓝牙连接权限
            };
        } else {
            return new String[]{
                    Manifest.permission.RECORD_AUDIO,
            };
        }
    }

    /**
     * 1.检查权限
     * @param
     * @return 权限是否允许标志
     */
    private boolean checkPermissions(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 2.如果没有权限，动态请求权限
     * @param
     * @return void
     */
    private void requestPermission(String[] permissions) {
        ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_CODE);
    }

    /**
     * 3.处理返回结果
     * @param grantResults 返回对应权限请求数组，如果成功，则为PERMISSION_GRANTED，否则是PERMISSION_DENIED
     * @return void
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_CODE) {
            boolean grantedFlag = true;
            // 判断是否所有的权限都已经授予了
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    grantedFlag = false;
                    break;
                }
            }
        }
    }

    /**
     * 初始化引擎
     * @param v
     */
    public void onTest2(View v) {
        initEngine();
    }

    private RtcEngine mRtcEngine;

    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {

        // 成功加入频道回调
        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            super.onJoinChannelSuccess(channel, uid, elapsed);
            runOnUiThread(() -> {
                Toast.makeText(TestActivity1.this, "Join channel success", Toast.LENGTH_SHORT).show();
            });
        }

        // 远端用户或主播加入当前频道回调
        @Override
        public void onUserJoined(int uid, int elapsed) {
            super.onUserJoined(uid, elapsed);
            runOnUiThread(() -> {
                Toast.makeText(TestActivity1.this, "User joined: " + uid, Toast.LENGTH_SHORT).show();
            });
        }

        // 远端用户或主播离开当前频道回调
        @Override
        public void onUserOffline(int uid, int reason) {
            super.onUserOffline(uid, reason);
            runOnUiThread(() -> {
                Toast.makeText(TestActivity1.this, "User offline: " + uid, Toast.LENGTH_SHORT).show();
            });
        }

    };

    /**
     * 初始化引擎
     */
    private void initEngine() {
        try {
            // 创建 RtcEngineConfig 对象，并进行配置
            RtcEngineConfig config = new RtcEngineConfig();
            config.mContext = getBaseContext();
            config.mAppId = appId;
            config.mEventHandler = mRtcEventHandler;
            // 创建并初始化 RtcEngine
            mRtcEngine = RtcEngine.create(config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加入频道并发布音频流
     * @param v
     */
    public void onTest3(View v) {
        joinChannel();
    }

    private void joinChannel() {
        // 创建 ChannelMediaOptions 对象，并进行配置
        ChannelMediaOptions options = new ChannelMediaOptions();
        // 设置用户角色为 BROADCASTER (主播) 或 AUDIENCE (观众)
        options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER;
        // 设置频道场景为 BROADCASTING (直播场景)
        options.channelProfile = Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;
        // 发布麦克风采集的音频
        options.publishMicrophoneTrack = true;
        // 自动订阅所有音频流
        options.autoSubscribeAudio = true;
        // 使用临时 Token 和频道名加入频道，uid 为 0 表示引擎内部随机生成用户名
        // 成功后会触发 onJoinChannelSuccess 回调
        try {
            uid = Integer.valueOf(edtvUid.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mRtcEngine.joinChannel(token, channelName, uid, options);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyEngine();
    }

    /**
     * 销毁引擎
     * @param v
     */
    public void onTest4(View v) {
        destroyEngine();
    }

    private void destroyEngine() {
        if (mRtcEngine != null) {
            // 离开频道
            mRtcEngine.leaveChannel();
            mRtcEngine = null;
            // 销毁引擎
            RtcEngine.destroy();
        }
    }

}