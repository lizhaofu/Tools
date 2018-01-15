import java.io.*;
import java.util.*;

import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


public class XML2Excel {


    public static void main(String[] args) {
        String file = "Data//0115//business.xml";
        XML2Excel xml2Excel = new XML2Excel();
        xml2Excel.writeExcel(file);
    }

    @SuppressWarnings({ "unchecked" })
    public static List<Map<String,String>> analysisXml(String xmlpath) {
        // 创建linkedHashmap，为有序map，map和hashMap均为无序的map
        List<Map<String,String>> sheetList = new ArrayList<>();
        // 创建SAXReader对象
        SAXReader reader = new SAXReader();
        // 读取文件 转换成Document
        try {
            Document document = reader.read(new File(xmlpath));
            // 获取根节点元素对象
            Element root = document.getRootElement();
            // 遍历根节点下面的所有子节点
            List<Element> rootlist = root.elements();
            Map<String,String> childerMap = null;

            // 循环遍历根节点下面的每一子节点
            for (Element testcase : rootlist) {
                childerMap = new HashMap<>();

                // 遍历子节点下面的节点
                List<Element> testCaseList = testcase.elements();
                for (int i = 0; i < testCaseList.size(); i++) {
                    childerMap.put(testCaseList.get(i).attributeValue("name"),
                            testCaseList.get(i).attributeValue("value"));

                }
                sheetList.add(childerMap);

            }

        } catch (DocumentException e) {
            System.out.println("xml file type error! ");
        }
        return sheetList;
    }


    public  void writeExcel(String file) {

        try {
            //The following three lines are written to the Excel initialization operation
            OutputStream os = new FileOutputStream("Data//result0115//business.xlsx");
            SXSSFWorkbook wb = new SXSSFWorkbook();
            SXSSFSheet sheet = (SXSSFSheet) wb.createSheet("Z9");
            SXSSFRow row;
            row = (SXSSFRow) sheet.createRow(0);
            row.createCell(0).setCellValue("Title");
            row.createCell(1).setCellValue("Cited");
            List<Map<String,String>> sheetList = analysisXml(file);
            for (int i = 0; i < sheetList.size(); i++) {

                row = (SXSSFRow) sheet.createRow(i+1);

                row.createCell(0).setCellValue(sheetList.get(i).get("TI"));
                if (sheetList.get(i).get("Z9")!=null) {
                    int a = Integer.parseInt(sheetList.get(i).get("Z9"));
                    row.createCell(1).setCellValue(a);
                }
            }
            wb.write(os);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}