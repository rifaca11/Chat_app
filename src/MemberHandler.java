import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class MemberHandler implements Runnable {

    // create an arraylist to keep track of all our members so that whenever a member
    //send a msg we can loop through our array list of members and send the message
    //to each client ( Responsible allowing to communicate or send msgs or broadcast
    //a msg to multiple members instead of just one or just the server)
    public static ArrayList<MemberHandler> memberHandlers = new ArrayList<>();

    // add a socket obj to establish a cnx btw the member and a server
    private Socket socket;

    // create a buffered reader used to read data specifically msgs
    private BufferedReader bufferedReader;

    // create a buffered writer used to send data specifically msgs
    private BufferedWriter bufferedWriter;

    // create a username for member
    private String username;

    // make a construct
    public MemberHandler(Socket socket){
        try{
            this.socket = socket;

            //get socket's output
            //in java there are a 2 types of streams : a byte stream(getOutputStream)
            //and a character stream (OutputStreamWriter)
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())) ;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //set username and add a press key
            this.username = bufferedReader.readLine();

            // add a username to the arraylist ( this represent a member handler obj)
            memberHandlers.add(this);
            broadcastMessage("SERVER : " + username + " has entered the chat");
        }catch(IOException e){
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }


    @Override
    //this method override when we implemented the runnable interface
    public void run() {

        //hold a msgs received from a member
        String messageFromMember;

        while (socket.isConnected()){
            try{
                //reading from our buffered reader
                messageFromMember = bufferedReader.readLine();
                broadcastMessage(messageFromMember);

            }catch(IOException e){
                closeEverything(socket,bufferedReader,bufferedWriter);
                break;
            }
        }

    }

    // broadcast method message used to send the message member wrote to evryone
    public void broadcastMessage(String messageToSend){

         //loop through an arraylist and send a msg to each member connected
        for (MemberHandler memberHandler : memberHandlers){
            try{
                //if a current member handler in the arraylist doesn't equal a username
                if(!memberHandler.username.equals((username))){
                        memberHandler.bufferedWriter.write(messageToSend);
                        //send a new line character
                        memberHandler.bufferedWriter.newLine();
                        // flush
                        memberHandler.bufferedWriter.flush();

                }
            }catch (IOException e){
                closeEverything(socket,bufferedReader,bufferedWriter);
            }
        }
    }

    // method to signal that a user has left the chat
    public void removeMemberHandler(){
        // remove the member handler from the arraylist
        memberHandlers.remove(this);
        broadcastMessage("SERVER " + username + "has left the chat");
    }

    // Close everything method
    public void closeEverything(Socket socket,BufferedReader bufferedReader,BufferedWriter bufferedWriter){
        removeMemberHandler();
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
}
