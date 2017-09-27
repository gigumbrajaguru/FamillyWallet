package ccpe001.familywallet.budget;

/**
 * Created by Gigum on 2017-08-29.
 */

/** This class use to store budget data**/
public class bdatastore {
    public String BudgetName;
    public String startDate;
    public String endDays;
    public String Amount;
    public String catagory;
    public String notification;
    public String familyId;
    public String user;
    public  String addD;
    public String lastUpdated;
    public String percentage;
    public String status;
    public  bdatastore(){}

    public  bdatastore(String uname,String Fname,String bname,String strDate,String endDate,String amount,String tnotify,String catogary,String adddate,String lastUpdateds,String stat){
        this.user=uname;
        this.familyId=Fname;
        this.BudgetName=bname;
        this.startDate=strDate;
        this.endDays=endDate;
        this.Amount=amount;
        this.notification=tnotify;
        this.catagory=catogary;
        this.addD=adddate;
        this.lastUpdated=lastUpdateds;
        this.status=stat;
        this.percentage="0";
    }
}
