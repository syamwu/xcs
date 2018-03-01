package com.xcs.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;

public class StreamUtils {

    /**
     * 将InputStream输出到指定FilePath
     * 
     * @param in
     * @param filePath
     */
    public static void inputStream2file(InputStream in, String filePath) {
        try {

            OutputStream os = new FileOutputStream(new File(filePath));
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = in.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String file2string(String filePath) {

        InputStream in = null;
        try {
            in = new FileInputStream(filePath);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        byte[] buffer = new byte[2048];
        int readBytes = 0;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            while ((readBytes = in.read(buffer)) > 0) {
                // stringBuilder.append(new String(buffer, 0, readBytes,));
                stringBuilder.append(new String(buffer, 0, readBytes, "UTF-8"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public static String inputStream2string(InputStream inputStream) {

        byte[] buffer = new byte[2048];
        int readBytes = 0;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            while ((readBytes = inputStream.read(buffer)) > 0) {
                // stringBuilder.append(new String(buffer, 0, readBytes,));
                stringBuilder.append(new String(buffer, 0, readBytes, "UTF-8"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public static byte[] file2byte(String filePath) throws IOException {
        InputStream in = new FileInputStream(filePath);
        return input2byte(in);
    }

    public static final byte[] input2byte(InputStream inStream) throws IOException {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[2048];
        int rc = 0;
        while ((rc = inStream.read(buff, 0, 100)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        byte[] in2b = swapStream.toByteArray();
        return in2b;
    }

    /**
     * 创建文件,如果已经存在就不创建
     * 
     * @param file
     * @throws IOException
     */
    public static void creatTxtFile(File file) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
            System.err.println(file + "已创建！ ");
        }
    }

    /**
     * 读取文件文本
     * 
     * @param filePath
     * @return
     */
    public static String readTxtFile(String filePath) {
        String read;
        FileReader fileread;
        BufferedReader bufread;
        String readStr = "";
        try {
            fileread = new FileReader(filePath);
            bufread = new BufferedReader(fileread);
            try {
                while ((read = bufread.readLine()) != null) {
                    readStr = readStr + read + "/r/n";
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return readStr;
    }

    /**
     * 写内容到文件尾
     * 
     * @param newStr
     * @param filePath
     * @throws IOException
     */
    public static void writeEndTxtFile(String newStr, String filePath) throws IOException {
        String filein = readTxtFile(filePath) + "/r/n" + newStr;
        RandomAccessFile mm = null;
        try {
            mm = new RandomAccessFile(filePath, "rw");
            mm.writeBytes(filein);
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            if (mm != null) {
                try {
                    mm.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        }
    }

    /**
     * 替换文本所有内容
     * 
     * @param newStr
     * @param filePath
     * @throws IOException
     */
    public static void writeTxtFile(String newStr, String filePath) throws IOException {
        String filein = newStr;
        RandomAccessFile mm = null;
        try {
            mm = new RandomAccessFile(filePath, "rw");
            mm.writeBytes(filein);
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            if (mm != null) {
                try {
                    mm.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        }
    }

    /**
     * 将文件中指定内容的第一行替换为其它内容 .
     * 
     * @param filePath
     * @param oldStr
     * @param replaceStr
     */
    public static void replaceTxtByStr(String filePath, String oldStr, String replaceStr) {
        String temp = "";
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuffer buf = new StringBuffer();

            // 保存该行前面的内容
            for (; (temp = br.readLine()) != null && !temp.equals(oldStr);) {
                buf = buf.append(temp);
                buf = buf.append(System.getProperty("line.separator"));
            }

            // 将内容插入
            buf = buf.append(replaceStr);

            // 保存该行后面的内容
            while ((temp = br.readLine()) != null) {
                buf = buf.append(System.getProperty("line.separator"));
                buf = buf.append(temp);
            }

            br.close();
            FileOutputStream fos = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(fos);
            pw.write(buf.toString().toCharArray());
            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
