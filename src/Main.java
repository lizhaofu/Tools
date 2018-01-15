import Config.PathConfig;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static Config.PathConfig.filePath;

/**
 * Created by lizhaofu on 2017/6/19.
 */
public class Main {
    public static void main(String[] args) {
        File files = new File(filePath);
        if (!files.exists()){
            files.mkdirs();
        }

        for (File file : files.listFiles()) {
            file.delete();
        }
        //czwddataprocess
        DataProcess dataProcess = new DataProcess();
        dataProcess.readRuleFile();
        //czwdataprocess
        Papers papers = new Papers();

        try {
            papers.run();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



}
