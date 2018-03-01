package test.hl7;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class HL7ToXmlConverter {

    public static String ConvertToXml(String sHL7) {
        Document document = ConvertToXmlObject(sHL7);
        String hl7str = document.asXML();
        return hl7str;
    }

    public static String ConvertToXml(Document document) {
        String hl7str = document.asXML();
        return hl7str;
    }

    public static Document ConvertToXmlObject(String sHL7) {
        Document document = CreateXmlDoc();

        // 把HL7分成段
        String[] sHL7Lines = sHL7.split("\n");

        // 去掉XML的关键字
        for (int i = 0; i < sHL7Lines.length; i++) {
            sHL7Lines[i] = sHL7Lines[i].replace("^~\\&", "").replace("MSH", "MSH|");
        }

        for (int i = 0; i < sHL7Lines.length; i++) {
            // 判断是否空行
            if (sHL7Lines[i] != null) {
                String sHL7Line = sHL7Lines[i];

                // 通过 |分隔
                String[] sFields = GetMessgeFields(sHL7Line);

                // 为段（一行）创建第一级节点
                Element el = document.getRootElement().addElement(sFields[0]);

                // 循环每一行
                Boolean isMsh = true;
                for (int a = 1; a < sFields.length; a++) {

                    // 是否包括HL7的连接符^~\\&
                    if (sFields[a].indexOf('^') > 0 || sFields[a].indexOf('~') > 0 || sFields[a].indexOf('\\') > 0
                            || sFields[a].indexOf('&') > 0) {// 0:如果这一行有任一分隔符

                        // 开始操作~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

                        // 通过~分隔
                        String[] sComponents = GetRepetitions(sFields[a]);
                        if (sComponents.length > 1) {// 1:如果可以分隔
                                                     // 0001^郭靖^体检号^EQ^AND~0002^东一区^病区号^EQ^AND

                            for (int b = 0; b < sComponents.length; b++) {
                                // Element fieldEl1 = el.addElement(sFields[0] +
                                // "." + a);
                                CreateComponents(el, sComponents[b], sFields[0], a, b);
                            }

                        } else {// 1：如果真的只有一个值的 0001^郭靖^体检号^EQ^AND
                                // 为字段创建第二级节点
                                // Element fieldEl = el.addElement(sFields[0] +
                                // "." + a);
                            CreateComponents(el, sFields[a], sFields[0], a, 0);

                            // fieldEl.setText(sFields[a]+"11111111111111");
                        }

                        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                    }

                    else {// 0:如果这一行没有任何分隔符
                          // 为字段创建第二级节点
                        Element fieldEl = el.addElement(sFields[0] + "." + a);
                        fieldEl.setText(sFields[a]);

                    }

                }

            } // end if

        } // end for

        // 修改MSH.1 和 MSH.2的值
        document.selectSingleNode("HL7Message/MSH/MSH.1").setText("|");
        document.selectSingleNode("HL7Message/MSH/MSH.2").setText("~^\\&");
        // document.selectNodes("MSH/MSH.1");

        return document;
    }

    @SuppressWarnings("unused")
    private static Element CreateComponents(final Element el, final String hl7Components, String sField, int a, int b) {
        Element componentEl = el.addElement(sField + "." + a);
        // Element componentEl =el;//.addElement(sField + "." + a + "." + b);

        // &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
        // 通过&分隔
        String[] subComponents = GetSubComponents(hl7Components);
        if (subComponents.length > 1) {// 2.如果有字组，一般是没有的。。。 子分组 用&很少用

        } else {// 2.如果没有了，就用^分组
            String[] sRepetitions = GetComponents(hl7Components);
            if (sRepetitions.length > 1) {
                Element repetitionEl = null;
                for (int c = 0; c < sRepetitions.length; c++) {
                    repetitionEl = componentEl.addElement(sField + "." + a + "." + (c + 1));
                    repetitionEl.setText(sRepetitions[c]);
                }
            } else {
                componentEl.setText(hl7Components);
            }

        }
        // &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&

        return el;

    }

    /// <summary>
    /// 通过|分隔 字段
    /// </summary>
    /// <param name="s"></param>
    /// <returns></returns>
    private static String[] GetMessgeFields(String s) {
        return s.split("\\|");
    }

    /// <summary>
    /// 通过^分隔 组字段
    /// </summary>
    /// <param name="s"></param>
    /// <returns></returns>
    private static String[] GetComponents(String s) {
        return s.split("\\^");
    }

    /// <summary>
    /// 通过&分隔 子分组组字段
    /// </summary>
    /// <param name="s"></param>
    /// <returns></returns>
    private static String[] GetSubComponents(String s) {
        return s.split("&");
    }

    /// <summary>
    /// 通过~分隔 重复
    /// </summary>
    /// <param name="s"></param>
    /// <returns></returns>
    private static String[] GetRepetitions(String s) {
        return s.split("~");
    }

    /// <summary>
    /// 创建XML对象
    /// </summary>
    /// <returns></returns>
    private static Document CreateXmlDoc() {
        Document output = DocumentHelper.createDocument();
        // 生成一个接点
        Element rootNode = output.addElement("HL7Message");
        return output;
    }

    public static String GetText(Document document, String path) {
        Node node = document.selectSingleNode("HL7Message/" + path);
        if (node != null) {
            return node.getText();
        } else {
            return null;
        }
    }

    public static String GetText(Document document, String path, int index) {
        List nodes = document.selectNodes("HL7Message/" + path);
        if (nodes != null) {
            return ((Node) nodes.get(index)).getText();
        } else {
            return null;
        }

    }

    public static List GetTexts(Document document, String path) {
        List nodes = document.selectNodes("HL7Message/" + path);
        return nodes;

    }

    public static void writeDocument(Document document, String filepath) {
        try {
            // 读取文件
            // FileWriter fileWriter = new FileWriter(filepath);
            Writer writer = new OutputStreamWriter(new FileOutputStream(filepath), "utf-8");

            // 设置文件编码
            OutputFormat xmlFormat = new OutputFormat();
            xmlFormat.setEncoding("utf-8");
            // 创建写文件方法
            XMLWriter xmlWriter = new XMLWriter(writer, xmlFormat);
            // 写入文件
            xmlWriter.write(document);
            // 关闭
            xmlWriter.close();
        } catch (IOException e) {
            System.out.println("文件没有找到");
            e.printStackTrace();
        }
    }

}