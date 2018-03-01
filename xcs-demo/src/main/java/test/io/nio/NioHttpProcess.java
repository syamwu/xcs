package test.io.nio;

import test.io.nio.to.request.NioHttpRequest;

public interface NioHttpProcess<T> {

    T process(NioHttpRequest nioHttpRequest);
    
}
