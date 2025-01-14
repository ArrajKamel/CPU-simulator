package units;

public class RegisterFile {
    Register[] registers;

    /**
     * Constructs a new 32-register file
     */
    public RegisterFile()
    {
        registers = new Register[8];
        for(int i = 0; i < 8; ++i)
            registers[i] = new Register(16);
    }

    /**
     * Read the register at the specified index
     * @param index the index of the register to be read
     * @return the register at the given index
     */
    public Register readRegister(int index)
    {
        return registers[index];
    }

    /**
     * Write the passed value to the register at the specified index
     * @param index the index of the register to write at
     * @param value the value to be written
     */
    public void writeRegister(int index, int value)
    {
        if(index < 0 || index > 7)
            System.out.println("index of register to be written is out of bounds");
        else
            registers[index].setValue(value);
    }

    /**
     * Returns a string representation of the register file
     */
    public String toString()
    {
        String r = "";
        for(int i = 0; i < 8; ++i)
            r += "RF("+i+")"+registers[i].toString() + "\n";
        return r;
    }
}
