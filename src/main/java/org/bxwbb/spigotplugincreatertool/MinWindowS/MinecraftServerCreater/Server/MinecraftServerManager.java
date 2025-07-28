package org.bxwbb.spigotplugincreatertool.MinWindowS.MinecraftServerCreater.Server;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

public class MinecraftServerManager {

    static final Logger logger = LoggerFactory.getLogger(MinecraftServerManager.class);

    // 版本清单的URL
    private static final String VERSION_MANIFEST_URL = "https://launchermeta.mojang.com/mc/game/version_manifest.json";

    // 存储所有版本信息
    private List<VersionInfo> allVersions;
    // 存储最新的发布版本
    private VersionInfo latestRelease;
    // 存储最新的快照版本
    private VersionInfo latestSnapshot;

    // 版本信息内部类
    public static class VersionInfo {
        public String id;          // 版本ID，如"1.19.4"
        public String type;        // 版本类型，如"release"或"snapshot"
        public String url;         // 版本详细信息的URL
        public String releaseTime; // 发布时间

        public VersionInfo(String id, String type, String url, String releaseTime) {
            this.id = id;
            this.type = type;
            this.url = url;
            this.releaseTime = releaseTime;
        }

        @Override
        public String toString() {
            return String.format("%s (%s) - 发布于: %s", id, type, releaseTime);
        }
    }

    // 构造函数，初始化时加载版本信息
    public MinecraftServerManager() {
        try {
            loadVersionManifest();
        } catch (Exception e) {
            logger.error("加载版本清单失败: {}", e.getMessage());
            allVersions = new ArrayList<>();
        }
    }

    // 从官方API加载版本清单
    private void loadVersionManifest() throws Exception {
        // 下载版本清单JSON
        String manifestJson = downloadString(VERSION_MANIFEST_URL);
        logger.info(manifestJson);
        JSONObject manifest = JSON.parseObject(manifestJson);

        // 解析最新版本信息
        JSONObject latest = manifest.getJSONObject("latest");
        String latestReleaseId = latest.getString("release");
        String latestSnapshotId = latest.getString("snapshot");

        // 解析所有版本
        JSONArray versionsArray = manifest.getJSONArray("versions");
        allVersions = new ArrayList<>(versionsArray.size());

        for (int i = 0; i < versionsArray.size(); i++) {
            JSONObject versionObj = versionsArray.getJSONObject(i);
            VersionInfo version = new VersionInfo(
                    versionObj.getString("id"),
                    versionObj.getString("type"),
                    versionObj.getString("url"),
                    versionObj.getString("releaseTime")
            );

            allVersions.add(version);

            // 标记最新版本
            if (version.id.equals(latestReleaseId)) {
                latestRelease = version;
            }
            if (version.id.equals(latestSnapshotId)) {
                latestSnapshot = version;
            }
            logger.trace("获取版本信息: {}", version.id);
        }

        logger.info("获取所有版本完成");
    }

    // 获取所有版本
    public List<VersionInfo> getAllVersions() {
        return new ArrayList<>(allVersions);
    }

    // 获取所有发布版本
    public List<VersionInfo> getReleaseVersions() {
        List<VersionInfo> releases = new ArrayList<>();
        for (VersionInfo version : allVersions) {
            if ("release".equals(version.type)) {
                releases.add(version);
            }
        }
        return releases;
    }

    // 获取所有快照版本
    public List<VersionInfo> getSnapshotVersions() {
        List<VersionInfo> snapshots = new ArrayList<>();
        for (VersionInfo version : allVersions) {
            if ("snapshot".equals(version.type)) {
                snapshots.add(version);
            }
        }
        return snapshots;
    }

    // 获取最新发布版本
    public VersionInfo getLatestRelease() {
        return latestRelease;
    }

    // 获取最新快照版本
    public VersionInfo getLatestSnapshot() {
        return latestSnapshot;
    }

    // 根据版本ID查找版本信息
    public VersionInfo findVersionById(String versionId) {
        for (VersionInfo version : allVersions) {
            if (version.id.equalsIgnoreCase(versionId)) {
                return version;
            }
        }
        return null;
    }

    // 下载指定版本的服务器端
    public boolean downloadServer(String versionId, String savePath) {
        try {
            VersionInfo version = findVersionById(versionId);
            if (version == null) {
                System.err.println("找不到版本: " + versionId);
                return false;
            }

            // 下载版本详细信息
            String versionJson = downloadString(version.url);
            JSONObject versionDetails = JSON.parseObject(versionJson);
            String serverUrl = versionDetails.getJSONObject("downloads")
                    .getJSONObject("server")
                    .getString("url");

            // 下载服务器JAR文件
            System.out.println("开始下载 " + versionId + " 服务器端...");
            downloadFile(serverUrl, savePath);
            System.out.println("下载完成，保存至: " + savePath);
            return true;
        } catch (Exception e) {
            logger.error("下载失败: {}", e.toString());
            return false;
        }
    }

    // 下载文本内容
    private String downloadString(String urlString) throws Exception {
        URL url = new URL(urlString);
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(url.openStream(), "UTF-8"))) {

            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            return result.toString();
        }
    }

    // 下载文件
    private void downloadFile(String urlString, String savePath) throws Exception {
        URL url = new URL(urlString);
        try (InputStream in = url.openStream();
             OutputStream out = Files.newOutputStream(Paths.get(savePath))) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            long totalBytes = 0;

            // 获取文件大小
            URLConnection connection = url.openConnection();
            long fileSize = connection.getContentLengthLong();

            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                totalBytes += bytesRead;

                // 显示下载进度
                if (fileSize > 0) {
                    int progress = (int) ((totalBytes * 100) / fileSize);
                    System.out.printf("\r下载进度: %d%%", progress);
                }
            }
            System.out.println(); // 换行
        }
    }

    // 主方法，提供控制台交互
    public static void main(String[] args) {
        MinecraftServerManager manager = new MinecraftServerManager();
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Minecraft服务器版本管理器 ===");
        System.out.println("1. 显示所有版本");
        System.out.println("2. 显示所有发布版本");
        System.out.println("3. 显示所有快照版本");
        System.out.println("4. 显示最新发布版本");
        System.out.println("5. 显示最新快照版本");
        System.out.println("6. 下载指定版本服务器端");
        System.out.println("0. 退出");

        while (true) {
            System.out.print("\n请选择操作 (0-6): ");
            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("请输入有效的数字!");
                continue;
            }

            switch (choice) {
                case 1:
                    System.out.println("\n--- 所有版本 ---");
                    for (VersionInfo version : manager.getAllVersions()) {
                        System.out.println(version);
                    }
                    break;

                case 2:
                    System.out.println("\n--- 所有发布版本 ---");
                    for (VersionInfo version : manager.getReleaseVersions()) {
                        System.out.println(version);
                    }
                    break;

                case 3:
                    System.out.println("\n--- 所有快照版本 ---");
                    for (VersionInfo version : manager.getSnapshotVersions()) {
                        System.out.println(version);
                    }
                    break;

                case 4:
                    VersionInfo latestRelease = manager.getLatestRelease();
                    if (latestRelease != null) {
                        System.out.println("\n--- 最新发布版本 ---");
                        System.out.println(latestRelease);
                    } else {
                        System.out.println("无法获取最新发布版本信息");
                    }
                    break;

                case 5:
                    VersionInfo latestSnapshot = manager.getLatestSnapshot();
                    if (latestSnapshot != null) {
                        System.out.println("\n--- 最新快照版本 ---");
                        System.out.println(latestSnapshot);
                    } else {
                        System.out.println("无法获取最新快照版本信息");
                    }
                    break;

                case 6:
                    System.out.print("请输入要下载的版本ID (例如: 1.19.4): ");
                    String versionId = scanner.nextLine().trim();

                    System.out.print("请输入保存路径 (例如: ./server.jar): ");
                    String savePath = scanner.nextLine().trim();

                    manager.downloadServer(versionId, savePath);
                    break;

                case 0:
                    System.out.println("感谢使用，再见!");
                    scanner.close();
                    System.exit(0);
                    break;

                default:
                    System.out.println("无效的选择，请输入0-6之间的数字!");
            }
        }
    }
}
    