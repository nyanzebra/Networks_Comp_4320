package WebApplication;


import ReliableDataTransfer.ReliableDataTransfer;
import ReliableDataTransfer.SegmentationAndReassembly.SegmentAndReassemble;
import UDPConnection.Exception.UDPException;
import WebApplication.Exception.WebException;

import GenericWrappers.Pair;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Objects;
/**
 * @author Robert
 * @date 12-Feb-15.
 */
public class WebClient extends HTTPConnection {
    public WebClient(String ip) {
        try {
            Reliable_Data_Transfer = new ReliableDataTransfer(ip, -1);
        } catch (UDPException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void request(String command, String file) throws UDPException, IOException, WebException {
        if (Objects.equals(command.toLowerCase(), "get")) {
            String request = "GET " + file + " HTTP/1.0";
            Reliable_Data_Transfer.sendRequest(request);
            byte[] header_packet = Reliable_Data_Transfer.receiveObjectHeader();

            Reliable_Data_Transfer.sendObjectHeaderAcknowledgement();

            Pair<String, Integer> file_pair = SegmentAndReassemble.headerInfo(header_packet);

            String file_name = file_pair.First;
            int file_size = file_pair.Second;

            File_Contents = new String(Reliable_Data_Transfer.receiveMessage(file_size));

            FileOutputStream response_file = new FileOutputStream(file_name);
            response_file.write(File_Contents.getBytes());
            response_file.close();
        }
    }

    public void printFile() throws UDPException, WebException, IOException {
        System.out.println(File_Contents);
    }

    private String File_Contents = "";
}
