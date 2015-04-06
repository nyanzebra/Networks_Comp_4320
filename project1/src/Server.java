import UDPConnection.Exception.UDPException;
import UDPConnection.UDPServer;

import java.io.IOException;
/**
 * @author Robert
 * @date 12-Feb-15.
 */
public class Server {
	public static void main(String[] args) throws UDPException, IOException {
		//create UDP Server listening on port 9876
		UDPServer udps = new UDPServer(9876);
		//set packet sizes
		udps.setSendPacketSize(256);
		udps.setReceivePacketSize(256);
		String response = "Success!";
        String shutdown = "Confirmed:   server is shutdown";
		while (true) {
			//we have data to look at
			byte[] data = udps.receive();
            String s_data = new String(data).replaceAll("\0", "");
            if (s_data.equals("Hello")) {
                udps.send(response.getBytes());
            }
            if (s_data.equals("shutdown")) {
                udps.send(shutdown.getBytes());
                udps.close();
                break;
            }
		}
	}
}