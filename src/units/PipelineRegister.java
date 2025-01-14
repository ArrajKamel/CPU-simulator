package units;

import java.util.HashMap;
import java.util.Map.Entry;

public class PipelineRegister {
    private HashMap<String, Register> registers;
    private PipelineRegister tmpRegister;

    /**
     * Constructs a new pipeline register. This register will have a temporary pipeline register
     * to avoid any overlapping within the simulation process.
     * @param type the type of the pipeline register
     */
    public PipelineRegister(int type)
    {
        this(type, false);
    }

    /**
     * Constructs a new pipeline register.
     * It populates the pipeline register with mini registers (segments) according to its type.
     * you can check the Distributions  of the pipeline registers in the attached file (intermediateRegisters.pdf)
     * @param type the type of the pipeline register (IF/ID, ID/EX, ...).
     * @param fake an indicator whether the constructed register is the original one
     * or a temporary (fake) pipeline register.
     */
    public PipelineRegister(int type, boolean fake)
    {
        registers = new HashMap<String, Register>();

        // IFtoID
        if(type == 0)
        {
            registers.put("PC", new Register(16));
            registers.put("Instruction", new Register(16));
        }// IDtoEx
        else if(type == 1)
        {
            //control signals
            registers.put("MemToReg", new Register(1));//bit 0
            registers.put("RegWrite", new Register(1));//bit 1
            registers.put("MemWrite", new Register(1));//bit 2
            registers.put("Branch", new Register(1));//bit 3
            registers.put("ALUOp", new Register(3));//bit 4,5,6
            registers.put("ALUSrc", new Register(1));//bit 7
            registers.put("RegDst", new Register(1));//bit 8

            registers.put("PC", new Register(16));//bit 9 -> 24
            registers.put("ReadData1", new Register(16));//bit 25 -> 40
            registers.put("ReadData2", new Register(16));//bit 41 -> 56
            registers.put("Ext_imm", new Register(16));//bit 57 -> 72
            registers.put("Function", new Register(3));//bit 73 -> 75
            registers.put("RTarget", new Register(3));//bit 76 -> 78
            registers.put("RDestination", new Register(3));//bit 79 -> 81
            registers.put("RSource", new Register(3));// for data forwarding unit
            registers.put("ShiftAmount", new Register(1));//bit 82
        }// ExToMem
        else if(type == 2)
        {
            //control signals
            registers.put("MemToReg", new Register(1));//bit 0
            registers.put("RegWrite", new Register(1));//bit 1
            registers.put("MemWrite", new Register(1));//bit 2
            registers.put("Branch", new Register(1));//bit 3

            registers.put("BranchTargetAddress", new Register(16));//bit 4 -> 19
            registers.put("Zero", new Register(1));//bit 20
            registers.put("ALUResult", new Register(16));//bit 21 -> 36
            registers.put("ReadData2", new Register(16));//bit 37 -> 52
            registers.put("WriteAddress", new Register(3));//bit 53 -> 55
        }// MemToWB
        else
        {
            //control signals
            registers.put("MemToReg", new Register(1));//bit 0
            registers.put("RegWrite", new Register(1));//bit 1

            registers.put("MemoryData", new Register(16));//bit 2 -> 17
            registers.put("ALUResult", new Register(16));//bit 18 -> 33
            registers.put("WriteAddress", new Register(3));//bit 34
        }

        if(!fake)
            tmpRegister = new PipelineRegister(type, true);
    }

    /**
     * Gets the mini register (specific section) from the pipeline register with the passed name.
     * @param registerName (segment name) the name of the mini register
     * @return the mini register with the passed name
     */
    public Register getRegister(String registerName)
    {
        return registers.get(registerName);
    }
    /**
     * Sets the mini register (specific segment) with the specified name in the pipeline register with the passed value.
     * This value will be temporarily stored in a temporary register until pipeline register is updated.
     * @param registerName (segment name) the name of the mini register
     * @param value the new value of the mini register
     */
    public void setRegister(String registerName, int value)
    {
        tmpRegister.getRegister(registerName).setValue(value);
    }

    /**
     * Updates the pipeline register by moving the contents of the mini registers of the temporary
     * pipeline register to those of the current pipeline register.
     */
    public void update()
    {
        for(Entry<String, Register> entry: registers.entrySet())
        {
            String registerName = entry.getKey();
            registers.get(registerName).setValue(tmpRegister.getRegister(registerName).getValue());
            tmpRegister.getRegister(registerName).clear();
        }
    }

    /**
     * Updates the pipeline register so it will contain the same values
     * that were present when the last clock cycle finished.
     */
    public void selfUpdate()
    {
        for(Entry<String, Register> entry: registers.entrySet())
            setRegister(entry.getKey(), entry.getValue().getValue());
    }

    public void clear(){
        for(Entry<String, Register> entry: registers.entrySet())
            setRegister(entry.getKey(), 0);
    }

    /**
     * Returns a string representation of the pipeline register
     */
    public String toString()
    {
        String r = "";
        for(Entry<String, Register> entry: registers.entrySet())
            r += String.format("%s\n %s\n", entry.getKey(), entry.getValue());
        return r;
    }

}
