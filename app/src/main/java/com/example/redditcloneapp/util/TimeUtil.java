package com.example.redditcloneapp.util;

import java.util.Date;

public class TimeUtil {
    public static String formatTimeAgo(Date date) {
        if (date == null) {
            return "";
        }

        long now = System.currentTimeMillis();
        long time = date.getTime();
        long diffMillis = now - time;

        if (diffMillis < 0) {
            // future date – fallback
            diffMillis = 0;
        }

        long seconds = diffMillis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        long months = days / 30;
        long years = days / 365;

        if (seconds < 60) {
            return "just now";
        } else if (minutes < 60) {
            return minutes + " min ago";
        } else if (hours < 24) {
            return hours + " h ago";
        } else if (days < 30) {
            return days + " d ago";
        } else if (months < 12) {
            return months + " mo ago";
        } else {
            return years + " y ago";
        }
    }
}
