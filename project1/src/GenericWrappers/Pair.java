/**
 * @author Robert
 * @date 05-Apr-15.
 */
package GenericWrappers;

public class Pair<T, U> {
    public Pair(T first, U second) {
        First = first;
        Second = second;
    }
    public T First = null;
    public U Second = null;
}
