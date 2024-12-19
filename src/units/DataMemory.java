package units;

public class DataMemory {
    private int[] data;
    private int maximumUsedAddress = -1;

    /**
     * Constructs a new memory for Data with the specified size.
     * @param size the maximum number of words the memory can hold, word's length is 16 bits (short type)
     */
    public DataMemory(int size)
    {
        data = new int[size];
    }

    /**
     * Reads the word from the memory at the specified address.
     * @param address the address of the word to be retrieved (byte addressing)
     * @return the word at the specified address
     */
    public int read(int address)
    {
        return data[address];
    }

    /**
     * Write a word to the memory at the specified address.
     * @param address the address at which the word will be stored (byte addressing)
     * @param value the word to be stored in the memory
     */
    public void write(int address, int value)
    {
        maximumUsedAddress = Math.max(maximumUsedAddress, address);
        data[address] = value;
    }

    /**
     * Returns a string representation of the data memory.
     */
    public String toString()
    {
        StringBuilder r = new StringBuilder();
        for(int i = 0; i <= maximumUsedAddress; ++i)
            r.append(String.format("%d: %s = %d\n", i, Integer.toBinaryString(data[i]), data[i]));
        return r.toString();
    }
}
