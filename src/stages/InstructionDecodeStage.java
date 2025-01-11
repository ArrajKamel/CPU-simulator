package stages;

import controller.Simulator;
import units.Register;

public class InstructionDecodeStage extends Stage {
    int PC;
    int readAddress1;
    int readAddress2;
    int writeAddress;
    int writeData;
    int opcode;
    int Ext_imm;
    int function;
    int rTarget;
    int rDestination;
    int shiftAmount;

    // to be sent back
    int jump; //this signal does not transfer to the next stage, but it goes to the previous stage (IF)
    int jumpAddress;

    //control signals go forward
    int memToReg;
    int regWrite;
    int memWrite;
    int branch;
    int ALUOp; //3 bits
    int ALUSrc;
    int regDst;

    /**
     * Constructs a new stage
     *
     * @param simulator the simulator to which this stage is associated
     */
    public InstructionDecodeStage(Simulator simulator) {
        super(simulator);
        shiftAmount = 0 ;
    }

    @Override
    public void run() {
        if(simulator.getInstructionNumber(1) == Simulator.EMPTY){
            simulator.setInstructionNumber(2, Simulator.EMPTY);
            return;
        }
        if(simulator.getInstructionNumber(1) == Simulator.NOP){
            simulator.setInstructionNumber(2, Simulator.NOP);
            simulator.getIDtoEx().clear();
            return;
        }
        this.jump = 0;

        // read from the previous pipe
        Register instruction = simulator.getIFtoID().getRegister("Instruction");
        PC = simulator.getIFtoID().getRegister("PC").getValue();

        //decoding the instruction
        instructionDecoding(instruction);

        // extract the control signals
        mainControl(opcode);
//        simulator.getIDtoEx().setRegister("ReadData1", simulator.getRegisterFile().readRegister(readAddress1).getValue());
//        simulator.getIDtoEx().setRegister("ReadData2", simulator.getRegisterFile().readRegister(readAddress2).getValue());

        if(jump == 1){
            jumpAddress = instruction.getSegment(12, 0);
            simulator.setInstructionNumber(0, jumpAddress);
            clearIFToIDStage();
            return;
        }
        // set the rest values to the next pipe
        simulator.getIDtoEx().setRegister("PC", PC);
        simulator.getIDtoEx().setRegister("Ext_imm", signExtend7to32(Ext_imm));
        simulator.getIDtoEx().setRegister("Function", function);
        simulator.getIDtoEx().setRegister("RTarget", rTarget);
        simulator.getIDtoEx().setRegister("RDestination", rDestination);
        simulator.getIDtoEx().setRegister("ShiftAmount", shiftAmount);
        simulator.getIDtoEx().setRegister("RSource", instruction.getSegment(12, 10));// forwarding purpose
        simulator.getIDtoEx().setRegister("ReadData1", simulator.getRegisterFile().readRegister(readAddress1).getValue());
        simulator.getIDtoEx().setRegister("ReadData2", simulator.getRegisterFile().readRegister(readAddress2).getValue());


        simulator.setInstructionNumber(2, simulator.getInstructionNumber(1));
    }

    public static int signExtend7to32(int input) {
        // Mask to extract the sign bit (6th bit in a 7-bit number, 0-indexed)
        int signBit = (input >> 6) & 1;

        if (signBit == 1) {
            // If the sign bit is 1, extend by filling the upper 25 bits with 1s
            return input | 0xFFFFFF80; // 0xFFFFFF80 is a mask with the lower 7 bits 0 and upper 25 bits 1
        } else {
            // If the sign bit is 0, simply return the input as it fits in 32 bits
            return input & 0x7F; // Ensure only the lower 7 bits are used
        }
    }

    private void clearIFToIDStage() {
        simulator.getIFtoID().getRegister("PC").clear();
        simulator.getIFtoID().getRegister("Instruction").clear();
    }

    /**
     * Decodes the given instruction to extract various components required for further processing.
     * The method retrieves segments of the instruction using indices to extract operation codes,
     * register addresses, shift amount, function codes, and immediate values. (see instruction structure)
     *
     * @param instruction the Register object representing the instruction to be decoded
     */
    private void instructionDecoding(Register instruction) {
        opcode = instruction.getSegment(15, 13);
        readAddress1 = instruction.getSegment(12,10);
        readAddress2 = instruction.getSegment(9,7);
        rTarget = instruction.getSegment(9,7);
        rDestination = instruction.getSegment(6,4);
        shiftAmount = instruction.getBit(3);
        function = instruction.getSegment(2,0);
        Ext_imm = instruction.getSegment(6,0);
    }

    /**
     * Sets the control signals based on the provided opcode. This method determines the type of instruction
     * (R-type, I-type, J-type) and updates control signals like MemToReg, RegWrite, MemWrite, Branch, ALUOp,
     * ALUSrc, RegDst, and Jump. The specified control signals are then propagated to the next pipeline stage
     * through the simulator's pipeline register.
     *
     * @param opcode the opcode of the instruction to determine the control signals
     */
    private void mainControl(int opcode){
        switch (opcode){
            case 0b000:// R_type
                memToReg = 0;
                regWrite = 1;
                memWrite = 0;
                branch = 0 ;
                ALUOp = 0b000; //3 bits
                ALUSrc = 0;
                regDst = 1;
                jump = 0;
                break;
            case 0b001:// I_type - set on less than immediate (SLTi)
                memToReg = 0;
                regWrite = 1;
                memWrite = 0;
                branch = 0 ;
                ALUOp = 0b001; //3 bits
                ALUSrc = 1;
                regDst = 0;
                jump = 0;
                break;
            case 0b010:// I_type - branch on not equal (BNQ)
                memToReg = 0;
                regWrite = 0;
                memWrite = 0;
                branch = 1 ;
                ALUOp = 0b010; //3 bits
                ALUSrc = 0;
                regDst = 0;
                jump = 0;
                break;
            case 0b011:// I_type - branch on equal (BEQ)
                memToReg = 0;
                regWrite = 0;
                memWrite = 0;
                branch = 1;
                ALUOp = 0b011; //3 bits
                ALUSrc = 0;
                regDst = 0;
                jump = 0;
                break;
            case 0b100:// I_type - add immediate (addi)
                memToReg = 0;
                regWrite = 1;
                memWrite = 0;
                branch = 0;
                ALUOp = 0b100; //3 bits
                ALUSrc = 1;
                regDst = 0;
                jump = 0;
                break;
            case 0b101:// I_type - load word (LW)
                memToReg = 1;
                regWrite = 1;
                memWrite = 0;
                branch = 0;
                ALUOp = 0b101; //3 bits
                ALUSrc = 1;
                regDst = 0;
                jump = 0;
                break;
            case 0b110:// I_type - store word (SW)
                memToReg = 0;
                regWrite = 0;
                memWrite = 1;
                branch = 0;
                ALUOp = 0b110; //3 bits
                ALUSrc = 1;
                regDst = 0;
                jump = 0;
                break;
            case 0b111: // J_type - jump instruction
                memToReg = 0;
                regWrite = 0;
                memWrite = 0;
                branch = 0;
                ALUOp = 0b111; //3 bits
                ALUSrc = 0;
                regDst = 0;
                jump = 1;
                break;
        }
        //set the control signals to the next pipe except the jump signal
        simulator.getIDtoEx().setRegister("MemToReg", memToReg);
        simulator.getIDtoEx().setRegister("RegWrite", regWrite);
        simulator.getIDtoEx().setRegister("MemWrite", memWrite);
        simulator.getIDtoEx().setRegister("Branch", branch);
        simulator.getIDtoEx().setRegister("ALUOp", ALUOp);
        simulator.getIDtoEx().setRegister("ALUSrc", ALUSrc);
        simulator.getIDtoEx().setRegister("RegDst", regDst);
    }
}
