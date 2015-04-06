package WebApplication;

import ReliableDataTransfer.ReliableDataTransfer;
import UDPConnection.Exception.UDPException;

/**
 * @author Robert
 * @date 24-Feb-15.
 */
public class HTTPConnection {

    public enum AcknowledgementCode {
        None,
        Acknowledged,
        Not_Acknowledged,
    }

    public void setSendSize(int size) throws UDPException {
       Reliable_Data_Transfer.getUDPConnection().setSendPacketSize(size);
    }

    public void setReceiveSize(int size) throws UDPException {
        Reliable_Data_Transfer.getUDPConnection().setReceivePacketSize(size);
    }

    public void setPort(int port) throws UDPException {
        Reliable_Data_Transfer.getUDPConnection().setPort(port);
    }

    public void setRootDirectory(String directory) {
        Root_Directory = directory;
    }

    protected ReliableDataTransfer Reliable_Data_Transfer;
    protected String Root_Directory = "";
}
