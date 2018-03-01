package com.xchushi.common.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.alibaba.fastjson.JSON;

public class SimpleFileQueue {

    private static final String SEPARATOR = System.getProperty("line.separator");

    private static final byte[] SEPARATOR_BYTES = SEPARATOR.getBytes();

    private static final int EMPTY_LEN = String.valueOf(Long.MAX_VALUE).length() + 1;

    private Lock lock = new ReentrantLock();

    private String charsetName = "";

    private String fileName = "";

    private RandomAccessFile rFile = null;

    public SimpleFileQueue(String fileName) throws IOException {
        this(fileName, "UTF-8");
    }

    public SimpleFileQueue(String fileName, String charsetName) throws IOException {
        this.fileName = fileName;
        this.charsetName = charsetName;
        File file = new File(fileName);
        if (!file.exists()) {
            file.createNewFile();
            clear();
        }
        this.rFile = new RandomAccessFile(fileName, "rw");
    }
    
    public static List<String> getMessages(String fileName, boolean rePolled) throws IOException {
        return new SimpleFileQueue(fileName).getMessages(true);
    }
    
    public List<String> getMessages() throws IOException {
        return getMessages(true);
    }

    public List<String> getMessages(boolean rePolled) throws IOException {
        List<String> list = null;
        try {
            lock.lock();
            rFile.seek(0);
            String offerIndexStr = rFile.readLine();
            String pollIndexStr = rFile.readLine();
            long pollIndex = 0l;
            if (rePolled){
                if (pollIndexStr == null || pollIndexStr.length() < 1) {
                    throw new RuntimeException(fileName + " doesn't have pollIndex");
                } else {
                    pollIndex = Long.valueOf(pollIndexStr.trim());
                    if (pollIndex < 0) {
                        rFile.close();
                        throw new RuntimeException("pollIndex can't be less than 0");
                    }
                    rFile.seek(offerIndexStr.getBytes().length + SEPARATOR_BYTES.length + pollIndexStr.getBytes().length
                            + SEPARATOR_BYTES.length + pollIndex);
                }
            }
            String message = rFile.readLine();
            while (message != null && message.length() > 0) {
                if (list == null) {
                    list = new ArrayList<String>();
                }
                list.add(new String(message.getBytes("ISO-8859-1"), charsetName));
                message = rFile.readLine();
            }
        } finally {
            lock.unlock();
        }
        return list;
    }

    public void offer(String message) throws IOException {
        try {
            lock.lock();
            rFile.seek(0);
            String offerIndexStr = rFile.readLine();
            String pollIndexStr = rFile.readLine();
            long offerIndex = 0l;
            if (offerIndexStr == null || offerIndexStr.length() < 1) {
                clear();
                rFile.seek(0);
                offerIndexStr = rFile.readLine();
                pollIndexStr = rFile.readLine();
                if (offerIndexStr == null || offerIndexStr.length() < 1) {
                    throw new RuntimeException(fileName + " doesn't have offerIndexStr");
                }
            } else {
                offerIndex = Long.valueOf(offerIndexStr.trim());
                rFile.seek(offerIndexStr.getBytes().length + SEPARATOR_BYTES.length + pollIndexStr.getBytes().length
                        + SEPARATOR_BYTES.length + offerIndex);
            }
            String content = message + SEPARATOR;
            byte[] bytes = content.getBytes(charsetName);
            rFile.write(bytes);
            rFile.seek(0);
            rFile.write(String.valueOf(offerIndex + bytes.length).getBytes());
        } finally {
            lock.unlock();
        }
    }

    public String poll() throws IOException {
        try {
            lock.lock();
            rFile.seek(0);
            String offerIndexStr = rFile.readLine();
            String pollIndexStr = rFile.readLine();
            long pollIndex = 0l;
            if (pollIndexStr == null || pollIndexStr.length() < 1) {
                throw new RuntimeException(fileName + " doesn't have pollIndex");
            } else {
                pollIndex = Long.valueOf(pollIndexStr.trim());
                if (pollIndex < 0) {
                    rFile.close();
                    throw new RuntimeException("pollIndex can't be less than 0");
                }
                rFile.seek(offerIndexStr.getBytes().length + SEPARATOR_BYTES.length + pollIndexStr.getBytes().length
                        + SEPARATOR_BYTES.length + pollIndex);
            }
            String message = rFile.readLine();
            if (message == null || message.length() < 1) {
                return null;
            }
            long newPollIndex = pollIndex + message.getBytes("ISO-8859-1").length + SEPARATOR_BYTES.length;
            long offerIndex = Long.valueOf(offerIndexStr.trim());
            rFile.seek(offerIndexStr.getBytes().length + SEPARATOR_BYTES.length);
            rFile.write(String.valueOf(newPollIndex).getBytes());
            if (newPollIndex >= offerIndex) {
                clear();
            }
            return new String(message.getBytes("ISO-8859-1"), charsetName);
        } finally {
            lock.unlock();
        }
    }

    public String peek() throws IOException {
        try {
            lock.lock();
            rFile.seek(0);
            String offerIndexStr = rFile.readLine();
            String pollIndexStr = rFile.readLine();
            long pollIndex = 0l;
            if (pollIndexStr == null || pollIndexStr.length() < 1) {
                throw new RuntimeException(fileName + " doesn't have pollIndex");
            } else {
                pollIndex = Long.valueOf(pollIndexStr.trim());
                if (pollIndex < 0) {
                    throw new RuntimeException("pollIndex can't be less than 0");
                }
                rFile.seek(offerIndexStr.getBytes().length + SEPARATOR_BYTES.length + pollIndexStr.getBytes().length
                        + SEPARATOR_BYTES.length + pollIndex);
            }
            String message = rFile.readLine();
            if (message == null || message.length() < 1) {
                return null;
            }
            return new String(message.getBytes("ISO-8859-1"), charsetName);
        } finally {
            lock.unlock();
        }
    }

    private void clear() throws IOException {
        FileWriter fileWriter = new FileWriter(fileName);
        fileWriter.write("");
        fileWriter.flush();
        fileWriter.close();
        RandomAccessFile rFile = new RandomAccessFile(fileName, "rw");
        rFile.write((String.valueOf(0) + getEmtyStr(EMPTY_LEN)).getBytes());
        rFile.write(SEPARATOR_BYTES);
        rFile.write((String.valueOf(0) + getEmtyStr(EMPTY_LEN)).getBytes());
        rFile.write(SEPARATOR_BYTES);
        rFile.close();
    }

    private String getEmtyStr(int len) {
        StringBuffer strBuff = new StringBuffer();
        for (int i = 0; i < len; i++) {
            strBuff.append(" ");
        }
        return strBuff.toString();
    }
    
    
    
    
    

    public static void main(String[] args) throws IOException {
        //test();
        test1();
        // System.out.println("看看10".getBytes("ISO-8859-1").length);
        // System.out.println("看看10".getBytes("UTF-8").length);
    }
    public static void test1() throws IOException {
        SimpleFileQueue f = new SimpleFileQueue("D:\\upload\\data18.txt");
        List<String> list = f.getMessages(false);
        System.out.println(JSON.toJSONString(list));
    }

    public static void test() throws IOException {
        SimpleFileQueue f = new SimpleFileQueue("D:\\upload\\data18.txt");
        for (int i = 0; i < 20; i++) {
            f.offer(JSON.toJSONString("挨个发圣诞噶噶身份撒发生发圣诞送的          阿斯顿撒" + i));
        }
//        for (;;) {
//            String str = f.poll();
//            if (str == null) {
//                break;
//            } else {
//                System.out.println(str);
//                // System.out.println(f.peek());
//            }
//        }
    }

}
