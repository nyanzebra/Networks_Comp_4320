package WebApplication;

import UDPConnection.Exception.UDPException;
import UDPConnection.UDPConnection;

/**
 * @author Robert
 * @date 24-Feb-15.
 */
public class HTTPConnection {

    public void setSendSize(int size) throws UDPException {
        UDP_Connection.setSendPacketSize(size);
    }

    public void setReceiveSize(int size) throws UDPException {
        UDP_Connection.setReceivePacketSize(size);
    }

    public void setPort(int port) throws UDPException {
        UDP_Connection.setPort(port);
    }

    public void setRootDirectory(String directory) {
        Root_Directory = directory;
    }

    public UDPConnection getUDPConnection() {
        return UDP_Connection;
    }

    public String getRootDirectory() {
        return Root_Directory;
    }

    protected UDPConnection UDP_Connection;
    protected String Root_Directory = "";
}
