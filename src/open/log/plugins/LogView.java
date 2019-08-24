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

import javax.swing.*;

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

    public LogView(ToolWindow toolWindow) {
//        hideToolWindowButton.addActionListener(e -> toolWindow.hide(null));
//        refreshToolWindowButton.addActionListener(e -> currentDateTime());
//
//        this.currentDateTime();
    }

    public JComponent getContent() {
        return mRoot;
    }
}
