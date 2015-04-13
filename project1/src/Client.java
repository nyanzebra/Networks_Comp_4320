
import UDPConnection.Exception.UDPException;
import UDPConnection.UDPClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
/**
 * @author Robert
 * @date 12-Feb-15.
 */
public class Client {
	public static void main(String[] args) throws IOException, UDPException {
        UDPClient udpc = new UDPClient(InetAddress.getByName("localhost"));
        udpc.setSendPacketSize(256);
        udpc.setReceivePacketSize(256);
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String s = br.readLine();
        udpc.setPort(9876);
        udpc.send(s.getBytes());
        byte[] data = new byte[0];
        data = udpc.receive();
        String out = new String(data).replaceAll("\0", "");
        System.out.println(out);
        udpc.close();
	}
}