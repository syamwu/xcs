package syamwu.xchushi.fw.transfer.response;

import org.apache.http.client.methods.CloseableHttpResponse;

import okhttp3.Response;

public class OkHttpTransferResponse implements TransferResponse {
    
    private Response response;

    public OkHttpTransferResponse(Response response) {
        this.response = response;
    }

    @Override
    public int getResultCode() {
        return response.code();
    }

    @Override
    public boolean getResponseStatus() {
        return response.code() == 200;
    }

    @Override
    public Object getResponseBody() throws Exception {
        return response.body().string();
    }

}
