package test.io.nio.to.request;

public class NioHttpBody extends NioBody {

    private byte[] bytes;
    
    public NioHttpBody(byte[] bytes){
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

}
