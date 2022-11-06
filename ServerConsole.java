import java.util.Scanner;

import common.ChatIF;

public class ServerConsole implements ChatIF {
	
	Scanner fromConsole;
	
	EchoServer server;
	
	final public static int DEFAULT_PORT = 5555;
	
	public ServerConsole(int port) {
		
		//create server object
		 server = new EchoServer(port, this);
	    
	    try 
	    {
	      server.listen(); //Start listening for connections
	    } 
	    catch (Exception ex) 
	    {
	      System.out.println("ERROR - Could not listen for clients!");

	    
	    }
	    
	    fromConsole = new Scanner(System.in);
	}
	

	@Override
	public void display(String message) {
		// TODO Auto-generated method stub
		System.out.println(message);
	}
	
	
	//accepts inputs from the server admin and sends them to EchoServer
	public void accept() 
	  {
	    try
	    {

	      String message;

	      while (true) 
	      {
	        message = fromConsole.nextLine();
	        server.handleMessageFromServerUI(message);
	        
	      }
	    } 
	    catch (Exception ex) 
	    {
	      System.out.println
	        ("Unexpected error while reading from console!");
	    }
	  }
	
	
	public static void main(String[] args) 
	  {
	    int port = 0; //Port to listen on
	    
	    //set the port number to the number passed as argument if not set it to the default port
	    try
	    {
	      port = Integer.parseInt(args[0]); //Get port from command line
	    }
	    catch(Throwable t)
	    {
	      port = DEFAULT_PORT; //Set port to 5555
	    }
	    
	    //create new Server console object
	    ServerConsole console = new ServerConsole(port);
	    console.accept();  //Wait for console data
		
	    
	  }

}
