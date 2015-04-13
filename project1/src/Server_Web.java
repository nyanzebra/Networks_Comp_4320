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

        ws.setRootDirectory("C:/Users/Robert/Documents/GitHub/Networks_Comp_4320/project1/src/");

        try {
            ws.setSendSize(512);
            ws.setReceiveSize(512);
        } catch (UDPException e) {
            e.printStackTrace();
        }
        
        try {
            ws.listen(8, 24);
        } catch (UDPException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WebException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
