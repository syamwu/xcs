package syamwu.logtranslate.vo;

public class Response {
    
    public static final String SUCCESS_CODE = "0";
    
    public static final String FAIL_CODE = "1";

    private String resultCode;

    private String resultMessage;

    private String result;
    
    public Response(){
        System.out.println(resultCode);
        System.out.println(resultMessage);
        this.resultCode = SUCCESS_CODE;
        this.resultMessage = "success";
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }
    
    public void setResultCodeAndMessage(String resultCode, String resultMessage) {
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
    }
    
}
