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

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.util.Vector;

/**
 * Created by EshelGuo on 2019/7/12.
 */
@Deprecated
public class TableView extends JTable{

    public TableView() {
        init();
    }

    public TableView(TableModel dm) {
        super(dm);
        init();
    }

    public TableView(TableModel dm, TableColumnModel cm) {
        super(dm, cm);
        init();
    }

    public TableView(TableModel dm, TableColumnModel cm, ListSelectionModel sm) {
        super(dm, cm, sm);
        init();
    }

    public TableView(int numRows, int numColumns) {
        super(numRows, numColumns);
        init();
    }

    public TableView(Vector rowData, Vector columnNames) {
        super(rowData, columnNames);
        init();
    }

    public TableView(@NotNull Object[][] rowData, @NotNull Object[] columnNames) {
        super(rowData, columnNames);
        init();
    }

    private void init(){
        setRowHeight((int) (getRowHeight()*1.5f));
    }
}
