package io.virtualapp.dev;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.lody.virtual.client.core.InstallStrategy;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.remote.InstallResult;

/**
 * author: weishu on 18/2/23.
 */

public class CmdReceiver extends BroadcastReceiver {

    private static final String ACTION = "chao.my.va.exposed.CMD";
    private static final String KEY_CMD = "cmd";
    private static final String KEY_PKG = "pkg";

    private static final String CMD_UPDATE = "update";
    private static final String CMD_REBOOT = "reboot";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (!ACTION.equalsIgnoreCase(action)) {
            return;
        }

        String cmd = intent.getStringExtra(KEY_CMD);
        if (TextUtils.isEmpty(cmd)) {
            showTips(context, "No cmd found!");
            return;
        }

        if (CMD_REBOOT.equalsIgnoreCase(cmd)) {
            VirtualCore.get().killAllApps();
            showTips(context, "Reboot Success!!");
            return;
        }

        if (CMD_UPDATE.equalsIgnoreCase(cmd)) {
            String pkg = intent.getStringExtra(KEY_PKG);
            if (TextUtils.isEmpty(pkg)) {
                showTips(context, "Please give me the update package!!");
                return;
            }

            PackageManager packageManager = context.getPackageManager();
            if (packageManager == null) {
                showTips(context, "system error, update failed!");
                return;
            }

            try {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(pkg, 0);
                String apkPath = applicationInfo.sourceDir;
                InstallResult installResult = VirtualCore.get().installPackage(apkPath, InstallStrategy.COMPARE_VERSION);
                if (installResult.isSuccess) {
                    if (installResult.isUpdate) {
                        showTips(context, "Update " + pkg + " Success!!");
                    }
                } else {
                    showTips(context, "Update " + pkg + " failed: " + installResult.error);
                }
            } catch (PackageManager.NameNotFoundException e) {
                showTips(context, "Can not found " + pkg + " outside!");
            }
        }
    }

    private void showTips(Context context, String tips) {
        Toast.makeText(context, tips, Toast.LENGTH_SHORT).show();

    }
}
