package chatting.application;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.text.*;
import java.net.*;
import java.io.*;

public class Server implements ActionListener {

    JTextField text;
    static JPanel a1;
    static Box vertical = Box.createVerticalBox();
    static JFrame f = new JFrame();
    static DataOutputStream dout;
    static JLabel status;

    Server() {

        f.setLayout(null);

        // ---------- HEADER PANEL ----------
        JPanel p1 = new JPanel();
        p1.setBackground(new Color(7, 94, 84));
        p1.setBounds(0, 0, 450, 70);
        p1.setLayout(null);
        f.add(p1);

        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icons/3.png"));
        Image i2 = i1.getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT);
        JLabel back = new JLabel(new ImageIcon(i2));
        back.setBounds(5, 20, 25, 25);
        p1.add(back);

        back.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent ae) {
                System.exit(0);
            }
        });

        ImageIcon i4 = new ImageIcon(ClassLoader.getSystemResource("icons/Akaza.jpg"));
        Image i5 = i4.getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT);
        JLabel profile = new JLabel(new ImageIcon(i5));
        profile.setBounds(40, 10, 50, 50);
        p1.add(profile);

        JLabel name = new JLabel("Akaza");
        name.setBounds(110, 15, 100, 18);
        name.setForeground(Color.WHITE);
        name.setFont(new Font("SAN_SERIF", Font.BOLD, 18));
        p1.add(name);

        status = new JLabel("Online");
        status.setBounds(110, 35, 100, 18);
        status.setForeground(Color.WHITE);
        status.setFont(new Font("SAN_SERIF", Font.BOLD, 14));
        p1.add(status);

        // ---------- CHAT PANEL ----------
        a1 = new JPanel();
        a1.setBounds(5, 75, 440, 570);
        f.add(a1);

        text = new JTextField();
        text.setBounds(5, 640, 310, 40);
        text.setFont(new Font("SAN_SERIF", Font.PLAIN, 16));
        f.add(text);

        // ---------- SEND BUTTON ----------
        JButton send = new JButton("Send");
        send.setBounds(320, 635, 123, 40);
        send.setBackground(new Color(7, 94, 84));
        send.setForeground(Color.WHITE);
        send.addActionListener(this);
        send.setFont(new Font("SAN_SERIF", Font.PLAIN, 16));
        f.add(send);

        // ---------- KEY LISTENER FOR TYPING ----------
        text.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent ke) {
                try {
                    if (dout != null) {
                        dout.writeUTF("typing..."); // send typing signal
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        f.setSize(450, 700);
        f.setLocation(200, 50);
        f.setUndecorated(true);
        f.getContentPane().setBackground(Color.WHITE);
        f.setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        try {
            String out = text.getText();

            JPanel p2 = formatLabel(out, true); // true = server message

            a1.setLayout(new BorderLayout());

            JPanel right = new JPanel(new BorderLayout());
            right.setOpaque(false);
            right.add(p2, BorderLayout.LINE_END);
            vertical.add(right);
            vertical.add(Box.createVerticalStrut(15));

            a1.add(vertical, BorderLayout.PAGE_START);

            if (dout != null) {
                dout.writeUTF(out); // send message to client
            }

            text.setText("");
            a1.revalidate();
            a1.repaint();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static JPanel formatLabel(String out, boolean isSender) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false); // transparent panel so chat background shows

        JLabel output = new JLabel("<html><p style=\"width: 150px\">" + out + "</p></html>");
        output.setFont(new Font("Tahoma", Font.PLAIN, 16));
        if (isSender) {
            output.setBackground(new Color(37, 211, 102)); // green
            output.setBorder(new EmptyBorder(15, 15, 15, 50));
        } else {
            output.setBackground(Color.WHITE); // received messages white
            output.setBorder(new EmptyBorder(15, 50, 15, 15));
        }
        output.setOpaque(true);
        output.setForeground(Color.BLACK);

        panel.add(output);

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        JLabel time = new JLabel(sdf.format(cal.getTime()));
        panel.add(time);

        return panel;
    }

    public static void main(String[] args) {
        new Server();

        try {
            ServerSocket skt = new ServerSocket(6001);
            System.out.println("Server started, waiting for client...");

            Socket s = skt.accept(); // accept client
            System.out.println("Client connected!");

            DataInputStream din = new DataInputStream(s.getInputStream());
            dout = new DataOutputStream(s.getOutputStream());

            while (true) {
                String msg = din.readUTF();

                if (msg.equals("typing...")) {
                    status.setText("Typing...");
                    // reset back to online after 2 seconds
                    new javax.swing.Timer(2000, e -> status.setText("Online")).start();
                } else {
                    status.setText("Online");
                    JPanel panel = formatLabel(msg, false); // false = received message
                    JPanel left = new JPanel(new BorderLayout());
                    left.setOpaque(false);
                    left.add(panel, BorderLayout.LINE_START);
                    vertical.add(left);
                    vertical.add(Box.createVerticalStrut(15));
                    a1.add(vertical, BorderLayout.PAGE_START);
                    a1.revalidate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
