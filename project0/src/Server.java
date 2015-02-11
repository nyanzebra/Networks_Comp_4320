import UDPConnection.Exception.UDPException;
import UDPConnection.UDPServer;

import java.io.IOException;

public class Server {
	public static void main(String[] args) throws UDPException, IOException {
		//create UDP Server listening on port 9876
		UDPServer udps = new UDPServer(9876);
		//set packet sizes
		udps.setSendPacketSize(256);
		udps.setReceivePacketSize(256);
		String response = "Success!";
		while (true) {
			//we have data to look at
			byte[] data = udps.receive();
            if (data != null) {
                udps.send(response.getBytes());
            }

		}
	}
}