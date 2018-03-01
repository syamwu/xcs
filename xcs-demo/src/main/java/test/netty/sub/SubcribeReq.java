package test.netty.sub;

import java.io.Serializable;

public class SubcribeReq implements Serializable{  
    private static final long nSerialVerUID = 2L;  
      
     private int nSubReqID;  
  
        private int nRespCode;  
  
        private String strDesc;  
  
        public final int getnSubReqID() {  
        return nSubReqID;  
        }  
  
        public final void setnSubReqID(int nSubReqID) {  
        this.nSubReqID = nSubReqID;  
        }  
  
        public final int getnRespCode() {  
        return nRespCode;  
        }  
  
        public final void setRespCode(int nRespCode) {  
        this.nRespCode = nRespCode;  
        }  
  
        public final String getstrDesc() {  
        return strDesc;  
        }  
  
        public final void setDesc(String strDesc) {  
        this.strDesc = strDesc;  
        }  
  
        @Override  
        public String toString() {  
        return "SubscribeResp [nSubReqID=" + nSubReqID + ", nRespCode=" + nRespCode  
            + ", strDesc=" + strDesc + "]";  
        }  
}  
