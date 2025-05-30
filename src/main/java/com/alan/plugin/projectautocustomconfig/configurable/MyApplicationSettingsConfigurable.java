package com.alan.plugin.projectautocustomconfig.configurable;

import com.alan.plugin.projectautocustomconfig.state.MyPluginSettingsState;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Alan
 */
public class MyApplicationSettingsConfigurable implements Configurable {

    private final JBTable configTable;
    private final DefaultTableModel tableModel;
    private final ToolbarDecorator toolbarDecorator;

    public MyApplicationSettingsConfigurable() {
        // 创建表格模型
        tableModel = new DefaultTableModel(new Object[]{"Key", "Value"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true; // 所有单元格都可编辑
            }
        };

        // 创建带工具栏的表格
        configTable = new JBTable(tableModel);
        configTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        configTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        configTable.getColumnModel().getColumn(1).setPreferredWidth(300);

        // 添加表格工具栏（添加/删除按钮）
        toolbarDecorator = ToolbarDecorator.createDecorator(configTable)
                .setAddAction(this::addRow)
                .setRemoveAction(this::removeRow)
                .setRemoveActionUpdater(e -> configTable.getSelectedRowCount() > 0);
    }

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        // 设置界面显示名称
        return "ProjectAutoCustomConfig";
    }

    @Override
    public JComponent createComponent() {
        // 创建主面板
        JPanel mainPanel = new JPanel(new BorderLayout());

        // 添加表格和工具栏
        JPanel tablePanel = toolbarDecorator.createPanel();
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        // 添加说明标签
        JLabel hintLabel = new JLabel("<html>Add custom key-value pairs. Keys must be unique.<br>Example: API_KEY=your_key, TIMEOUT=30</html>");
        hintLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(hintLabel, BorderLayout.SOUTH);

        return mainPanel;
    }

    private void addRow(AnActionButton e) {
        // 添加空行
        tableModel.addRow(new Object[]{"", ""});
        // 滚动到最后一行
        configTable.scrollRectToVisible(configTable.getCellRect(tableModel.getRowCount() - 1, 0, true));
    }

    private void removeRow(AnActionButton e) {
        // 删除选中行
        int selectedRow = configTable.getSelectedRow();
        if (selectedRow >= 0) {
            tableModel.removeRow(selectedRow);
        }
    }

    @Override
    public boolean isModified() {
        MyPluginSettingsState state = MyPluginSettingsState.getInstance();
        Map<String, String> currentConfig = getConfigFromTable();

        // 检查配置项数量是否变化
        if (currentConfig.size() != state.configEntries.size()) {
            return true;
        }

        // 检查每个键值对是否变化
        for (Map.Entry<String, String> entry : currentConfig.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (!state.configEntries.containsKey(key) ||
                    !state.configEntries.get(key).equals(value)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void apply() throws ConfigurationException {
        MyPluginSettingsState state = getInitializedState();
        Map<String, String> newConfig = getConfigFromTable();

        // 验证键是否唯一
        if (newConfig.size() < tableModel.getRowCount()) {
            Messages.showErrorDialog(
                    "Duplicate keys found. Each key must be unique.",
                    "Configuration Error"
            );
            return;
        }

        state.configEntries.clear();
        state.configEntries.putAll(newConfig);
    }

    @Override
    public void reset() {
        // 确保状态对象已初始化
        MyPluginSettingsState state = getInitializedState();

        // 清空表格
        tableModel.setRowCount(0);

        // 从状态加载数据
        for (Map.Entry<String, String> entry : state.configEntries.entrySet()) {
            tableModel.addRow(new Object[]{entry.getKey(), entry.getValue()});
        }
    }

    private MyPluginSettingsState getSettings() {
        return MyPluginSettingsState.getInstance();
    }

    private Map<String, String> getConfigFromTable() {
        Map<String, String> config = new LinkedHashMap<>();
        int rowCount = tableModel.getRowCount();

        for (int i = 0; i < rowCount; i++) {
            String key = (String) tableModel.getValueAt(i, 0);
            String value = (String) tableModel.getValueAt(i, 1);

            // 跳过空键
            if (key != null && !key.trim().isEmpty()) {
                config.put(key.trim(), value != null ? value.trim() : "");
            }
        }

        return config;
    }

    private MyPluginSettingsState getInitializedState() {
        MyPluginSettingsState state = MyPluginSettingsState.getInstance();
        // 确保状态对象非空
        if (state == null) {
            state = new MyPluginSettingsState();
        }
        state.init(); // 显式初始化
        return state;
    }
}
