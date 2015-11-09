import java.util.Random;

public class Utilities {
    public static String generateItemId() {
        Random r = new Random();
        int id = r.nextInt(90000)+10000;
        return "id" + id;
    }
}
