
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import Config.*;

/**
 * Created by tsinghua on 2017/7/28.
 */
public class DataProcess {
    private static Map<String,String> cleanRule;
    public DataProcess(){
        cleanRule = new HashMap<String,String>();
    }
    public void readRuleFile() {
        File files = new File(PathConfig.mergeRulePath);
        String[] filelist = files.list();
        for (int i = 0; i < filelist.length; i++) {
            File file = new File(PathConfig.mergeRulePath + "/" + filelist[i]);
            if (file.exists() && file.isFile()) {
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
                    String lineTxt = null;
                    String stem_name = null;
                    String[] firm_name = null;
                    while ((lineTxt = bufferedReader.readLine()) != null) {
                        if (lineTxt.contains("*")) {
                            stem_name = lineTxt.replaceAll("\\*", "").trim();
                            cleanRule.put(stem_name, stem_name);

                        }
                        if (lineTxt.contains("^")) {
                            firm_name = lineTxt.split("\\^|\\$");
                            if (firm_name.length > 1) {
                                cleanRule.put(firm_name[1], stem_name);
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("读取文件错误");
                }
            } else {
                System.out.println("找不到文件");
            }
        }
    }
    public static String getStemOrganization(String organization){
        Object a =null;
        if ((a=cleanRule.get(organization))==null){
            a=organization;
        }
        return a.toString();
    }

    public Map<String,String> getCleanRule(){
        return cleanRule;
    }

    public static void main(String[] args) {
        DataProcess dataProcess = new DataProcess();
        dataProcess.readRuleFile();
        Set set = dataProcess.getCleanRule().keySet();
        System.out.println(set.size());
        System.out.println(dataProcess.getCleanRule().get("MAHLE Behr GmbH")+"``````````````````````````````````");


    }

}
