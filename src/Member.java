import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Member {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    // create a construct used to instantiate every property
    public  Member(Socket socket,String username){
        try{
            this.socket= socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
        }catch (IOException e){
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }

    // method used to send messages to our member handler
    public void sendMessage(){
        try{
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);
            while(socket.isConnected()){
                String messageToSend = scanner.nextLine();
                bufferedWriter.write(username + ": "+ messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();

            }

        }catch (IOException e){
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }

    // method for listening for msgs from the server
    public void listeningForMessage(){

        //create a new thread and pass a runnable obj
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromGroupChat;
                try{
                while (socket.isConnected()){
                    msgFromGroupChat = bufferedReader.readLine();
                    System.out.println(msgFromGroupChat);
                }

                }catch (IOException e){
                    closeEverything(socket,bufferedReader,bufferedWriter);
                }

            }
        }).start();

    }

    // create a closeEverything method
    public void closeEverything(Socket socket,BufferedReader bufferedReader,BufferedWriter bufferedWriter){
        try{
            //make sure everything isn't equal to null
            if(bufferedReader != null){
                bufferedReader.close();
            }
            if(bufferedWriter != null){
                bufferedWriter.close();
            }
            if(socket != null){
                socket.close();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    // create a min method to run everything
    public static void main(String[] args) throws IOException {

        //create another scanner obj
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username for the group chat");
        String username = scanner.nextLine();
        Socket socket = new Socket("localhost",1234);
        Member member = new Member(socket,username);
        member.listeningForMessage();
        member.sendMessage();






    }

}
