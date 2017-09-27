package ccpe001.familywallet.transaction;

/**
 * Created by Knight on 9/22/2017.
 */

/* Class to make an object for group in firebase */
public class GroupDetails {

    public String userID;
    public String familyID;
    public String firstName;

    public GroupDetails(){

    }

    public GroupDetails(String uid, String fn){
        this.userID=uid;
        this.firstName=fn;
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

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }


}
