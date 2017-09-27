package ccpe001.familywallet.summary;

/**
 * Created by Gihan_Kumarasinghe on 9/14/2017.
 */

public class SummaryDataRead {
    private String TransacAmount;
    private String TransacCategory;

    public SummaryDataRead() {
    }

    public SummaryDataRead(String TrAmount, String TrCat)
    {
        this.TransacAmount=TrAmount;
        this.TransacCategory=TrCat;
    }

    public void setTransacAmount(String TrAmount) {this.TransacAmount=TrAmount;}
    public String getTransacAmount() {return TransacAmount;}
    public void setTransacCategory(String TrCat) {this.TransacCategory=TrCat;}
    public String getTransacCategory() {return TransacCategory;}
}
