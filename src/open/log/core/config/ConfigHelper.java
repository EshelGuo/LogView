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

package open.log.core.config;

import com.google.gson.Gson;
import com.intellij.ide.util.PropertiesComponent;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by EshelGuo on 2019/6/20.
 * 存取配置至Disk工具
 */
public class ConfigHelper {

    /**
     * 保存配置至磁盘
     */
    public static  <T extends BaseConfig> void save(@NotNull T config){
        PropertiesComponent.getInstance().setValue(config.getClass().getName(), new Gson().toJson(config));
    }

    /**
     * 读取配置文件, 如果没有则创建一个默认配置
     */
    public static @NotNull <T extends BaseConfig> T load(@NotNull Class<T> clazz){
        String json = PropertiesComponent.getInstance().getValue(clazz.getName());
        if(json == null){
            try {
                Constructor<T> constructor = clazz.getConstructor();
                return constructor.newInstance();
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                e.printStackTrace();
                throw new RuntimeException(clazz.getName()+"必须要有一个空参的构造方法");
            }
        }
        return new Gson().fromJson(json, clazz);
    }

    public static @NotNull <T extends BaseConfig> T load(@NotNull Class<T> clazz, T defaultValue){
        String json = PropertiesComponent.getInstance().getValue(clazz.getName());
        if(json == null){
            return defaultValue;
        }
        return new Gson().fromJson(json, clazz);
    }
}
