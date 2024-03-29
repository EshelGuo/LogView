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

package open.log.plugins.log_level;

import com.intellij.ui.JBColor;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 * Created by eshel on 2019/8/31.
 */
public class Debug extends LogLevel{

    @Override
    protected void configTextStyle(SimpleAttributeSet attribute) {
        super.configTextStyle(attribute);
        StyleConstants.setForeground(attribute,new JBColor(0x018AFF, 0x018AFF));
    }
}
