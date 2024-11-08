package com.alan.plugin.projectautocustomconfig.listener;

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

public class MyProjectActivity implements ProjectActivity {

    private static final Logger logger = Logger.getInstance(MyProjectActivity.class);

    @Override
    public @Nullable Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
        // 项目启动完毕后执行的代码
        String projectPath = project.getBasePath();
        String webAppPath = projectPath + "/webapp/src/main/webapp";
        String configPath = projectPath + "/webapp/src/main/resources/application-default.properties";
        if (new File(configPath).exists()) {
            Path configFilePath = Path.of(configPath);
            ConfigModifier configModifier = new ConfigModifier(configFilePath);
            try {
                configModifier.modifyConfig("lms.root", webAppPath);
            } catch (IOException e) {
                logger.error("配置文件修改失败", e);
            }
        }
        return null;
    }
}
