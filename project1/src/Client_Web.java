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
        double damageProbability;
        double dropProbability;

        if (args.length > 1) {
            try {
                damageProbability = Double.parseDouble(args[0]);
                if (damageProbability > 1.0) {
                    System.err.println("Argument" + args[0] + " must be less than or equal to 1.0.");
                    System.exit(1);
                }
                dropProbability = Double.parseDouble(args[1]);
                if (dropProbability > 1.0) {
                    System.err.println("Argument" + args[1] + " must be less than or equal to 1.0.");
                    System.exit(1);
                }
                Gremlin.setDamageProbability(damageProbability);
                Gremlin.setDropProbability(dropProbability);
            } catch (NumberFormatException e) {
                System.err.println("Arguments must be a double.");
                System.exit(1);
            }
        }

        try {
            wc.setPort(9876);
            wc.setSendSize(512);
            wc.setReceiveSize(512);
        } catch (UDPException e) {
            e.printStackTrace();
        }

        try {
            wc.request("Get", "testfile.php");
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
