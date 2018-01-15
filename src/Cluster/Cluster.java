package Cluster;


import org.w3c.dom.*;
import org.xml.sax.*;

import javax.xml.parsers.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.*;

/**
 * Created by dujunfeiz on 2017/6/15.
 */
public class Cluster {
    private Map<String,Map<String,Integer>>nodeClass;
    private List<String> fileName;
    public Cluster(){
        nodeClass=new HashMap<>();
        fileName=new ArrayList<>();
    }
    private void readXML(String path){
        File file=new File(path);

        if (file.exists()){
            File[]fileList=file.listFiles();
            for (int i = 0; i <fileList.length ; i++) {
                fileName.add(fileList[i].getName().replace(".xml",""));
            }
        }
        DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            for (int i = 0; i < fileName.size(); i++) {
                Map<String,Integer>classCount=new HashMap<>();
                Document doc=db.parse(path+fileName.get(i)+".xml");
                NodeList nodeList=doc.getElementsByTagName("node");
                for (int j = 0; j <nodeList.getLength() ; j++) {
                    Node node=nodeList.item(i);
                    for (Node nodeAtt=node.getFirstChild();nodeAtt!=null;nodeAtt=nodeAtt.getNextSibling()) {
                        if (nodeAtt.getNodeType() == Node.ELEMENT_NODE&&((Element)nodeAtt).getAttribute("name").equals("classID")){
                            String classID=((Element) nodeAtt).getAttribute("value");
                            if (classCount.containsKey(classID)){
                                classCount.put(classID,classCount.get(classID)+1);
                            }else{
                                classCount.put(classID,1);
                            }
                        }
                    }
                }
                nodeClass.put(fileName.get(i),classCount);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void writeNodeClass(){
        try{
        BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("Data/Results/clusterNodeClass.txt "))));
            for (int i = 0; i <fileName.size() ; i++) {
                List<Map.Entry<String,Integer>>clusterNodeClass=new ArrayList<>(nodeClass.get(fileName.get(i)).entrySet());
                Collections.sort(clusterNodeClass,new SortByValue());
                bufferedWriter.write(fileName.get(i)+"\n");
                for (int j = 0; j <clusterNodeClass.size() ; j++) {
                  bufferedWriter.write(clusterNodeClass.get(j).getKey()+"\t"+clusterNodeClass.get(j).getValue());
                }
                bufferedWriter.write("\n");
            }
    }catch (Exception e){
            e.printStackTrace();
        }
    }
    class SortByValue implements Comparator<Map.Entry<String,Integer>>{
        @Override
        public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
            return o2.getValue().compareTo(o1.getValue());
        }
    }
}
