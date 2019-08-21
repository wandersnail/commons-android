package cn.wandersnail.commons.helper;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.wandersnail.commons.util.FileUtils;

/**
 * 调用系统文件管理选择文件
 * <p>
 * date: 2019/8/8 09:42
 * author: zengfansheng
 */
public class SysFileChooser {
    private static final int REQUEST_CODE = 13342;

    public static final String MIME_TYPE_AUDIO = "audio/*";
    public static final String MIME_TYPE_APPLICATION = "application/*";
    public static final String MIME_TYPE_IMAGE = "image/*";
    public static final String MIME_TYPE_VIDEO = "video/*";
    public static final String MIME_TYPE_TEXT = "text/*";
    public static final String MIME_TYPE_ALL = "*/*";

    public static class Options {
        public boolean allowMultiple;
        public boolean localOnly;
        public String[] mimeTypes;
        public String title;
    }

    private Intent generateIntent(Options options) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(options.mimeTypes != null && options.mimeTypes.length == 1 ? options.mimeTypes[0] : MIME_TYPE_ALL);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, options.allowMultiple);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, options.localOnly);
        if (options.mimeTypes != null && options.mimeTypes.length > 1) {
            intent.putExtra(Intent.EXTRA_MIME_TYPES, options.mimeTypes);
        }
        return Intent.createChooser(intent, options.title);
    }

    public boolean choose(@NonNull Activity activity, @NonNull Options options) {
        Objects.requireNonNull(activity, "activity is null");
        Objects.requireNonNull(options, "options is null");
        try {
            activity.startActivityForResult(generateIntent(options), REQUEST_CODE);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean choose(@NonNull Fragment fragment, @NonNull Options options) {
        Objects.requireNonNull(fragment, "fragment is null");
        Objects.requireNonNull(options, "options is null");
        try {
            fragment.startActivityForResult(generateIntent(options), REQUEST_CODE);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 从选择结果中获取文件的真实路径
     */
    public List<String> getRealPathsFromResultData(@NonNull Context context, int requestCode, int resultCode, Intent data) {
        List<String> paths = new ArrayList<>();
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            ClipData clipData = data.getClipData();
            if (clipData != null) {
                int count = clipData.getItemCount();
                for (int i = 0; i < count; i++) {
                    ClipData.Item item = clipData.getItemAt(i);
                    String path = FileUtils.getFileRealPath(context, item.getUri());
                    if (path != null) {
                        paths.add(path);
                    }
                }
            } else if (data.getData() != null) {
                String path = FileUtils.getFileRealPath(context, data.getData());
                if (path != null) {
                    paths.add(path);
                }
            }
        }
        return paths;
    }
}
