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

package open.log.plugins;

import org.jetbrains.annotations.NotNull;

/**
 * Created by eshel on 2019/8/31.
 */
public class LogUtils {

    public static boolean checkPackageName(@NotNull String packageName){
        for (int i = 0; i < packageName.length(); i++) {
            char c = packageName.charAt(i);
            boolean pass = (c == '_' || (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '1' && c <= '9') || c == '0' || c == '.');
            if(!pass)
                return false;
        }
        return true;
    }

    public static Integer parseInt(String value){
        try {
            return Integer.parseInt(value);
        }catch(Exception exception){
            exception.printStackTrace();
        }
        return null;
    }
}
