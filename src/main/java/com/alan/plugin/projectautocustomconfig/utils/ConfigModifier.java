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
     * 如果新值和旧值相同，则不进行写操作。
     *
     * @param key   要修改的配置项的键
     * @param value 新的配置值
     * @throws IOException 如果读取或写入文件时发生异常
     */
    public void modifyConfig(String key, String value) throws IOException {
        // 读取文件的所有行
        List<String> lines = Files.readAllLines(configFilePath);

        // 查找指定的配置项并判断新值是否与当前值相同
        boolean modified = false;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.startsWith(key + "=")) {
                // 获取当前值
                String[] strings = line.split(key + "=");
                String currentValue = "";
                if (strings.length == 2) {
                    currentValue = strings[1];
                }
                // 如果新值和当前值不同，才进行修改
                if (!currentValue.equals(value)) {
                    lines.set(i, key + "=" + value); // 更新该行的值
                    modified = true;
                }
                break;
            }
        }

        // 如果配置项已被修改，才写回文件
        if (modified) {
            writeFile(lines);
        }
    }

    /**
     * 将修改后的内容写回文件，保持原文件的顺序和注释。
     * @param lines 修改后的文件内容
     * @throws IOException 如果写入文件时发生异常
     */
    private void writeFile(List<String> lines) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(configFilePath)) {
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                writer.write(line);

                // 只有当不是最后一行时，才添加换行符
                if (i < lines.size() - 1) {
                    writer.newLine();
                }
            }
        }
    }

//    public static void main(String[] args) {
//        try {
//            // 示例：指定配置文件路径
//            Path configFilePath = Path.of("path/to/your/config.properties");
//
//            // 初始化配置修改器并修改配置项
//            ConfigModifier configModifier = new ConfigModifier(configFilePath);
//            configModifier.modifyConfig("yourKey", "newValue");
//
//            System.out.println("配置项已修改");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
