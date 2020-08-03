package cn.wandersnail.commons.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

/**
 * date: 2020/7/22 10:03
 * author: zengfansheng
 */
public class BatteryOptimizationsUtils {
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean isIgnoringBatteryOptimizations(@NonNull Context context) {
        boolean isIgnoring = false;
        PowerManager powerManager = (PowerManager) context.getApplicationContext().getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            isIgnoring = powerManager.isIgnoringBatteryOptimizations(context.getPackageName());
        }
        return isIgnoring;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void requestIgnoreBatteryOptimizations(@NonNull Context context) {
        try {
            @SuppressLint("BatteryLife") Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void requestIgnoreBatteryOptimizations(@NonNull Activity activity, int requestCode) {
        try {
            @SuppressLint("BatteryLife") Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + activity.getPackageName()));
            activity.startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 跳转到指定应用的首页
     */
    private static void showActivity(@NonNull Context context, @NonNull String packageName) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        context.startActivity(intent);
    }

    /**
     * 跳转到指定应用的指定页面
     */
    private static void showActivity(@NonNull Context context, @NonNull String packageName, @NonNull String activityDir) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(packageName, activityDir));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void goHuaweiSetting(@NonNull Context context) {
        try {
            showActivity(context, "com.huawei.systemmanager",
                    "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity");
        } catch (Exception e) {
            try {
                showActivity(context, "com.huawei.systemmanager",
                        "com.huawei.systemmanager.optimize.bootstart.BootStartActivity");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void goXiaomiSetting(@NonNull Context context) {
        try {
            showActivity(context, "com.miui.securitycenter",
                    "com.miui.permcenter.autostart.AutoStartManagementActivity");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void goOPPOSetting(@NonNull Context context) {
        try {
            showActivity(context, "com.coloros.oppoguardelf", 
                    "com.coloros.powermanager.fuelgaue.PowerUsageModelActivity");
        } catch (Exception e) {
            try {
                showActivity(context, "com.coloros.phonemanager");
            } catch (Exception e1) {
                try {
                    showActivity(context, "com.oppo.safe");
                } catch (Exception e2) {
                    try {
                        showActivity(context, "com.coloros.oppoguardelf");
                    } catch (Exception e3) {
                        try {
                            showActivity(context, "com.coloros.safecenter");
                        } catch (Exception e4) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

    }

    public static void goVIVOSetting(@NonNull Context context) {
        try {
            showActivity(context, "com.iqoo.secure", 
                    "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity");
        } catch (Exception e) {
            try {
                showActivity(context, "com.iqoo.secure");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }        
    }

    public static void goMeizuSetting(@NonNull Context context) {
        try {
            showActivity(context, "com.meizu.safe", 
                    "com.meizu.safe.permission.SmartBGActivity");
        } catch (Exception e) {
            try {
                showActivity(context, "com.meizu.safe");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }        
    }

    public static void goSmartisanSetting(@NonNull Context context) {
        try {
            showActivity(context, "com.smartisanos.security");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void goSamsungSetting(@NonNull Context context) {
        try {
            showActivity(context, "com.samsung.android.sm_cn",
                    "com.samsung.android.sm_cn.app.dashboard.SmartManagerDashBoardActivity");
        } catch (Exception e) {
            try {
                showActivity(context, "com.samsung.android.sm",
                        "com.samsung.android.sm.app.dashboard.SmartManagerDashBoardActivity");
            } catch (Exception ex) {
                try {
                    showActivity(context, "com.samsung.android.sm_cn");
                } catch (Exception ex1) {
                    try {
                        showActivity(context, "com.samsung.android.sm");
                    } catch (Exception ex2) {
                        ex.printStackTrace();
                    }
                }
            }
        }        
    }
}
