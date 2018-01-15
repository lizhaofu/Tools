
import Config.PathConfig;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by dujunfei on 2017/6/2.
 */
public class Test {
    public static void main(String[] args) throws IOException, JSONException {
        Papers papers = new Papers();
        papers.initialize();
  //      buildXMLDoc(papers.getCitedPaperIndex(), papers.getAllCitedIndex(), papers.getPapersJson());//分类数据
    }

    public static void buildXMLDoc(Map<Integer, List<Integer>> citedPaperIndex, List<Integer> allCitedIndex, List<JSONObject> paperJson) throws IOException, JSONException {
        Element root = new Element("graph").setAttribute("directed", "0");
        Document document = new Document(root);
        List<String> attribute;
        if (paperJson.get(0).has("classID")) {
            attribute = new ArrayList<>(Arrays.asList("classID", "TI", "SO", "PY", "Z9"));
        } else {
            attribute = new ArrayList<>(Arrays.asList("TI", "SO", "PY", "Z9"));
        }

        for (int i = 0; i < allCitedIndex.size(); i++) {
            Element elements = new Element("node").setAttribute("id", paperJson.get(allCitedIndex.get(i)).getString("ID"));
            Element element = new Element("att").setAttribute("name", "name");
            element.setAttribute("value", Integer.toString(allCitedIndex.get(i)));
            elements.addContent(element);
            for (int j = 0; j < attribute.size(); j++) {
                if (paperJson.get(allCitedIndex.get(i)).has(attribute.get(j))) {
                    Element att = new Element("att").setAttribute("name", attribute.get(j));
                    att.setAttribute("value", paperJson.get(allCitedIndex.get(i)).get(attribute.get(j)).toString().replace("||", " "));
                    elements.addContent(att);
                }
            }
            Element author = new Element("att").setAttribute("name", "AU");
            if (paperJson.get(allCitedIndex.get(i)).has("AU")) {
                String string = paperJson.get(allCitedIndex.get(i)).get("AU").toString().replace(", ", "-");
                string = string.replace("||", " ");
                author.setAttribute("value", string);
            }
            elements.addContent(author);
            root.addContent(elements);

        }
        for (Integer index1 : citedPaperIndex.keySet()) {
            for (Integer index2 : citedPaperIndex.get(index1)) {
                Element elements = new Element("edge").setAttribute("source", paperJson.get(index1).getString("ID"));
                elements.setAttribute("target", paperJson.get(index2).getString("ID"));
                root.addContent(elements);
                elements.addContent("");

            }
        }
        Format format = Format.getPrettyFormat();
        XMLOutputter XMLOut = new XMLOutputter(format);
        XMLOut.output(document, new FileOutputStream(PathConfig.refRelationship));
    }
}
