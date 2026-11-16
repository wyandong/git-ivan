package main.com.ivan;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FOStream {

    static class student {
        private String name;
        private int age;

        private double score;

        public student(String name, int age, double score) {
            this.name = name;
            this.age = age;
            this.score = score;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public int getAge() {
            return age;
        }

        public void setScore(double score) {
            this.score = score;
        }
        public double getScore() {
            return score;
        }

        @Override
        public String toString() {
            return "student{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    ", score=" + score +
                    '}';
        }
    }

    public static void wirteStudents() throws IOException {
        List<student> students = Arrays.asList(new student("Alice", 20, 85.5), new student("Bob", 22, 90.0),
                new student("Charlie", 19, 78.0));
        DataOutputStream dos = new DataOutputStream(new FileOutputStream("stu.dat"));

        try {
            dos.writeInt(students.size());
            for (student s : students) {
                dos.writeUTF(s.getName());
                dos.writeInt(s.getAge());
                dos.writeDouble(s.getScore());
            }
        } finally {
            dos.close();
        }
    }

    public static List<student> readStudents() throws IOException {
        DataInputStream dis = new DataInputStream(new FileInputStream("D:\\stu.dat"));

        List<student> students = null;

        try {
            int size = dis.readInt();
            students = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                String name = dis.readUTF();
                int age = dis.readInt();
                double score = dis.readDouble();
                students.add(new student(name, age, score));
            }
        } finally {
            dis.close(); 
        }
        return students;    
    }

    public static void fileWrite() throws IOException {
        OutputStream out = new FileOutputStream("D:\\Personal\\inbound_outbound.txt", true);

        String str = "在留資格を早めに取得したいです。";
        try {
            out.write(("\r\n" + str).getBytes(Charset.forName("UTF-8")));
        } finally {
            out.close();
        }
        System.out.println("ファイルに書き込みました。");
    }

    public static String fileRead() throws IOException {
        InputStream in = new FileInputStream("D:\\Personal\\inbound_outbound.txt");

        String rtnStr = "";

        try {
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int len;
            while ((len = in.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            rtnStr = bos.toString("UTF-8");

            System.out.println("ファイルから読み込みました。内容は以下になります。");
        } finally {
            in.close();
        }

        return rtnStr;
    }

    public static void main(String[] args) throws IOException {

        // 写入文件
        // fileWrite();

        // 读取文件内容
        // System.out.println(fileRead());

        // 写入学生数据（二进制字节数据）
        wirteStudents();

        // 读取学生数据
        // System.out.println(readStudents());

        // System.out.println(Charset.defaultCharset());

        File f = new File(".");
        System.out.println(f.listFiles());
        for (File file : f.listFiles()) {
            System.out.println(file.getName());
        }
        System.out.println(f.getAbsolutePath());
        System.out.println(f.isDirectory());
        
    }

} 

