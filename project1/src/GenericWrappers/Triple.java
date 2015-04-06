package GenericWrappers;

/**
 * @author Robert
 * @date 05-Apr-15.
 */
public class Triple<T, U, V> {
    public Triple(T first, U second, V third) {
        First = first;
        Second = second;
        Third = third;
    }
    public T First = null;
    public U Second = null;
    public V Third = null;
}
