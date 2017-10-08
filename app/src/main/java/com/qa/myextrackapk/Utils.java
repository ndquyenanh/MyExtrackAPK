package com.qa.myextrackapk;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sev_user on 18-Mar-15.
 */
public class Utils {

    public static List<AppDetail> getAppDetails(Context mContext) {
        List<AppDetail> appDetails = new ArrayList<>();

        PackageManager manager = mContext.getPackageManager();
        File file;
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> infos = manager.queryIntentActivities(intent, 0);
        for (ResolveInfo info : infos) {

            file = new File(info.activityInfo.applicationInfo.publicSourceDir);
            String path = info.activityInfo.applicationInfo.publicSourceDir;
            String pkg = info.activityInfo.applicationInfo.packageName;
            AppDetail appDetail = new AppDetail(info.loadLabel(manager).toString(), file.length(), info.activityInfo.loadIcon(manager), path, pkg);
            appDetails.add(appDetail);
        }

        return appDetails;
    }

    public static void showDialog(Context context, String title, String msg, Drawable icon,
                                  DialogInterface.OnClickListener onClickListener,
                                  DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(title).setMessage(msg).setIcon(icon).setPositiveButton("Yes", onClickListener)
                .setNegativeButton("No", null)
                .setNeutralButton("Share", listener);
        builder.show();
        builder = null;
    }
}
