/**
 * Created by lizhaofu on 2017/5/24.
 */
public class Patent {
    private String patentTitle;
    private String patentAuthor;
    private String patentAbstracts;
    private int patentCitedAmount;
    private int patentYear;
    private String patentNum;
    private String AllContext;
    private String pn;

    public String getPatentNum() {
        return patentNum;
    }

    public void setPatentNum(String patentNum) {
        this.patentNum = patentNum;
    }

    public Patent(){
        patentTitle = "";
        patentAuthor = "";
        patentAbstracts = "";
        AllContext = "";
    }

    public String getPatentTitle() {
        return patentTitle;
    }

    public void setPatentTitle(String patentTitle) {
        this.patentTitle = patentTitle;
    }

    public String getPatentAuthor() {
        return patentAuthor;
    }

    public void setPatentAuthor(String patentAuthor) {
        this.patentAuthor = patentAuthor;
    }

    public String getPatentAbstracts() {
        return patentAbstracts;
    }

    public void setPatentAbstracts(String patentAbstracts) {
        this.patentAbstracts = patentAbstracts;
    }

    public int getPatentCitedAmount() {
        return patentCitedAmount;
    }

    public void setPatentCitedAmount(int patentCitedAmount) {
        this.patentCitedAmount = patentCitedAmount;
    }

    public int getPatentYear() {
        return patentYear;
    }

    public void setPatentYear(int patentYear) {
        this.patentYear = patentYear;
    }

    public String getAllContext() {
        return AllContext;
    }

    public void setAllContext(String allContext) {
        AllContext = allContext;
    }

    public String getPn() {
        return pn;
    }

    public void setPn(String pn) {
        this.pn = pn;
    }
}
