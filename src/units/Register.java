package units;

public class Register {
    String name = "";
    int size ;
    int value ;


    /**
     * Constructs a new register with the specified number of bits
     * @param size the size of the register in bits
     */
    public Register(int size)
    {
        this.size = size;
    }

    /**
     * Retrieves the bits of the register between left and right indices, inclusive
     * @param left the index of the leftmost bit to be retrieved
     * @param right the index of the rightmost bit to be retrieved
     * @return the segment of bits between left and right in success case, -1 in failure case
     */
    public int getSegment(int left, int right)
    {
        if(left < right || right > size || left <= 0 || right < 0)
            return -1;
        else
            return (value >> right) & ((1 << left - right + 1)-1);
    }

    public int getBit(int position){
        return (value >> position) & ((1 << position + 1)-1);
    }

    /**
     * Returns the value of the register
     * @return the value of the register
     */
    public int getValue()
    {
        return value;
    }

    /**
     * Sets the value of the register.
     * Value range = 0 -> (2^size) -1.
     * No negative value.
     * To avoid unexpected result, sent the value in binary representation, where its length <= register size.
     * @param value to be written over the register
     */
    public boolean setValue(int value)
    {
//        if(value < 0 || value > Math.pow(2, size) -1)
//            return false;
//        else {
            this.value = value;
            return true;
//        }
    }

    /**
     * Returns the size of the register in bits
     * @return the size of the register in bits
     */
    public int getSize()
    {
        return size;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Sets register to 0
     */
    public void clear()
    {
        value = 0;
    }

    /**
     * Returns a String representation of the register
     */
    public String toString()
    {
        String r = Integer.toBinaryString(value);
        while(r.length() < size)
            r = "0" + r;
        if(name != null)
            r = name + ": " + r;
        return r + " (DEC = "+value+", HEX = "+ Integer.toHexString(value) +")";
    }


}
