package ccpe001.familywallet.transaction;

import com.google.android.gms.internal.pi;

/**
 * Created by Knight on 9/22/2017.
 */

/* Class to make an object for group in firebase */
public class GroupDetails {

    public String userID;
    public String familyID;
    public String firstName;
    public String proPic;

    public GroupDetails(){

    }

    public GroupDetails(String uid, String fn, String pic){
        this.userID=uid;
        this.firstName=fn;
        this.proPic= pic;
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

    public String getProPic() {
        return proPic;

    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }


}
