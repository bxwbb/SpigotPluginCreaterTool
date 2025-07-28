package org.bxwbb.spigotplugincreatertool.MinWindowS.MinecraftServerCreater.Server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CraftBukkitWebParser {
    // SpigotMC的CraftBukkit下载页面（可靠来源）
    private static final String CRAFTBUKKIT_DOWNLOAD_PAGE = "https://www.spigotmc.org/wiki/buildtools/";
    private static final String VERSION_TABLE_PATTERN = "<table[^>]*id=\"versions\"[^>]*>([\\s\\S]*?)</table>";
    private static final String VERSION_ROW_PATTERN = "<tr[^>]*>([\\s\\S]*?)</tr>";
    private static final String VERSION_CELL_PATTERN = "<td[^>]*>([\\s\\S]*?)</td>";
    private static final String LINK_PATTERN = "<a[^>]*href=\"([^\"]*)\"[^>]*>([\\s\\S]*?)</a>";

    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY = 2000;
    private static Scanner scanner = new Scanner(System.in);
    private static List<VersionInfo> versionList = new ArrayList<>();
    private static String downloadDir = System.getProperty("user.dir");

    public static void main(String[] args) {
        try {
            System.out.println("=== CraftBukkit版本解析工具 ===");
            System.out.println("正在从SpigotMC官网获取版本信息...");

            boolean loaded = false;
            for (int i = 0; i < MAX_RETRIES; i++) {
                try {
                    loadVersionsFromWeb();
                    loaded = true;
                    break;
                } catch (Exception e) {
                    System.out.println("获取失败，重试中 (" + (i + 1) + "/" + MAX_RETRIES + ")");
                    Thread.sleep(RETRY_DELAY);
                }
            }

            if (!loaded) {
                System.out.println("无法获取版本信息，请检查网络或页面结构是否变更");
                return;
            }

            if (versionList.isEmpty()) {
                System.out.println("未解析到任何CraftBukkit版本");
                return;
            }

            showMainMenu();

        } catch (Exception e) {
            System.err.println("错误: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    /**
     * 从网页解析版本信息
     */
    private static void loadVersionsFromWeb() throws Exception {
        String pageContent = downloadPageContent(CRAFTBUKKIT_DOWNLOAD_PAGE);

        // 提取版本表格
        Matcher tableMatcher = Pattern.compile(VERSION_TABLE_PATTERN).matcher(pageContent);
        if (!tableMatcher.find()) {
            throw new Exception("未找到版本表格，页面结构可能已变更");
        }

        // 提取行数据
        Matcher rowMatcher = Pattern.compile(VERSION_ROW_PATTERN).matcher(tableMatcher.group(1));
        boolean isFirstRow = true; // 跳过表头

        while (rowMatcher.find()) {
            if (isFirstRow) {
                isFirstRow = false;
                continue;
            }

            // 提取单元格数据
            Matcher cellMatcher = Pattern.compile(VERSION_CELL_PATTERN).matcher(rowMatcher.group(1));
            List<String> cells = new ArrayList<>();
            while (cellMatcher.find()) {
                cells.add(cleanHtml(cellMatcher.group(1)));
            }

            // 解析有效数据（至少需要版本号和下载信息）
            if (cells.size() >= 3) {
                String versionId = cells.get(0).trim();
                String description = cells.get(1).trim();
                String linkHtml = cells.get(2).trim();

                // 提取下载链接
                Matcher linkMatcher = Pattern.compile(LINK_PATTERN).matcher(linkHtml);
                String downloadUrl = null;
                if (linkMatcher.find()) {
                    downloadUrl = linkMatcher.group(1);
                    // 补全相对链接
                    if (downloadUrl.startsWith("/")) {
                        downloadUrl = "https://www.spigotmc.org" + downloadUrl;
                    }
                }

                if (versionId.length() > 0 && downloadUrl != null) {
                    versionList.add(new VersionInfo(
                            versionId,
                            description,
                            downloadUrl
                    ));
                }
            }
        }

        System.out.println("成功解析 " + versionList.size() + " 个CraftBukkit版本");
    }

    /**
     * 清理HTML标签，保留文本内容
     */
    private static String cleanHtml(String html) {
        return html.replaceAll("<[^>]+>", "").replaceAll("\\s+", " ").trim();
    }

    /**
     * 下载页面内容
     */
    private static String downloadPageContent(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
        connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml");

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("页面请求失败，响应码: " + responseCode);
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), "UTF-8"))) {

            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            return content.toString();
        } finally {
            connection.disconnect();
        }
    }

    /**
     * 显示主菜单
     */
    private static void showMainMenu() {
        while (true) {
            System.out.println("\n请选择操作:");
            System.out.println("1. 查看所有版本");
            System.out.println("2. 下载指定版本");
            System.out.println("3. 设置下载目录 (当前: " + downloadDir + ")");
            System.out.println("4. 退出");
            System.out.print("请输入选项(1-4): ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    showVersions();
                    break;
                case "2":
                    downloadVersion();
                    break;
                case "3":
                    setDownloadDir();
                    break;
                case "4":
                    System.out.println("再见！");
                    return;
                default:
                    System.out.println("无效选项，请重新输入");
            }
        }
    }

    /**
     * 显示版本列表
     */
    private static void showVersions() {
        System.out.println("\n=== 可用CraftBukkit版本 ===");
        System.out.printf("%-5s %-20s %-30s %s%n", "序号", "版本号", "描述", "下载链接");
        System.out.println("------------------------------------------------------------------------------------------------");

        for (int i = 0; i < versionList.size(); i++) {
            VersionInfo info = versionList.get(i);
            String urlPreview = info.downloadUrl.length() > 30 ?
                    info.downloadUrl.substring(0, 30) + "..." : info.downloadUrl;

            System.out.printf("%-5d %-20s %-30s %s%n",
                    i + 1,
                    info.versionId,
                    info.description.length() > 30 ? info.description.substring(0, 27) + "..." : info.description,
                    urlPreview);
        }
    }

    /**
     * 下载指定版本
     */
    private static void downloadVersion() {
        System.out.print("\n请输入版本序号(1-" + versionList.size() + "): ");
        try {
            int index = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (index < 0 || index >= versionList.size()) {
                System.out.println("无效序号");
                return;
            }

            VersionInfo version = versionList.get(index);
            System.out.println("准备下载: " + version.versionId);

            File outputFile = new File(downloadDir, "craftbukkit-" + version.versionId + ".jar");
            if (outputFile.exists()) {
                System.out.print("文件已存在，是否覆盖? (y/n): ");
                if (!scanner.nextLine().trim().equalsIgnoreCase("y")) {
                    System.out.println("已取消");
                    return;
                }
            }

            downloadFile(version.downloadUrl, outputFile.getAbsolutePath());
            System.out.println("下载完成: " + outputFile.getAbsolutePath());

        } catch (NumberFormatException e) {
            System.out.println("请输入数字");
        } catch (Exception e) {
            System.err.println("下载失败: " + e.getMessage());
        }
    }

    /**
     * 下载文件
     */
    private static void downloadFile(String urlStr, String outputPath) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(30000);
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("下载失败，响应码: " + responseCode);
        }

        try (InputStream in = connection.getInputStream();
             OutputStream out = new FileOutputStream(outputPath)) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            long total = 0;
            int fileSize = connection.getContentLength();

            System.out.println("文件大小: " + (fileSize > 0 ? fileSize / 1024 + " KB" : "未知"));

            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                total += bytesRead;

                if (fileSize > 0) {
                    int progress = (int) (total * 100 / fileSize);
                    if (progress % 10 == 0) {
                        System.out.println("进度: " + progress + "%");
                    }
                }
            }
        } finally {
            connection.disconnect();
        }
    }

    /**
     * 设置下载目录
     */
    private static void setDownloadDir() {
        System.out.print("请输入新下载目录: ");
        String dir = scanner.nextLine().trim();
        if (dir.isEmpty()) {
            System.out.println("保持当前目录");
            return;
        }

        File file = new File(dir);
        if (file.exists() && file.isDirectory()) {
            downloadDir = dir;
            System.out.println("目录已更新");
        } else if (file.mkdirs()) {
            downloadDir = dir;
            System.out.println("目录创建成功");
        } else {
            System.out.println("无法设置目录");
        }
    }

    /**
     * 版本信息类
     */
    private static class VersionInfo {
        String versionId;
        String description;
        String downloadUrl;

        VersionInfo(String versionId, String description, String downloadUrl) {
            this.versionId = versionId;
            this.description = description;
            this.downloadUrl = downloadUrl;
        }
    }
}
