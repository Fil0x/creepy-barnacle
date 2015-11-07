import java.util.ArrayList;

public class BroadcasterMediator {

    private final ClientForm client_ui;
    private final Server server;

    public BroadcasterMediator(ClientForm client_ui, Server server){
        this.client_ui = client_ui;
        this.server = server;
    }

    public void connect(String dest_ip_addr, String dest_port, int name, String ip_addr, int port) {

        if(dest_ip_addr.equals(ip_addr) && dest_port.equals(Integer.toString(port)) ) {
            System.out.println("error: trying to connect to ourselves"); //TODO del
            client_ui.append_to_log("(WARN) Can't connect to yourself.");
            return;
        }
        if(Server.players.size()!=1){
            System.out.println("error: already connected to a game, first disconnect"); //TODO del
            client_ui.append_to_log("(WARN) Already connected to a game.");
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
                server.clearRound();
                Server.players.add(new Player(p.getName(), p.getIp_address(), p.getPort()));
                client_ui.set_disconnected();
                client_ui.update_player_label(1);
                return;
            }
        }
        throw new IllegalArgumentException("Name not found");
    }

    public void new_view(ArrayList<String> destinations, String message) {
        Broadcaster b = new Broadcaster(destinations, message, this.client_ui);
        (new Thread(b)).start();
    }

    public void send_move(String name, String choice){
        ArrayList<String> destinations = new ArrayList<>();
        for (Player p : Server.players) {
            if(!(p.getName().equals(name))) {
                // Destinations
                String player_data = p.getIp_address().getHostAddress() + " " + p.getPort();
                destinations.add(player_data);
            }
        }
        String msg = "choice," + name + "," + choice;
        client_ui.game_panel_active(false);
        server.addMove(name, choice);
        server.finalizeRound();

        Broadcaster b = new Broadcaster(destinations, msg, this.client_ui);
        (new Thread(b)).start();
    }
}
