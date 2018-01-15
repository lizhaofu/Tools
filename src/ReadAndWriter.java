import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ReadAndWriter {
    List patentList = new ArrayList();
    public List DataRead(String inputPath) {
        String lineCurrent = null;
        File file = new File(inputPath);
        File[] tempList = file.listFiles();
        //file.listFiles();
        for (int j = 0; j < tempList.length; j++) {
            String patent = null;
            if (!tempList[j].exists()) {
                System.out.println("file " + file + " is not existed, exit");
            }
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(tempList[j]), "UTF-8"));
                lineCurrent = br.readLine();
                while (lineCurrent != null) {
                    patent = lineCurrent.toString() ;
                    patentList.add(patent);
                    lineCurrent = br.readLine();
                }
//                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return patentList;
    }

    public void PatentWriter(String outputPath,List patentList){
        File f=new File(outputPath);
        BufferedWriter bw= null;
        try {
            bw = new BufferedWriter(new FileWriter(f));
            for (int i = 0; i < patentList.size(); i++) {
                bw.write(patentList.get(i).toString());
                bw.newLine();
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String inputPath = "Data/20171127";
        String outputPath = "Data/paper1023.txt";
        ReadAndWriter raw = new ReadAndWriter();
        raw.PatentWriter(outputPath,raw.DataRead(inputPath));
    }
}
