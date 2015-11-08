
public class Wish {

    private float maxValue;
    private String itemname;

    public Wish(String itemname, float maxValue) {
        this.itemname = itemname;
        this.maxValue = maxValue;
    }

    public boolean isSatisfied(String candidate, float value) {
        if(this.itemname.contains(candidate) && this.maxValue >= value)
            return true;
        return false;
    }

    public String getItemname() {
        return itemname;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

    public float getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
    }
}
