package ccpe001.familywallet.budget;

/**
 * Created by Gigum on 2017-09-05.
 */


/********* This class use to pass data ****/
public class Datatrasmit {
    public boolean checks;
    public boolean checkAddname;
    public String Amount;
    public String BudgetName;
    public String addD;
    public String catagory;
    public String endDays;
    public String  familyId;
    public String notification;
    public String percentage;
    public String startDate;
    public String status;
    public String user;
    public Datatrasmit(){
    }
    public Datatrasmit(boolean s, boolean ss, String am, String budName, String adDate, String cat, String enDate, String famId, String noty, String percent, String starDate, String stat, String uses) {

        this.checks=s;
        this.checkAddname=ss;
        this.Amount=am;
        this.BudgetName=budName;
        this.addD=adDate;
        this.catagory=cat;
        this.endDays=enDate;
        this.familyId=famId;
        this.notification=noty;
        this.percentage=percent;
        this.startDate=starDate;
        this.status=stat;
        this.user=uses;

    }
    public void setCheck(boolean ss) {
        this.checks = ss;
    }
    public boolean getCheck(){return checks;}
    public void setCheckName(boolean ss) {
        this.checkAddname = ss;
    }
    public boolean getCheckName(){return checkAddname;}

    public void setamount(String am) {
        this.Amount=am;
    }
    public String getamount(){return Amount;}
    public void setBudgetName(String budName) {
        this.BudgetName=budName;
    }
    public String getBudgetName(){return BudgetName;}
    public void setaddD(String adDate) {
        this.addD=adDate;
    }
    public String getaddD(){return addD;}
    public void setcatagory(String cat) {
        this.catagory=cat;
    }
    public String getcatagory(){return catagory;}
    public void setendDays(String enDate) {
        this.endDays=enDate;
    }
    public String getendDays(){return endDays;}
    public void setfamilyId(String famId) {
        this.familyId=famId;
    }
    public String getfamilyId(){return familyId;}
    public void setnotification(String noty) {
        this.notification=noty;
    }
    public String getnotification(){return notification;}
    public void setpercentage(String percent) {
        this.percentage=percent;
    }
    public String getpercentage(){return percentage;}
    public void setstartDate(String starDate) {
        this.startDate=starDate;
    }
    public String getstartDate(){return startDate;}
    public void setstatus(String stat) {
        this.status=stat;
    }
    public String getstatus(){return status;}
    public void setuser(String uses) {
        this.user=uses;
    }
    public String getuser(){return user;}



}
