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

import com.intellij.openapi.wm.ToolWindow;
import open.log.plugins.log_level.AndroidLogLevel;
import open.log.plugins.log_level.LogLevel;
import open.log.plugins.log_parser.AndroidLogLevelParser;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * Created by eshel on 2019/8/23.
 */
public class LogView {
    private JComboBox cbFiles;
    private JComboBox cbLevel;
    private JTextField etFilter;
    private JCheckBox cbRegex;
    private JComboBox logText;
    private JPanel mRoot;
    private JTextPane logContent;

    public LogView(ToolWindow toolWindow) {
        //设置不可编辑
        logContent.setEditable(false);
        logContent.setDocument(new DefaultStyledDocument());
        //显示文本指针
        logContent.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent mouseEvent) {
                logContent.setCursor(new Cursor(Cursor.TEXT_CURSOR));   //鼠标进入Text区后变为文本输入指针
            }

            public void mouseExited(MouseEvent mouseEvent) {
                logContent.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));   //鼠标离开Text区后恢复默认形态
            }
        });
        //添加光标
        logContent.getCaret().addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                logContent.getCaret().setVisible(true);   //使Text区的文本光标显示
            }
        });

        new DropTarget(logContent, DnDConstants.ACTION_COPY_OR_MOVE, new DropTargetAdapter() {
            @Override
            public void drop(DropTargetDropEvent dtde) {
                try {
                    // 如果拖入的文件格式受支持
                    if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                        // 接收拖拽来的数据
                        dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                        @SuppressWarnings("unchecked")
                        List<File> list = (List<File>) (dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor));
                        logContent.setText("");
                        File logFile = IntStream.range(0, list.size()).mapToObj(list::get).findFirst().orElse(null);

                        if (Objects.nonNull(logFile)) {
                            // 使用流读取文件, 写入日志区域
                            BufferedReader br = new BufferedReader(new FileReader(logFile));
                            String line;
                            LogLevel lastLevel = AndroidLogLevel.Verbose;
                            while ((line = br.readLine()) != null) {
                                LogLevel logLevel = AndroidLogLevelParser.INSTANCE.checkLogLevel(line);
                                if(logLevel == null){
                                    logLevel = lastLevel;
                                }else {
                                    lastLevel = logLevel;
                                }
                                line += "\n";
                                Document document = logContent.getDocument();
                                document.insertString(document.getLength(), line, logLevel.textStyle());
                            }
                            br.close();
                        }

                        // 指示拖拽操作已完成
                        dtde.dropComplete(true);
                    } else {
                        // 拒绝拖拽来的数据
                        dtde.rejectDrop();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public JComponent getContent() {
        return mRoot;
    }
}
