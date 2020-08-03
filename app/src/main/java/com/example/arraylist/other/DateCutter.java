package com.example.arraylist.other;

import androidx.annotation.NonNull;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateCutter {
    String headerText;

    public DateCutter(@NonNull String headerText) {
        this.headerText = headerText;
    }

    // "–" и "-" ЭТО РАЗНЫЕ СИМВОЛЫ!!! Нужно использовать "–" (1)

    SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
    //Получение даты начала
    public Long getFirst() throws ParseException {
        String firstDate = headerText.substring(0, headerText.indexOf("–") - 1);
        return translateFromStringToLong(firstDate);
    }
    //Дата конца
    public Long getSecond() throws ParseException {
        String secondDate = headerText.substring( headerText.indexOf("–") + 2);
        return translateFromStringToLong(secondDate);
    }

    public Long translateFromStringToLong(String date) throws ParseException {
        if (date.contains(".")) {
            if (date.indexOf(".") != date.lastIndexOf(".")) {//мес с . есть год
                date = date.substring(0, date.indexOf(".")) +
                        date.substring(date.indexOf(".") + 1,
                                date.lastIndexOf(".") - 2);
            }
            else if (!date.substring(date.indexOf(".") - 2,
                    date.indexOf(".") - 1).equals(" ")) { //мес c . без года
                date = date.substring(0, date.indexOf(".")) + " " + getCurrentYear();
            }
            else { //мес без . есть год
                date = date.substring(0, date.indexOf(".") - 2);
            }
        }
        else { //мес без . без года
            date = date + " " + getCurrentYear();
        }
        return setZeroTimeDate(formatter.parse(date)).getTime();
    }

    public static int getCurrentYear()
    {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
        calendar.setTime(new java.util.Date());
        return calendar.get(java.util.Calendar.YEAR);
    }

    public Date setZeroTimeDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        date = calendar.getTime();
        return date;
    }
}
