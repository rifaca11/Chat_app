import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    //proprieties
    private ServerSocket serverSocket;

    //construct

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer(){
        try{
            //while the server socket is open
            while(!serverSocket.isClosed()){
                //waiting for member to connect
                Socket socket = serverSocket.accept();
                System.out.println("A new member has connected !");

                //create object class and give it a constructor that accepts the
                //socket object that was created from our accept method
                MemberHandler memberHandler = new MemberHandler(socket);

                //create a thread to connect and we pass in our instance of our
                // class member handler that will implement runnable
                Thread thread = new Thread(memberHandler);

                //use the start method to begin the execution of this thread
                thread.start();

            }

        }catch (IOException e){
            e.printStackTrace();
        }
    }
    //create a method for handling the error so that we can avoid nested try catches
    //(shut down a server socket hahah )
    public void closeServerSocket(){
        //make a server socket not null cuz if it's that we get a null pointer exception
        //if we called the close method
        try{
            if(serverSocket != null){
                serverSocket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
