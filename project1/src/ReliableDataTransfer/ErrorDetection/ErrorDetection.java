package ReliableDataTransfer.ErrorDetection;

/**
 * @author Robert
 * @date 05-Apr-15.
 */
public class ErrorDetection {
    public static byte calculateChecksum(byte[] data) { // Need better checksum, this one sucks!
        byte sum = 0;
        for (int i = 0; i < data.length; ++i) {
            if (i % 2 == 0) {
                sum += data[i];
            } else if (i % 3 == 0) {
                if (data[i] != 0) {
                    sum /= data[i];
                } else {
                    sum += data[i];
                }
            } else {
                sum -= data[i];
            }
        }
        return sum;
    }

    public static boolean isCorrupted(byte[] data, int checksum) {
        int calculated_checksum = calculateChecksum(data);

        return ((calculated_checksum != checksum) && (calculated_checksum != 0));
    }
}
