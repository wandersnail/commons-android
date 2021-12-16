package cn.wandersnail.commons.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.util.Objects;

/**
 * date: 2021/12/16 10:56
 * author: zengfansheng
 */
public class ClipBoardUtil {
    /**
     * 获取剪切板内容
     */
    @NonNull
    public static String getContent(@NonNull Context context){
        ClipboardManager manager = (ClipboardManager) context.getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
        if (manager.hasPrimaryClip() && manager.getPrimaryClip().getItemCount() > 0) {
            CharSequence addedText = manager.getPrimaryClip().getItemAt(0).getText();
            String addedTextString = String.valueOf(addedText);
            if (!TextUtils.isEmpty(addedTextString)) {
                return addedTextString;
            }
        }
        return "";
    }

    /**
     * 清空剪切板
     */
    public static void clear(@NonNull Context context){
        ClipboardManager manager = (ClipboardManager) context.getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
        try {
            manager.setPrimaryClip(Objects.requireNonNull(manager.getPrimaryClip()));
            manager.setPrimaryClip(ClipData.newPlainText("",""));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 复制文字到剪贴板
     *
     * @param context 上下文
     * @param label   内容标签
     * @param text    内容
     */
    public static void copyToClip(@NonNull Context context, @NonNull String label, @NonNull String text) {
        //获取剪贴板管理器：  
        ClipboardManager cm = (ClipboardManager) context.getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData  
        ClipData clipData = ClipData.newPlainText(label, text);
        // 将ClipData内容放到系统剪贴板里。  
        cm.setPrimaryClip(clipData);
    }
}
