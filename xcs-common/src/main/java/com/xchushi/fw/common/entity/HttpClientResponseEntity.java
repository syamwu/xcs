package com.xchushi.fw.common.entity;

import org.apache.http.client.methods.CloseableHttpResponse;

public class HttpClientResponseEntity extends Entity<CloseableHttpResponse> {

    private CloseableHttpResponse response;

    private boolean success = false;

    public CloseableHttpResponse getResponse() {
        return response;
    }

    public void setResponse(CloseableHttpResponse response) {
        this.response = response;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public HttpClientResponseEntity(CloseableHttpResponse response, EntityType entityType) {
        super(response, entityType);
        this.response = response;
        this.entityType = entityType;
        if (response == null) {
            this.success = false;
        } else {
            if (response.getStatusLine().getStatusCode() == 200) {
                this.success = true;
            } else {
                this.success = false;
            }
        }
    }

    public static void main(String[] args) {
        System.out.println(Entity.class.isAssignableFrom(HttpClientResponseEntity.class));
    }

    @Override
    public CloseableHttpResponse getMessage() {
        return response;
    }

}
