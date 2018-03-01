package test.jvmtest;

public class StackOverFlow {

    private int i;

    public void plus() {

        i++;

        plus();

    }

    /**
     * 
     * @param args
     * 
     * @Author YHJ create at 2011-11-12 下午08:19:21
     * 
     */

    public static void main(String[] args) {

        StackOverFlow stackOverFlow = new StackOverFlow();

        try {

            stackOverFlow.plus();

        } catch (Exception e) {

            System.out.println("Exception:stack length:" + stackOverFlow.i);

            e.printStackTrace();

        } catch (Error e) {

            System.out.println("Error:stack length:" + stackOverFlow.i);

            e.printStackTrace();

        }

    }

}
