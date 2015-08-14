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
 * WSConnection.java
 *
 *
 * @author Hannu Jarvinen
 * @version 0.1
 */

package fi.aalto.mediatech.jade.mtp.ws;

import org.java_websocket.WebSocket;

public class WSConnection{

    private String address = null;
    private WebSocket webSocket = null;
    private AgentClient agent = null;

    public WSConnection(String address_) {
	address = address_;
    }

    public String getAddress(){
	return address;
    }

    public void setAgentClient(AgentClient agent_){
	agent = agent_;
    }

    public AgentClient getAgentClient(){
	return agent;
    }

    public WebSocket getWebSocket(){
	return webSocket;
    }

    public void setWebSocket(WebSocket webSocket_){
	webSocket = webSocket_;
    }
}
