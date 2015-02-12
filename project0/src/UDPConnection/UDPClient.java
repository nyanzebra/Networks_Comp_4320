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
	//public
	//constructor
	public UDPClient(InetAddress ip, int port) throws UDPException {
        if (port > 9999 || port < 0) {
            throw new UDPException("UDPException:	Port must be within valid range: 0 - 9999");
        }
        m_Port = port;
        m_IP = ip;
        try {
            m_Socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
	}
}