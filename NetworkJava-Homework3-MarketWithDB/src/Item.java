import java.io.Serializable;

public class Item implements Serializable{

    private String item_name;
    private String itemid;
    private String name;
    private float price;

    public String getItem_name() {
        return item_name;
    }

    public float getPrice() {
        return price;
    }

    public String getItemid() {
        return itemid;
    }

    public String getName() {
        return name;
    }

    public Item(String name, String item_name, String item_id, float price) {
        this.itemid = item_id;
        this.name = name;
        this.item_name = item_name;
        this.price = price;
    }

    public Object[] toObjectArray(){
        return new Object[]{itemid, item_name, price};
    }
}
