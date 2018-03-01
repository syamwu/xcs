package test.hl7;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.Node;

public class Hl7test {
    public static void main(String[] args) {
        
//        MSH|^~\&|01||PMI||20170720193110||ADT^ZP1|BB0000001|P|2.4|||AL|AL|CHN|UNICODE
//        EVN|ZP1|20170720193110|20170720193110|||20170720193110|AA
//        PID|1||AA00000001^^^^LocalID~441522199311307990^^^^IdentifyNO~AA00000001^^^^IDCard~^^^^PatientNO~^^^^OtherID~^^^^UPID||蔡泽濠^^^^^1||19931130000000|M|||^^^^^^RH~^^^^^^H~^^^^^^C~^^^^^^O~^^^^^^BDL||13632301234^^PH^^^^13632301234~13632301234^^CP|||^0||||||^0|||||||||N
//        NK1|1||^0
//        IN1|1|1|
        
        //String myHL7string = "MSH|^~\\&|455755610_0100||0200||20110624160404|000|QRY^A19^QRY_A19|0123456001|P|2.6\nQRD|||||||||0001^郭靖^体检号^EQ^AND~0002^东一区^病区号^EQ^AND\nQRF||20110627|20110803";
        String myHL7string ="MSH|^~\\&|01||PMI||20170720193110||ADT^ZP1|BB0000001|P|2.4|||AL|AL|CHN|UNICODE\nEVN|ZP1|20170720193110|20170720193110|||20170720193110|AA\nPID|1||AA00000001^^^^LocalID~441522199311307990^^^^IdentifyNO~AA00000001^^^^IDCard~^^^^PatientNO~^^^^OtherID~^^^^UPID||蔡泽濠^^^^^1||19931130000000|M|||^^^^^^RH~^^^^^^H~^^^^^^C~^^^^^^O~^^^^^^BDL||13632301234^^PH^^^^13632301234~13632301234^^CP|||^0||||||^0|||||||||N\nNK1|1||^0\nIN1|1|1|";
        Document document = HL7ToXmlConverter.ConvertToXmlObject(myHL7string);

        String documentStr = document.asXML();
        System.out.println(documentStr);
        
        // 获取事件
        String eventName = HL7ToXmlConverter.GetText(document, "MSH/MSH.9/MSH.9.3");
        System.out.println("eventName:" + eventName);

        // List nodeValue = document.selectNodes("MSH.1");
        String nodeValue = document.selectSingleNode("HL7Message/MSH/MSH.1").getText();
        String nodeValue2 = document.selectSingleNode("HL7Message/MSH/MSH.3").getText();
        // DocumentElement.SelectNodes(path);
        System.out.println(nodeValue + ":" + nodeValue2);

        String value = HL7ToXmlConverter.GetText(document, "QRD/QRD.9/QRD.9.1", 0);
        String value1 = HL7ToXmlConverter.GetText(document, "QRD/QRD.9/QRD.9.1", 1);
        String value2 = HL7ToXmlConverter.GetText(document, "QRD/QRD.9/QRD.9.1");
        System.out.println(value + ":" + value1 + ":" + value2);

        List<Node> list = HL7ToXmlConverter.GetTexts(document, "QRD/QRD.9/QRD.9.1");
        for (Node node : list) {
            System.out.println(":" + node.getText());
        }

        System.out.println(HL7ToXmlConverter.ConvertToXml(myHL7string));
    }
}
