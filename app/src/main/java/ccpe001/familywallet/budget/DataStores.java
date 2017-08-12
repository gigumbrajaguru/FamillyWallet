package ccpe001.familywallet.budget;

/**
 * Created by Gigum on 2017-07-09.
 */

public class DataStores {
    public String user;
    public   String accountName;
    public  Double amount;
    public  String types;
    public  String bankID;
    public  String addDate;
    public  String lastUpdated;
    public  String isPrivate;
    public  String Notify;
    public String curType;
    public  String familyId;
    public DataStores(){}

    public DataStores(String user,String accountName,Double amount,String types,String bankID,String addDate,String lastUpdated,String isPrivate,String Notify,String curTypes,String family){
        this.user=user;
        this.accountName=accountName;
        this.amount=amount;
        this.types=types;
        this.bankID=bankID;
        this.addDate=addDate;
        this.lastUpdated=lastUpdated;
        this.isPrivate=isPrivate;
        this.Notify=Notify;
        this.curType=curTypes;
        this.familyId=family;

    }




}
