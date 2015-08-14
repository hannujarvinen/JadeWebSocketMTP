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
 * MessageTransportProtocol.java
 *
 *
 * @author Hannu Jarvinen
 * @version 0.1
 */


package fi.aalto.mediatech.jade.mtp.ws;

import jade.mtp.InChannel;
import jade.mtp.MTP;
import jade.mtp.MTPException;
import jade.mtp.TransportAddress;
import jade.mtp.http.HTTPIO;
import jade.mtp.http.XMLCodec;
import jade.domain.FIPAAgentManagement.Envelope;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.util.ExtendedProperties;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.ACLCodec;
import jade.lang.acl.StringACLCodec;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import java.util.LinkedList;
import java.lang.InterruptedException;
import java.net.InetAddress;
import java.sql.Timestamp;

import java.util.*;
import java.io.*;

import org.apache.crimson.parser.XMLReaderImpl;

/**
 * MessageTransportProtocol
 *
 * Implements the WebSocket MTP for JADE.
 */
public class MessageTransportProtocol implements MTP {
	
    // DEFAULT VALUES
    private static final int    IN_PORT    = 7778;
    private static final String PREFIX     = "jade_mtp_ws_";
    
    private String[] protocols = {};
    private String FIPA_NAME = "fipa.mts.mtp.ws.std";
    
    private CommunicationHandler comm;
    private byte[] myIP; // = {10,0,1,5};
    private int myPort = 7778;

    private TransportAddress myTA;

    private InChannel.Dispatcher dispatcher;
    private XMLCodec codec;
    static String CODEC   = "org.apache.crimson.parser.XMLReaderImpl";
    //static String CODEC = "org.apache.xerces.parsers.SAXParser";

    /** MTP Interface Methods */
    public TransportAddress strToAddr(String rep) throws MTPException {
	try {
	    return new WSAddress(rep);
	} catch (Exception e){
	    e.printStackTrace();
	}
	return null;
    }
	
    public String addrToStr(TransportAddress ta) throws MTPException {
	if(ta==null)
	    System.out.println("ta==null");
	try {
	    return ((WSAddress) ta).toString();
	} catch(Exception e) {
	    throw new MTPException(e.toString());
	}
    }
    
    public String getName() {
	return FIPA_NAME;
    }
    
    public String[] getSupportedProtocols() {
	return protocols;
    }
    
    /********************************
     *   InChannel Interface Methods *
     *********************************/
    
    /**
     * Old method, only for compliance with former versions (prior 3.0)
     */
    public TransportAddress activate(InChannel.Dispatcher disp)
	throws MTPException {
	try {
	    return activate(disp,new ProfileImpl(new ExtendedProperties()));
	} catch(Exception e) {
	    throw new MTPException(e.getMessage());
	}
    }
    
    public void activate(InChannel.Dispatcher disp, TransportAddress ta)
	throws MTPException {
	try {
	    activate(disp,ta,new ProfileImpl(new ExtendedProperties()));
	} catch(Exception e) {
	    throw new MTPException(e.getMessage());
	}
    }
    
    public TransportAddress activate(InChannel.Dispatcher disp, Profile p)
	throws MTPException {
	//Active the new WSAddress
	return activateServer(disp,null,p);
    }
    
    /**
     * Actual method to activate the WebSocket MTP.
     *
     */
    public void activate(InChannel.Dispatcher disp, TransportAddress ta, Profile p) throws MTPException {
	activateServer(disp, ta, p);
    }
    
    /* Activates the WebSocket server
     */
    private TransportAddress activateServer(InChannel.Dispatcher disp, TransportAddress ta, Profile p) throws MTPException {
	// Set the dispatcher for delivering msgs to Jade
	dispatcher = disp;
	// Create coded for decoding incoming messages
	codec = new XMLCodec(CODEC);
	// Create server here and prepare message listener
	System.out.println("Activating WebSocket server");
	WSAddress hta = null;
	try {
	    if (ta != null) {
		hta = (WSAddress)ta;
	    } 
	    
	} catch (Exception e) {
	    e.printStackTrace();
	}
	
	protocols = new String[]{hta.getProto()};
	myTA = hta;
	// Initialize WebSocket server
	comm = new CommunicationHandler(this);
	return hta;
    }

    public TransportAddress getTransportAddress(){
	return myTA;
    }
    
    public void deactivate(TransportAddress ta) throws MTPException {
	// Shutdown WebSocket Server
	comm.killServer();
    }
    
    public void deactivate() throws MTPException {
	deactivate(myTA);
    }
    
    /* Handles incoming messages from the communication handler by
     * forwarding them to the JADE platform
     */
    public void onMessage(String envelope, String payload){
	try{
	    // Create Envelope from the XML string
	    StringReader sr = new StringReader(envelope);
	    Envelope env = codec.parse(sr);
	    
	    // Post the Message to Jade platform	
	    synchronized (dispatcher) {		
		dispatcher.dispatchMessage(env,payload.getBytes("ISO-8859-1"));
	    }
	    
	}catch(Exception e){
	    e.printStackTrace();
	}
    }	
    
    /********************************
     *  OutChannel Interface Methods *
     *********************************/
    
    /* Prepares a message and forwards it to the communication
     * handler for sending
     */
    public void deliver(String addr, Envelope env, byte[] payload) throws MTPException {
	byte[] request;
	
	try {
	    // Prepare the message

	    // Create a delimiter
	    StringBuffer delimiter = new StringBuffer();
	    for( int i=0 ; i < 31 ; i++ ) {
		delimiter.append(Integer.toString((int)Math.round(Math.random()*15),16));
	    }
	    byte[] delimiterBytes = delimiter.toString().getBytes("ISO-8859-1");
	    // Create the message body
	    byte[] body = HTTPIO.createHTTPBody(env,delimiterBytes,payload);
	    
	    // Send the message
	    comm.send(addr, new String(body));

	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
    
    public byte[] getIP(){
	return myIP;
    }
    
    public int getPort(){
	return myPort;
    }
    
    
} // End of MessageTransportProtocol class
