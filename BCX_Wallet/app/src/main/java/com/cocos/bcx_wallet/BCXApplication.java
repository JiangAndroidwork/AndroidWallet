package com.cocos.bcx_wallet;

import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

import com.alibaba.android.arouter.launcher.ARouter;
import com.cocos.bcx_sdk.bcx_api.CocosBcxApiWrapper;
import com.cocos.bcx_sdk.bcx_callback.IBcxCallBack;
import com.cocos.bcx_wallet.launch.WelcomeActivity;
import com.cocos.library_base.base.BaseApplication;
import com.cocos.library_base.config.ModuleLifecycleConfig;
import com.cocos.library_base.crash.CaocConfig;
import com.cocos.library_base.crash.DefaultErrorActivity;
import com.cocos.library_base.entity.BaseResult;
import com.cocos.library_base.entity.NodeInfoModel;
import com.cocos.library_base.global.SPKeyGlobal;
import com.cocos.library_base.utils.KLog;
import com.cocos.library_base.utils.SPUtils;
import com.cocos.library_base.utils.multi_language.LocalManageUtil;
import com.cocos.library_base.utils.node.NodeConnectUtil;
import com.cocos.library_base.utils.singleton.GsonSingleInstance;

import java.util.Arrays;
import java.util.List;


/**
 * @author ningkang.guo
 * @Date 2019/1/28
 */
public class BCXApplication extends BaseApplication {


    @Override
    protected void attachBaseContext(Context base) {
        //保存系统选择语言
        LocalManageUtil.saveSystemCurrentLanguage(base);
        super.attachBaseContext(base);
    }

    /**
     * 改变系统语言时会调用到
     *
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //保存系统选择语言
        LocalManageUtil.onConfigurationChanged(getApplicationContext());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //是否开启日志打印
        KLog.init(BuildConfig.IS_TEST_ENV);
        //初始化 ARouter
        if (BuildConfig.DEBUG) { //开启InstantRun之后，一定要在ARouter.init之前调用openDebug
            ARouter.openDebug();
            ARouter.openLog();
        }
        ARouter.init(this);
        ModuleLifecycleConfig.getInstance().initModuleAhead(this); //初始化组件(靠前)
        ModuleLifecycleConfig.getInstance().initModuleLow(this);     //初始化组件(靠后)
        // 初始化sdk
        CocosBcxApiWrapper.getBcxInstance().init(this);
        NodeConnectUtil.requestNodeListData(this);

//
//        //初始化工具类
//        List<String> mListNode = Arrays.asList("ws://192.168.90.46:8149", "ws://192.168.90.46:8149");
//        String faucetUrl = "http://47.93.62.96:4000";
//        String chainId = "dc57c58b0366a06b33615a10fb624c380557f3642278d51910580ade3ab487fe";
//        String coreAsset = "COCOS";
//        boolean isOpenLog = true;
//        CocosBcxApiWrapper.getBcxInstance().init(this);
//        CocosBcxApiWrapper.getBcxInstance().connect(this, chainId, mListNode, faucetUrl, coreAsset, isOpenLog,
//                new IBcxCallBack() {
//                    @Override
//                    public void onReceiveValue(String value) {
//                        Log.i("initBcxSdk", value);
//                        BaseResult resultEntity = GsonSingleInstance.getGsonInstance().fromJson(value, BaseResult.class);
//                        if (resultEntity.isSuccess()) {
//                            NodeInfoModel.DataBean nodeInfoModel = new NodeInfoModel.DataBean();
//                            nodeInfoModel.chainId = "dc57c58b0366a06b33615a10fb624c380557f3642278d51910580ade3ab487fe";
//                            nodeInfoModel.faucetUrl = "http://47.93.62.96:4000";
//                            nodeInfoModel.coreAsset = "COCOS";
//                            nodeInfoModel.ws = "ws://192.168.90.46:8149";
//                            SPUtils.putObject(BCXApplication.this, SPKeyGlobal.NODE_WORK_MODEL_SELECTED, nodeInfoModel);
//                        }
//                    }
//                });

        //配置全局异常崩溃操作
        CaocConfig.Builder.create()
                .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT)
                .enabled(true) //是否启动全局异常捕获
                .showRestartButton(true) //是否显示重启按钮
                .showErrorDetails(BuildConfig.IS_TEST_ENV)
                .trackActivities(true) //是否跟踪Activity
                .restartActivity(WelcomeActivity.class) //重新启动后的activity
                .errorActivity(DefaultErrorActivity.class) //崩溃后的错误activity
                .apply();
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        ARouter.getInstance().destroy();
    }
}
