package ReliableDataTransfer.ErrorDetection;

/**
 * @author Robert
 * @date 05-Apr-15.
 */
public class ErrorDetection {
    public static byte calculateChecksum(byte[] data) {
        byte sum = 0;
        for (byte b : data) {
            sum += b;
        }
        return sum;
    }

    public static boolean isCorrupted(byte[] data, int checksum) {
        int calculated_checksum = calculateChecksum(data);

        return ((calculated_checksum != checksum) && (calculated_checksum != 0));
    }
}
