package ccpe001.familywallet.budget;

/**
 * Created by Gigum on 2017-09-05.
 */

public class dumpData {
    public boolean checks;
    public boolean checkAddname;
    public dumpData(){
    }
    public dumpData(boolean s,boolean ss) {

        this.checks=s;
        this.checkAddname=ss;

    }
    public void setCheck(boolean ss) {
        this.checks = ss;
    }
    public boolean getCheck(){return checks;}
    public void setCheckName(boolean ss) {
        this.checkAddname = ss;
    }
    public boolean getCheckName(){return checkAddname;}
}
