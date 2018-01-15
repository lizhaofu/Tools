import java.util.*;

/**
 * Created by tsinghua on 2017/7/12.
 */
public class czwtest {
    czwtest(){}

    public static void main(String[] args) {
        List<Map.Entry<String, Integer>> infoIds;///
        List<Map.Entry<String, Integer>> infoIds1;///
        Map<String,Integer> map = new HashMap<String,Integer>();
        map.put("1",1);
        map.put("2",2);
        map.put("3",3);
        map.put("4",4);
        infoIds = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());

        infoIds1 = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
        infoIds = infoIds1;
        System.out.println(infoIds.toString());



    }
}
