package me.wilk3z.kpractice.utils;

public class TimeUtil
{
    public String formatTime(int time)
    {
        int seconds = time % 60;
        int minutes = (time / 60) % 60;
        int hours = ((time / 60) / 60) % 24;
        int days = (((time / 60) / 60) / 24);
        String format;
        if(days > 0)
        {
            format = days + ":" + (hours >= 10 ? hours : "0" + hours) + ":" + (minutes >= 10 ? minutes : "0" + minutes) + ":" + (seconds >= 10 ? seconds : "0" + seconds);
            return format;
        }
        if(hours > 0)
        {
            format = hours + ":" + (minutes >= 10 ? minutes : "0" + minutes) + ":" + (seconds >= 10 ? seconds : "0" + seconds);
            return format;
        }
        if(minutes > 0)
        {
            format = minutes + ":" + (seconds >= 10 ? seconds : "0" + seconds);
            return format;
        }
        format = seconds + "s";
        return format;
    }

    public int getSeconds(int seconds)
    {
        return seconds;
    }

    public int getSeconds(int minutes, int seconds)
    {
        return (minutes * 60) + seconds;
    }

    public int getSeconds(int hours, int minutes, int seconds)
    {
        return ((hours * 60) * 60) + (minutes * 60) + seconds;
    }

    public int getSeconds(int days, int hours, int minutes, int seconds)
    {
        return (((days * 24) * 60) * 60) + ((hours * 60) * 60) + (minutes * 60) + seconds;
    }
}
