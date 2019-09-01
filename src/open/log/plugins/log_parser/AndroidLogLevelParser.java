/*
 * Copyright (c) 2019 Eshel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package open.log.plugins.log_parser;

import open.log.plugins.LogUtils;
import open.log.plugins.log_level.AndroidLogLevel;
import open.log.plugins.log_level.LogLevel;
import org.apache.http.util.TextUtils;

/**
 * Created by eshel on 2019/8/31.
 */
public class AndroidLogLevelParser extends LogLevelParser{
    public static AndroidLogLevelParser INSTANCE = new AndroidLogLevelParser();

    private AndroidLogLevelParser() {
    }

    @Override
    public LogLevel checkLogLevel(String text) {
        if(TextUtils.isEmpty(text))
            return null;
        int offset = 0;
        //解析日期
        {
            int index = text.indexOf(" ", offset);
            if(index == -1)
                return null;

            String date = text.substring(offset, index);
            String[] split = date.split("-");
            if(split.length != 2){
                return null;
            }

            Integer month = LogUtils.parseInt(split[0]);
            Integer day = LogUtils.parseInt(split[1]);
            if(month == null || day == null) {
                return null;
            }
            offset = index + 1;
        }

        //解析时间: hour:minute:second.millisecond
        {
            int index = text.indexOf(" ", offset);
            if(index == -1)
                return null;

            String time = text.substring(offset, index);
            String[] split = time.split(":");
            if(split.length != 3){
                return null;
            }

            Integer hour = LogUtils.parseInt(split[0]);
            Integer minute = LogUtils.parseInt(split[1]);
            if(hour == null || minute == null)
                return null;

            String[] split1 = split[2].split("\\.");
            if(split1.length != 2)
                return null;
            Integer second = LogUtils.parseInt(split1[0]);
            Integer millisecond = LogUtils.parseInt(split1[1]);
            if(second == null || millisecond == null)
                return null;

            offset = index + 1;
        }

        //解析 PID-TID
        {
            int index = text.indexOf("/", offset);
            if(index == -1)
                return null;

            String data = text.substring(offset, index);
            String[] split = data.split("-");
            if(split.length != 2){
                return null;
            }

            Integer pid = LogUtils.parseInt(split[0]);
            Integer tid = LogUtils.parseInt(split[1]);
            if(pid == null || tid == null) {
                return null;
            }
            offset = index + 1;
        }

        //解析包名
        {
            int index = text.indexOf(" ", offset);
            if(index == -1)
                return null;

            String _package = text.substring(offset, index);

            if(!_package.equals("?")){
                boolean isPackage = LogUtils.checkPackageName(_package);
                if(!isPackage)
                    return null;
            }
            offset = index + 1;
        }

        //解析日志级别
        {
            int index = text.indexOf("/", offset);
            if(index == -1)
                return null;

            String level = text.substring(offset, index);
            if(TextUtils.isEmpty(level))
                return null;

            switch (level){
                case "V":
                    return AndroidLogLevel.Verbose;
                case "D":
                    return AndroidLogLevel.Debug;
                case "I":
                    return AndroidLogLevel.Info;
                case "W":
                    return AndroidLogLevel.Warn;
                case "E":
                    return AndroidLogLevel.Error;
            }

            offset = index + 1;
        }
        return null;
    }
}
