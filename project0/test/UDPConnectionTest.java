import org.junit.Before;
import org.junit.Test;
import UDPConnection.*;
import java.net.InetAddress;

import static org.junit.Assert.assertEquals;

public class UDPConnectionTest {

    private UDPConnection Client;
    private UDPConnection Server;

    @Before
    public void setUp() throws Exception {
        Client = new UDPClient(InetAddress.getByName("localhost"));
    }

    @Test
    public void testSendReceiveClientToServer() throws Exception {
        Client = new UDPClient(InetAddress.getByName("localhost"));
        Server = new UDPServer(7876);

        Client.setSendPacketSize(256);
        Client.setReceivePacketSize(256);
        Client.setPort(7876);
        Client.send(("Hello World!").getBytes());

        Server.setSendPacketSize(256);
        Server.setReceivePacketSize(256);
        assertEquals("ERROR:    Received message did match Sent Message", "Hello World!", new String(Server.receive()).replaceAll("\0", ""));

        Server.close();
        Client.close();
    }

    @Test
    public void testSetPort() throws Exception {
        Client.setPort(9876);
        assertEquals("ERROR:    Port was not set to correct value", Client.getPort(), 9876);
    }

    @Test
    public void testSendReceiveServerToClient() throws Exception {
        Client = new UDPClient(InetAddress.getByName("localhost"));
        Server = new UDPServer(8876);

        Client.setSendPacketSize(256);
        Client.setReceivePacketSize(256);
        Client.setPort(8876);
        Client.send(("Here is my address!").getBytes());

        Server.setSendPacketSize(256);
        Server.setReceivePacketSize(256);
        Server.receive();
        Server.send(("Hello World!").getBytes());
        assertEquals("ERROR:    Received message did match Sent Message", "Hello World!", new String(Client.receive()).replaceAll("\0", ""));

        Server.close();
        Client.close();
    }

    @Test
    public void testSetSendPacketSize() throws Exception {
        Client.setSendPacketSize(256);
        assertEquals("ERROR:    Size was not set to correct amount", Client.getSendPacketSize(), 256);
    }

    @Test
    public void testSetReceivePacketSize() throws Exception {
        Client.setReceivePacketSize(256);
        assertEquals("ERROR:    Size was not set to correct amount", Client.getReceivePacketSize(), 256);
    }
}