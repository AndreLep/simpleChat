// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import ocsf.client.*;
import common.*;
import java.io.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 
  
  //String to keep track of each clients login id 
  String loginID;

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String loginID, String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    this.loginID = loginID;
    
    //if the login id is not set terminate the client
    if (this.loginID.equals("#Quit Now")) {
    	clientUI.display("ERROR - No login ID specified.  Connection aborted.");
    	this.quit();
    	
    //if not procede as usual
    } else {
    	openConnection();
    	sendToServer("#login " + loginID);
    	clientUI.display(loginID + " has logged on");
    }
    
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
    try
    {
	    if(message.startsWith("#")) {
	    	handleCommand(message);
	    	
	    } else {
	    	sendToServer(message);
	    }
	    
    }
    catch(IOException e)
    {
      clientUI.display
        ("Could not send message to server.  Terminating client.");
      quit();
    }
  }
  
  //method to handle commands from the client console if they start with a #
  private void handleCommand(String cmd){
	  
	  //executes the regular stoppage of the client
	  if(cmd.equals("#quit")) {
		  clientUI.display("the client will now quit");
		  quit();
		  
		  //disconnects the client from the server but does not terminate the client
	  } else if(cmd.equals("#logoff")){
		  
		  try {
			  if(this.isConnected()) {
				  clientUI.display("the client will now disconnect from the server");
				  this.closeConnection();
			  } else {
				  clientUI.display("you are already logged off");
			  }
			  
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  //changes the clients port only if the client is not currently connected
	  } else if(cmd.startsWith("#setport")){
		  
		  if (this.isConnected()) {
			  clientUI.display("Can not change port number while connected");
		  } else {
			  int portNumber = 0;
			  
			// get the second argument of the command which is the port
			  String [] splitCmd = cmd.split(" ");
			  try {
				 portNumber = Integer.parseInt(splitCmd[1]);
				 this.setPort(portNumber);
				 clientUI.display("Port is now set to: " + portNumber);
			  }
			  // no port number given
			  catch(ArrayIndexOutOfBoundsException ai) {
				  clientUI.display("You have not provided a port number");
			  }
			  //port not a number
			  catch(NumberFormatException ne) {
				  clientUI.display("the port number you have entered is invalid ");
			  }
		  }
		      
		//changes the clients host only if the client is not currently connected
	  } else if(cmd.startsWith("#sethost")){
		  
		  if (this.isConnected()) {
			  clientUI.display("Can not change host while connected");
		  } else {
			  String host = "";
			  
			// get the second argument of the command which is the host
			  String [] splitCmd = cmd.split(" ");
			  try {
				 host = splitCmd[1];
				 this.setHost(host);
			  }
			  
			  //no host given
			  catch(ArrayIndexOutOfBoundsException ai) {
				  clientUI.display("You have not provided a host name");
			  }
			  
		  }
		  
		  //connects the client to the server only when it is not already connected
	  } else if(cmd.equals("#login")) {
		  
		  if (this.isConnected()) {
			  clientUI.display("the client is already logged in");
		  } else {
			  clientUI.display("the client will now log in");
			  try {
				  
				this.openConnection();
				sendToServer("#login " + loginID);
		    	clientUI.display(loginID + " has logged on");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  }
		 
		  //returns the current host that the client is using
	  } else if(cmd.equals("#gethost")) {
		  
		  clientUI.display(this.getHost());
		
		//returns the current port that the client is using
	  } else if(cmd.equals("#getport")) {
		  
		  clientUI.display("" +this.getPort());
		  
		  
	  } 
	  //executed when the command entered is invalid
	  else {
		clientUI.display("that is not a valid command");
	  }
	  
	  
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
  
  /**
	 * implementation of hook method called after the connection has been closed. The default
	 * implementation does nothing. The method may be overriden by subclasses to
	 * perform special processing such as cleaning up and terminating, or
	 * attempting to reconnect.
	 */
	protected void connectionClosed() {
		clientUI.display("The connection to the server has been closed");
	}

	/**
	 * implementation of hook method called each time an exception is thrown by the client's
	 * thread that is waiting for messages from the server. The method may be
	 * overridden by subclasses.
	 * 
	 * @param exception
	 *            the exception raised.
	 */
	protected void connectionException(Exception exception) {
		clientUI.display("The server has shut down");
		System.exit(0);
	}
}
//End of ChatClient class
