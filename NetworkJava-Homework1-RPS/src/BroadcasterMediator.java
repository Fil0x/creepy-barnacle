import java.lang.reflect.Array;
import java.util.ArrayList;

public class BroadcasterMediator {
    public static void connect(String dest_ip_addr, String dest_port, int name, String ip_addr, int port) {
        String msg = "connect," + name + "," + ip_addr + "," + port;
        ArrayList<String> dest = new ArrayList<String>() {{
            add(dest_ip_addr + " " + dest_port);
        }};
        Broadcaster b = new Broadcaster(dest, msg);
        (new Thread(b)).start();
    }

    public static void disconnect(String name, ClientForm cf) {
        for(Player p: Server.players) {
            if(p.getName().equals(name)) {
                Server.players.remove(p);
                // Broadcast it
                ArrayList<String> destinations = new ArrayList<>();
                StringBuffer msg = new StringBuffer("new_view");
                for(Player pp: Server.players) {
                    // Message
                    msg.append("," + pp.toString());
                    // Destinations
                    String player_data = pp.getIp_address().getHostAddress() + " " + pp.getPort();
                    destinations.add(player_data);
                }

                cf.clear_player_list();
                Broadcaster b = new Broadcaster(destinations, msg.toString());
                (new Thread(b)).start();

                return;
            }
        }
        // Exception
    }

    public static void new_view(ArrayList<String> destinations, String message) {
        Broadcaster b = new Broadcaster(destinations, message);
        (new Thread(b)).start();
    }
}
