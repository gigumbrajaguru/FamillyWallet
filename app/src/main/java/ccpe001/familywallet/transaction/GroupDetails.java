package ccpe001.familywallet.transaction;

import com.google.android.gms.internal.pi;

/**
 * Created by Knight on 9/22/2017.
 */

/* Class to make an object for group in firebase */
public class GroupDetails {

    private String userID;
    private String familyID;
    private String firstName;
    private String proPic;
    private String inGroup;
    private Double TotalIncome;
    private Double TotalExpense;

    public GroupDetails(){

    }

    public GroupDetails(String uid, String fn, String pic, String inGrp){
        this.userID=uid;
        this.firstName=fn;
        this.proPic= pic;
        this.inGroup=inGrp;
        this.TotalIncome=null;
        this.TotalExpense=null;
    }

    public String getUserID() {
        return userID;
    }

    public String getFamilyID() {
        return familyID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setFamilyID(String familyID) {
        this.familyID = familyID;
    }

    public void setProPic(String proPic) {
        this.proPic = proPic;
    }

    public void setInGroup(String inGroup) {
        this.inGroup = inGroup;
    }


    public String getInGroup() {
        return inGroup;
    }

    public Double getTotalIncome() {
        return TotalIncome;
    }

    public Double getTotalExpense() {
        return TotalExpense;
    }

    public String getProPic() {
        return proPic;

    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }


}
