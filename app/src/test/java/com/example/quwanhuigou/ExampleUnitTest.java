package com.example.quwanhuigou;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    /**
     * 获取两个日期之间的毫秒属
     */
    @Test
    public void testDate() throws Exception{


        String startTime = "2021-09-05 12:45:50";
        Date startDate = getDate(startTime);
        System.out.println("字符串转日期后"+startDate);

        Calendar nowDate=Calendar.getInstance();
        Calendar oldDate=Calendar.getInstance();
        nowDate.setTime(new Date());//设置为当前系统时间
        System.out.println("当前系统时间"+new Date());

        oldDate.setTime(startDate);//设置为想要比较的日期
        Long timeNow=nowDate.getTimeInMillis();
        System.out.println("当前系统时间毫秒"+timeNow);
        Long timeOld=oldDate.getTimeInMillis();
        System.out.println("比较时间毫秒"+timeOld);
        Long time = (timeOld-timeNow);//相差毫秒数
        System.out.println("相差毫秒数"+time);
    }


    /**
     * 字符串转日期
     * @param startTime  任务开始时间
     * @return
     */
    public Date getDate(String startTime) throws Exception{
        //字符串转日期格式
        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startTime);
        return  date;
    }


    /**
     * 日期减几秒
     */
    @Test
    public void a(){

        Date date = new Date();
        System.out.println(date);
        long time = 1*1000;//1秒
        Date afterDate = new Date(date .getTime() + time);//1秒后的时间
        System.out.println(afterDate);
        Date beforeDate = new Date(date .getTime() - time);//1秒前的时间
        System.out.println(beforeDate);
    }



//    function b(e) {
//        for (var t = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789", n = "", r = 0; r < e; r++) {
//            var a = Math.floor(Math.random() * t.length);
//            n += t.substring(a, a + 1)
//        }
//        return n
//    }


    @Test
    public void b(){

        int e = 16;

        String t = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        String n = "";

        for (int r = 0; r < e; r++) {

            int a = (int)Math.floor(Math.random() * t.length());
            n += t.substring(a,a+1);
        }

        System.out.println(n+"   "+n.length());

    }

}