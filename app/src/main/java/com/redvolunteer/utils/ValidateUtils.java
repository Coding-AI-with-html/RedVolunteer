package com.redvolunteer.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateUtils {



    public static boolean isEmpty(String str) {
        if(str == null)
            return true;
        if(str.trim().equals(""))
            return true;
        return false;
    }

    public static boolean isUserName(String userName) {
        Pattern pattern = Pattern.compile("^([\u4E00-\u9FA5]|[\uF900-\uFA2D]|[\u258C]|[\u2022]|[\u2E80-\uFE4F])+$");
        Matcher mc = pattern.matcher(userName);
        return mc.matches();
    }


    public static boolean isValidEmail(String mail) {
        Pattern pattern = Pattern.compile("^[A-Za-z0-9][\\w\\._]*[a-zA-Z0-9]+@[A-Za-z0-9-_]+\\.([A-Za-z]{2,4})");
        Matcher mc = pattern.matcher(mail);
        return mc.matches();
    }


    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }


    public static boolean isPhone(String mobiles) {
        // Pattern p =
        // Pattern.compile("^((13[0-9])|(17[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
        Pattern p = Pattern.compile("1[0-9]{10}");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    public static boolean isMobileOrPhone(String str) {
        String regex = "^((([0\\+]\\d{2,3}-)|(0\\d{2,3})-))(\\d{7,8})(-(\\d{3,}))?$|^1[0-9]{10}$";
        return match(regex, str);
    }

    public static boolean isPrice(String price) {
        String regex = "^([1-9][0-9]{0,7})(\\.\\d{1,2})?$";
        return match(regex, price);
    }



    public static boolean isLetter(String str) {
        Pattern p = Pattern.compile("^[A-Za-z]+$");
        Matcher m = p.matcher(str);
        return m.matches();
    }


    public static boolean isPassword(String str) {
        Pattern p = Pattern.compile("[A-Za-z0-9]{6,20}");
        Matcher m = p.matcher(str);
        return m.matches();
    }



    public static boolean isEmail(String email) {
        if (null == email || "".equals(email))
            return false;
        // Pattern p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}"); //简单匹配
        Pattern p = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");// 复杂匹配
        Matcher m = p.matcher(email);
        return m.matches();
    }



    public static boolean isLength(String str) {
        Pattern pattern = Pattern.compile(".{6,20}");
        return pattern.matcher(str).matches();
    }




    public static boolean isAddressLength(String str) {
        Pattern pattern = Pattern.compile(".{1,250}");
        return pattern.matcher(str).matches();
    }



    public static boolean isNameLength(String str) {
        Pattern pattern = Pattern.compile(".{2,25}");
        return pattern.matcher(str).matches();
    }




    public static int String_length(String value) {
        int valueLength = 0;
        String chinese = "[\u4e00-\u9fa5]";
        for (int i = 0; i < value.length(); i++) {
            String temp = value.substring(i, i + 1);
            if (temp.matches(chinese)) {
                valueLength += 2;
            } else {
                valueLength += 1;
            }
        }
        return valueLength;
    }



    public static String stringFilter(String str) {
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("");
    }


    public static boolean isPasswLength(String str) {
        String regex = "^\\S{6,20}$";
        return match(regex, str);
    }



    private static boolean match(String regex, String str) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }








    public static boolean isDate(String strDate) {
        Pattern pattern = Pattern.compile("^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");
        Matcher m = pattern.matcher(strDate);
        if (m.matches()) {
            return true;
        } else {
            return false;
        }
    }



}
