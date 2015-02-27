import org.junit.Before;
import org.junit.Test;
import WebApplication.*;

import static org.junit.Assert.assertEquals;

public class HTTPConnectionTest {

    HTTPConnection Client;
    HTTPConnection Server;

    @Before
    public void setUp() throws Exception {
        Client = new WebClient("localhost");
    }

    @Test
    public void testSetSendSize() throws Exception {
        Client.setSendSize(256);
        assertEquals("ERROR:    Size was not set to correct amount", Client.getUDPConnection().getSendPacketSize(), 256);
    }

    @Test
    public void testSetReceiveSize() throws Exception {
        Client.setReceiveSize(256);
        assertEquals("ERROR:    Size was not set to correct amount", Client.getUDPConnection().getReceivePacketSize(), 256);
    }

    @Test
    public void testSetPort() throws Exception {
        Client.setPort(1234);
        assertEquals("ERROR:    Port was not set to correct value", Client.getUDPConnection().getPort(), 1234);
    }

    @Test
    public void testSetRootDirectory() throws Exception {
        Server = new WebServer(1234);
        Server.setRootDirectory("/");
        assertEquals("ERROR:    Root Directory does not match or was not set", Server.getRootDirectory(), "/");
    }
}