import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.*;

public class Client extends JFrame {

    Socket socket;

    private JLabel heading = new JLabel("Client");
    private JTextArea messageArea = new JTextArea();
    private JTextField messageInput = new JTextField();
    private Font font = new Font("Roboto",Font.PLAIN,20);

    //VARIABLES FOR READING AND WRITING
    BufferedReader bufferedReader;     // Reading Purpose
    PrintWriter printWriter;

    Client(){

        try{
            System.out.print("SENDING REQUEST TO SERVER");
            socket = new Socket("192.168.1.6",7777);
            System.out.println("CLIENT AND SERVER IS CONNECTED");

            bufferedReader =  new BufferedReader(new InputStreamReader(socket.getInputStream())); // data comes in byte form and get converted to character with the help of 'getInputStream()' method
            printWriter =  new PrintWriter(socket.getOutputStream());// now data send to Client from server

            createGUI();
            hangleEvents();

            startReading(); // it started reading the data
            startWriting(); // it starts writing the data

        }
        catch(SocketTimeoutException e){
            System.out.println();
            System.out.println("Connection timed out ");
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private void hangleEvents() {

        messageInput.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

                if(e.getKeyCode()==10){
                    String contentToSend = messageInput.getText();
                    if (contentToSend.equals("exit")) {
                        System.out.println("Server has stop messaging you");
                        messageInput.setEnabled(false);


                    }
                    messageArea.append("Me: "+contentToSend+"\n");
                    printWriter.println(contentToSend);
                    printWriter.flush();
                    messageInput.setText("");
                    messageInput.requestFocus();

                }

            }
        });
    }

    private void createGUI(){

        this.setTitle("Client");
        this.setSize(500,500);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //CODING FOR COMPONENT
        heading.setFont(font);
        //heading.setIcon(new ImageIcon("chat.png"));
        messageArea.setFont(font);
        messageInput.setFont(font);

        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        messageArea.setEditable(false);

        //FRAME KA LAYOUT SET
        this.setLayout(new BorderLayout());

        //ADDING COMPONENT TO FRAME
        this.add(heading,BorderLayout.NORTH);
        JScrollPane jScrollPane = new JScrollPane(messageArea);
        this.add(jScrollPane,BorderLayout.CENTER);
        this.add(messageInput,BorderLayout.SOUTH);



        this.setVisible(true);
    }



    // WE HAVE TO DO BOTH  startReading() and startWriting() work together simultaneously
    public void startWriting() {

        //thread -  keep reading data
        Runnable task1 = () ->{
            System.out.println("WRITING IS STARTED");
            try {
                while (!socket.isClosed()) {
                    String messageFromServer = bufferedReader.readLine();
                    if (messageFromServer.equals("exit")) {
                        System.out.println("Server has stop messaging you");
                        JOptionPane.showMessageDialog(this,"Server has stop messaging you");
                        messageInput.setEnabled(false);
                        socket.close();
                        break;
                    }
                    //System.out.println("Server: " + messageFromServer);
                    messageArea.append("Server: " + messageFromServer+"\n");
                }
            }
            catch(SocketTimeoutException e){
                System.out.println("CONNECTION IS CLOSED");
            }
            catch(Exception e){
                System.out.println("CONNECTION IS CLOSED");
            }
        };
        new Thread(task1).start();
    }

    public void startReading() {

        //thread -  server takes data and send it to Client
        Runnable task2 = () ->{
            System.out.println("READING IS DOING FINE");
            try {
                while(!socket.isClosed()) {
                    BufferedReader bufferedReader_OnClient = new BufferedReader(new InputStreamReader(System.in)); // to read the content and take input from console

                    String content = bufferedReader_OnClient.readLine();
                    printWriter.println(content); // enter message and send to client
                    printWriter.flush();

                    if(content.equals("exit")){
                        System.out.println("Server has stop messaging you");
                        JOptionPane.showMessageDialog(this,"Server has stop messaging you");
                        messageInput.setEnabled(false);
                        socket.close();
                        break;
                    }
                    messageArea.append("Server: " + content+"\n");
                }
            }
            catch(Exception e){
                System.out.println("CONNECTION IS CLOSED");
                }
        };
        new Thread(task2).start();
    }

    public static void main(String[] args) {
        System.out.println("CLIENT IS RUNNING");
        new Client();
    }

}
