import UDPConnection.Exception.UDPException;
import WebApplication.Exception.WebException;
import WebApplication.WebServer;

import java.io.IOException;
/**
 * @author Robert
 * @date 12-Feb-15.
 */
public class Server_Web {
    public static void main(String[] args) {
        WebServer ws = new WebServer(9876);

        try {
            ws.setSendSize(256);
            ws.setReceiveSize(256);
        } catch (UDPException e) {
            e.printStackTrace();
        }

        ws.setRootDirectory("C:\\Users\\Robert\\Documents\\GitHub\\Networks_Comp_4320\\project0\\src\\");
        
        try {
            ws.listen();
        } catch (UDPException | IOException | WebException e) {
            e.printStackTrace();
        }
    }
}
