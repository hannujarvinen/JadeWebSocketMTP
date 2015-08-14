========================
 WebSocket MTP for Jade
========================

WebSocket Message Transport Protocol (MTP) enables Jade platforms to communicate using WebSocket communication. 

Homepage: http://media.tkk.fi/webservices/


-------------
Installation:
-------------

1. Unzip the package to the Jade root directory:

   unzip JadeWebSocketMTP_0.1.zip -d {jade_root_dir}

2. Install Jade WebSocket MTP:

   cd {jade_root_dir}/JadeWebSocketMTP/
   ant install

3. Start Jade with WebSocket MTP:

   cd {jade_root_dir}
   java -cp .:lib/* jade.Boot -gui -mtp "fi.aalto.mediatech.jade.mtp.ws.MessageTransportProtocol(ws://put.here.your.ip:7778/acc)"


--------
Author:
--------

	Hannu Järvinen, Aalto University, hannu dot jarvinen at aalto dot fi


-----------------
Acknowledgements:
-----------------

	The development has been partly funded by The Doctoral Programme in the Built Environment (RYM-TO) funded through the Academy of Finland and the Ministry of Education and Culture.


---------------------
Third-Party Software:
---------------------

This software includes the following software:

     Java-WebSocket v1.0.0 (https://github.com/TooTallNate/Java-WebSocket)
     MIT License

     Apache Crimson v1.1.3 (http://xml.apache.org/crimson/)
     The Apache Software License, Version 1.1
