package test.io.nio.to;

public class HttpHeaderNode {
    private String nodeName;

    private String nodeValue;
    
    public HttpHeaderNode(String nodeName, String nodeValue){
        this.nodeName = nodeName;
        this.nodeValue = nodeValue;
    }
    
    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getNodeValue() {
        return nodeValue;
    }

    public void setNodeValue(String nodeValue) {
        this.nodeValue = nodeValue;
    }
}
