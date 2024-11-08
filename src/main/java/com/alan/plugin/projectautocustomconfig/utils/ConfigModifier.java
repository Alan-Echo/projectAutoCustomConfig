package com.alan.plugin.projectautocustomconfig.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ConfigModifier {

    private final Path configFilePath;

    public ConfigModifier(Path configFilePath) {
        this.configFilePath = configFilePath;
    }

    /**
     * 修改配置文件中的指定配置项值，保持文件的原始顺序和注释。
     *
     * @param key   要修改的配置项的键
     * @param value 新的配置值
     * @throws IOException 如果读取或写入文件时发生异常
     */
    public void modifyConfig(String key, String value) throws IOException {
        // 读取文件的所有行
        List<String> lines = Files.readAllLines(configFilePath);

        // 修改特定的配置行
        List<String> modifiedLines = lines.stream().map(line -> {
            // 检查该行是否是目标配置项
            if (line.startsWith(key + "=")) {
                return key + "=" + value; // 更新该行的值
            }
            return line; // 保持原行不变
        }).toList();

        // 将修改后的内容写回文件
        try (BufferedWriter writer = Files.newBufferedWriter(configFilePath)) {
            for (String line : modifiedLines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

//    public static void main(String[] args) {
//        try {
//            // 示例：指定配置文件路径
//            Path configFilePath = Path.of("path/to/your/config.properties");
//
//            // 初始化配置修改器并修改配置值
//            ConfigModifier configModifier = new ConfigModifier(configFilePath);
//            configModifier.modifyConfig("yourKey", "newValue");
//
//            System.out.println("配置项已修改");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}

