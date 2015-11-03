//import javax.swing.*;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class ClientForm extends JFrame{

    String ip_addr;
    int port;

    JPanel main_panel;
    JLabel status_label;

    public ClientForm(String ip_addr, int port) {
        this.ip_addr = ip_addr;
        this.port = port;

        this.init_components();
    }

    private void init_components() {
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setTitle("RPS Client");

        this.setLayout(new BorderLayout());

        this.create_menu();
        this.create_panel();
        this.create_status_bar();

        this.pack();
    }

    private void create_status_bar() {
        JPanel status_panel = new JPanel();
        status_panel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        status_panel.setPreferredSize(new Dimension(this.getWidth(), 16));
        status_panel.setLayout(new BoxLayout(status_panel, BoxLayout.X_AXIS));
        status_label = new JLabel("Status bar");
        status_label.setHorizontalAlignment(SwingConstants.LEFT);
        status_panel.add(status_label);

        this.add(status_panel, BorderLayout.SOUTH);
    }

    private void create_panel() {
        main_panel = new JPanel();
        main_panel.setLayout(new GridBagLayout());
        main_panel.setPreferredSize(new Dimension(300, 300));

        GridBagConstraints c = new GridBagConstraints();

        // ROCK button
        JButton rock_button = new JButton("Rock");
        rock_button.setName("rock");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        main_panel.add(rock_button, c);

        // SCISSORS button
        JButton scissors_button = new JButton("Scissors");
        rock_button.setName("scissors");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 1;
        c.gridy = 0;
        main_panel.add(scissors_button, c);

        // PAPER button
        JButton paper_button = new JButton("Paper");
        rock_button.setName("paper");
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

        JLabel player_label = new JLabel("Players(X):");
        c.gridx = 2;
        c.gridy = 1;
        main_panel.add(player_label, c);

        // LOG
        JTextArea log = new JTextArea();
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
        JTextArea connected = new JTextArea();
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

        JMenuItem connect = new JMenuItem("Connect");
        connect.addActionListener(new ConnectAction());
        menu.add(connect);

        JMenuItem disconnect = new JMenuItem("Disconnect");
        disconnect.addActionListener(new DisconnectAction());
        menu.add(disconnect);

        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(new ExitAction());
        menu.add(exit);

        mb.add(menu);
        this.setJMenuBar(mb);
    }

    private class ConnectAction extends AbstractAction {
        public ConnectAction() {
            super();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Connect");
        }
    }

    private class DisconnectAction extends AbstractAction {
        public DisconnectAction() {
            super();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Disconnect");
        }
    }

    private class ExitAction extends AbstractAction {
        public ExitAction() {
            super();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Exit");
        }
    }
}
