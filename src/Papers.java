

import Config.PathConfig;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.io.*;
import java.util.*;

import static Config.PathConfig.filePath;
import static Config.PathConfig.originalData;

/**
 * Created by Administrator on 2017/6/2.
 */
public class Papers {
    private Map<Integer, List<Integer>> citedPaperIndex;
    Map<Map.Entry<String, String>, Integer> organizationCitingMap;
    private List<JSONObject> documentObjectList;
    private List<JSONObject> papersJson;
    private List<Paper> paperList;
    private List<String> DIOList;
    private List<Integer> allCitedIndex;
    Map<String, String> fileClass;
    Map<Integer, String> indexToFileName;
    int maxClass;
    Map<String, Integer> classCount;
    List<Map.Entry<String, Integer>> infoIds;///
    private static int top = 50;//请修改对应的数目
    private static int startyear = 1980;
    private static int endyear = 2017;
    Map<String, Integer> hashMap = new HashMap<String, Integer>();//机构对应文章数
    private  List<ConPaper> conPaperList;


    public Papers() {
        citedPaperIndex = new HashMap<>();
        papersJson = new ArrayList<>();
        paperList = new ArrayList<>();
        DIOList = new ArrayList<>();
        allCitedIndex = new ArrayList<>();
        fileClass = new HashMap<>();
        indexToFileName = new HashMap<>();
        maxClass = 0;
        classCount = new HashMap<>();
        organizationCitingMap = new HashMap<>();
        conPaperList = new ArrayList<>();
        documentObjectList = new ArrayList<>();
    }

    public List<ConPaper> getConPaperList() {
        return conPaperList;
    }

    public List<JSONObject> getDocumentObjectList() {
        return documentObjectList;
    }

    private void readPapers(String path) {
        File file = new File(path);
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (!files[i].exists()) {
                System.out.println("file" + "\"" + files[i] + "\"" + "is not exist!");
            }
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(files[i]), "UTF-8"));
                String lineText;
                JSONObject jsonObject = new JSONObject();
                StringBuilder content = new StringBuilder();
                while ((lineText = bufferedReader.readLine()) != null) {
                    if (!lineText.startsWith("  ") && lineText != "" && !lineText.startsWith("FN") && !lineText.startsWith("VR")) {
                        if (content.length() > 2) {
                            jsonObject.put(content.toString().substring(0, 2), content.toString().substring(3));
                        }
                        if (lineText.startsWith("PT")) {
                            jsonObject = new JSONObject();//reset jsonObject
                        }
                        content = new StringBuilder(lineText);
                        if (lineText.startsWith("ER") && jsonObject.has("DI")) {//ER文件结束
                            jsonObject.put("classID", i);//按文件排名分类0-N
                            jsonObject.put("ID", Integer.toString(paperList.size()));
                            paperList.add(new Paper(jsonObject, papersJson.size()));
                            papersJson.add(jsonObject);
                            documentObjectList.add(jsonObject);
                        }
                    } else if (lineText.startsWith("  ")) {
                        content.append("||");
                        content.append(lineText.trim());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void SortWrite() {
        List<String> organizationList = new ArrayList<>();
        for (int i = 0; i < paperList.size(); i++) {//将出现的所有机构加入list
            if (paperList.get(i).getOrganization() != null) {
//                organizationList.add(paperList.get(i).getOrganization());
                for (int j = 0; j < paperList.get(i).getOrgList().size(); j++) {
                    organizationList.add(paperList.get(i).getOrgList().get(j));
                }
            }
        }

        for (String temp : organizationList) {//统计
            Integer count = hashMap.get(temp);
            hashMap.put(temp, (count == null) ? 1 : count + 1);
        }
        infoIds = new ArrayList<Map.Entry<String, Integer>>(hashMap.entrySet());
        Collections.sort(infoIds, new Comparator<Map.Entry<String, Integer>>() {//sort
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return (o2.getValue() - o1.getValue());
            }
        });
        try {
            //The following three lines are written to the Excel initialization operation
            OutputStream os = new FileOutputStream(filePath + "organizationsort.xlsx");
            SXSSFWorkbook wb = new SXSSFWorkbook();
            SXSSFSheet sheet = (SXSSFSheet) wb.createSheet("organizationsort");
            SXSSFRow row;
            for (int i = 0; i < infoIds.size(); i++) {
                row = (SXSSFRow) sheet.createRow(i);
                String[] s = infoIds.get(i).toString().split("=");
                if (s[0] != "") {
                    row.createCell(0).setCellValue(s[0]);
                    row.createCell(1).setCellValue(s[1]);
                }
            }
            wb.write(os);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void SortWriteTop() {
        List<String> organizationList = new ArrayList<>();
        for (int i = 0; i < paperList.size(); i++) {
                if (paperList.get(i).getOrganization() != null
                        && paperList.get(i).getYear() > startyear && paperList.get(i).getYear() < endyear
                        ) {//修改
//                organizationList.add(paperList.get(i).getOrganization());
                    for (int j = 0; j < paperList.get(i).getOrgList().size(); j++) {
                        for (int k = 0; k < 100; k++) {//czw
                            String[] s1 = infoIds.get(k).toString().split("=");
                            if (paperList.get(i).getOrgList().get(j).equals(s1[0])) {
                                organizationList.add(paperList.get(i).getOrgList().get(j));
                            }
                        }
                    }
                }
        }
        Map<String, Integer> hashMap = new HashMap<String, Integer>();
        for (String temp : organizationList) {
            Integer count = hashMap.get(temp);
            hashMap.put(temp, (count == null) ? 1 : count + 1);
        }

//        try {
//            //The following three lines are written to the Excel initialization operation
//            OutputStream os = new FileOutputStream(filePath + "Toporganizationsort.xlsx");
//            SXSSFWorkbook wb = new SXSSFWorkbook();
//            SXSSFSheet sheet = (SXSSFSheet) wb.createSheet("organizationsort");
//            SXSSFRow row;
//            List<Map.Entry<String, Integer>> infoList = new ArrayList<Map.Entry<String, Integer>>(hashMap.entrySet());
//            Collections.sort(infoList, new Comparator<Map.Entry<String, Integer>>() {//sort
//                public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
//                    return (o2.getValue() - o1.getValue());
//                }
//            });
//            for (int i = 0; i < infoList.size(); i++) {
//                String[] s = infoList.get(i).toString().split("=");
//                row = (SXSSFRow) sheet.createRow(i);
//                row.createCell(0).setCellValue(s[0]);
//                row.createCell(1).setCellValue(s[1]);
//
//            }
//
//            wb.write(os);
//            os.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
    public void aimORGpaperscnt() {
        List<String> organizationList = new ArrayList<>();
        for (int i = 0; i < paperList.size(); i++) {
            if (paperList.get(i).getOrganization() != null
                    && paperList.get(i).getYear() > startyear && paperList.get(i).getYear() < endyear
                    ) {//修改
//                organizationList.add(paperList.get(i).getOrganization());
                for (int j = 0; j < paperList.get(i).getOrgList().size(); j++) {
                    for (int k = 0; k < top; k++) {//czw
                        String[] s1 = infoIds.get(k).toString().split("=");
                        if (paperList.get(i).getOrgList().get(j).equals(s1[0])) {
                            organizationList.add(paperList.get(i).getOrgList().get(j));
                        }
                    }
                }
            }
        }
        Map<String, Integer> hashMap = new HashMap<String, Integer>();
        for (String temp : organizationList) {
            Integer count = hashMap.get(temp);
            hashMap.put(temp, (count == null) ? 1 : count + 1);
        }

        try {
            //The following three lines are written to the Excel initialization operation
            OutputStream os = new FileOutputStream(filePath + "aimorganization.xlsx");
            SXSSFWorkbook wb = new SXSSFWorkbook();
            SXSSFSheet sheet = (SXSSFSheet) wb.createSheet("organizationpapercnt");
            SXSSFRow row;
            for (int i = 0; i < infoIds.size(); i++) {
                String[] s = infoIds.get(i).toString().split("=");
                row = (SXSSFRow) sheet.createRow(i);
                row.createCell(0).setCellValue(s[0]);
                if (hashMap.get(s[0])!=null) {
                    row.createCell(1).setCellValue(hashMap.get(s[0]));
                }else {
                    row.createCell(1).setCellValue((Integer)0);
                }

            }

            wb.write(os);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class Organization {
        String name;
        Map<String, Integer> citingInfo;

        public Organization() {
            name = "";
            citingInfo = new HashMap<>();
        }
    }
    class OrganizationCiting {
        String name;
        Map<String, Integer> citedInfos;

        public OrganizationCiting() {
            name = "";
            citedInfos = new HashMap<>();
        }

    }

    public void OrganizationCiting() {
        int top = 50;
        List<Organization> organizationList = new ArrayList<>();
        Organization organization = new Organization();
        for (int i = 0; i < top; i++) {
            String[] s1 = infoIds.get(i).toString().split("=");
            for (int j = 0; j < top; j++) {
                String[] s2 = infoIds.get(j).toString().split("=");
                int b = 0;
                for (int k = 0; k < paperList.size(); k++) {
                    if (paperList.get(k).getOrganization() != null) {
                        if (paperList.get(k).getOrganization().equals(s1[0])) {
                            int a = 0;
                            for (int l = 0; l < paperList.size(); l++) {
                                if (paperList.get(l).getOrganization() != null) {
                                    for (String str : paperList.get(k).getCitationDOI()) {
                                        if (str.equals(paperList.get(l).getDOI())
                                                && paperList.get(l).getOrganization().equals(s2[0])) {
                                            a = a + 1;
                                        }
                                    }
                                }
                            }
                            b = b + a;
                        }
                    }
                }
                organization = new Organization();
                organization.name = s1[0];
                organization.citingInfo.put(s2[0], b);
                organizationList.add(organization);
            }

        }


        try {
            //The following three lines are written to the Excel initialization operation
            OutputStream os = new FileOutputStream(filePath + "organizationcitingnetwork.xlsx");
            SXSSFWorkbook wb = new SXSSFWorkbook();
            SXSSFSheet sheet = (SXSSFSheet) wb.createSheet("organizationciting");
            SXSSFRow row;
            SXSSFRow row1;
            row = (SXSSFRow) sheet.createRow(0);
            for (int i = 0; i < top; i++) {//czw
                String[] s1 = infoIds.get(i).toString().split("=");
                row.createCell(i + 1).setCellValue(s1[0]);
            }
            for (int j = 0; j < top; j++) {//czw
                String[] s2 = infoIds.get(j).toString().split("=");
                row = (SXSSFRow) sheet.createRow(j + 1);
                row.createCell(0).setCellValue(s2[0]);
                for (int i = 0; i < top; i++) {//czw
                    String[] s1 = infoIds.get(i).toString().split("=");
                    for (int k = 0; k < organizationList.size(); k++) {
                        if (organizationList.get(k).name.equals(s1[0])) {
                            for (Map.Entry<String, Integer> entry : organizationList.get(k).citingInfo.entrySet()) {
                                if (entry.getKey().equals(s2[0])) {
                                    row.createCell(i + 1).setCellValue(entry.getValue());

                                }
                            }

                        }

                    }
                }

            }

            wb.write(os);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getaimOranization(){
        List<Map.Entry<String, Integer>> infoIds_tmp;///
        Map<String,Integer> map = new HashMap<String,Integer>();
        File directory  = new File(PathConfig.aimOrgPath);
        if (directory.isDirectory()){
            String[] filelist = directory.list();
            if(filelist.length>0) {
                for (int i = 0; i < filelist.length; i++) {
                    File file = new File(PathConfig.aimOrgPath + "/" + filelist[i]);
                    try {
                        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
                        String lineTxt = null;
                        while ((lineTxt = br.readLine()) != null) {
                            if (hashMap.containsKey(lineTxt)) {
                                map.put(lineTxt, hashMap.get(lineTxt));
                            } else {
                                System.out.println("现有机构集合中无：" + lineTxt);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("读取文件错误");
                    }
                }
                infoIds_tmp = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
                infoIds = infoIds_tmp;
            }
        }else {
            System.out.println("文件夹路径错误");
        }


    }//检测AimOrganization下是否有目标文件，有的话更新infoIds

    public void AllOrganizationCiting() {
        int top = 50;
        List<Organization> organizationList = new ArrayList<>();
        Organization organization = new Organization();

        Map<String, Paper> paperIDMap = new HashMap<>();
        for (int p = 0; p < paperList.size(); p++) {
            paperIDMap.put(paperList.get(p).getDOI(), paperList.get(p));
            int i = 0;
        }

        //引用矩阵生成
        List<OrganizationCiting> organizationCitingList = new ArrayList<>();
        OrganizationCiting organizationCiting = new OrganizationCiting();
        for (int i = 0; i < top; i++) {//遍历第一个机构
            String[] s1 = infoIds.get(i).toString().split("=");
            System.out.println("引用关系："+i);
            for (int j = 0; j < top; j++) {//遍历第二个机构
                String[] s2 = infoIds.get(j).toString().split("=");
                int b = 0;
                for (int k = 0; k < paperList.size(); k++) {//遍历所有文章k
                    for (int p = 0; p < paperList.get(k).getOrgList().size(); p++) {//遍历数据k中的机构列表
                        if (paperList.get(k).getOrgList().get(p) != null && paperList.get(k).getCitationDOI() != null
                                && paperList.get(k).getYear() > startyear && paperList.get(k).getYear() < endyear) {//如果第k个数据属于目标时间段
                            if (paperList.get(k).getOrgList().get(p).equals(s1[0])) {//如果数据k中的机构p等于s1
                                int a = 0;
                                for (int l = 0; l < paperList.size(); l++) {//遍历所有文章l
                                    if (l!=k){//l，k不同
                                        for (int m = 0; m < paperList.get(l).getOrgList().size(); m++) {//遍历m中的所有机构
                                            if (paperList.get(l).getOrgList().get(m)!=null){
                                                for (String str : paperList.get(k).getCitationDOI()){//遍历数据k中的引用doi
                                                    if (str.equals(paperList.get(l).getDOI())&&paperList.get(l).getOrgList().get(m).equals(s2[0])){//
                                                        a= a+1;
                                                    }
                                                }
                                            }

                                        }
                                    }

                                }

                                b = b + a;
                            }
                        }
                    }
                }
                organizationCiting = new OrganizationCiting();
                organizationCiting.name = s2[0];
                organizationCiting.citedInfos.put(s1[0], b);//为了后续画图不用转置，换了s1和s2的位置。
                organizationCitingList.add(organizationCiting);

            }
        }
        System.out.println("ok1");
        //被引用矩阵生成

        for (int i = 0; i < top; i++) {
            System.out.println("被引信息："+i);
            String[] s1 = infoIds.get(i).toString().split("=");
            for (int j = 0; j < top; j++) {
                String[] s2 = infoIds.get(j).toString().split("=");
                int b = 0;


                for (int k = 0; k < paperList.size(); k++) {//遍历所有数据，k
                    if (paperList.get(k).getYear() < endyear && paperList.get(k).getYear() > startyear) {//限定k为目标阶段的数据
                        for (int p = 0; p < paperList.get(k).getOrgList().size(); p++) {//遍历数据k中的机构列表 p
                            if (paperList.get(k).getOrgList().get(p) != null && paperList.get(k).getCitationDOI() != null) {

                                if (paperList.get(k).getOrgList().get(p).equals(s1[0])) {//如果数据k中的机构列表p为s1

                                    int a = 0;
                                    for (int l = 0; l < paperList.size(); l++) {//遍历所有数据l
                                        if (paperList.get(l).getCitationDOI().contains(paperList.get(k).getDOI())
                                                &&(paperList.get(l).getYear()>startyear&&paperList.get(l).getYear()<endyear)) {
                                            continue;
                                        }
                                        if (l!=k){//如果l，k不是同一个数据
                                            for (int m = 0; m < paperList.get(l).getOrgList().size(); m++) {//遍历数据l中的所有机构
                                                if (paperList.get(l).getOrgList().get(m)!=null){
                                                    for (String str : paperList.get(l).getCitationDOI()){//遍历l中的所有引用
                                                        if (str.equals(paperList.get(k).getDOI())&&paperList.get(l).getOrgList().get(m).equals(s2[0])){
                                                            a= a+1;
                                                        }
                                                    }
                                                }

                                            }
                                        }

//                                        for (String str : paperList.get(l).getCitationDOI()) {
//                                            if (!(paperIDMap.get(str) != paperList.get(k)) && paperIDMap.get(str) != null && paperIDMap.get(str).getOrgList().size() > 0) {
//                                                for (int n = 0; n < paperIDMap.get(str).getOrgList().size(); n++) {
//                                                    if (paperIDMap.get(str).getOrgList().get(n).equals(s2[0])) {
//                                                        a = a + 1;
//                                                    }
//                                                }
//                                            }
//                                        }



                                    }
                                    b = b + a;
                                }

                            }
                        }
                    }
                }
                organization = new Organization();
                organization.name = s1[0];//为了画图的时候方便，这里不改，改引用对应位置的顺序
                organization.citingInfo.put(s2[0], b);
                organizationList.add(organization);
            }
        }
        System.out.println("ok2");


        try {
            //The following three lines are written to the Excel initialization operation
            OutputStream os = new FileOutputStream(filePath + "organizationcitingnetwork.xlsx");
            SXSSFWorkbook wb = new SXSSFWorkbook();
            SXSSFSheet sheet = (SXSSFSheet) wb.createSheet("organizationciting");
            SXSSFRow row;
            row = (SXSSFRow) sheet.createRow(0);
            for (int q = 0; q < 50; q++) {
                String[] ts1 = infoIds.get(q).toString().split("=");
                row.createCell(q + 1).setCellValue(ts1[0]);
            }
            for (int j = 0; j < 50; j++) {
                String[] s2 = infoIds.get(j).toString().split("=");
                row = (SXSSFRow) sheet.createRow(j + 1);
                row.createCell(0).setCellValue(s2[0]);
                for (int ti = 0; ti < 50; ti++) {
                    String[] ts1 = infoIds.get(ti).toString().split("=");
                    for (int k = 0; k < organizationList.size(); k++) {
                        for (int l = 0; l < organizationCitingList.size(); l++) {
                            if (organizationList.get(k).name.equals(ts1[0]) && organizationCitingList.get(l).name.equals(ts1[0])) {
                                for (Map.Entry<String, Integer> entry : organizationList.get(k).citingInfo.entrySet()) {
                                    for (Map.Entry<String, Integer> entryt : organizationCitingList.get(l).citedInfos.entrySet()) {
                                        if (entryt.getKey().equals(s2[0]) && entry.getKey().equals(s2[0])) {
                                            row.createCell(ti + 1).setCellValue(entry.getValue() + entryt.getValue());
                                            int h = 0;
                                        }

                                    }
                                }
                            }

                        }

                    }
                }

            }

            wb.write(os);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("ok3");
        try {
            //The following three lines are written to the Excel initialization operation
            OutputStream os = new FileOutputStream(filePath + "citing.xlsx");
            SXSSFWorkbook wb = new SXSSFWorkbook();
            SXSSFSheet sheet = (SXSSFSheet) wb.createSheet("citing");
            SXSSFRow row;
            row = (SXSSFRow) sheet.createRow(0);
            for (int q = 0; q < 50; q++) {
                String[] ts1 = infoIds.get(q).toString().split("=");
                row.createCell(q + 1).setCellValue(ts1[0]);
            }
            for (int j = 0; j < 50; j++) {
                String[] s2 = infoIds.get(j).toString().split("=");
                row = (SXSSFRow) sheet.createRow(j + 1);
                row.createCell(0).setCellValue(s2[0]);
                for (int ti = 0; ti < 50; ti++) {
                    String[] ts1 = infoIds.get(ti).toString().split("=");

                    for (int l = 0; l < organizationCitingList.size(); l++) {
                        if (organizationCitingList.get(l).name.equals(ts1[0])) {

                            for (Map.Entry<String, Integer> entryt : organizationCitingList.get(l).citedInfos.entrySet()) {
                                if (entryt.getKey().equals(s2[0])) {
                                    row.createCell(ti + 1).setCellValue(entryt.getValue());
                                    int h = 0;
                                }
                            }
                        }
                    }
                }
            }
            wb.write(os);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("输出引用");

        try {
            //The following three lines are written to the Excel initialization operation
            OutputStream os = new FileOutputStream(filePath + "cited.xlsx");
            SXSSFWorkbook wb = new SXSSFWorkbook();
            SXSSFSheet sheet = (SXSSFSheet) wb.createSheet("cited");
            SXSSFRow row;
            row = (SXSSFRow) sheet.createRow(0);
            for (int q = 0; q < 50; q++) {
                String[] ts1 = infoIds.get(q).toString().split("=");
                row.createCell(q + 1).setCellValue(ts1[0]);
            }
            for (int j = 0; j < 50; j++) {
                String[] s2 = infoIds.get(j).toString().split("=");
                row = (SXSSFRow) sheet.createRow(j + 1);
                row.createCell(0).setCellValue(s2[0]);
                for (int ti = 0; ti < 50; ti++) {
                    String[] ts1 = infoIds.get(ti).toString().split("=");
                    for (int l = 0; l < organizationList.size(); l++) {
                        if (organizationList.get(l).name.equals(ts1[0])) {
                            for (Map.Entry<String, Integer> entry : organizationList.get(l).citingInfo.entrySet()) {
                                if (entry.getKey().equals(s2[0])) {
                                    row.createCell(ti + 1).setCellValue(entry.getValue());
                                    int h = 0;
                                }
                            }
                        }
                    }
                }
            }
            wb.write(os);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("输出被引用");
    }

    class ConPaper {
        Paper name;
        Map<Paper, Integer> conciting;
        public ConPaper() {
            name = new Paper();
            conciting = new HashMap<>();
        }
    }


    public void ConCiting() {
        //生成共引矩阵
        for (int i = 0; i < paperList.size(); i++) {
            for (int j = 0; j < paperList.size(); j++) {
                ConPaper conPaper = new ConPaper();
                int a = 0;
                for (int k = 0; k < paperList.get(i).getReferenceList().size(); k++) {
                    for (int l = 0; l < paperList.get(j).getReferenceList().size(); l++) {
                        if (paperList.get(i).getReferenceList().get(k) .equals(paperList.get(j).getReferenceList().get(l)) ) {
                            a = a + 1;
                        }
                    }
                }
                conPaper.name = paperList.get(i);
                conPaper.conciting.put(paperList.get(j),a);
                conPaperList.add(conPaper);
            }
        }
        //写入共引
        try {
            //The following three lines are written to the Excel initialization operation
            OutputStream os = new FileOutputStream(filePath + "conciting-DI.xlsx");
            SXSSFWorkbook wb = new SXSSFWorkbook();
            SXSSFSheet sheet = (SXSSFSheet) wb.createSheet("conciting");
            SXSSFRow row;
            row = (SXSSFRow) sheet.createRow(0);
            int paperListLen = paperList.size();
            int top = 100;
            for (int q = 0; q < paperListLen; q++) {
                row.createCell(q + 1).setCellValue(paperList.get(q).getID());
            }
            for (int j = 0; j < paperListLen; j++) {
                row = (SXSSFRow) sheet.createRow(j + 1);
                row.createCell(0).setCellValue(paperList.get(j).getID());
                for (int q = 0; q < paperListLen; q++) {
                    for (int i = 0; i < conPaperList.size(); i++) {
                        if (conPaperList.get(i).name.getTitle().equals(paperList.get(j).getTitle()) ) {
                            for (Map.Entry<Paper, Integer> entryt : conPaperList.get(i).conciting.entrySet()) {
                                if (entryt.getKey().getTitle().equals(paperList.get(q).getTitle()) ) {
                                    row.createCell(q+1 ).setCellValue(entryt.getValue());
                                }
                            }
                        }
                    }
                }
            }
            wb.write(os);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //提取摘要和题目
    public void WriteAbstract() {
        try {

            for (int i = 0; i < paperList.size(); i++) {
                BufferedWriter bw = new BufferedWriter(new FileWriter(filePath + i));
                bw.write(paperList.get(i).getTitle());
                bw.write("\n"+paperList.get(i).getAbstracts());
                bw.flush();
                bw.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //输出XML
    public void buildXMLDocCopy() throws IOException, JSONException {

        Element root = new Element("graph").setAttribute("directed", "0");
        Document document = new Document(root);
        List<String> attribute;
        attribute = new ArrayList<>(Arrays.asList("classID", "TI", "SO", "PY", "Z9","AB"));

        for (int i = 0; i < paperList.size(); i++) {
            Element elements = new Element("node").setAttribute("id", Integer.toString(paperList.get(i).getID()));
            Element element = new Element("att").setAttribute("name", "name");
            element.setAttribute("value", Integer.toString(paperList.get(i).getID()));
            elements.addContent(element);
            for (int j = 0; j < attribute.size(); j++) {
                if (documentObjectList.get(i).has(attribute.get(j))) {
                    Element att = new Element("att").setAttribute("name", attribute.get(j));
                    att.setAttribute("value", documentObjectList.get(i).get(attribute.get(j)).toString().replace("||", " "));
                    elements.addContent(att);
                }
            }
            root.addContent(elements);
        }

        for (int i = 0; i < conPaperList.size(); i++) {
            for (Map.Entry<Paper, Integer> entryt : conPaperList.get(i).conciting.entrySet()) {
                if (entryt.getValue() != 0&&Integer.toString(conPaperList.get(i).name.getID())!=Integer.toString(entryt.getKey().getID())){
                    Element elements = new Element("edge").setAttribute("source", Integer.toString(conPaperList.get(i).name.getID()));

                    elements.setAttribute("target", Integer.toString(entryt.getKey().getID()));
                    root.addContent(elements);
                    elements.addContent("");
                }
            }
        }

        Format format = Format.getPrettyFormat();
        XMLOutputter XMLOut = new XMLOutputter(format);
        XMLOut.output(document, new FileOutputStream(PathConfig.refRelationship));
    }

    public class Country2Country {
        private String name;
        private HashMap<String,Integer> countryCiting;

        public Country2Country(){
            name = "";
            countryCiting = new HashMap<>();
        }
    }

    public void countryCiting(){
        Set<String> countrySet = new HashSet<>();
        List<Country2Country> country2CountryList = new ArrayList<>();
        for (int i = 0; i < paperList.size(); i++) {
            if (paperList.get(i).getCountry()!=null
                    &&paperList.get(i).getYear() > 1970 && paperList.get(i).getYear() < 1981) {
                countrySet.add(paperList.get(i).getCountry());
            }
        }
        List<String> countryList = new ArrayList<>();
        countryList.addAll(countrySet);
        for (int i = 0; i < countryList.size(); i++) {

            for (int j = 0; j < countryList.size(); j++) {
                int countryCitingCount = 0;
                for (int k = 0; k < paperList.size(); k++) {
                    int paperCitingCount = 0;
                    if (paperList.get(k).getCountry()!= null
                            &&paperList.get(k).getCountry().equals(countryList.get(i))
                            &&paperList.get(k).getYear() > 1970 && paperList.get(k).getYear() < 1981){
                            for (int m = 0; m < paperList.size(); m++) {
                                if (paperList.get(m).getCountry()!=null&&paperList.get(m).getCountry().equals(countryList.get(j))
                                                &&paperList.get(k).getCitationDOI().contains(paperList.get(m).getDOI())){
                                    paperCitingCount = paperCitingCount+1;
                                }
                            }
                        countryCitingCount = countryCitingCount + paperCitingCount;
                    }
                }
                Country2Country c2c = new Country2Country();
                c2c.name = countryList.get(i);
                c2c.countryCiting.put(countryList.get(j),countryCitingCount);
                country2CountryList.add(c2c);
            }
        }

        //写入引用
        try {
            //The following three lines are written to the Excel initialization operation
            OutputStream os = new FileOutputStream(filePath + "1971-1980countryciting.xlsx");
            SXSSFWorkbook wb = new SXSSFWorkbook();
            SXSSFSheet sheet = (SXSSFSheet) wb.createSheet("countryciting");
            SXSSFRow row;
            row = (SXSSFRow) sheet.createRow(0);
            int paperListLen = countryList.size();
            int top = 100;
            for (int q = 0; q < paperListLen; q++) {
                row.createCell(q +1).setCellValue(countryList.get(q).toUpperCase());
            }
            for (int j = 0; j < paperListLen; j++) {
                row = (SXSSFRow) sheet.createRow(j + 1);
                row.createCell(0).setCellValue(countryList.get(j).toUpperCase());

                for (int q = 0; q < paperListLen; q++) {
                    for (int i = 0; i < country2CountryList.size(); i++) {
                        if (country2CountryList.get(i).name != null &&
                                country2CountryList.get(i).name.equals(countryList.get(j))) {
                            for (Map.Entry<String, Integer> entryt : country2CountryList.get(i).countryCiting.entrySet()) {
                                if (entryt.getKey() != null && entryt.getKey().equals(countryList.get(q))) {
                                    row.createCell(q + 1).setCellValue(entryt.getValue());
                                }
                            }
                        }
                    }
                }
            }
            wb.write(os);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }



    public void run() throws IOException, JSONException {
        readPapers(originalData);
        System.out.println("Complete the file reading work!");

//        SortWrite();
//        SortWriteTop();
//        OrganizationCiting();
//        getaimOranization();
//        aimORGpaperscnt();
       ConCiting();

//        AllOrganizationCiting();
//        WriteAbstract();
        buildXMLDocCopy();
//        countryCiting();


    }


    private void readPapersAndClass(String paperPath, String classPath) throws JSONException {

/**
 * 读分类文件
 */
        File classFile = new File(classPath);
        if (!classFile.exists()) {
            System.out.println("file" + "\"" + classFile + "\"" + "is not exist!");
        }
        else {
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(classFile)));
                String classID;
                while ((classID = bufferedReader.readLine()) != null) {
                    String[] indexAndClass = classID.split("\t");
                    if (indexAndClass.length > 1) {
                        if (classCount.containsKey(indexAndClass[1].trim())) {
                            classCount.put(indexAndClass[1].trim(), classCount.get(indexAndClass[1].trim()) + 1);
                        } else {
                            classCount.put(indexAndClass[1].trim(), 1);
                        }
                        maxClass = Math.max(Integer.parseInt(indexAndClass[1].trim()), maxClass);
                        fileClass.put(indexAndClass[0].trim(), indexAndClass[1].trim());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        //readPapers(paperPath);
        for (String fileName : fileClass.keySet()) {
            File file = new File(paperPath + fileName + ".txt");
            if (!file.exists()) {
                System.out.println("file" + "\"" + file + "\"" + "is not exist!");
            } else {
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
                    String lineText;
                    JSONObject jsonObject = new JSONObject();
                    StringBuilder content = new StringBuilder();
                    while ((lineText = bufferedReader.readLine()) != null) {
                        if (!lineText.startsWith("  ") && lineText != "" && !lineText.startsWith("FN") && !lineText.startsWith("VR")) {
                            if (content.length() > 2) {
                                jsonObject.put(content.toString().substring(0, 2), content.toString().substring(3));
                            }
                            if (lineText.startsWith("PT")) {
                                jsonObject = new JSONObject();
                            }
                            content = new StringBuilder(lineText);
                            if (lineText.startsWith("ER") && jsonObject.has("DI")) {
                                paperList.add(new Paper(jsonObject, papersJson.size()));
                                jsonObject.put("ID", fileName);
                                jsonObject.put("classID", fileClass.get(fileName));
                                papersJson.add(jsonObject);
                            }
                        } else if (lineText.startsWith("  ")) {
                            content.append("||");
                            content.append(lineText.trim());
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        /**
         *  去掉未分类的数据
         */
//        for (int i = papersJson.size() - 1; i > -1; i--) {
//            if (!papersJson.get(i).has("classID")) {
//                papersJson.remove(i);
//                paperList.remove(i);
//            }
//        }
    }


    private void setCitedPaperIndex() {
        for (int i = 0; i < paperList.size(); i++) {
            for (int j = 0; j < DIOList.size(); j++) {
                if (paperList.get(i).getCitationDOI().contains(DIOList.get(j))) {
                    if (!allCitedIndex.contains(i)) {
                        allCitedIndex.add(i);
                    }
                    if (!allCitedIndex.contains(j)) {
                        allCitedIndex.add(j);
                    }
                    if (citedPaperIndex.containsKey(i)) {
                        citedPaperIndex.get(i).add(j);
                    } else {
                        citedPaperIndex.put(i, new ArrayList<Integer>());
                        citedPaperIndex.get(i).add(j);
                    }
                }
            }
        }
        Collections.sort(allCitedIndex);
    }

    public void generateDOIList() {
        for (int i = 0; i < paperList.size(); i++) {
            if (paperList.get(i).getDOI() != null) {
                DIOList.add(paperList.get(i).getDOI());
            } else {
                DIOList.add(null);
            }
        }
    }

    public void initialize() throws JSONException {
        String path = PathConfig.originalData;
        readPapers(path);//                         按文件名分类
        //readPapersAndClass(PathConfig.originalData, PathConfig.classPath + "分类结果.txt");//paper为单个文本、有分类文本的数据
        generateDOIList();
        setCitedPaperIndex();
        classCitedRelationship();
    }

    public Map<Integer, List<Integer>> getCitedPaperIndex() {
        return citedPaperIndex;
    }

    public List<Paper> getPaperList() {
        return paperList;
    }

    public void writeDOIToLocal() {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(((new OutputStreamWriter(new FileOutputStream(new File(PathConfig.DOIFile)), "UTF-8"))));
            for (int i = 0; i < paperList.size(); i++) {
                bufferedWriter.write("No:" + i + "\n");
                bufferedWriter.write("DOI:" + paperList.get(i).getDOI() + "\n");
                bufferedWriter.write("citedDOI:");
                for (String doi : paperList.get(i).getCitationDOI()) {
                    bufferedWriter.write(doi + "\n" + "         ");
                }
                bufferedWriter.write("\n");
            }
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeYear() throws JSONException {
        int earliestYear = 2016;
        int latestYear = 2016;
        for (JSONObject paper : papersJson) {
            if (paper.has("PY")) {
                int year = paper.getInt("PY");
                if (year > latestYear) {
                    latestYear = year;
                }
                if (year < earliestYear) {
                    earliestYear = year;
                }
            }
        }
        System.out.println("earliestYear:" + earliestYear);
        System.out.println("latestYear:" + latestYear);
    }

    private void classCitedRelationship() throws JSONException {
        int[][] classRef = new int[maxClass + 1][maxClass + 1];
        for (Integer source : citedPaperIndex.keySet()) {
            for (Integer target : citedPaperIndex.get(source)) {
                if (papersJson.get(source).has("classID") && papersJson.get(target).has("classID")) {
                    int a = papersJson.get(source).getInt("classID");
                    int b = papersJson.get(target).getInt("classID");
                    if (a > b) {
                        classRef[a][b] = classRef[a][b] + 1;
                    } else {
                        classRef[b][a] = classRef[b][a] + 1;
                    }
                }
            }
        }
        Map<String, Integer> classCite = new HashMap<>();
        for (int i = 1; i < classRef.length; i++) {
            for (int j = 0; j < i; j++) {
                classCite.put(i + "-" + j, classRef[i][j]);
            }
        }
        List<Map.Entry<String, Integer>> sortedClassRef = new ArrayList<>(classCite.entrySet());
        Collections.sort(sortedClassRef, new SortByValue());
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(PathConfig.classRefPath))));
            for (Map.Entry entry : classCount.entrySet()) {
                bufferedWriter.write(entry.getKey().toString() + "\t" + entry.getValue() + "\n");
            }
            for (int i = 0; i < sortedClassRef.size(); i++) {
                bufferedWriter.write(sortedClassRef.get(i).getKey() + "\t" + sortedClassRef.get(i).getValue() + "\n");
            }
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Integer> getAllCitedIndex() {
        return allCitedIndex;
    }

    public List<JSONObject> getPapersJson() {
        return papersJson;
    }

    public Map<Integer, String> getIndexToFileName() {
        return indexToFileName;
    }

    class SortByValue implements Comparator<Map.Entry<String, Integer>> {

        @Override
        public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
            return o2.getValue().compareTo(o1.getValue());
        }
    }
}
