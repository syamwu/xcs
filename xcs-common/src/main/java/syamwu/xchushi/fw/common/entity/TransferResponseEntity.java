package syamwu.xchushi.fw.common.entity;

import syamwu.xchushi.fw.transfer.response.TransferResponse;

public class TransferResponseEntity extends Entity<TransferResponse> {

    private TransferResponse response;

    private boolean success = false;

    public TransferResponse getResponse() {
        return response;
    }

    public void setResponse(TransferResponse response) {
        this.response = response;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public TransferResponseEntity(TransferResponse response, EntityType entityType) {
        super(response, entityType);
        this.response = response;
        this.entityType = entityType;
        if (response == null) {
            this.success = false;
        } else {
            this.success = response.getResponseStatus();
        }
    }

}
