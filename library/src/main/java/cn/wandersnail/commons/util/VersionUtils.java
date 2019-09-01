package cn.wandersnail.commons.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 版本号工具
 * <p>
 * date: 2019/8/6 18:21
 * author: zengfansheng
 */
public class VersionUtils {
    //将字母和数字拆分出来
    private static List<String> splitCell(String s) {
        List<String> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        char[] chars = s.toCharArray();
        Boolean isNum = null;
        for (char c : chars) {
            boolean same = true;
            boolean num = c >= '0' && c <= '9';
            if (isNum == null) {
                isNum = num;
            } else {
                same = isNum && num;
                isNum = num;
            }
            if (!same) {
                list.add(sb.toString());
                sb.setLength(0);
            }
            sb.append(c);
        }
        //添加最后一个
        list.add(sb.toString());
        return list;
    }

    private static Integer toInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e){
            return null;
        }
    }

    /**
     * 比较版本号大小
     *
     * @return 相等，则返回值0；小于，则返回负数；大于，则返回正数。
     */
    public static int compare(String ver1, String ver2) {
        if (ver1.isEmpty() && ver2.isEmpty()) {
            return 0;
        } else if (ver1.isEmpty()) {
            return -1;
        } else if (ver2.isEmpty()) {
            return 1;
        }
        //将将版本号字符串以.分割进行比较
        String[] vCells1 = ver1.replaceAll(" ", "").split("\\.");
        String[] vCells2 = ver2.replaceAll(" ", "").split("\\.");
        //以长度短的，对分割元素逐个比较
        int len = Math.min(vCells1.length, vCells2.length);
        for (int i = 0; i < len; i++) {
            List<String> cellList1 = splitCell(vCells1[i]);
            List<String> cellList2 = splitCell(vCells2[i]);
            //同样逐个比较
            int len1 = Math.min(cellList1.size(), cellList2.size());
            for (int j = 0; j < len1; j++) {
                String cell1 = cellList1.get(j);
                String cell2 = cellList2.get(j);
                Integer cell1Int = toInt(cell1);
                Integer cell2Int = toInt(cell2);
                int result;
                if (cell1Int != null && cell2Int != null) {
                    result = cell1Int.compareTo(cell2Int);
                } else {
                    result = cell1.compareTo(cell2);
                }
                if (result != 0) {
                    return result;
                }
            }
            //如果到这里，说明短的部分是相等的，最后比较长度
            int result = Integer.compare(cellList1.size(), cellList2.size());
            if (result != 0) {
                return result;
            }
        }
        //如果到这里，说明短的部分是相等的，最后比较长度
        return Integer.compare(vCells1.length, vCells2.length);
    }
}
