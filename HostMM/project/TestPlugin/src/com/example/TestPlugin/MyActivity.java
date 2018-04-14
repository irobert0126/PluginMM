package com.example.TestPlugin;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PermissionInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.morgoo.helper.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class MyActivity extends AppCompatActivity {

    String ACTION_c2_INSTALL_APK = "com.example.action.ACTION_INSTALL_APK";
    public ABroadcastReceiver mABroadcastReceiver = new ABroadcastReceiver();
    public class ABroadcastReceiver extends BroadcastReceiver {

        void registerReceiver(Context con) {
            IntentFilter f = new IntentFilter();
            f.addAction(ACTION_c2_INSTALL_APK);
            con.registerReceiver(this, f);
        }

        void unregisterReceiver(Context con) {
            con.unregisterReceiver(this);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            android.util.Log.d("tluo", "ABroadcastReceiver Triggered: " + intent.getAction() + "|" + intent.getStringExtra("APK_PATH"));
            if (ACTION_c2_INSTALL_APK.equals(intent.getAction())) {
                ComponentName comp = new ComponentName(context.getPackageName(), C2Service.class.getName());
                intent.setComponent(comp);
                //context.startService(intent);
                /*try {
                    String c2Command = "com.example.INSTALL_APK";
                    Intent msgIntent = new Intent(context, C2Service.class);
                    msgIntent.setAction(c2Command);
                    msgIntent.putExtra("APK_PATH", intent.getData().getPath());
                    context.startService(msgIntent);

                } catch (Exception e) {
                    e.printStackTrace();
                }*/
            } else {
            }
        }
    }

    private static final String TAG = "MyActivity";


    private ViewPager mViewPager;
    private FragmentStatePagerAdapter mFragmentStatePagerAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new InstalledFragment();
            } else {
                return new ApkFragment();
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return "已安装";
            } else {
                return "待安装";
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mFragmentStatePagerAdapter);
        mABroadcastReceiver.registerReceiver(getApplication());
//        getPerms();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void getPerms() {
        final PackageManager pm = getPackageManager();
        final List<PackageInfo> pkgs = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS);
        new Thread() {
            @Override
            public void run() {
                try {
                    StringBuilder sb = new StringBuilder();
                    Log.e(TAG, "===========包权限start===========");
                    Set<String> ps = new TreeSet<String>();
                    for (PackageInfo pkg : pkgs) {
                        if (pkg.permissions != null && pkg.permissions.length > 0) {
                            for (PermissionInfo permission : pkg.permissions) {
                                ps.add(permission.name);
                            }
                        }
                    }
                    for (String p : ps) {
                        PermissionInfo permission = pm.getPermissionInfo(p, 0);
                        PackageInfo pkg = pm.getPackageInfo(permission.packageName, 0);
                        String re = String.format("<uses-permission android:name=\"%s\"/>", permission.name);
                        String ms = String.format("%s,%s,%s,%s,%s,%s,%s,%s", permission.packageName, pkg.applicationInfo.loadLabel(pm), permission.name, permission.group, permission.protectionLevel, permission.loadLabel(pm), permission.loadDescription(pm), re);
                        sb.append(ms).append("\r\n");
                        Log.e(TAG, "packageName=%s, name=%s group=%s protectionLevel=%s", permission.packageName, permission.name, permission.group, permission.protectionLevel);
                    }

                    FileWriter w = null;
                    try {
                        w = new FileWriter(new File(Environment.getExternalStorageDirectory(), "per.txt"));
                        w.write(sb.toString());

                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (w != null) {
                            try {
                                w.close();
                            } catch (IOException e) {
                            }
                        }
                    }
                    Log.e(TAG, "===========包权限end===========");
                } catch (NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


}
