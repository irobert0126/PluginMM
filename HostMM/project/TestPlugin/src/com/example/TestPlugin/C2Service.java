package com.example.TestPlugin;

import android.app.IntentService;
import android.content.Intent;
import android.os.RemoteException;
import android.util.Log;

import com.morgoo.droidplugin.pm.PluginManager;
import com.morgoo.helper.compat.PackageManagerCompat;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class C2Service extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_c2_INSTALL_APK = "com.example.action.ACTION_INSTALL_APK";
    private static final String ACTION_c2_UNINSTALL_APK  = "com.example.UNINSTALL_APK";

    // TODO: Rename parameters
    private static final String ACTION_INSTALL_APK_EXTRA_PARAM1 = "APK_PATH";
    private static final String ACTION_UNINSTALL_APK_EXTRA_PARAM1 = "PACKAGENAME";


    public C2Service() {
        super("C2Service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("tluo","C2Service onHandleIntent: " + intent.getAction());
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_c2_INSTALL_APK.equals(action)) {
                final String param1 = intent.getStringExtra(ACTION_INSTALL_APK_EXTRA_PARAM1);
                handleActionInstallApk(param1);
            } else if (ACTION_c2_UNINSTALL_APK.equals(action)) {
                final String param2 = intent.getStringExtra(ACTION_UNINSTALL_APK_EXTRA_PARAM1);
                handleActionUnInstallApk(param2);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionInstallApk(String apk_path) {
        //Log.d("tluo", "Environment:" + Environment.getExternalStorageState());
        Log.d("tluo", "TODO: Installing APK @ " + apk_path);
        try {
            final int re = PluginManager.getInstance().installPackage(apk_path, 0);
            switch (re) {
                case PluginManager.INSTALL_FAILED_NO_REQUESTEDPERMISSION:
                    Log.d("tluo", "安装失败，文件请求的权限太多");
                    break;
                case PackageManagerCompat.INSTALL_FAILED_NOT_SUPPORT_ABI:
                    Log.d("tluo", "宿主不支持插件的abi环境，可能宿主运行时为64位，但插件只支持32位");
                    break;
                case PackageManagerCompat.INSTALL_SUCCEEDED:
                    Log.d("tluo", "[" + apk_path + "] 安装完成");
                    break;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionUnInstallApk(String param1) {
        Log.d("tluo", "TODO: Handle action Baz");
    }
}
