
public class Wish {

    private float maxValue;
    private String itemname;

    public Wish(String itemname, float maxValue) {
        this.itemname = itemname;
        this.maxValue = maxValue;
    }

    public boolean isSatisfied(String candidate, float value) {
        return candidate.contains(this.itemname) && this.maxValue >= value;
    }
}
