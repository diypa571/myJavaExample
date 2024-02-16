import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

@SuppressWarnings("serial")
public class TrafficMonitor extends JFrame {

    private JTextArea textArea;
    private JTextField portField;
    private JButton startButton;
    private ServerSocket serverSocket;

    public TrafficMonitor() {
        super("Kommande Request Övervakning");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 800);
        initComponents();
    }

    private void initComponents() {
        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        portField = new JTextField(20);
        startButton = new JButton("Övervaka");

        JPanel panel = new JPanel();
        panel.add(new JLabel("Port:"));
        panel.add(portField);
        panel.add(startButton);

        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(panel, BorderLayout.SOUTH);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int port = Integer.parseInt(portField.getText());
                startMonitoring(port);
            }
        });
    }

    private void startMonitoring(int port) {
        new Thread(() -> {
            try {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close();
                }
                serverSocket = new ServerSocket();
                serverSocket.setReuseAddress(true);
                serverSocket.bind(new InetSocketAddress(port));
                appendText("Data monitoring for port " + port);

                while (!serverSocket.isClosed()) {
                    Socket clientSocket = serverSocket.accept();
                    appendText("Incoming request from: " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());

                    handleClient(clientSocket);
                }
            } catch (IOException e) {
                appendText("Error: " + e.getMessage());
            }
        }).start();
    }

    private void handleClient(Socket clientSocket) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String inputLine;
            StringBuilder request = new StringBuilder();

            while ((inputLine = in.readLine()) != null && !inputLine.isEmpty()) {
                request.append(inputLine).append("\n");
            }
            appendText("Request captured:\n" + request);

            clientSocket.close();
        } catch (IOException e) {
            appendText("Error: " + e.getMessage());
        }
    }

    private void appendText(String text) {
        SwingUtilities.invokeLater(() -> {
            textArea.append(text + "\n");
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TrafficMonitor frame = new TrafficMonitor();
            frame.setVisible(true);
        });
    }
}
