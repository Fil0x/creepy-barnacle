import java.util.ArrayList;

public class BroadcasterMediator {

    private final ClientForm client_ui;

    public BroadcasterMediator(ClientForm client_ui){
        this.client_ui = client_ui;
    }

    public void connect(String dest_ip_addr, String dest_port, int name, String ip_addr, int port) {

        if(dest_ip_addr.equals(ip_addr) && dest_port.equals(Integer.toString(port)) ) {
            System.out.println("error: trying to connect to ourselves"); //TODO del
            client_ui.append_to_log("error: trying to connect to ourselves");
            return;
        }
        if(Server.players.size()!=1){
            System.out.println("error: already connected to a game, first disconnect"); //TODO del
            client_ui.append_to_log("error: already connected to a game, first disconnect");
            client_ui.set_connected();
            return;
        }
        String msg = "connect," + name + "," + ip_addr + "," + port;
        ArrayList<String> dest = new ArrayList<>();
        dest.add(dest_ip_addr + " " + dest_port);
        Broadcaster b = new Broadcaster(dest, msg, this.client_ui);
        (new Thread(b)).start();
        client_ui.set_connected();
    }

    public void disconnect(String name) {
        for(Player p: Server.players) {
            if(p.getName().equals(name)) {
                Server.players.remove(p);
                // Broadcast it
                ArrayList<String> destinations = new ArrayList<>();
                String msg = "new_view";
                for(Player pp: Server.players) {
                    // Message
                    msg = msg.concat("," + pp.toString());
                    // Destinations
                    String player_data = pp.getIp_address().getHostAddress() + " " + pp.getPort();
                    destinations.add(player_data);
                }
                client_ui.clear_player_list();

                Broadcaster b = new Broadcaster(destinations, msg, this.client_ui);
                (new Thread(b)).start();
                Server.players.clear();
                Server.players.add(new Player(p.getName(), p.getIp_address(), p.getPort()));
                client_ui.set_disconnected();
                return;
            }
        }
        throw new IllegalArgumentException("Name not found");
    }

    public void new_view(ArrayList<String> destinations, String message) {
        Broadcaster b = new Broadcaster(destinations, message, this.client_ui);
        (new Thread(b)).start();
    }
}
