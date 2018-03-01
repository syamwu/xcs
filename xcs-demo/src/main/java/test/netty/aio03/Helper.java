package test.netty.aio03;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
 
public class Helper {
    private static BlockingQueue<String> words;
    private static Random random;
     
    public Helper() throws InterruptedException{
        words = new ArrayBlockingQueue<String>(5);
        words.put("hi");
        words.put("who");
        words.put("what");
        words.put("where");
        words.put("bye");  
         
        random = new Random();
    }
     
    public String getWord(){
        return words.poll();
    }
 
    public void sleep() {
        try {
            TimeUnit.SECONDS.sleep(random.nextInt(3));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }  
     
    public static void sleep(long l) {
        try {
            TimeUnit.SECONDS.sleep(l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
     
    public static String getAnswer(String question){
        String answer = null;
         
        switch(question){
        case "who":
            answer = "我是小娜\n";
            break;
        case "what":
            answer = "我是来帮你解闷的\n";
            break;
        case "where":
            answer = "我来自外太空\n";
            break;
        case "hi":
            answer = "hello\n";
            break;
        case "bye":
            answer = "88\n";
            break;
        default:
                answer = "请输入 who， 或者what， 或者where";
        }
         
        return answer;
    }
}
