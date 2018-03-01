package test.guava;

import com.google.common.base.MoreObjects;

import test.io.nio.to.request.NioHttpRequest;

public class TestGua{
    
    public static void main(String[] args) {
        NioHttpRequest nio = new NioHttpRequest();
        nio.setCharset("hhh");
        
       // System.out.println(MoreObjects.toStringHelper(nio).add(, "2"));

        
    }
}