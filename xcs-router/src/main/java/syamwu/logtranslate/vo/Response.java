package syamwu.logtranslate.vo;

public class Response<T> {

    public static final int SUCCESS_CODE = 200;

    public static final int FAIL_CODE = 500;

    private int resultCode;

    private String resultMessage;

    private T result;

    public Response() {
        this.resultCode = SUCCESS_CODE;
        this.resultMessage = "success";
    }

    public Response(T t) {
        this.resultCode = SUCCESS_CODE;
        this.resultMessage = "success";
        this.result = t;
    }

    public Response(int resultCode, String resultMessage) {
        this(resultCode, resultMessage, null);
    }

    public Response(int resultCode, String resultMessage, T result) {
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
        this.result = result;
    }

    public int getResultCode() {
        return resultCode;
    }

    public Response<T> setResultCode(int resultCode) {
        this.resultCode = resultCode;
        return this;
    }

    public T getResult() {
        return result;
    }

    public Response<T> setResult(T result) {
        this.result = result;
        return this;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public Response<T> setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
        return this;
    }

    public Response<T> setResultCodeAndMessage(int resultCode, String resultMessage) {
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
        return this;
    }

}
