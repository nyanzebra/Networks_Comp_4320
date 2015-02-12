import UDPConnection.Exception.UDPException;
import WebApplication.WebClient;

import java.io.IOException;
/**
 * @author Robert
 * @date 12-Feb-15.
 */
public class Client_Web {
    public static void main(String[] args) {
        WebClient wc = new WebClient("localhost", 9876, 256, 256);
        try {
            wc.request("Get", "html0.html");
        } catch (UDPException | IOException e) {
            e.printStackTrace();
        }
        wc.printFile();
    }
}
