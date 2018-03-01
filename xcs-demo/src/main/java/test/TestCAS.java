package test;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;


public class TestCAS {
    public static Long offset = null;
    
static{
    
    /*Field field = null;
    try {
        field = User.class.getDeclaredField("score");
        unsafe = (Unsafe) field.get(null);
        field.setAccessible(true);
        unsafe.objectFieldOffset(field);
        offset = unsafe.objectFieldOffset(field);
    } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    } */
}
    
    public static void main(String[] args) {
        
        
//        try {
//            Field f = Unsafe.class.getDeclaredField("theUnsafe"); // Internal
//            f.setAccessible(true);
//            unsafe = (Unsafe) f.get(null);
//
//            // This creates an instance of player class without any
//            // initialization
//            //p = (User) unsafe.allocateInstance(User.class);
//            Field field = User.class.getDeclaredField("score");
//            field.setAccessible(true);
//            
//            unsafe.objectFieldOffset(field);
//            offset = unsafe.objectFieldOffset(field);
//            
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
        
//        final User user = new User("张三", 50);
//        
//        new Thread(new Runnable() {
//            public void run() {
//                while(true){
//                    user.downScore();
//                    System.out.println(user);
//                }
//            }
//        }).start();
//        new Thread(new Runnable() {
//            public void run() {
//                while(true){
//                    user.upScore();
//                    System.out.println(user);
//                }
//            }
//        }).start();
        
        System.out.println(User.class.isAssignableFrom(Thing.class));
        System.out.println(User.class.isAssignableFrom(User.class));
        System.out.println(Thing.class.isAssignableFrom(User.class));
        System.out.println(Rable.class.isAssignableFrom(User.class));
        System.out.println(User.class.isAssignableFrom(Rable.class));
    }
    
    static class Thing{
        
    }
    
    static class Person extends Thing{
        
    }
    
    static interface Rable{
        
    }

    static class User extends Person implements Rable{
        public String name;
        public volatile int score;
        AtomicIntegerFieldUpdater<User> up = AtomicIntegerFieldUpdater.newUpdater(User.class, "score");
        
        public User(){
            
        }
        
        public User(String name, int score){
            this.name = name;
            this.score = score;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }
        
        @Override
        public String toString() {
            return "name : " + name + " , score" + score;
        }
        
        public boolean upScore(){
            for(;;){
                int oldScore = this.getScore();
                int newScore = oldScore + 1;
                if(up.compareAndSet(this, oldScore, newScore)){
                    return true;
                }else{
                    System.out.println("upScore false");
                }
            }
        }
        
        public boolean downScore(){
            for(;;){
                int oldScore = this.getScore();
                int newScore = oldScore - 1;
                if(up.compareAndSet(this, oldScore, newScore)){
                    return true;
                }else{
                    System.out.println("downScore false");
                }
            }
        }

    }
}
