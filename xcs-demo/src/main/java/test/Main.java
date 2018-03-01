package test;

public class Main {
    public static void main(String[] args) {
        
        Object[] objs = new Object[]{new Object(),new Object()};
        for (int i = 0; i < objs.length; i++) {
            System.out.println(objs[i]);
        }
        Object[] objs2 = objs.clone();
        for (int i = 0; i < objs2.length; i++) {
            System.out.println(objs2[i]+""+objs2[i].getClass().getInterfaces()[0].getName());
        }
    }
}
