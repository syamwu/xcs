package test.hl7;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import ca.uhn.hl7v2.model.v24.message.ADT_A01;
import ca.uhn.hl7v2.model.v24.segment.EVN;
import ca.uhn.hl7v2.model.v24.segment.IN1;
import ca.uhn.hl7v2.model.v24.segment.MSH;
import ca.uhn.hl7v2.model.v24.segment.NK1;
import ca.uhn.hl7v2.model.v24.segment.PDA;
import ca.uhn.hl7v2.model.v24.segment.PID;
import ca.uhn.hl7v2.parser.DefaultXMLParser;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.parser.PipeParser;

public class QRY_RO2_TEST {

    public static void main(String[] args) {
        try {
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String nowDate = sdf.format(new Date());
            ADT_A01 adt = new ADT_A01();

            // MSH消息段(Segment)
            //MSH|^~\&|01||PMI||20170821171644||ADT^ZP1|2017082117164499|P|2.4|||AL|AL|CHN|UNICODE
            MSH mshSegment = adt.getMSH();
            mshSegment.getFieldSeparator().setValue("|");//MSH-1
            mshSegment.getEncodingCharacters().setValue("^~\\&");//MSH-2
            mshSegment.getMsh3_SendingApplication().getHd1_NamespaceID().setValue("01");
            mshSegment.getMsh5_ReceivingApplication().getHd1_NamespaceID().setValue("PMI");
            mshSegment.getDateTimeOfMessage().getTimeOfAnEvent().setValue(nowDate);//MSH-7
            mshSegment.getMessageType().getMessageType().setValue("ADT");//MSH-91
            mshSegment.getMessageType().getTriggerEvent().setValue("ZP1");//MSH-92
            mshSegment.getMsh10_MessageControlID().setValue(nowDate + new Random().nextInt(100));//MSH-10
            mshSegment.getMsh11_ProcessingID().getProcessingID().setValue("P");
            mshSegment.getMsh12_VersionID().getVersionID().setValue("2.4");
            mshSegment.getMsh15_AcceptAcknowledgmentType().setValue("AL");
            mshSegment.getMsh16_ApplicationAcknowledgmentType().setValue("AL");
            mshSegment.getMsh17_CountryCode().setValue("CHN");
            mshSegment.getMsh18_CharacterSet(0).setValue("UNICODE");
            
            //EVN|ZP1|20170720193110|20170720193110|||20170720193110|AA
            EVN evn = adt.getEVN();
            evn.getEvn1_EventTypeCode().setValue("ZP1");
            evn.getEvn2_RecordedDateTime().getTs1_TimeOfAnEvent().setValue(nowDate);
            evn.getEvn3_DateTimePlannedEvent().getTs1_TimeOfAnEvent().setValue(nowDate);
            evn.getEvn6_EventOccurred().getTs1_TimeOfAnEvent().setValue(nowDate);
            evn.getEvn7_EventFacility().getHd1_NamespaceID().setValue("AA");
            
            // MSH消息段(PID)
            //PID|1||AA00000003^^^^LocalID~441522199311307990^^^^IdentifyNO~AA00000003^^^^IDCard~^^^^PatientNO~^^^^OtherID~^^^^UPID||测试个^^^^^1||19931130000000|M|||^^^^^^RH~^^^^^^H~^^^^^^C~^^^^^^O~^^^^^^BDL||13632301234^^PH^^^^13632301234~13632301234^^CP|||^0||||||^0|||||||||N
            PID pid = adt.getPID();
            pid.getPid1_SetIDPID().setValue("1");
            
            pid.getPid3_PatientIdentifierList(0).getCx1_ID().setValue("AA00000003");
            pid.getPid3_PatientIdentifierList(0).getCx5_IdentifierTypeCode().setValue("LocalID");
            pid.getPid3_PatientIdentifierList(1).getCx1_ID().setValue("441522199311307990");
            pid.getPid3_PatientIdentifierList(1).getCx5_IdentifierTypeCode().setValue("IdentifyNO");
            pid.getPid3_PatientIdentifierList(2).getCx1_ID().setValue("AA00000003");
            pid.getPid3_PatientIdentifierList(2).getCx5_IdentifierTypeCode().setValue("IDCard");
            pid.getPid3_PatientIdentifierList(3).getCx1_ID().setValue("");
            pid.getPid3_PatientIdentifierList(3).getCx5_IdentifierTypeCode().setValue("PatientNO");
            pid.getPid3_PatientIdentifierList(4).getCx1_ID().setValue("");
            pid.getPid3_PatientIdentifierList(4).getCx5_IdentifierTypeCode().setValue("OtherID");
            pid.getPid3_PatientIdentifierList(5).getCx1_ID().setValue("");
            pid.getPid3_PatientIdentifierList(5).getCx5_IdentifierTypeCode().setValue("UPID");
            
            //pid.getPatientName(0).getGivenName().setValue("John");
            pid.getPid5_PatientName(0).getXpn1_FamilyName().getFn1_Surname().setValue("测试个");
            pid.getPid5_PatientName(0).getXpn6_DegreeEgMD().setValue("1");
            
            pid.getPid7_DateTimeOfBirth().getTs1_TimeOfAnEvent().setValue("19931130000000");
            
            pid.getPid8_AdministrativeSex().setValue("M");
            
            pid.getPid11_PatientAddress(0).getXad7_AddressType().setValue("RH");
            pid.getPid11_PatientAddress(1).getXad7_AddressType().setValue("H");
            pid.getPid11_PatientAddress(2).getXad7_AddressType().setValue("C");
            pid.getPid11_PatientAddress(3).getXad7_AddressType().setValue("O");
            pid.getPid11_PatientAddress(4).getXad7_AddressType().setValue("BDL");
            
            pid.getPid13_PhoneNumberHome(0).getXtn1_9999999X99999CAnyText().setValue("13632301234");
            pid.getPid13_PhoneNumberHome(0).getXtn3_TelecommunicationEquipmentType().setValue("PH");
            pid.getPid13_PhoneNumberHome(0).getXtn7_PhoneNumber().setValue("13632301234");
            pid.getPid13_PhoneNumberHome(1).getXtn1_9999999X99999CAnyText().setValue("13632301234");
            pid.getPid13_PhoneNumberHome(1).getXtn3_TelecommunicationEquipmentType().setValue("CP");
            
            pid.getPid16_MaritalStatus().getCe2_Text().setValue("0");
            
            pid.getPid22_EthnicGroup(0).getCe2_Text().setValue("0");
            
            pid.getPid31_IdentityUnknownIndicator().setValue("N");
            
            //NK项目
            NK1 nk1 = adt.getNK1();
            nk1.getNk11_SetIDNK1().setValue("1");
            nk1.getNk13_Relationship().getCe2_Text().setValue("0");

            //IN项目
            IN1 in1 = adt.getINSURANCE().getIN1();
            in1.getIn11_SetIDIN1().setValue("1");
            in1.getIn12_InsurancePlanID().getCe1_Identifier().setValue("1");
            //in1.getIn13_InsuranceCompanyID(0).getCx1_ID().setValue(" ");
            
            // HL7在应用程序中的传输格式
            Parser parser = new PipeParser();
            String encodedMessage = parser.encode(adt);
            System.out.println("ER7格式:");
            System.out.println(encodedMessage);

            
           // System.out.println(pid.getPid3_PatientIdentifierList(0).getCx1_ID().getValue());
            
            // XML格式
            parser = new DefaultXMLParser();
            encodedMessage = parser.encode(adt);
            System.out.println("XML格式:");
            //System.out.println(encodedMessage);
            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
