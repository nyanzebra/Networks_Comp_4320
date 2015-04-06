package UDPConnection;

import UDPConnection.Exception.UDPException;

import java.net.DatagramSocket;
import java.net.SocketException;
/**
 * @author Robert
 * @date 12-Feb-15.
 */
public class UDPServer extends UDPConnection {

	public UDPServer(int port) throws UDPException {
        if (port > 9999 || port < 0) {
            throw new UDPException("UDPException:	Port must be within valid range: 0 - 9999");
        }
        Port = port;
        try {
            Socket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
}