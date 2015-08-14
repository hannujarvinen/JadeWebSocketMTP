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
 * AgentServer.java
 *
 *
 * @author Hannu Jarvinen
 * @version 0.1
 */

package fi.aalto.mediatech.jade.mtp.ws;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Set;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketServer;
import org.java_websocket.handshake.ClientHandshake;

/**
 * A simple WebSocketServer implementation for Agent Platforms.
 */
public class AgentServer extends WebSocketServer {

    public AgentServer( int port ) throws UnknownHostException {
	super(new InetSocketAddress( InetAddress.getByName(InetAddress.getLocalHost().getHostAddress()), port ));
    }

    public AgentServer( byte[] ip, int port ) throws UnknownHostException {
	super( new InetSocketAddress( InetAddress.getByAddress(ip), port ) );
    }

    public AgentServer( InetSocketAddress address ) {
	super( address );
    }

	@Override
	public void onOpen( WebSocket conn, ClientHandshake handshake ) {
	}

	@Override
	public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
	}

	@Override
	public void onMessage( WebSocket conn, String message ) {
		System.out.println("Got message: " + message);
	}


	@Override
	public void onError( WebSocket conn, Exception ex ) {
		ex.printStackTrace();
	}

	/**
	 * Sends <var>text</var> to all currently connected WebSocket clients.
	 * 
	 * @param text
	 *            The String to send across the network.
	 * @throws InterruptedException
	 *             When socket related I/O errors occur.
	 */
	public void sendToAll( String text ) throws InterruptedException {
		Set<WebSocket> con = connections();
		synchronized ( con ) {
			for( WebSocket c : con ) {
				c.send( text );
			}
		}
	}
}
