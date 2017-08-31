package ccpe001.familywallet.budget;

/**
 * Created by Gigum on 2017-08-29.
 */

public class bdatastore {
    public String bName;
    public String strtDate;
    public String endDays;
    public String tAmount;
    public String cat;
    public String notify;
    public String familyId;
    public String user;
    public  String addD;
    public String lastUpdated;
    public String status;
    public  bdatastore(){}

    public  bdatastore(String uname,String Fname,String bname,String strDate,String endDate,String amount,String tnotify,String catogary,String adddate,String lastUpdateds,String stat){
        this.user=uname;
        this.familyId=Fname;
        this.bName=bname;
        this.strtDate=strDate;
        this.endDays=endDate;
        this.tAmount=amount;
        this.notify=tnotify;
        this.cat=catogary;
        this.addD=adddate;
        this.lastUpdated=lastUpdateds;
        this.status=stat;

    }
}
