package com.alan.plugin.projectautocustomconfig.listener;

import com.alan.plugin.projectautocustomconfig.state.MyPluginSettingsState;
import com.alan.plugin.projectautocustomconfig.utils.ConfigModifier;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Alan
 */
public class MyProjectActivity implements ProjectActivity {

    private static final Logger logger = Logger.getInstance(MyProjectActivity.class);

    @Override
    public @Nullable Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
        // 项目启动完毕后执行的代码
        String projectPath = project.getBasePath();
        String webAppPath = projectPath + "/webapp/src/main/webapp";
        String configPath = projectPath + "/webapp/src/main/resources/application-default.properties";
        if (new File(configPath).exists()) {
            // 获取自定义配置
            MyPluginSettingsState config = MyPluginSettingsState.getInstance();
            Path configFilePath = Path.of(configPath);
            ConfigModifier configModifier = new ConfigModifier(configFilePath);
            try {
                Map<String, String> configEntries = new HashMap<>();
                if (config.configEntries != null) {
                    configEntries.putAll(config.configEntries);
                }
                configEntries.put("lms.root", webAppPath);
                configModifier.batchModifyConfig(configEntries);
            } catch (IOException e) {
                logger.error("配置文件修改失败", e);
            }
        }
        return null;
    }
}
