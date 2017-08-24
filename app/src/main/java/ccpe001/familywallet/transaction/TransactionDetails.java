package ccpe001.familywallet.transaction;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Knight on 7/9/2017.
 */

public class TransactionDetails {

    public String userID;
    public String familyID;
    public String amount;
    public String title;
    public String categoryName;
    public String date;
    public Integer categoryID;
    public String time;
    public String account;
    public String location;
    public String type;
    public String currency;
    public String recurringPeriod;




    public TransactionDetails() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public TransactionDetails(String userID, String amount, String title, String categoryName, String date, Integer categoryID, String time, String account, String location, String type, String currency, String familyID) {
        this.userID = userID;
        this.familyID = familyID;
        this.amount = amount;
        this.title = title;
        this.categoryName = categoryName;
        this.date = date;
        this.categoryID = categoryID;
        this.time = time;
        this.account = account;
        this.location = location;
        this.type = type;
        this.currency = currency;
    }

    public TransactionDetails(String userID, String amount, String title, String categoryName, String date, Integer categoryID, String time, String account, String location, String type, String currency, String familyID, String recurringPeriod) {
        this.userID = userID;
        this.familyID = familyID;
        this.amount = amount;
        this.title = title;
        this.categoryName = categoryName;
        this.date = date;
        this.categoryID = categoryID;
        this.time = time;
        this.account = account;
        this.location = location;
        this.type = type;
        this.currency = currency;
        this.recurringPeriod = recurringPeriod;
    }



    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userID", userID);
        result.put("familyID", familyID);
        result.put("amount", amount);
        result.put("title", title);
        result.put("categoryName", categoryName);
        result.put("categoryID", categoryID);
        result.put("date", date);
        result.put("time", time);
        result.put("account", account);
        result.put("location", location);
        result.put("type", type);
        result.put("currency", currency);
        result.put("recurringPeriod", recurringPeriod);

        return result;
    }

    public String getUserID() {
        return userID;
    }

    public String getRecurringPeriod() {
        return recurringPeriod;
    }

    public String getFamilyID() {
        return familyID;
    }

    public String getAmount() {
        return amount;
    }

    public String getTitle() {
        return title;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getDate() {
        return date;
    }

    public Integer getCategoryID() {
        return categoryID;
    }

    public String getTime() {
        return time;
    }

    public String getAccount() {
        return account;
    }


    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setFamilyID(String familyID) {
        this.familyID = familyID;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setCategoryID(Integer categoryID) {
        this.categoryID = categoryID;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getLocation() {
        return location;
    }

    public String getType() {
        return type;
    }

    public String getCurrency() {
        return currency;
    }
}
