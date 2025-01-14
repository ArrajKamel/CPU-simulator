package units;

public class InstructionMemory {
    int[] instructions;
    int numberOfInstructions;
    int size;

    /**
     * Constructs a new instruction memory with the specified size.
     * (size * 2) = memory capacity in bytes where each slot's length is 16 bits(2 Byte).
     * @param size the maximum number of instructions the memory can hold.
     */
    public InstructionMemory(int size)
    {
        this.size = size;
        this.instructions = new int[size];
    }

    /**
     * Gets the instruction at the specified index.
     * each address consists of 16 bits which is the length of the instruction
     * @param index the base address of the instruction (0 -> size -1)
     * @return a 16-bit instruction retrieved from index in success case, 0 in failure case or if the corresponding index is empty
     */
    public int getInstruction(int index) {
        if(index < 0 || index >= this.size || instructions[index] == 0)
            return 0 ;
        else
            return instructions[index];
    }

    /**
     * Sets the instruction at the specified index (each index represent an instruction)
     * @param index the base address of the instruction (0 -> size-1)
     * @param value 16-bit instruction to be stored at index (make sure to send a short type value)
     * @return true in success case, false in failure case
     */
    public boolean setInstruction(int index, int value) {
        if(index < 0 || index >= this.size || instructions[index] != 0)
            return false;
        else {
            instructions[index] = value;
            numberOfInstructions++;
            return true;
        }
    }

    /**
     * Gets the number of instructions stored in the instruction memory
     * which is equivalent to the number of used bytes times four
     * @return the number of instructions in the memory
     */
    public int getNumberOfInstructions()
    {
        return numberOfInstructions;
    }

    public void clear(){
        for (int i =0 ;  i < numberOfInstructions ; i++){
            instructions[i] = 0;
        }
        numberOfInstructions = 0;
    }

}
