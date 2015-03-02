import UDPConnection.Exception.UDPException;
import WebApplication.Exception.WebException;
import WebApplication.WebClient;

import java.io.IOException;
/**
 * @author Robert
 * @date 12-Feb-15.
 */
public class Client_Web {
    public static void main(String[] args) {
        WebClient wc = new WebClient("localhost");

        try {
            wc.setPort(9876);
            wc.setSendSize(256);
            wc.setReceiveSize(256);
        } catch (UDPException e) {
            e.printStackTrace();
        }

        try {
            wc.request("Get", "html0.html");
        } catch (UDPException | IOException | WebException e) {
            e.printStackTrace();
        }
        wc.printFile();
    }
}
