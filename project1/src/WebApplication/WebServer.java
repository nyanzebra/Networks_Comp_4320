package WebApplication;

import UDPConnection.Exception.UDPException;
import WebApplication.Exception.WebException;
import ReliableDataTransfer.ReliableDataTransfer;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 * @author Robert
 * @date 12-Feb-15.
 */
public class WebServer extends HTTPConnection {
    public WebServer(int port) {
        try {
            Reliable_Data_Transfer = new ReliableDataTransfer(null, port);
        } catch (UDPException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void listen(int window_size, int sequence_modulus) throws UDPException, IOException, WebException, InterruptedException {
        while (true) {
            String message = new String(Reliable_Data_Transfer.receiveRequest());

            if (isRequest(message)) {
                respond(message, window_size, sequence_modulus);
            }
        }
    }

    private void respond(String message, int window_size, int sequence_modulus) throws IOException, UDPException, WebException, InterruptedException {
        String file_location = findFileLocation(message);

        Reliable_Data_Transfer.setFile(file_location, Root_Directory);
        Reliable_Data_Transfer.sendObjectHeader();

        if ( Reliable_Data_Transfer.receiveObjectHeaderAcknowledgement() == AcknowledgementCode.Acknowledged) {
            Reliable_Data_Transfer.sendMessage(window_size, sequence_modulus);
        }
    }

    private String findFileLocation(String message) {
        return message.substring(message.indexOf(' ') + 1, message.lastIndexOf(' '));
    }

    private boolean isRequest(String message) {
        return message != null && message.contains("GET");
    }
}
