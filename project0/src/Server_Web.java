import UDPConnection.Exception.UDPException;
import UDPConnection.Exception.WebException;
import WebApplication.WebServer;

import java.io.IOException;
/**
 * @author Robert
 * @date 12-Feb-15.
 */
public class Server_Web {
    public static void main(String[] args) {
        WebServer ws = new WebServer(9876, 256, 256);
        ws.setRootDirectory("C:\\Users\\Robert\\Documents\\GitHub\\Networks_Comp_4320\\project0\\src\\");
        try {
            ws.listen();
        } catch (UDPException | IOException | WebException e) {
            e.printStackTrace();
        }
    }
}
