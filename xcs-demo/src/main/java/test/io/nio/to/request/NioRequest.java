package test.io.nio.to.request;

import test.io.nio.to.response.NioResponse;

public abstract class NioRequest {

    private NioHeader nioHeader;
    
    private NioBody nioBody;
    
    public NioHeader getNioHeader() {
        return nioHeader;
    }

    public void setNioHeader(NioHeader nioHeader) {
        this.nioHeader = nioHeader;
    }

    public NioBody getNioBody() {
        return nioBody;
    }

    public void setNioBody(NioBody nioBody) {
        this.nioBody = nioBody;
    }
    
    public abstract NioResponse doRequest(NioRequest nioRequest);
    
    
}
