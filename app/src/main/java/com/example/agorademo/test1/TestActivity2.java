package com.example.agorademo.test1;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.agorademo.R;

import io.agora.rtc2.ChannelMediaOptions;
import io.agora.rtc2.Constants;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.RtcEngineConfig;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import io.agora.rtc2.ChannelMediaOptions;
import io.agora.rtc2.Constants;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.RtcEngineConfig;

public class TestActivity2 extends AppCompatActivity {

    // 填写声网控制台中获取的 App ID
    private String appId = "823e9c477e9e4803bf7562f7030acc47";
    // 填写频道名
    private String channelName = "10000";
    // 填写声网控制台中生成的临时 Token
    private String token = "007eJxTYHiY0m12dKlrdPqE9X/m/a9rasy54MXftnPHyuMPC6Ptg6coMFgYGadaJpuYm6dapppYGBgnpZmbmhmlmRsYGyQmA8V/VPmlNwQyMmTemsnKyACBID4rg6EBEDAwAAATJiD2";
    private int uid = 10;
    private RtcEngine mRtcEngine;

    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        // 成功加入频道回调
        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            super.onJoinChannelSuccess(channel, uid, elapsed);
            runOnUiThread(() -> {
                Toast.makeText(TestActivity2.this, "Join channel success", Toast.LENGTH_SHORT).show();
            });
        }

        // 远端用户或主播加入当前频道回调
        @Override
        public void onUserJoined(int uid, int elapsed) {
            super.onUserJoined(uid, elapsed);
            runOnUiThread(() -> {
                Toast.makeText(TestActivity2.this, "User joined: " + uid, Toast.LENGTH_SHORT).show();
            });
        }

        // 远端用户或主播离开当前频道回调
        @Override
        public void onUserOffline(int uid, int reason) {
            super.onUserOffline(uid, reason);
            runOnUiThread(() -> {
                Toast.makeText(TestActivity2.this, "User offline: " + uid, Toast.LENGTH_SHORT).show();
            });
        }
    };

    private void initializeAndJoinChannel() {
        try {
            // 创建 RtcEngineConfig 对象，并进行配置
            RtcEngineConfig config = new RtcEngineConfig();
            config.mContext = getBaseContext();
            config.mAppId = appId;
            config.mEventHandler = mRtcEventHandler;
            // 创建并初始化 RtcEngine
            mRtcEngine = RtcEngine.create(config);
        } catch (Exception e) {
            throw new RuntimeException("Check the error.");
        }

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
        mRtcEngine.joinChannel(token, channelName, uid, options);
    }

    private static final int PERMISSION_REQ_ID = 22;

    // 获取体验实时音频互动所需的权限
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

    private boolean checkPermissions() {
        for (String permission : getRequiredPermissions()) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, permission);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);
        // 如果已经授权，则初始化 RtcEngine 并加入频道
        if (checkPermissions()) {
            initializeAndJoinChannel();
        } else {
            ActivityCompat.requestPermissions(this, getRequiredPermissions(), PERMISSION_REQ_ID);
        }
    }

    // 系统权限申请回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (checkPermissions()) {
            initializeAndJoinChannel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRtcEngine != null) {
            // 离开频道
            mRtcEngine.leaveChannel();
            mRtcEngine = null;
            // 销毁引擎
            RtcEngine.destroy();
        }
    }


}