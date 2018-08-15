package syamwu.logtranslate.entity;

public class LogNode {

     /**/
     private String id;
     public void setId(String id) {
          this.id = id;
     }
     public String getId() {
          return id;
     }

     /*节点host/ip*/
     private String host;
     public void setHost(String host) {
          this.host = host;
     }
     public String getHost() {
          return host;
     }

     /*节点端口*/
     private String port;
     public void setPort(String port) {
          this.port = port;
     }
     public String getPort() {
          return port;
     }

     /*搜索uri*/
     private String searchUrl;
     public void setSearchUrl(String searchUrl) {
          this.searchUrl = searchUrl;
     }
     public String getSearchUrl() {
          return searchUrl;
     }

     /*创建时间*/
     private String createdTime;
     public void setCreatedTime(String createdTime) {
          this.createdTime = createdTime;
     }
     public String getCreatedTime() {
          return createdTime;
     }

     private String beginCreatedTime;
     public void setBeginCreatedTime(String beginCreatedTime) {
          this.beginCreatedTime = beginCreatedTime;
     }
     public String getBeginCreatedTime() {
          return beginCreatedTime;
     }

     private String endCreatedTime;
     public void setEndCreatedTime(String endCreatedTime) {
          this.endCreatedTime = endCreatedTime;
     }
     public String getEndCreatedTime() {
          return endCreatedTime;
     }

     /*更新时间*/
     private String updateTime;
     public void setUpdateTime(String updateTime) {
          this.updateTime = updateTime;
     }
     public String getUpdateTime() {
          return updateTime;
     }

     private String beginUpdateTime;
     public void setBeginUpdateTime(String beginUpdateTime) {
          this.beginUpdateTime = beginUpdateTime;
     }
     public String getBeginUpdateTime() {
          return beginUpdateTime;
     }

     private String endUpdateTime;
     public void setEndUpdateTime(String endUpdateTime) {
          this.endUpdateTime = endUpdateTime;
     }
     public String getEndUpdateTime() {
          return endUpdateTime;
     }

}