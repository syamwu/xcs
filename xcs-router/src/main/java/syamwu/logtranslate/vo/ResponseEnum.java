package syamwu.logtranslate.vo;

public enum ResponseEnum {

    RES_200(200, "OK"), 
    RES_400(400, "Request denied"), 
    RES_404(404, "Resources not found"), 
    RES_500(500, "System exception"),
    ;

    ResponseEnum(int resultCode, String resultMessage) {
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
    }

    private int resultCode;

    private String resultMessage;

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public <T> Response<T> getResponse() {
        return new Response<>(this.resultCode, this.resultMessage);
    }

}
