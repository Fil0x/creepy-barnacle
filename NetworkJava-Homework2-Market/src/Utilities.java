import java.util.Random;

public class Utilities {
    public static String generateItemId() {
        Random r = new Random();
        int id = r.nextInt(10000);
        return "id" + id;
    }
}
