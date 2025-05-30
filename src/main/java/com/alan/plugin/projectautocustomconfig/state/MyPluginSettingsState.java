package com.alan.plugin.projectautocustomconfig.state;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Alan
 */
@State(
        name = "projectAutoCustomConfig",
        storages = @Storage("projectAutoCustomConfig.xml") // 存储文件名
)
public class MyPluginSettingsState implements PersistentStateComponent<MyPluginSettingsState> {

    // 使用LinkedHashMap保持插入顺序
    public Map<String, String> configEntries = new LinkedHashMap<>();

    // 添加无参构造函数确保正确实例化
    public MyPluginSettingsState() {}

    @Nullable
    @Override
    public MyPluginSettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull MyPluginSettingsState state) {
        // 添加空值检查
        if (state.configEntries != null) {
            this.configEntries = new LinkedHashMap<>(state.configEntries);
        }
    }

    public static MyPluginSettingsState getInstance() {
        return ApplicationManager.getApplication().getService(MyPluginSettingsState.class);
    }

    // 添加初始化方法确保非空
    public void init() {
        if (configEntries == null) {
            configEntries = new LinkedHashMap<>();
        }
    }
}
