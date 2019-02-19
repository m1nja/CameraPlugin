package com.wonderkiln.camerakit.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by nlmartian on 8/1/15.
 */
public class DateUtil {
    public static final String FORMAT_DATE = "yyyy-MM-dd HH:mm:ss";

    public static final String FORMAT_DATA_TAG = "yyyy:MM:dd HH:mm:ss";

    public static final String FORMAT_DAY_TIME = "M月dd日 HH:mm";

    public static final String FORMAT_DAY = "M月dd日（EEE）";

    public static final String FORMAT_REQUEST_DATE = "yyyy-MM-dd";

    public static final String FORMAT_LIST_DATE = "MM/dd HH:mm";

    public static final String FORMAT_LIST_TIME = "HH:mm";

    public static final String FORMAT_TASK_LIMIT = "HH:mm:ss";

    public static String getDayString(Date date) {
        String result = "";
        if (date != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(FORMAT_DAY);
            try {
                result = dateFormat.format(date);
            } catch (Exception e) {
                e.printStackTrace();
                return result;
            }
        }
        return result;
    }

    public static String getRequestDateString(Date date) {
        String result = "";
        if (date != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(FORMAT_REQUEST_DATE);
            try {
                result = dateFormat.format(date);
            } catch (Exception e) {
                e.printStackTrace();
                return result;
            }
        }
        return result;
    }

    public static String getFormatTime(Date time, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        String result = null;
        try {
            result = dateFormat.format(time);
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
        return result;
    }

    public static String getListDuration(Date startTime, Date endTime) {
        if (startTime == null) {
            startTime = new Date();
        }
        if (endTime == null) {
            endTime = new Date();
        }
        Calendar calStart = new GregorianCalendar();
        calStart.setTime(startTime);
        Calendar calEnd = new GregorianCalendar();
        calEnd.setTime(endTime);
        StringBuilder sb = new StringBuilder();

        if (calStart.get(Calendar.DATE) == calEnd.get(Calendar.DATE)) {
            sb.append(getFormatTime(startTime, FORMAT_LIST_DATE));
            sb.append(" ~ ");
            sb.append(getFormatTime(endTime, FORMAT_LIST_TIME));
        } else {
            sb.append(getFormatTime(startTime, FORMAT_LIST_DATE));
            sb.append(" ~ ");
            sb.append(getFormatTime(endTime, FORMAT_LIST_DATE));
        }
        return sb.toString();
    }

    public static String getFriendlyTime(Date vieTime) {
        if (vieTime == null) {
            return "";
        }
        Calendar calStart = new GregorianCalendar();
        calStart.setTime(vieTime);
        Calendar calNow = new GregorianCalendar();

        if (calStart.get(Calendar.YEAR) != calNow.get(Calendar.YEAR)) {
            return getFormatTime(vieTime, FORMAT_DAY_TIME);
        } else {
            if (calStart.get(Calendar.DATE) != calNow.get(Calendar.DATE)) {
                return getFormatTime(vieTime, FORMAT_DAY_TIME);
            } else {
                return getFormatTime(vieTime, FORMAT_DAY_TIME);
            }
        }
    }

    public static String getCastFormatTime(String ms) {
        long msTime = Long.parseLong(ms);
        msTime = msTime*1000;
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date=new Date(msTime);
        return format.format(date);
    }

    /**
     * 获取工单完成时间剩余时间小时数
     * @param taskEndTime
     * @return
     */
    public static int getTaskRemainTime(long taskEndTime){
        Date date = new Date();
        long currTime = date.getTime();
        long timeDiff = taskEndTime - currTime;
        int remaineTime = (int)(timeDiff/(3600000));
        return remaineTime;
    }





    /**
     * 小于一小时的时候,获取工单还剩余多少分钟
     * @param taskEndTime
     * @return
     */
    public static int getTaskRemainMinute(long taskEndTime) {
        Date date = new Date();
        long currTime = date.getTime();
        long timeDiff = taskEndTime - currTime;
        int remaineMinute = (int)(timeDiff/(60000));
        return remaineMinute;
    }



    public static String getCastFormatTimeMinute(int ms) {
        int mm = (ms/1000)/60;
        int ss = ((ms/1000)%60);
       String result = mm + ":" + ss;
        if (mm < 10) {
            result = "0" + mm + ":"+ ss;
            if (ss < 10) {
                result = "0" + mm + ":" + "0" + ss;
            }
        }
        return result;
    }

    /**
     * 比较当前时间大于开始时间
     * @param taskStartTime
     * @return
     */
    public static boolean judgeTaskIsStart(long taskStartTime) {
        Date date = new Date();
        long currTime = date.getTime();
        if ((currTime - taskStartTime) >= 0) {
            return true;
        }
        return false;
    }

    /**
     * 判断当前时间是否小于开始时间一小时
     * @param taskStartTime
     * @return
     */
    public static boolean judgeTimelessOneHour(long taskStartTime) {
        Date date = new Date();
        long currTime = date.getTime();
        if ((taskStartTime - currTime) <= 60*60*1000) {
            return true;
        }
        return false;
    }


    /**
     * 获取当前是星期几
     * @return
     */
    public static int getWeek() {
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String mWay= String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        int mWeek = 0;

        if("1".equals(mWay)){
            mWeek = 7;
        } else if("2".equals(mWay)){
            mWeek = 1;
        }else if("3".equals(mWay)){
            mWeek = 2;
        }else if("4".equals(mWay)){
            mWeek = 3;
        }else if("5".equals(mWay)){
            mWeek = 4;
        }else if("6".equals(mWay)){
            mWeek = 5;
        }else if("7".equals(mWay)){
            mWeek = 6;
        }

        return mWeek;
    }

    public static String recordRockon(int s) {
        int mm = s/60;
        int ss = s%60;
        String mmStr;
        String ssStr;

        if (mm < 10) {
            mmStr = "0"+mm;
        } else {
            mmStr =""+mm;
        }
        if (ss < 10) {
            ssStr = "0"+ss;
        } else {
            ssStr = ""+ss;
        }

        return mmStr+":"+ssStr;
    }


    /**
     * 小时－>毫秒
     * @return
     */
    public static long hourToMill(String hour) {
        int temp;
        try {
            temp = Integer.parseInt(hour) * 60 * 60 * 1000;
        } catch (NumberFormatException e) {
            temp = Integer.parseInt("24") * 60 * 60 * 1000;
        }
        return temp;
    }


    /**
     * 抢单时间+执行时间 < 但前时间
     * @param taskVieTime
     * @return
     */
    public static boolean taskTimeEffectiv(long taskVieTime) {
        Date date = new Date();
        long currTime = date.getTime();

        if (currTime > taskVieTime) {
            return false;
        }

        return  true;
    }


    /**
     * 结束时间小于30分钟，撤销任务提示时间
     * @param taskVieTime
     * @return
     */
    public static long taskTimeSurplus30Minute(long taskVieTime) {
        Date date = new Date();
        long currTime = date.getTime();

        if (currTime < (taskVieTime) && currTime > (taskVieTime  - (30 * 60 * 1000))) {
            return (taskVieTime - currTime) / (60000);
        }
        return -1;
    }


    /**
     * 检查任务结束时间大于 限制任务结束时间
     */
    public static boolean checkEndTimeGreaterLimi(long endTime, long limit) {
        if (endTime > limit) {//按限制时间处理
            return true;
        } else {//按结束时间处理
            return false;
        }
    }


    /**
     * 任务结束时间小于2小时,显示还剩下多少分钟
     */
    public static long checkEndTimeSurplus2Hour(Date endDate) {
        if (endDate == null) {
            endDate = new Date();
        }

        Date date = new Date();

        long surplus = endDate.getTime() - date.getTime();

        if (surplus > 0 && surplus < (2 * 60 * 60 * 1000)) {//返回还剩下多少分钟
            return (surplus/(60 * 1000));
        } else {
            return -1;
        }
    }

    /**
     * 获取时间戳
     */
    public static String getCurrentTime() {
        return (System.currentTimeMillis()/1000)+"";
    }


    /**
     *
     *
     *
     */
    public static String castMinut(long s) {
        if (s > 60 && s % 60 != 0) {
            return (s/60)+"分"+(s % 60)+"秒";
        } else if ((s==60) || (s>60 && s % 60 == 0)) {
            return (s/60)+"分";
        } else {
            return s+"秒";
        }
    }


    public static String getExifDateString() {
        String result = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat(FORMAT_DATA_TAG);
        try {
            result = dateFormat.format(new Date());
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }

        return result;
    }



}
