import java.io.*;
import java.util.*;

import static Config.PathConfig.filePath;

/**
 * Created by lizhaofu on 2017/5/24.
 */
public class Patents {
    private List<Patent> patentList = new ArrayList<>();
    public List<Patent> DataRead(String inputPath) {
        String lineCurrent = null;
        File file = new File(inputPath);
        File[] tempList = file.listFiles();
        //file.listFiles();
        for (int j = 0; j < tempList.length; j++) {
            if (!tempList[j].exists()) {
                System.out.println("file " + file + " is not existed, exit");
            }
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(tempList[j]), "UTF-8"));
                lineCurrent = br.readLine();
                Patent patent = new Patent();
                Boolean flag = false;
                Boolean flagAll = false;
                while (lineCurrent != null) {
                    String[] wordFlag = lineCurrent.split("  - ");
                    if (wordFlag.length == 0) {
                        lineCurrent = br.readLine();
                        continue;
                    }
                    if (lineCurrent.startsWith("Record")){
                        patent = new Patent();
                    }
                    if (wordFlag[0].equals("pn")) {
                        flagAll = true;
                        patent.setPn(wordFlag[1]);
                        patent.setPatentNum(lineCurrent.substring(5).trim());
                    }
                    patent.setAllContext(patent.getAllContext() + lineCurrent + "\r\n" );

                    if (wordFlag[0].equals("py")) {
                        int ll = lineCurrent.length();
                        if (wordFlag.length>1) {
                            patent.setPatentYear(Integer.parseInt(lineCurrent.substring(6).trim()));
                        }
                    }
                    if (wordFlag[0].equals("abd")) {//read paper file
                        flag = true;
                        patent.setPatentAbstracts(lineCurrent.substring(6).trim());
                    } else if (flag && wordFlag[0].equals("")) {
                        patent.setPatentAbstracts(patent.getPatentAbstracts() + lineCurrent.substring(6).trim());
                    } else if (flag && !wordFlag[0].equals("")) {
                        flag = false;
                    }
                    if (wordFlag[0].equals("frntpgimg")){
                        patentList.add(patent);//
                    }
                    lineCurrent = br.readLine();
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return patentList;
    }
//按照专利名字输出每个专利，并以专利号命名txt文件
    public void CountryWrite(){

        try {
            for (int i = 0; i < patentList.size(); i++) {
                BufferedWriter bw = new BufferedWriter(new FileWriter(filePath + patentList.get(i).getPn() + ".txt"));
                bw.write(patentList.get(i).getAllContext());
                bw.flush();
                bw.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void Run (){
        File files = new File(filePath);
        if (!files.exists()) {
            files.mkdirs();
        }
        for (File file : files.listFiles()) {
            file.delete();
        }
        String inputFilePath = "Data/20171127/";

        DataRead(inputFilePath);
        CountryWrite();

    }

    public static void main(String[] args) {
        Patents ps = new Patents();
        ps.Run();

    }


}
