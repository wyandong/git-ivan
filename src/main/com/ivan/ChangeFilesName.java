package main.com.ivan;

import java.io.File;

/**
 * 批量重命名工具类
 *
 * <p>
 * 功能：遍历指定目录 D:\aaa 下的所有文件，对其文件名做统一处理：
 * <ol>
 * <li>移除文件名中的【字幕】和LLM 大模型翻译等标记；</li>
 * <li>在文件扩展名前追加 "zh" 后缀（如 video.srt -> videozh.srt）；
 * 若文件名末尾已有 "zh" 则不重复追加；</li>
 * <li>若目标文件名已存在或重命名失败，则跳过并打印提示，不覆盖已有文件。</li>
 * </ol>
 */
public class ChangeFilesName {

    /**
     * 程序入口：定位目标目录，遍历其中每个文件并重命名
     */
    public static void main(String[] args) {

        // 批量处理 D:\aaa 文件夹下的所有文件
        renameFiles("D:\\aaa");
    }

    private static void renameFiles(String file_path) {

        // 需要批量重名的目标文件夹
        File dir = new File(file_path);

        // 校验目录合法性：不存在或不是目录时直接退出
        if (!dir.exists() || !dir.isDirectory()) {
            System.out.println("Folder not found: " + dir.getAbsolutePath());
            return;
        }

        // 列出目录下所有文件和子文件夹
        // 注意：listFiles() 在不可读（如权限不足）时返回 null
        File[] files = dir.listFiles();

        if (files == null) {
            System.out.println("Cannot read folder: " + dir.getAbsolutePath());
            return;
        }

        // 逐个处理每个文件
        for (File file : files) {

            String oldName = file.getName();
            String newName = buildNewName(oldName);

            // 新名字与旧名字相同说明无需改动，直接跳过
            if (oldName.equals(newName)) {
                System.out.println("Skip unchanged: " + oldName);
                continue;
            }

            // 重名总是发生在同一个父目录内，因此用当前文件的父目录拼接新文件名
            File newFile = new File(file.getParentFile(), newName);

            // 目标名已存在时不能覆盖（不同 hash/content 可能被同名的视频撞名），直接跳过
            if (newFile.exists()) {
                System.out.println("Skip target already exists: " + newFile.getName());
                continue;
            }

            // 执行重命名，成功/失败都打印日志便于排查
            boolean success = file.renameTo(newFile);

            if (success) {
                System.out.println("Renamed: " + oldName + " -> " + newName);
            } else {
                // 重命名失败的常见原因：文件被其他进程占用、路径无效等
                System.out.println("Failed:  " + oldName + " -> " + newName);
            }
        }
    }

    /**
     * 根据原文件名生成新文件名
     *
     * <p>
     * 规则：
     * <ul>
     * <li>删除文件名中出现的【字幕】字样，去掉两端空白；</li>
     * <li>定位最后一个 "." 作为扩展名分隔符（如 .srt）；
     * 如果存在扩展名，就在「基础名 + . 扩展名」中间插入 "zh"；</li>
     * <li>如果末尾已是 "zh" 结尾，保持原样不重复追加；</li>
     * <li>无扩展名的文件，在末尾追加 "zh"。</li>
     * </ul>
     *
     * @param fileName 原文件名（不含路径）
     * @return 处理后的新文件名
     */
    private static String buildNewName(String fileName) {

        // 第 1 步：移除【字幕】和LLM 大模型翻译等标记，并 trim 掉可能被替换出的首尾空格
        String cleaned = fileName.replace("【字幕】", "").replaceAll("LLM 大模型翻译", "").trim();

        // 第 2 步：从末尾往前找第一个 "." 的位置，可以用来切出扩展名
        int dotIndex = cleaned.lastIndexOf(".");

        // 第 3 步：存在扩展名时（合法情况：既不在开头也不在末尾），拼接 "zh"
        if (dotIndex > 0 && dotIndex < cleaned.length() - 1) {

            // 切出基础名（扩展名前的部分）
            String baseName = cleaned.substring(0, dotIndex);

            //
            String extension = cleaned.substring(dotIndex);

            // 追加 "zh" 后缀，避免像 "video.srtr" / "videozh.srt" 这样的名字重名
            if (!baseName.endsWith("zh")) {
                baseName = baseName + "zh";
            }

            return baseName + extension;
        }

        return cleaned;
    }
}