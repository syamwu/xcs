package com.xchushi.fw.transfer.response;

public interface TransferResponse {

    int getResultCode();
    
    boolean getResponseStatus();
    
    Object getResponseBody() throws Exception;
    
}
