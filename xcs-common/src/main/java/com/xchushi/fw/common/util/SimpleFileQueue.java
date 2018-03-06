package com.xchushi.fw.common.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.alibaba.fastjson.JSON;
import com.xchushi.fw.common.entity.Entity.EntityType;
import com.xchushi.fw.common.entity.SimpleEntity;

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
//            String message = rFile.readLine();
//            while (message != null && message.length() > 0) {
//                System.out.println(System.currentTimeMillis());
//                if (list == null) {
//                    list = new ArrayList<String>();
//                }
//                list.add(new String(message.getBytes("ISO-8859-1"), charsetName));
//                message = rFile.readLine();
//            }
            String msgLenStr = rFile.readLine();
            while(msgLenStr!=null && msgLenStr.length() >0){
                if (msgLenStr == null || msgLenStr.length() < 1) {
                    return null;
                }
                int msgLen = Integer.valueOf(msgLenStr);
                byte[] messageByte = new byte[msgLen];
                rFile.read(messageByte, 0, msgLen);
                rFile.read(new byte[SEPARATOR_BYTES.length]);
                if (list == null) {
                  list = new ArrayList<String>();
                }
                list.add(new String(messageByte, charsetName));
                msgLenStr = rFile.readLine();
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
            byte[] contentBytes = content.getBytes(charsetName);
            String contentLen = String.valueOf(contentBytes.length - SEPARATOR_BYTES.length) + SEPARATOR;
            byte[] contentLenBytes = contentLen.getBytes(charsetName);
            rFile.write(contentLenBytes);
            rFile.write(contentBytes);
            rFile.seek(0);
            rFile.write(String.valueOf(offerIndex + contentLenBytes.length + contentBytes.length).getBytes());
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
            String msgLenStr = rFile.readLine();
            if (msgLenStr == null || msgLenStr.length() < 1) {
                return null;
            }
            int msgLen = Integer.valueOf(msgLenStr);
            byte[] messageByte = new byte[msgLen];
            rFile.read(messageByte, 0, msgLen);
            long newPollIndex = pollIndex + msgLenStr.getBytes("ISO-8859-1").length + SEPARATOR_BYTES.length + msgLen
                    + SEPARATOR_BYTES.length;
            long offerIndex = Long.valueOf(offerIndexStr.trim());
            rFile.seek(offerIndexStr.getBytes().length + SEPARATOR_BYTES.length);
            rFile.write(String.valueOf(newPollIndex).getBytes());
            if (newPollIndex >= offerIndex) {
                clear();
            }
            return new String(messageByte, charsetName);
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
            String msgLenStr = rFile.readLine();
            if (msgLenStr == null || msgLenStr.length() < 1) {
                return null;
            }
            int msgLen = Integer.valueOf(msgLenStr);
            byte[] messageByte = new byte[msgLen];
            rFile.read(messageByte, 0, msgLen);
            return new String(messageByte, charsetName);
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
//        System.out.println(System.currentTimeMillis());
//        RandomAccessFile re = new RandomAccessFile("D:\\upload\\data18.txt","rw");
//        System.out.println(System.currentTimeMillis());
//        String str1 =re.readLine();
//        String str2 =re.readLine();
//        System.out.println(System.currentTimeMillis()+"---"+str1+str2);
//        String str = re.readLine()+re.readLine();
//        System.out.println(System.currentTimeMillis()+"---"+str);
//        int len = str.getBytes().length;
//        System.out.println(System.currentTimeMillis()+"---"+len);
//        byte[] by = new byte[10024];
//        re.read(by,0,10024);
//        re.seek(0);
//        byte[] by1 = new byte[10024];
//        re.read(by1,0,10024);
//        String str = new String(by);
//        String str5 = new String(by1);
//        System.out.println(System.currentTimeMillis());
        test1();
        // System.out.println("看看10".getBytes("ISO-8859-1").length);
        // System.out.println("看看10".getBytes("UTF-8").length);
    }
    public static void test1() throws IOException {
        SimpleFileQueue f = new SimpleFileQueue("D:\\upload\\data9.txt");
        RandomAccessFile f1 = new RandomAccessFile("D:\\upload\\data18.txt","r");
        f1.readLine();
        f1.readLine();
//        for (int i = 0; i < 300; i++) {
//            f.offer(f1.readLine());
//        }
//        long time = System.currentTimeMillis();
//        List<String> list = new ArrayList<String>();
//        for (int i = 0; i < 3; i++) {
//            list.add(f.peek());
//        }
//        System.out.println(System.currentTimeMillis()-time);
        
        long time = System.currentTimeMillis();
        List<String> list = f.getMessages(false);
        System.out.println(System.currentTimeMillis()-time);
        JSON.toJSONString(list);
        System.out.println(System.currentTimeMillis()-time);
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
