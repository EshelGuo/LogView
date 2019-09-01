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

/**
 * Created by eshel on 2019/9/1.
 */
public class LogSupport {

    /**
     * 判断支持的文件格式
     * @param fileName 文件名(包括后缀)
     * @return 是否支持
     */
    public static boolean supportFileType(String fileName){
        return true;
    }
}
