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

package open.log.core.view.table;

import open.log.core.util.ReflectHelper;
import open.log.core.util.Util;
import open.log.core.view.table.anno.Name;

import javax.swing.table.AbstractTableModel;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by EshelGuo on 2019/7/12.
 */
public abstract class BaseTableModel<T extends TableData> extends AbstractTableModel {

    private Class<T> dataClass;
    protected List<T> mData;
    private List<Title> mTitle;
    protected boolean editable = true;

    public BaseTableModel() {
        init();
    }

    public BaseTableModel(List<T> mData) {
        this.mData = mData;
        init();
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public void setNewData(List<T> data){
        mData = data;
        fireTableDataChanged();
    }

    public void addData(List<T> data){
        if(mData == null)
            mData = data;
        mData.addAll(data);
        fireTableDataChanged();
    }

    public void addData(T data){
        if(mData == null)
            mData = new ArrayList<>(10);
        mData.add(data);
        fireTableDataChanged();
    }

    public void addData(List<T> datas, int index){
        if(mData == null)
            mData = new ArrayList<>(10);
        mData.addAll(index, datas);
        fireTableDataChanged();
    }

    public void remove(int row){
        mData.remove(row);
        fireTableDataChanged();
    }

    public void remove(int[] rows){
        for (int i = rows.length - 1; i >= 0; i--) {
            mData.remove(rows[i]);
        }
        fireTableDataChanged();
    }

    public void addEmptyDataToFirst(int size){
        addData(createEmptyDatas(size), 0);
    }

    public void addEmptyDataToLast(int size){
        addData(createEmptyDatas(size));
    }

    public void insertLine(int line){
        if(mData != null && line > mData.size())
            line = mData.size();
        addData(createEmptyData(), line);
    }

    public void lineUp() {
        if (Util.isEmpty(mData))
            return;
        Iterator<T> it = mData.iterator();

        while (it.hasNext()) {
            T next = it.next();
            if (isEmptyData(next)) {
                it.remove();
                break;
            }
        }

        if (!isEmptyData(mData.get(mData.size() - 1))){
            addEmptyDataToLast(1);
        } else {
            fireTableDataChanged();
        }
    }

    public void lineDown(){
        addEmptyDataToFirst(1);
    }

    public List<T> getDatas(){
        if(Util.isEmpty(mData))
            return null;
        ArrayList<T> result = new ArrayList<>(mData.size());
        for (T data : mData) {
            if(isEmptyData(data))
                continue;
            result.add(data);
        }
        return result;
    }

    public final List<T> getDatasIncludeEmpty(){
        return mData;
    }

    public void addData(T data, int index){
        if(mData == null)
            mData = new ArrayList<>(10);
        mData.add(index, data);
        fireTableDataChanged();
    }

    public T createEmptyData(){
        try {
            return dataClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isEmptyData(T data){
        if(Util.isEmpty(mTitle) || Util.isNull(data))
            return true;
        for (Title title : mTitle) {
            Field field = title.field;
            if(!field.isAccessible())
                field.setAccessible(true);
            try {
                Object value = field.get(data);
                if(value == null)
                    continue;

                if(int.class.isInstance(value) || Integer.class.isInstance(value)){
                    if(((int)value) != 0)
                        return false;
                }else if(long.class.isInstance(value) || Long.class.isInstance(value)){
                    if(((long)value) != 0)
                        return false;
                }else if(float.class.isInstance(value) || Float.class.isInstance(value)){
                    if(((float)value) != 0)
                        return false;
                }else if(double.class.isInstance(value) || Double.class.isInstance(value)){
                    if(((double)value) != 0)
                        return false;
                }else if(char.class.isInstance(value) || Character.class.isInstance(value)){
                    if(((char)value) != 0)
                        return false;
                }else if(short.class.isInstance(value) || Short.class.isInstance(value)){
                    if(((short)value) != 0)
                        return false;
                }else if(byte.class.isInstance(value) || Byte.class.isInstance(value)){
                    if(((byte)value) != 0)
                        return false;
                }else {
                    return false;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public List<T> createEmptyDatas(int size){
        ArrayList<T> emptys = new ArrayList<>();
        if(size <= 0)
            return emptys;
        emptys.ensureCapacity(size);
        for (int i = 0; i < size; i++) {
            emptys.add(createEmptyData());
        }
        return emptys;
    }

    private void init(){
        mTitle = initTitleData();
    }

    private List<Title> initTitleData() {
        Type type = ReflectHelper.getGenericClass(getClass(), BaseTableModel.class, 0);
        if (!(type instanceof Class)){
            return null;
        }
        Class clazz = (Class) type;
        if(!TableData.class.isAssignableFrom(clazz))
            return null;

        dataClass = clazz;

        List<Title> tableNames = new ArrayList<>(5);
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Name annotation = field.getAnnotation(Name.class);
            if(annotation == null)
                continue;
            tableNames.add(new Title(annotation.value(), field));
        }
        return tableNames;
    }

    @Override
    public final int getRowCount() {
        if(mData == null)
            return 0;
        return mData.size();
    }

    @Override
    public final int getColumnCount() {
        if(mTitle == null)
            return 0;
        return mTitle.size();
    }

    @Override
    public final String getColumnName(int column) {
        if(mTitle.isEmpty())
            return "";
        String name = mTitle.get(column).name;
        if(name == null)
            name = "";

        return name;
    }

    @Override
    public final Object getValueAt(int rowIndex, int columnIndex) {
        T data = mData.get(rowIndex);
        if(data == null)
            return null;
        return getValueAt(data, columnIndex, mTitle.get(columnIndex).field.getAnnotation(Name.class));
    }

    /**
     * 如果字段是List Map, 需要重写该方法
     * @param data 数据
     * @param index 带有 Name 注解的字段的顺序
     * @param annotation 根据注解 Name 中的 id 字段来确定是 data 中的哪个字段
     * @return 返回要显示到界面的字符串
     */
    protected Object getValueAt(T data, int index, Name annotation){
        Field field = mTitle.get(index).field;
        if(!field.isAccessible())
            field.setAccessible(true);
        try {
            return field.get(data);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void setValueAt(T data, Object value, int index, Name annotation, int position){
        if(data == null)
            return;
        Field field = mTitle.get(index).field;
        if(!field.isAccessible())
            field.setAccessible(true);
        try {
            field.set(data, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public final void setValueAt(Object value, int position, int index) {
        T data = mData.get(position);
        setValueAt(data, value, index, mTitle.get(index).field.getAnnotation(Name.class), position);
        fireTableCellUpdated(position, index);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if(columnIndex < 0)
            return false;
        return editable;
    }

    private class Title{
        private String name;
        private Field field;

        public Title(String name, Field field) {
            this.name = name;
            this.field = field;
        }
    }
}