// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import java.io.IOException;

import common.ChatIF;
import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  ChatIF serverUI; 
  
  
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port, ChatIF serverConsole) 
  {
    super(port);
    this.serverUI = serverConsole;
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  {
	  String message = (String) msg;
	  serverUI.display("Message received: " + msg + " from " + client.getInfo("loginId"));
	  if (message.startsWith("#login")) {
		  
		  if( client.getInfo("loginId") != null) {
			  
			  try {
				client.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			  
		  } else {
			  String [] splitmsg = message.split(" ");
			  String id = splitmsg[1];
			  client.setInfo("loginId", id);
			  serverUI.display(id + " has logged on");
		  }
		  
		  
		  
		  
	  } else {
		  
		    this.sendToAllClients((String) client.getInfo("loginId") + ": " + msg);
	  }
	  
    
  }
  
  public void handleMessageFromServerUI (String msg)
  {	
	  if(msg.startsWith("#")) {
	    	handleCommand(msg);
	  
	  
	  } else {
		  String message = "SERVER MSG> " + msg;
		  serverUI.display(message);
		  this.sendToAllClients(message);
	  }
	  
  }
  
  // handles commands from the server console that starts with a #
  public void handleCommand(String cmd) {
	  
	  //closes the server normally
	  if (cmd.equals("#quit")) {
		  try {
			serverUI.display("the server is now closing");
			this.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
		  // stop listening for new clients if the server is currently listening
	  } else if (cmd.equals("#stop")) {
		  
		  if (this.isListening()) {
			  this.stopListening();
		  } else {
			  serverUI.display("the server is already stopped");
		  }
		  
		  //serverUI.display("the server will now stop listening for clients");
		  
		  //stop listening for connections if currently listening and disconnect all other clients that are connected 
	  } else if (cmd.equals("#close")) {
	  		
		  
		  
		  try {
			this.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			  
		  
	      serverUI.display("the server will now disconnect connected clients");
	      
	      //sets the port of the server if the server is not currently listening
	  }  else if(cmd.startsWith("#setport")){
		  
		  if (this.isListening() || this.getNumberOfClients() != 0) {
			  serverUI.display("Can not change port number when server is not closed");
		  } else {
			  int portNumber = 0;
			  
			  // get the second argument of the command which is the port
			  String [] splitCmd = cmd.split(" ");
			  try {
				 portNumber = Integer.parseInt(splitCmd[1]);
				 this.setPort(portNumber);
				 serverUI.display("Port is now set to: " + portNumber);
			  }
			  catch(ArrayIndexOutOfBoundsException ai) {
				  serverUI.display("You have not provided a port number");
			  }
			  catch(NumberFormatException ne) {
				  serverUI.display("the port number you have entered is invalid ");
			  }
		  }
		 //starts listening for connections only if the server is not already listening
	  } else if (cmd.equals("#start")) {
	  		
		  if(this.isListening()) {
			  serverUI.display("already listening for clients");
		  } else {
			  try {
					serverUI.display("the server will now start listening for clients");
					
					this.listen();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		  }
		 
		  //returns the port number of the server
	  } else if (cmd.equals("#getport")) {
	  		
		  serverUI.display("" +  this.getPort());
	 //executed when the command entered is invalid
	  } else {
		  serverUI.display("that is not a valid command");
	  }
	  
	  
  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
	  serverUI.display
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
	  serverUI.display
      ("Server has stopped listening for connections.");
  }
  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
   */
  
  
  /**
   * implementation of hook method called each time a new client connection is
   * accepted. The default implementation does nothing.
   * @param client the connection connected to the client.
   */
  protected void clientConnected(ConnectionToClient client) {
	  serverUI.display("A client has connected to the server");
	  
  }

  /**
   * implementation of hook method called each time a client disconnects.
   * The default implementation does nothing. The method
   * may be overridden by subclasses but should remains synchronized.
   *
   * @param client the connection with the client.
   */
  synchronized protected void clientDisconnected(ConnectionToClient client) {
	  serverUI.display("The client: " + client.getInfo("loginId") + " has disconnected  from the server");
	     
  }
  
  /**
   * Implementation of hook method called each time an exception is thrown in a
   * ConnectionToClient thread.
   * The method may be overridden by subclasses but should remains
   * synchronized.
   *
   * @param client the client that raised the exception.
   * @param Throwable the exception thrown.
   */
  synchronized protected void clientException(ConnectionToClient client, Throwable exception) {
	  serverUI.display("a client disconnected from the server");
  }
}
//End of EchoServer class
