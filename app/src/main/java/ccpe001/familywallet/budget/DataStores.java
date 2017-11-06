package ccpe001.familywallet.budget;

/**
 * Created by Gigum on 2017-07-09.
 */

/***This class used to pass data for account function*****/
public class DataStores {
    public String user;
    public   String accountName;
    public  Double amount;
    public  String types;
    public  String bankID;
    public  String addDate;
    public  String lastUpdated;
    public  String isSaving;
    public String curType;
    public  String familyId;
     String outputname="Other";


    public DataStores(){}

    public DataStores(String user,String accountName,Double amount,String types,String bankID,String addDate,String lastUpdated,String isSaving,String curTypes,String family){
        this.user=user;
        this.accountName=accountName;
        this.amount=amount;
        this.types=types;
        this.bankID=bankID;
        this.addDate=addDate;
        this.lastUpdated=lastUpdated;
        this.isSaving=isSaving;
        this.curType=curTypes;
        this.familyId=family;
    }
    public  String datachange(String cat){
            switch (cat) {
                case "Travel":
                case "ගමන්":
                    outputname = "Travel";
                    break;
                case "Food and Drinks":
                case "කෑම සහ බීම":
                    outputname = "Food & Drinks";
                    break;
                case "Gifts":
                case "තෑගි":
                    outputname = "Gifts";
                    break;
                case "Bill":
                case "බිල්":
                    outputname = "Bill";
                    break;
                case "Entertainment":
                case "විනෝදාස්වාදය":
                    outputname = "Entertainment";
                case "Utilities":
                case "උපයෝගිතා":
                    outputname = "Utilities";
                    break;
                case "Shopping":
                case "සාප්පු සවාරි":
                    outputname = "Shopping";
                    break;
                case "Healthcare":
                case "සෞඛ්ය සත්කාර":
                    outputname = "Healthcare";
                    break;
                case "Clothing":
                case "ඇඳුම්":
                    outputname = "Clothing";
                    break;
                case "Groceries":
                case "සිල්ලර බඩු":
                    outputname = "Groceries";
                    break;
                case "Pets":
                case "සුරතල් සතුන්":
                    outputname = "Pets";
                    break;
                case "Education":
                case "අධ්යාපන":
                    outputname = "Education";
                    break;
                case "Kids":
                case "ළමයි":
                    outputname = "Kids";
                    break;
                case "Loan":
                case "ණය":
                    outputname = "Loan";
                    break;
                case "Business":
                case "ව්යාපාර":
                    outputname = "Business";
                    break;
                case "Other":
                case "වෙනත්":
                    outputname = "Other";
                    break;
                case "Wallet":
                case "ුදල් පසුම්බිය":
                    outputname = "Wallet";
                    break;
                case "Bank account":
                case "බැංකු ගිණුම":
                    outputname = "Bank account";
                    break;
                case "LKR.":
                case "රු.":
                    outputname = "LKR.";
                    break;
                case "USD.":
                case "ඇ. ඩොලර්.":
                    outputname = "USD.";
                    break;
                case "EUR.":
                case "යුරෝ.":
                    outputname = "EUR.";
                    break;
                case "GBP.":
                case "පවුම්.":
                    outputname = "GBP.";
                    break;
                case "INR.":
                case "ඉන්දීය රු.":
                    outputname = "INR.";
                    break;
            }
            return outputname;
    }





}
