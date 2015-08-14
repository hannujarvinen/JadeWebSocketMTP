/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2000 CSELT S.p.A.

GNU Lesser General Public License

This library is free software; you can redistribute it sand/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation,
version 2.1 of the License.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
*****************************************************************/

/**
 * WebSocket MTP for JADE - 2012  
 * CommunicationHandler.java
 *
 *
 * @author Hannu Jarvinen
 * @version 0.1
 */

package fi.aalto.mediatech.jade.mtp.ws;

import org.java_websocket.WebSocket;
import jade.core.*;
import java.net.URI;
import java.util.ListIterator;
import java.util.Iterator;
import java.util.LinkedList;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.ACLCodec;
import jade.lang.acl.StringACLCodec;


/**
 * CommunicationHandler
 *
 * Abstraction to provide WebSocket communication functionalities
 * for the WebSocket MTP.
 */
public class CommunicationHandler{

	private LinkedList channels = new LinkedList();
	private AgentClient agent = null;
	private WebSocket webSocket = null;
	private MessageTransportProtocol owner;
	private int port = 7778;
	private AgentServer agentServer = null;

	public CommunicationHandler(MessageTransportProtocol owner_) {
		owner = owner_;
		this.initiateWSServer();
	}

    /* Sends the given message to the given address.
     * Takes care of handling the channel list which contains WebSockets
     * and their associated addresses. New channels are created and removed
     * when necessary.
     */
    public void send(String address, String msg){
	//System.out.println("send to: " + address);
	//System.out.println("msg: " + msg);
    	WSConnection connection = null;
	// search for existing WSConnection
    	ListIterator i = channels.listIterator();
    	while(i.hasNext()){
    		connection = (WSConnection)i.next();
    		if(connection.getAddress().equals(address)){
		// send using the WSConnection
    			WebSocket webSocket = connection.getWebSocket();
    			if(webSocket!=null){
    				try {
    					webSocket.send(msg);
    					return;
    				} catch ( InterruptedException ex ) {
    					ex.printStackTrace();
    				}
    			} else {
    				AgentClient agent = connection.getAgentClient();
    				if(agent!=null){
    					agent.write(msg);
    					return;
    				} else
    				System.out.println("Error: WSConnection is empty, though there should always be either a client or a server once created");
    			}

    		}
    	}
	// there was no existing WSConnection -> make a new one
    	System.out.println("Creating WebSocket connection to " + address);
    	connection = new WSConnection(address);
    	try{
    		agent = new AgentClient( new URI(address)){

		    // DEFINE WHAT TO DO WHEN A MESSAGE IS RECEIVED
    			@Override
    			public void onMessage( String message ) {
			// Extract envelope and payload (ACL) from the message
    				String envelope;
    				String payload;
    				int i = 0;
    				i = message.indexOf("--");
    				String delimiter = message.substring(i,i+32);
    				String[] strs = message.split(delimiter);
    				i = strs[1].indexOf("<envelope>");
    				strs[1] = strs[1].substring(i, strs[1].length());
    				i = strs[2].indexOf("(");
    					strs[2] = strs[2].substring(i, strs[2].length());
    					envelope = strs[1];
    					payload = strs[2];

			//System.out.println("Received Message");
			//System.out.println("Envelope: " + envelope );
			//System.out.println("Payload: " + payload );

			// Pass message to MTP
    					owner.onMessage(envelope, payload);
    				}
		    // Delete channel when WebSocket is closed
    				@Override
    				public void onClose( int code, String reason, boolean remote) {
    					try {
    						System.out.println("WebSocket was closed - Removing it from the active channels - (WS: " + agent.getConnection().toString() + " )");
    						removeChannel(agent.getConnection());
    					} catch ( Exception ex ) {
    						System.out.println("Error while onClose in communication handler");
    						ex.printStackTrace();
    					}
    				}

    			};
	    // CONNECT TO THE SERVER
    			try{
    				agent.connect();
    			} catch(Exception e){
    				System.out.println("Unable to create WebSocket connection! Message dropped!");
    				e.printStackTrace();
    				return;
    			}
	    // WAIT FOR THE CONNECTION TO BE ESTABLISHED
	    // If we dont wait, the message is lost!

	    // Wait until there is a WebSocket
    			int tryCount = 0;
    			while(tryCount<10){
    				try{
    					Thread.sleep(500);
    					WebSocket s = agent.getConnection();
    					if(s!=null){
    						break;
    					}
    					tryCount++;
    				} catch(Exception e){
    					System.out.println("Unable to create WebSocket connection! Message dropped!");
    					e.printStackTrace();
    					return;
    				}

    			}

    			if (agent.getConnection()==null) {
    				System.out.println("Unable to create WebSocket connection! Message dropped!");
    				return;    				
    			}
    			if(agent.getReadyState()==2 || agent.getReadyState()==3){
    				System.out.println("WebSocket closed/closing. Not able to open connection. Message dropped!");
    				return;
    			}

	    		// Wait until the WebSocket is OPEN
    			try{
    				while(agent.getReadyState()!=1){
    				}
    			} catch(Exception e){
    				System.out.println("Unable to open WebSocket connection! Message dropped!");
    				e.printStackTrace();
    				return;
    			}

    			System.out.println("agent.getreadystate==1");

	    // SENDING A MESSAGE
    			agent.write(msg);
    		} catch(Exception e){
    			System.out.println(e.toString());
    			return;
    		}
    		connection.setAgentClient(agent);
	// add connection to the list
    		channels.add(connection);
    		System.out.println("Added a new WebSocket channel (client)");

    	}

    /* Checks if the receiver address in the message already
     * has an associated WebSocket in the channels list
     */
    private boolean hasChannel(WebSocket conn, String message){
    	try{
	    // get Agent ID from the message content
    		WSConnection connection = null;
	    //byte[] bytes = message.getBytes("US-ASCII");
    		byte[] bytes = message.getBytes("ISO-8859-1");
    		ACLCodec codec = new StringACLCodec();
	    //ACLMessage msg = codec.decode(bytes,"US-ASCII"); 
    		ACLMessage msg = codec.decode(bytes,"ISO-8859-1"); 
    		Iterator senders = msg.getSender().getAllAddresses();
    		while(senders.hasNext()){
    			String addr = (String)senders.next();

		// go through channels list for the same address, return true if found
    			ListIterator i = channels.listIterator();
    			while(i.hasNext()){
    				connection = (WSConnection)i.next();
    				if(connection.getAddress().equals(addr)){
    					return true;
    				}
    			}

    		}

	    //return false if address not in channels list
    		return false;
    	} catch (Exception e) {
    		e.printStackTrace();
    		return false;
    	}
    }

    /* Adds new channel(s) to the list. Channel(s) are associated with the
     * receiver address(es) in the message and the given WebSocket
     */
    private void addChannel(WebSocket conn, String message){
    	try{
	    	// get Agent ID from the message content
	    	//byte[] bytes = message.getBytes("US-ASCII");
    		byte[] bytes = message.getBytes("ISO-8859-1");
    		ACLCodec codec = new StringACLCodec();
	    	//ACLMessage msg = codec.decode(bytes,"US-ASCII"); 
    		ACLMessage msg = codec.decode(bytes,"ISO-8859-1"); 
    		Iterator senders = msg.getSender().getAllAddresses();
	    	// create channel for each address , all associated with the same WebSocket
    		while(senders.hasNext()){
    			String addr = (String)senders.next();
				// create a WSConnection for this agent and WebSocket
    			WSConnection newConn = new WSConnection(addr);
    			newConn.setWebSocket(conn);
				// add new WSConnection to the channels list
    			channels.add(newConn);
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}

    }
    
    /* Remove the channel associated with given WebSocket
     * from the channels list 
     */
    private void removeChannel(WebSocket conn) {
	// Find the channel
    	WSConnection connection = null;
    	ListIterator i = channels.listIterator();
    	while(i.hasNext()){
    		connection = (WSConnection)i.next();
    		if(connection.getWebSocket().toString().equals(conn.toString())){
    			System.out.println("Removing WebSocket ( " + conn.toString() + " )");
    			i.remove();
    		}	
    	}
    }
    
    /* Stops the WebSocket server
     */
    public void killServer(){
    	try{
    		agentServer.stop();
    	} catch(Exception e){
    		e.printStackTrace();
    	}
    }


    /* Starts the WebSocket server and listen incoming messages
     */
    public void initiateWSServer(){
    	byte[] ipNums = new byte[4];

    	try{
    		String host = owner.getTransportAddress().getHost();
    		String[] ipStrs = host.split("\\.");
    		ipNums[0] = (byte)Integer.parseInt(ipStrs[0]);
    		ipNums[1] = (byte)Integer.parseInt(ipStrs[1]);
    		ipNums[2] = (byte)Integer.parseInt(ipStrs[2]);
    		ipNums[3] = (byte)Integer.parseInt(ipStrs[3]);
    	}catch(Exception e){
    		System.out.println("WebSocket MTP address has to be given in numerical format!");
    		e.printStackTrace();
    		System.exit(0);
    	}
    	try{
	    // CREATE NEW WS SERVER
    		agentServer = new AgentServer( ipNums, owner.getPort() ){

		    // DEFINE WHAT TO DO WHEN A MESSAGE IS RECEIVED
		    // AND STORE THE WEBSOCKET TO BE ABLE TO SEND
		    // THERE DATA PROACTIVELY
    			@Override
    			public void onMessage( WebSocket conn, String message ) {
    				try {
			    // Extract envelope and payload (ACL) from the message
    					String envelope;
    					String payload;
    					int i = 0;
    					i = message.indexOf("--");
    					String delimiter = message.substring(i,i+32);
    					String[] strs = message.split(delimiter);
    					i = strs[1].indexOf("<envelope>");
    					strs[1] = strs[1].substring(i, strs[1].length());
    					i = strs[2].indexOf("(");
    						strs[2] = strs[2].substring(i, strs[2].length());
    						envelope = strs[1];
    						payload = strs[2];

			    //System.out.println("Received Message");
			    //System.out.println("Envelope: " + envelope );
			    //System.out.println("Payload: " + payload );

			    // Pass message to MTP
    						owner.onMessage(envelope, payload);
			    // Add new channels to the list
    						if(!hasChannel(conn,payload)){
    							addChannel(conn,payload);
    							System.out.println("Added a new WebSocket channel (server)");
    						}
    					} catch ( Exception ex ) {
    						System.out.println("Error while onMessage in communication handler");
    						ex.printStackTrace();
    					}
    				}

		    // Delete channel when WebSocket is closed
    				@Override
    				public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
    					try {
    						System.out.println("WebSocket was closed - Removing it from the active channels - (WS: " + conn.toString() + " )");
    						removeChannel(conn);
    					} catch ( Exception ex ) {
    						System.out.println("Error while onClose in communication handler");
    						ex.printStackTrace();
    					}
    				}

    			};

	    // START THE SERVER
    			agentServer.start();

    			System.out.println( "WebSocket server started on port: " + agentServer.getPort() );

    		} catch (Exception e){
    			System.out.println("Error: " + e);
    		}
    	}


    }
