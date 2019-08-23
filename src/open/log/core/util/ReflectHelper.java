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

package open.log.core.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.LinkedList;

/**
 * Created by EshelGuo on 2019/7/12.
 */
public class ReflectHelper {

    /**
     * ## 不支持获取接口泛型
     * owner: A.class, target: B.class
     * 获取类B上声明的第 index 个泛型的在 类 A 中的实际类型
     * @param owner (子类)泛型的拥有者
     * @param index target声明的位置从左到右数第 index 个泛型
     * @param target (父类)声明泛型的类, target 必须是 owner 的父类
     * 例如:
     *      class<T> B{
     *          public Class getGenericClass(){
     *               return ReflectHelper.getGenericClass(getClass(), B.class, 0);
     *          }
     *      }
     *      class A extends B<String>{
     *
     *      }
     *      class C extends B<Integer>{
     *
     *      }
     *      class D<A> extends B<A>{
     *
     *      }
     *
     *     System.out.println(new A().getGenericClass() instanceOf String.class);
     *     System.out.println(new C().getGenericClass() instanceOf Integer.class);
     *     System.out.println(new D<Double></>().getGenericClass() instanceOf Double.class);
     *     打印结果为:
     *               true
     *               true
     *               false
     */
    public static<T> Class getGenericClass(Class<? extends T> owner, Class<T> target, int index){

        if(owner == target)
            return null;

        if(!target.isAssignableFrom(owner))
            return null;

        TypeVariable<Class<T>>[] tps = target.getTypeParameters();
        if(index >= tps.length)
            return null;

        LinkedList<Class> classes = new LinkedList<>();
        classes.add(owner);
        Class superClazz = owner;
        while ((superClazz = superClazz.getSuperclass()) != target){
            classes.add(superClazz);
        }

        Class supClazz = classes.removeLast();
        Type typeVar = ((ParameterizedType) supClazz.getGenericSuperclass()).getActualTypeArguments()[index];

        if(typeVar instanceof Class)
            return (Class) typeVar;

        while (!classes.isEmpty()) {
            index = Util.indexOf(supClazz.getTypeParameters(), typeVar);
            supClazz = classes.removeLast();
            typeVar = ((ParameterizedType) supClazz.getGenericSuperclass()).getActualTypeArguments()[index];
            if(typeVar instanceof Class)
                return (Class) typeVar;
        }
        return null;
    }



//    public static void main(String[] args){
//        System.out.println(getGenericClass(Test2.class, Test.class, 1));
//    }
}
