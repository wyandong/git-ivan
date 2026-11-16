package main.com.ivan;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Stream;

/**
 * Java File 类典型操作演示
 * 主要涉及：
 * 1. 目录的创建（单级/多级）
 * 2. 文件的创建、删除
 * 3. 目录的浅遍历与递归遍历（含 NIO）
 * 4. 目录属性的获取
 * 5. 重命名 / 移动 / 复制
 */
public class FileOperate {

    /** 演示用的根目录（相对于项目根目录） */
    private static final String BASE_DIR = "file_demo";

    public static void main(String[] args) throws IOException {
        // 0. 清理旧的演示目录，保证可重复运行
        String base = new File(BASE_DIR).getAbsolutePath();
        File dirFile = new File(base);
        deleteRecursively(dirFile);

        // 1. 创建目录
        mkdirDemo(base);
        // 2. 创建文件
        createFileDemo(base);
        // 3. 浅遍历
        listShallow(base);
        // 4. 递归遍历（传统 File）
        walkRecursive(new File(base), 0);
        // 5. NIO 遍历
        walkNio(base);
        // 6. 打印目录属性
        printFileAttrs(new File(base, "sub"));
        // 7. 重命名/移动/复制
        renameCopyDemo(base);

        // 8. 清理
        deleteRecursively(dirFile);
        System.out.println("演示完成，已清理目录: " + dirFile);
    }

    /**
     * 目录创建演示：mkdir（单级）、mkdirs（多级）
     */
    static void mkdirDemo(String base) {
        File root = new File(base);
        File one = new File(root, "dir");

        boolean r1 = root.mkdir();
        System.out.println("mkdir(root): " + r1);

        boolean r2 = one.mkdir();
        System.out.println("mkdir(dir): " + r2);

        File a = new File(root, "a");
        File b = new File(a, "b");
        File c = new File(b, "c");
        boolean r3 = c.mkdirs(); // 一次性创建 a/b/c
        System.out.println("mkdirs(a/b/c): " + r3 + "  存在: " + c.exists());
    }

    /**
     * 创建文件演示
     */
    static void createFileDemo(String base) throws IOException {
        File root = new File(base);
        File f1 = new File(new File(root, "dir"), "hello.txt");
        boolean created = f1.createNewFile();
        System.out.println("createNewFile(hello.txt): " + created);

        // NIO 创建带内容文件
        Path f2 = Paths.get(base, "dir", "hello2.txt");
        Files.write(f2, "Hello from Files.write".getBytes());
        System.out.println("Files.write(hello2.txt) 存在: " + Files.exists(f2));
    }

    /**
     * 浅遍历：只列出第一层
     *  list()         -> String[]，可指定文件名过滤器
     *  listFiles()    -> File[]
     *  listFiles(FileFilter)
     */
    static void listShallow(String base) throws IOException {
        File root = new File(base);
        System.out.println("\n===== list(): 所有条目（仅文件） =====");
        String[] fileNames = root.list((dir, name) -> !new File(dir, name).isDirectory());
        Arrays.stream(fileNames).sorted().forEach(n -> System.out.println("  " + n));

        System.out.println("===== listFiles() + 过滤器：只列目录 =====");
        File rootDir = new File(base, "a/b");
        File[] dirs = rootDir.listFiles(File::isDirectory);
        Arrays.stream(dirs).forEach(f -> System.out.println("  [DIR] " + f.getName()));
    }

    /**
     * 递归遍历：传统 File 实现
     */
    static void walkRecursive(File dir, int depth) {
        String indent = "  ".repeat(depth);
        System.out.println(indent + (dir.isDirectory() ? "[DIR] " : "[FILE] ") + dir.getName());

        if (!dir.isDirectory()) {
            return;
        }
        File[] children = dir.listFiles();
        if (children == null) return;
        // 排序：目录在前，文件在后，目录按名称
        Arrays.sort(children, Comparator.comparing(File::isDirectory).reversed()
                .thenComparing(File::getName));
        for (File child : children) {
            walkRecursive(child, depth + 1);
        }
    }

    /**
     * NIO 递归遍历（推荐）：Files.walk / Files.find / 类型过滤
     */
    static void walkNio(String base) throws IOException {
        Path root = Paths.get(base);
        System.out.println("\n===== NIO Files.walk: 全部（缩进排序） =====");
        try (Stream<Path> stream = Files.walk(root)) {
            stream.sorted((p1, p2) -> {
                boolean d1 = Files.isDirectory(p1);
                boolean d2 = Files.isDirectory(p2);
                if (d1 != d2) return Boolean.compare(d2, d1); // 目录在前
                return p1.getFileName().toString().compareTo(p2.getFileName().toString());
            }).forEach(p -> {
                int depth = root.relativize(p).getNameCount();
                String indent = "  ".repeat(depth);
                String tag = Files.isDirectory(p) ? "[DIR] " : "[FILE] ";
                System.out.println(indent + tag + p.getFileName());
            });
        }

        System.out.println("===== NIO 过滤：只展示 .txt 文件 =====");
        try (Stream<Path> stream = Files.walk(root)) {
            stream.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".txt"))
                    .forEach(p -> System.out.println("  " + p));
        }
    }

    /**
     * 打印 File 的常用属性
     */
    static void printFileAttrs(File f) {
        if (!f.exists()) return;
        System.out.println("\n===== File 属性: " + f.getName() + " =====");
        System.out.println("绝对路径: " + f.getAbsolutePath());
        System.out.println("相对路径: " + f.getPath());
        System.out.println("父路径:   " + f.getParent());
        System.out.println("是目录:   " + f.isDirectory());
        System.out.println("是文件:   " + f.isFile());
        System.out.println("可写:     " + f.canWrite());
        System.out.println("可读:     " + f.canRead());
        System.out.println("大小(B):  " + f.length());
        System.out.println("修改时间: " + new java.util.Date(f.lastModified()));
    }

    /**
     * 重命名 / 移动 / 复制
     */
    static void renameCopyDemo(String base) throws IOException {
        File root = new File(base);
        File src = new File(new File(root, "dir"), "hello.txt");

        // rename: 目标路径不存在，才算重命名成功
        File renamed = new File(new File(root, "dir"), "world.txt");
        boolean r = src.renameTo(renamed);
        System.out.println("rename " + src.getName() + " -> " + renamed.getName() + ": " + r);

        // 复制文件
        File copy = new File(new File(root, "dir"), "world_copy.txt");
        Files.copy(renamed.toPath(), copy.toPath());
        System.out.println("copy 存在: " + copy.exists());

        // 移动（并重命名）
        File moved = new File(root, "moved.txt");
        Files.move(renamed.toPath(), moved.toPath());
        System.out.println("move 后源存在: " + renamed.exists() + "  目标存在: " + moved.exists());
    }

    /**
     * 递归删除目录及其中所有文件
     */
    static void deleteRecursively(File f) {
        if (f == null || !f.exists()) return;
        File[] children = f.listFiles();
        if (children != null) {
            for (File child : children) {
                deleteRecursively(child);
            }
        }
        boolean ok = f.delete();
        System.out.println("delete: " + f + " -> " + ok);
    }

    /**
     * NIO 递归删除（基于 walk 倒序，先删子再删父）
     */
    static void deleteRecursivelyNio(String rootPath) throws IOException {
        Path root = Paths.get(rootPath);
        if (!Files.exists(root)) return;
        try (Stream<Path> stream = Files.walk(root)) {
            stream.sorted(Comparator.reverseOrder())
                    .forEach(p -> {
                        try {
                            Files.delete(p);
                        } catch (IOException e) {
                            System.err.println("删除失败: " + p + ", " + e);
                        }
                    });
        }
    }
}
