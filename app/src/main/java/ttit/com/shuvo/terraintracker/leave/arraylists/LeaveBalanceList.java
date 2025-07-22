package ttit.com.shuvo.terraintracker.leave.arraylists;

public class LeaveBalanceList {
    private String category;
    private String code;
    private String opening_qty;
    private String current_qty;
    private String consumed;
    private String transferred;
    private String cash_taken;
    private String balance_qty;

    public LeaveBalanceList(String category, String code, String opening_qty, String current_qty, String consumed, String transferred, String cash_taken, String balance_qty) {
        this.category = category;
        this.code = code;
        this.opening_qty = opening_qty;
        this.current_qty = current_qty;
        this.consumed = consumed;
        this.transferred = transferred;
        this.cash_taken = cash_taken;
        this.balance_qty = balance_qty;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getOpening_qty() {
        return opening_qty;
    }

    public void setOpening_qty(String opening_qty) {
        this.opening_qty = opening_qty;
    }

    public String getCurrent_qty() {
        return current_qty;
    }

    public void setCurrent_qty(String current_qty) {
        this.current_qty = current_qty;
    }

    public String getConsumed() {
        return consumed;
    }

    public void setConsumed(String consumed) {
        this.consumed = consumed;
    }

    public String getTransferred() {
        return transferred;
    }

    public void setTransferred(String transferred) {
        this.transferred = transferred;
    }

    public String getCash_taken() {
        return cash_taken;
    }

    public void setCash_taken(String cash_taken) {
        this.cash_taken = cash_taken;
    }

    public String getBalance_qty() {
        return balance_qty;
    }

    public void setBalance_qty(String balance_qty) {
        this.balance_qty = balance_qty;
    }
}

