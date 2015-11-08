
public class Item {

    private String item_name;
    private String itemid;
    private String name;
    private float price;

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getItemid() {
        return itemid;
    }

    public void setItemid(String itemid) {
        this.itemid = itemid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Item(String name, String item_name, String item_id, float price) {
        this.itemid = item_id;
        this.name = name;
        this.item_name = item_name;
        this.price = price;
    }
}
