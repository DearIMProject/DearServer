package com.wmy.study.DearIMProject.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailUtils {
    public static boolean isEmail(String email) {
        // 电子邮件
        String check =
                "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher("dffdfdf@qq.com");
        boolean isMatched = matcher.matches();
        return isMatched;
    }
}
