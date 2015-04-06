import UDPConnection.Exception.UDPException;
import UDPConnection.Gremlin;
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
            wc.setPort(9876);
            wc.setSendSize(256);
            wc.setReceiveSize(256);
        } catch (UDPException e) {
            e.printStackTrace();
        }

        try {
            wc.request("Get", "html0.html");
            wc.printFile();
        } catch (UDPException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WebException e) {
            e.printStackTrace();
        }
    }
}
