import java.awt.event.ActionListener;
import java.util.*;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Timer;

public class ClientForm extends JFrame{
    private int name;
    private String ip_addr;
    private int port;
    private String waiting = "Connect to another player or wait for a connection.";
    private final static String newline = "\n";

    private JPanel main_panel;
    private JLabel status_label, player_label;
    private Timer timer;
    private JTextArea connected;
    private JTextArea log;
    private JMenuItem connect, disconnect;
    private JButton rock_button, scissors_button, paper_button;


    public ClientForm(int name, String ip_addr, int port) {
        this.name = name;
        this.ip_addr = ip_addr;
        this.port = port;

        this.init_components();
    }

    private void init_components() {
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setTitle("Virtual Marketplace - Client 0.1a");

        this.setLayout(new BorderLayout());

        this.create_menu();
        this.create_panel();
        this.create_status_bar();

        this.pack();
        this.setLocationRelativeTo(null); // center the window
    }

    public synchronized void gamepanel_active(boolean state) {
        this.rock_button.setEnabled(state);
        this.scissors_button.setEnabled(state);
        this.paper_button.setEnabled(state);
    }

    public synchronized void clear_player_list() {
        this.connected.setText("");
    }

    public synchronized void append_to_log(String msg) { this.log.append(msg + "\n"); }

    private void create_status_bar() {
        JPanel status_panel = new JPanel();
        status_panel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        status_panel.setPreferredSize(new Dimension(this.getWidth(), 16));
        status_panel.setLayout(new BoxLayout(status_panel, BoxLayout.X_AXIS));
        status_label = new JLabel(this.waiting);
        status_label.setHorizontalAlignment(SwingConstants.LEFT);
        status_panel.add(status_label);

        this.add(status_panel, BorderLayout.SOUTH);
    }

    private void create_panel() {
        main_panel = new JPanel();
        main_panel.setLayout(new GridBagLayout());
        main_panel.setPreferredSize(new Dimension(400, 300));

        GridBagConstraints c = new GridBagConstraints();

        // ROCK button
        rock_button = new JButton("Rock");
        rock_button.addActionListener(new ButtonClickListener());
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        main_panel.add(rock_button, c);

        // SCISSORS button
        scissors_button = new JButton("Scissors");
        scissors_button.addActionListener(new ButtonClickListener());
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 1;
        c.gridy = 0;
        main_panel.add(scissors_button, c);

        // PAPER button
        paper_button = new JButton("Paper");
        paper_button.addActionListener(new ButtonClickListener());
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 2;
        c.gridy = 0;
        main_panel.add(paper_button, c);

        // LABELS
        JLabel log_label = new JLabel("Log:");
        c.gridx = 0;
        c.gridy = 1;
        main_panel.add(log_label, c);

        player_label = new JLabel("Players:");
        c.gridx = 2;
        c.gridy = 1;
        main_panel.add(player_label, c);

        // LOG
        log = new JTextArea();
        log.setEditable(false);
        DefaultCaret caret = (DefaultCaret)log.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane scroll = new JScrollPane(log);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.PAGE_END;
        c.gridx = 0;
        c.gridwidth = 2;
        c.gridy = 2;
        main_panel.add(scroll, c);

        // PLAYERS CONNECTED
        connected = new JTextArea();
        connected.setEditable(false);
        JScrollPane conn_scroll = new JScrollPane(connected);
        conn_scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.PAGE_END;
        c.gridx = 2;
        c.gridwidth = 2;
        c.gridy = 2;
        main_panel.add(conn_scroll, c);

        this.add(main_panel, BorderLayout.CENTER);
    }

    private void create_menu() {
        JMenuBar mb = new JMenuBar();
        JMenu menu = new JMenu("Actions");

        connect = new JMenuItem("Connect");
        connect.addActionListener(new ConnectAction());
        menu.add(connect);

        disconnect = new JMenuItem("Disconnect");
        disconnect.addActionListener(new DisconnectAction());
        menu.add(disconnect);

        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(new ExitAction());
        menu.add(exit);

        mb.add(menu);
        this.setJMenuBar(mb);
    }

    private class ButtonClickListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String choice = e.getActionCommand();

        }
    }

    private class ConnectAction extends AbstractAction {
        public ConnectAction() {
            super();
        }

        @Override
        public void actionPerformed(ActionEvent e) {

        }
    }

    private class DisconnectAction extends AbstractAction {
        public DisconnectAction() {
            super();
        }

        @Override
        public void actionPerformed(ActionEvent e) {

        }
    }

    private class ExitAction extends AbstractAction {
        public ExitAction() {
            super();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }
}
