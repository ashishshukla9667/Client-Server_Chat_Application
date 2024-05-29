import java.io.*;
import java.net.*;

public class Server {

    ServerSocket serverSocket;
    Socket socket;

    //VARIABLES FOR READING AND WRITING
    BufferedReader bufferedReader;     // Reading Purpose
    PrintWriter printWriter;          //Writing Purpose
    //CONSTRUCTOR
    Server(){
        try {
            serverSocket =  new ServerSocket(7777);
            System.out.println("SERVER IS READY TO LISTEN INCOMING CLIENT CONNECTION....");
            System.out.println("SERVER IS WAITING");

            //TO ACCEPT THE CONNECTION
            socket=serverSocket.accept();

            bufferedReader =  new BufferedReader(new InputStreamReader(socket.getInputStream())); // data comes in byte form and get converted to character with the help of 'getInputStream()' method
            printWriter =  new PrintWriter(socket.getOutputStream());// now data send to Client from server

            startReading(); // it started reading the data
            startWriting(); // it start writing the data
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    // WE HAVE TO DO BOTH  startReading() and startWriting() work together simultaneously
    public void startWriting() {

        //thread -  keep reading data
        Runnable task1 = () -> {
            System.out.println("WRITING IS STARTED");

            try {
                while (!socket.isClosed()) {
                    String messageFromClient = bufferedReader.readLine();
                    if (messageFromClient.equals("exit")) {
                        System.out.println("Client has stop messaging you");
                        socket.close();
                        break;
                    }
                    System.out.println("Client: " + messageFromClient);
                }
            }
             catch(SocketTimeoutException e)
             {
                 System.out.println("CONNECTION IS CLOSED");
             }
            catch (Exception e){
                System.out.println("CONNECTION IS CLOSED");
            }
        };
        new Thread(task1).start();
    }

    public void startReading() {

        //thread -  server takes data and send it to Client
        Runnable task2 = () ->{
            System.out.println("READING IS DOING FINE");
            try
            {
                while(!socket.isClosed())
                {
                    BufferedReader bufferedReader_OnServer = new BufferedReader(new InputStreamReader(System.in)); // to read the content and take input from console

                    String content = bufferedReader_OnServer.readLine();
                    printWriter.println(content); // enter message and send to client
                    printWriter.flush();

                    if(content.equals("exit")){
                        socket.close();
                        break;
                    }
                }
            }
            catch (Exception e)
            {
                System.out.println("CONNECTION IS CLOSED");
            }
        };
        new Thread(task2).start();
    }

    public static void main(String[] args) {

        System.out.println("SERVER IS RUNNING");
        new Server();
    }
}
