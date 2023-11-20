package com.wmy.study.DearIMProject.Utils;

import java.util.Calendar;
import java.util.Date;

public class TimeUtils {
    public static boolean isExpire(long time) {
        long nowTime = new Date().getTime();
        return nowTime >= time;
    }

    /**
     * 获取指定日期所在月份开始的时间戳
     *
     * @param date 指定日期
     * @return
     */
    public static long getMonthBegin(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        // 设置为1号,当前日期既为本月第一天
        c.set(Calendar.DAY_OF_MONTH, 1);
        // 将小时至0
        c.set(Calendar.HOUR_OF_DAY, 0);
        // 将分钟至0
        c.set(Calendar.MINUTE, 0);
        // 将秒至0
        c.set(Calendar.SECOND, 0);
        // 将毫秒至0
        c.set(Calendar.MILLISECOND, 0);
        // 获取本月第一天的时间戳
        return c.getTimeInMillis();
    }
}
