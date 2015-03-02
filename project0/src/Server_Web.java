import UDPConnection.Exception.UDPException;
import UDPConnection.Gremlin;
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

        // Get Gremlin probability runtime argument.
        // If no argument is given, probability defaults to 0.
        double gremlinProbability;
        if (args.length > 0) {
            try {
                gremlinProbability = Double.parseDouble(args[0]);
                if (gremlinProbability > 1.0) {
                    System.err.println("Argument" + args[0] + " must be less than or equal to 1.0.");
                    System.exit(1);
                }
                Gremlin.setDamageProbability(gremlinProbability);
            } catch (NumberFormatException e) {
                System.err.println("Argument" + args[0] + " must be a double.");
                System.exit(1);
            }
        }

        try {
            ws.setSendSize(256);
            ws.setReceiveSize(256);
        } catch (UDPException e) {
            e.printStackTrace();
        }

        ws.setRootDirectory("/Users/jonathanhart/Developer/Networks_Comp_4320/project0/src/");
        
        try {
            ws.listen();
        } catch (UDPException | IOException | WebException e) {
            e.printStackTrace();
        }
    }
}
