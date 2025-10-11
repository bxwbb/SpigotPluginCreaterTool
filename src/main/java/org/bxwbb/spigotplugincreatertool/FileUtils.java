package org.bxwbb.spigotplugincreatertool;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class FileUtils {
    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    // 进程相关变量
    private Process currentProcess;
    private boolean isExecuting = false;
    private OutputStreamWriter processInput;

    // 事件监听器列表
    private final List<Consumer<Integer>> completionListeners = new ArrayList<>();
    private final List<Consumer<String>> outputListeners = new ArrayList<>();

    /**
     * 异步执行BAT文件
     */
    public void executeBatAsync(String batFilePath) {
        // 参数校验
        if (batFilePath == null || batFilePath.trim().isEmpty()) {
            logger.error("BAT文件路径不能为空");
            return;
        }

        File batFile = new File(batFilePath);
        if (!batFile.exists() || !batFile.isFile() || !batFile.getName().toLowerCase().endsWith(".bat")) {
            logger.error("无效的BAT文件: {}", batFilePath);
            return;
        }

        // 终止现有进程
        if (isExecuting) {
            logger.warn("终止现有BAT进程");
            stopExecution();
        }

        // 异步执行
        executor.submit(() -> {
            ProcessBuilder pb = new ProcessBuilder();
            pb.directory(batFile.getParentFile());
            pb.command("cmd.exe", "/c", "\"" + batFilePath + "\"");

            // 配置输入输出流用于交互
            pb.redirectInput(ProcessBuilder.Redirect.PIPE);
            pb.redirectOutput(ProcessBuilder.Redirect.PIPE);
            pb.redirectErrorStream(true);

            try {
                currentProcess = pb.start();
                isExecuting = true;
                logger.info("BAT进程已启动: {}", batFilePath);

                // 初始化输入流
                processInput = new OutputStreamWriter(
                        currentProcess.getOutputStream(),
                        StandardCharsets.UTF_8
                );

                // 启动输出监听线程
                startOutputListener(currentProcess.getInputStream());

                // 等待进程完成
                int exitCode = currentProcess.waitFor();
                logger.info("BAT进程执行完成，退出码: {}", exitCode);

                // 触发完成事件
                triggerCompletionEvent(exitCode);

            } catch (IOException e) {
                logger.error("BAT执行失败", e);
                triggerCompletionEvent(-2);
            } catch (InterruptedException e) {
                logger.error("BAT执行被中断", e);
                Thread.currentThread().interrupt();
                triggerCompletionEvent(-3);
            } finally {
                isExecuting = false;
                closeResources();
            }
        });
    }

    /**
     * 启动输出监听线程，有新输出时触发事件
     */
    private void startOutputListener(InputStream inputStream) {
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // 触发输出事件
                    triggerOutputEvent(line);
                }
            } catch (IOException e) {
                if (!isExecuting()) {
                    logger.info("输出流已关闭");
                } else {
                    logger.error("读取输出失败", e);
                }
            }
        }, "BatOutputListener").start();
    }

    // 触发完成事件
    private void triggerCompletionEvent(int exitCode) {
        for (Consumer<Integer> listener : completionListeners) {
            try {
                listener.accept(exitCode);
            } catch (Exception e) {
                logger.error("完成事件监听器执行失败", e);
            }
        }
    }

    // 触发输出事件
    private void triggerOutputEvent(String outputLine) {
        logger.info("[BAT输出] {}", outputLine);
        for (Consumer<String> listener : outputListeners) {
            try {
                listener.accept(outputLine);
            } catch (Exception e) {
                logger.error("输出事件监听器执行失败", e);
            }
        }
    }

    /**
     * 添加执行完成事件监听器
     */
    public void addCompletionListener(Consumer<Integer> listener) {
        if (listener != null) {
            completionListeners.add(listener);
        }
    }

    /**
     * 添加输出事件监听器
     */
    public void addOutputListener(Consumer<String> listener) {
        if (listener != null) {
            outputListeners.add(listener);
        }
    }

    /**
     * 移除所有监听器
     */
    public void clearListeners() {
        completionListeners.clear();
        outputListeners.clear();
    }

    /**
     * 检查是否正在执行
     */
    public boolean isExecuting() {
        return isExecuting && currentProcess != null && currentProcess.isAlive();
    }

    /**
     * 向BAT进程发送输入
     */
    public boolean sendInput(String input) {
        if (!isExecuting() || processInput == null) {
            logger.error("没有运行中的BAT进程，无法发送输入");
            return false;
        }

        try {
            processInput.write(input);
            processInput.write(System.lineSeparator());
            processInput.flush();
            logger.info("已发送输入: {}", input);
            return true;
        } catch (IOException e) {
            logger.error("发送输入失败", e);
            return false;
        }
    }

    /**
     * 停止当前执行的BAT进程
     */
    public void stopExecution() {
        if (isExecuting() && currentProcess != null) {
            currentProcess.destroy();
            logger.info("已终止BAT进程");
        }
        isExecuting = false;
        closeResources();
    }

    // 关闭资源
    private void closeResources() {
        if (processInput != null) {
            try {
                processInput.close();
            } catch (IOException e) {
                logger.warn("关闭输入流失败", e);
            }
            processInput = null;
        }
        currentProcess = null;
    }

    // 原有文件操作方法
    public static boolean writeLinesToFile(String filePath, Iterable<String> contentList) {
        if (filePath == null || filePath.trim().isEmpty()) {
            logger.error("文件路径不能为空");
            return false;
        }
        if (contentList == null) {
            logger.error("写入内容列表不能为null");
            return false;
        }

        Path file = Paths.get(filePath);
        Path parentDir = file.getParent();

        try {
            if (parentDir != null && !Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
                logger.info("已创建目录: {}", parentDir);
            }

            try (var writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                for (String line : contentList) {
                    writer.write(line);
                    writer.newLine();
                }
            }

            logger.info("文件写入成功: {}", filePath);
            return true;
        } catch (IOException e) {
            logger.error("文件写入失败: " + filePath, e);
            return false;
        }
    }

    public static List<String> readLinesFromFile(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            logger.error("文件路径不能为空");
            return null;
        }

        Path file = Paths.get(filePath);
        if (!Files.exists(file) || !Files.isRegularFile(file)) {
            logger.error("文件不存在或无效: {}", filePath);
            return null;
        }

        try {
            List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
            logger.info("文件读取成功: {}（{}行）", filePath, lines.size());
            return lines;
        } catch (IOException e) {
            logger.error("文件读取失败: " + filePath, e);
            return null;
        }
    }

    // 测试方法
    public static void main(String[] args) throws InterruptedException {
        FileUtils executor = new FileUtils();

        // 创建测试BAT
        String batPath = "test_event.bat";
        writeLinesToFile(batPath, List.of(
                "@echo off",
                "echo 启动中...",
                "timeout /t 1 /nobreak >nul",
                "echo 请输入服务器名称:",
                "set /p serverName=",
                "echo 服务器名称已设置为: %serverName%",
                "echo 启动完成",
                "pause"
        ));

        // 添加输出事件监听器
        executor.addOutputListener(output -> {
            System.out.println("【输出事件】" + output);

            // 自动响应交互
            if (output.contains("请输入服务器名称:")) {
                executor.sendInput("我的世界服务器");
            } else if (output.contains("按任意键继续")) {
                executor.sendInput(""); // 响应pause
            }
        });

        // 添加完成事件监听器
        executor.addCompletionListener(exitCode -> {
            System.out.println("【完成事件】执行结束，退出码: " + exitCode);
        });

        // 执行BAT
        executor.executeBatAsync(batPath);

        // 等待执行完成
        while (executor.isExecuting()) {
            Thread.sleep(500);
        }
    }

}