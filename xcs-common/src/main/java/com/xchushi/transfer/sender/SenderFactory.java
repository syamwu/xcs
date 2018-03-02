package com.xchushi.transfer.sender;

public class SenderFactory {

    public static Sender getSender(Class<?> cls){
        return HttpSender.getSender(cls);
    }
    
}
