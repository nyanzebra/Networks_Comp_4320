import java.io.*;
import java.net.*;
import UDPConnection.*;
import UDPConnection.Exception.UDPException;
/**
 * @author Robert
 * @date 12-Feb-15.
 */
public class Client {
	public static void main(String[] args) throws IOException, UDPException {
		UDPClient udpc = new UDPClient(InetAddress.getByName("localhost"), 9876);
		udpc.setSendPacketSize(256);
		udpc.setReceivePacketSize(256);
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String s = br.readLine();
        udpc.send(s.getBytes());
        byte[] data = udpc.receive();
        String out = new String(data).replaceAll("\0", "");
        System.out.println(out);
        udpc.close();
	}
}