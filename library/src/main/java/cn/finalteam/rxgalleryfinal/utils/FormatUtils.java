package cn.finalteam.rxgalleryfinal.utils;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Copyright (C), 2017-2019, GWorld(平潭)互联网有限公司
 *
 * @author :        Chee <z-code@foxmail.com>
 * @date :          2019/11/27 15:51
 * @desc :
 */
public class FormatUtils {

    public static String formatDurationTime(long duration) {
        return String.format(Locale.getDefault(), "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
    }

}
