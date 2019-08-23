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

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;

/**
 * Created by EshelGuo on 2019/6/19.
 */
public class PsiUtils {

    /**
     * 获取 PsiElementFactory 用于创建 类 方法 变量 注解等
     */
    public static PsiElementFactory getElementFactory(PsiElement element){
        return JavaPsiFacade.getElementFactory(element.getProject());
    }

    /**
     * 创建私有String类型常量
     */
    public static void createPrivateStringConstFailed(PsiClass psiClass, String constName, String constValue){
        createPrivateStringConstFailed(psiClass, constName, constValue, null);
    }

    /**
     * 创建带注释的String常量
     * @param constName 变量名
     * @param constValue 变量的值
     * @param javaDoc 变量的注释
     */
    public static void createPrivateStringConstFailed(PsiClass psiClass, String constName, String constValue, String javaDoc){
        PsiType psiType = PsiType.getTypeByName("java.lang.String", psiClass.getProject()
                , GlobalSearchScope.EMPTY_SCOPE);//创建一个数据类型
        // 根据变量名查找类种是否包含此变量, 第二个参数含义是是否查找父类
        PsiField psiField = psiClass.findFieldByName(constName, false);
        if(psiField == null) {//避免生成重复变量
            //使用PsiElementFactory创建一个成员变量
            psiField = getElementFactory(psiClass).createField(constName, psiType);
            //PsiUtil是SDK提供的一个工具类
            //给成员变量添加修饰符, 参数2为true代表添加, false代表移除
            PsiUtil.setModifierProperty(psiField, PsiModifier.STATIC, true);
            PsiUtil.setModifierProperty(psiField, PsiModifier.FINAL, true);
            PsiUtil.setModifierProperty(psiField, PsiModifier.PRIVATE, true);

            //设置变量初始化表达式
            PsiExpression psiInitializer = getElementFactory(psiClass).createExpressionFromText("\"" + constValue + "\"", psiField);
            psiField.setInitializer(psiInitializer);
            //在变量后写入单行注释
            writeComment(psiClass, javaDoc, psiField);
            //将变量添加至类中
            psiClass.add(psiField);
        }else {
            PsiExpression psiInitializer = getElementFactory(psiClass).createExpressionFromText("\"" + constValue + "\"", psiField);
            psiField.setInitializer(psiInitializer);
            writeComment(psiClass, javaDoc, psiField);
        }
    }

    private static void writeComment(PsiClass psiClass, String javaDoc, PsiField psiField) {
        if(javaDoc != null) {
            //获取变量最后一个元素
            PsiElement lastChild = psiField.getLastChild();
            if(!(lastChild instanceof PsiComment)) {//避免重复写入注释
                //创建一个单行注释
                PsiComment comment = PsiUtils.getElementFactory(psiClass).createCommentFromText("//" + javaDoc, psiField);
                //将单行注释添加至最后一个元素后
                psiField.addAfter(comment, lastChild);
            }
        }
    }

    /**
     * 获取当前正在编辑的文件
     */
    public static PsiFile getCurrentFile(AnActionEvent event){
        return event.getData(LangDataKeys.PSI_FILE);
    }

    /**
     * 获取光标所在的类
     */
    public static PsiClass getCurrentClass(AnActionEvent event){
        Editor editor = event.getData(DataKeys.EDITOR);
        PsiElement element = getCurrentFile(event).findElementAt(editor != null ? editor.getCaretModel().getOffset() : 0);
        return PsiTreeUtil.getParentOfType(element, PsiClass.class);
    }

    /**
     * 获取类名
     */
    public static String getClassName(PsiClass clazz){
        return clazz.getNameIdentifier().getText();
    }

    /**
     * 创建方法并添加至类中
     */
    public static PsiMethod createMethodFromTextAdd(PsiClass clazz, String text){
        PsiMethod method = getElementFactory(clazz).createMethodFromText(text, clazz);
        PsiMethod[] methods = clazz.findMethodsByName(method.getName(), false);
        if(methods != null && methods.length != 0) {
            return methods[0];
        }
        clazz.add(method);
        return method;
    }
    /**
     * 创建方法
     */
    public static PsiMethod createMethodFromText(PsiClass clazz, String text){
        return getElementFactory(clazz).createMethodFromText(text, clazz);
    }

    /**
     * 创建一个接口
     */
    public static PsiClass createInterface(PsiClass clazz, String interfaceCame){
        PsiClass innerInterface = getElementFactory(clazz).createInterface(interfaceCame);
        PsiModifierList modifierList = innerInterface.getModifierList();
        modifierList.setModifierProperty(PsiModifier.PUBLIC, true);
        return innerInterface;
    }

    /**
     * 创建一个接口并添加至类中
     */
    public static PsiClass createInterfaceAdd(PsiClass clazz, String interfaceName){
        PsiClass anInterface = createInterface(clazz, interfaceName);
        clazz.add(anInterface);
        return anInterface;
    }

    public static String getConstValue(PsiClass clazz, String name) {
        PsiField[] fields = clazz.getAllFields();
//        PsiFile currentFile = getCurrentFile(null);


        if(!Util.isEmpty(fields)){
            for (PsiField field : fields) {
                if(field.getName().equals(name)){
                    return field.getInitializer().toString().replace("\"", "");
                }
            }
        }

        PsiElement parent = clazz.getParent();
        if(parent instanceof PsiClass){
            return getConstValue((PsiClass) parent, name);
        }

        return null;
    }

    /**
     * 获得内部类所在的外部类
     * @param target
     * @return
     */
    public static PsiClass getParentClass(PsiClass target) {
        PsiElement parent = target.getParent();
        if(parent instanceof PsiClass)
            return (PsiClass) parent;
        return target;
    }
}
