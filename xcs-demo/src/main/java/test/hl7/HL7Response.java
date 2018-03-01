package test.hl7;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v24.message.RSP_K22;
import ca.uhn.hl7v2.model.v24.message.RSP_K23;
import ca.uhn.hl7v2.model.v24.segment.ERR;
import ca.uhn.hl7v2.model.v24.segment.MSA;
import ca.uhn.hl7v2.model.v24.segment.MSH;
import ca.uhn.hl7v2.parser.DefaultXMLParser;
import ca.uhn.hl7v2.parser.EncodingNotSupportedException;
import ca.uhn.hl7v2.parser.GenericParser;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.parser.PipeParser;

public class HL7Response {

    public static void main(String[] args) throws Exception {
//        String msg = "MSH|^~\\&|HIS|RIH|EKG|EKG|199904140038||ADT^A01||P|2.4|||AL|AL|CHN|UNICODE\n"
//                + "PID|0001|00009874|00001122|A00977|SMITH^JOHN^M|MOM|19581119|F|NOTREAL^LINDA^M|C|564 SPRING ST^^NEEDHAM^MA^02494^US|0002|(818)565-1551|(425)828-3344|E|S|C|0000444444|252-00-4414||||SA|||SA||||NONE|V1|0001|I|D.ER^50A^M110^01|ER|P00055|11B^M011^02|070615^BATMAN^GEORGE^L|555888^NOTREAL^BOB^K^DR^MD|777889^NOTREAL^SAM^T^DR^MD^PHD|ER|D.WT^1A^M010^01|||ER|AMB|02|070615^NOTREAL^BILL^L|ER|000001916994|D||||||||||||||||GDD|WA|NORM|02|O|02|E.IN^02D^M090^01|E.IN^01D^M080^01|199904072124|199904101200|199904101200||||5555112333|||666097^NOTREAL^MANNY^P\n"
//                + "NK1|0222555|NOTREAL^JAMES^R|FA|STREET^OTHER STREET^CITY^ST^55566|(222)111-3333|(888)999-0000|||||||ORGANIZATION\n"
//                + "PV1|0001|I|D.ER^1F^M950^01|ER|P000998|11B^M011^02|070615^BATMAN^GEORGE^L|555888^OKNEL^BOB^K^DR^MD|777889^NOTREAL^SAM^T^DR^MD^PHD|ER|D.WT^1A^M010^01|||ER|AMB|02|070615^VOICE^BILL^L|ER|000001916994|D||||||||||||||||GDD|WA|NORM|02|O|02|E.IN^02D^M090^01|E.IN^01D^M080^01|199904072124|199904101200|||||5555112333|||666097^DNOTREAL^MANNY^P\n"
//                + "PV2|||0112^TESTING|55555^PATIENT IS NORMAL|NONE|||19990225|19990226|1|1|TESTING|555888^NOTREAL^BOB^K^DR^MD||||||||||PROD^003^099|02|ER||NONE|19990225|19990223|19990316|NONE\n"
//                + "AL1||SEV|001^POLLEN\n"
//                + "GT1||0222PL|NOTREAL^BOB^B||STREET^OTHER STREET^CITY^ST^77787|(444)999-3333|(222)777-5555||||MO|111-33-5555||||NOTREAL GILL N|STREET^OTHER STREET^CITY^ST^99999|(111)222-3333\n"
//                + "IN1||022254P|4558PD|BLUE CROSS|STREET^OTHER STREET^CITY^ST^00990||(333)333-6666||221K|LENIX|||19980515|19990515|||PATIENT01 TEST D||||||||||||||||||02LL|022LP554";
        //String msg ="MSH|^~\\&|01||PMI||20170720193110||ADT^ZP1|BB0000001|P|2.4|||AL|AL|CHN|UNICODE\nEVN|ZP1|20170720193110|20170720193110|||20170720193110|AA\nPID|1||AA00000001^^^^LocalID~441522199311307990^^^^IdentifyNO~AA00000001^^^^IDCard~^^^^PatientNO~^^^^OtherID~^^^^UPID||蔡泽濠^^^^^1||19931130000000|M|||^^^^^^RH~^^^^^^H~^^^^^^C~^^^^^^O~^^^^^^BDL||13632301234^^PH^^^^13632301234~13632301234^^CP|||^0||||||^0|||||||||N\nNK1|1||^0\nIN1|1|1|";
        
        String msg = "MSH|^~\\&|PMI||01||20170720193110||RSP^K23|BB0000001|P|2.4|||AL|AL|CHN|UNICODE\rMSA|AA|BB0000001|[MsgInfo] Method Type: ZP1 -Success Flag: AA -MSG: success create\rPID|||AA00000001^^^^LocalID~441522199311307990^^^^IdentifyNO~AA00000001^^^^IDCard~^^^^PatientNO~^^^^OtherID~AK1826302^^^^UPID||蔡泽濠";
        
        // 从pipe格式或xml格式转化成类对象
        //Parser p = new PipeParser();
        Parser p = new GenericParser();//通用解析器
        Message hapiMsg; 
        try {
            hapiMsg = p.parse(msg);
        } catch (EncodingNotSupportedException e) {
            e.printStackTrace();
            return;
        } catch (HL7Exception e) {
            e.printStackTrace();
            return;
        }
        
        
        // ADT_A01是Message字类
        RSP_K23 adtMsg = (RSP_K23) hapiMsg;

        MSH msh = adtMsg.getMSH();
        
        MSA pid = adtMsg.getMSA();
        
        ERR pda = adtMsg.getERR();
        
        
        //HL格式
        Parser parser = new PipeParser();
        String str = parser.encode(adtMsg);
        System.out.println(str);
        // XML格式
        parser = new DefaultXMLParser();
        str = parser.encode(adtMsg);
        System.out.println("XML格式:");
        //System.out.println(str);
        
        System.out.println("---"+pda.isEmpty());
        
        System.out.println("---"+pid.getMessage());
        
        System.out.println("---"+pid.getMsa3_TextMessage().getValue());
        
        
        // getValue获取值
//        String msgType = msh.getMessageType().getMessageType().getValue();
//        String msgTrigger = msh.getMessageType().getTriggerEvent().getValue();
//        System.out.println(msgType + " " + msgTrigger);
//
//        XPN[] patientName = adtMsg.getPID().getPatientName();
//        String familyName = patientName[0].getFamilyName().getSurname().getValue();
//        String givenName = patientName[0].getGivenName().getValue();
//        System.out.println(familyName + " " + givenName);
    }
}
