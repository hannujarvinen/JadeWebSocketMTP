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
 * WSAddress.java
 *
 *
 * @author Hannu Jarvinen
 * @version 0.1
 */

package fi.aalto.mediatech.jade.mtp.ws;

import jade.mtp.TransportAddress;
import java.net.InetAddress;

public class WSAddress implements TransportAddress {
  
    private String url;
    
    private String proto;
    private String host;
    private String port;
    private String file;
    private String anchor;

    /* Argument addr needs to be in form:
     * ws://10.0.1.10:7778/acc
     */
    WSAddress(String addr) {
	url = addr;
 	int i = addr.indexOf(":");
	int j = addr.indexOf(":", i+1);
	int k = addr.indexOf("/", j+1);
	proto = addr.substring(0, i);
	host = addr.substring(i+3, j);
	port = addr.substring(j+1,k);
	file = addr.substring(k, addr.length());
	anchor = null;
    }
        
    
    /** Get the value of protocol */    
    public String getProto() {
	return proto;
    }
    
    /** Get the value of host */
    public String getHost() {
	return host;
    }
    
    /** Get the value of port */
    public String getPort() {
	return port;
    }
    
    /** Get the value of port */
    public int getPortNo() {
	return Integer.parseInt(port);
    }
    
    /** Get the value of file */
    public String getFile() {
	return file;
    }
    
    /** Get the value of anchor */
    public String getAnchor() {
	return url;
    }   
    
    /** convert to String */
    public String toString() {
	if(url!=null)
	    return url;
	else
	    return proto+"://"+host+":"+port+"/"+file;
	
    }

} //End of WSAddress class
