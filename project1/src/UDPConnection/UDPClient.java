package UDPConnection;

import UDPConnection.Exception.UDPException;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
/**
 * @author Robert
 * @date 12-Feb-15.
 */
public class UDPClient extends UDPConnection {

    public UDPClient(InetAddress ip) throws UDPException {
        IP = ip;
        try {
            Socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
	}
}