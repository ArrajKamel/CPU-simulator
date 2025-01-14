package stages;

import controller.Simulator;

public class WriteBackStage extends Stage{

    int memToReg;
    int memoryData;
    int ALUResult;
    int writeAddress;
    // stage output
    int writeData;

    /**
     * Constructs a new stage
     *
     * @param simulator the simulator to which this stage is associated
     */
    public WriteBackStage(Simulator simulator) {
        super(simulator);
    }

    @Override
    public void run() {
        if(simulator.getInstructionNumber(4) == Simulator.EMPTY)
            return;
        if(simulator.getInstructionNumber(4) == Simulator.NOP)
            return;

        // if regWrite is zero then this stage does nothing
        if(simulator.getMemToWb().getRegister("RegWrite").getValue() == 1){
            readFromMEMToWB();
            // the only work here is to determine the value which has to be written back to RF
            writeData = (memToReg == 1) ? memoryData : ALUResult;
            simulator.getRegisterFile().writeRegister(writeAddress, writeData);
        }
    }
    /**
     * Reads and stores values from the MEM/WB pipeline register.
     * These values are used during the Write-Back stage of the simulator pipeline.
     *
     * The method retrieves the values for the following:
     * - MemToReg: Determines if the value to write back comes from memory or the ALU.
     * - MemoryData: The data obtained from memory during the MEM stage.
     * - ALUResult: The result of the arithmetic or logic operation performed in the EX stage.
     * - WriteAddress: The destination register address for the Write-Back stage.
     */
    private void readFromMEMToWB() {
        memToReg = simulator.getMemToWb().getRegister("MemToReg").getValue();
        memoryData = simulator.getMemToWb().getRegister("MemoryData").getValue();
        ALUResult = simulator.getMemToWb().getRegister("ALUResult").getValue();
        writeAddress = simulator.getMemToWb().getRegister("WriteAddress").getValue();
    }
}
