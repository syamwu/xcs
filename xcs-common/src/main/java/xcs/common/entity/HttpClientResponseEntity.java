package xcs.common.entity;

import org.apache.http.client.methods.CloseableHttpResponse;

import xcs.common.entity.Entity.EntityType;

public class HttpClientResponseEntity {

    private CloseableHttpResponse response;

    private EntityType entityType;

    private boolean success = false;

    public CloseableHttpResponse getResponse() {
        return response;
    }

    public void setResponse(CloseableHttpResponse response) {
        this.response = response;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public HttpClientResponseEntity(CloseableHttpResponse response, EntityType entityType) {
        this.response = response;
        this.entityType = entityType;
        if (response == null) {
            this.success = false;
        }
        if (response.getStatusLine().getStatusCode() == 200) {
            this.success = true;
        } else {
            this.success = false;
        }
    }

}
