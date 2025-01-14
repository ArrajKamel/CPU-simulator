package stages;
import controller.Simulator;

public class InstructionFetchStage extends Stage{
    public int PC ;
    int instruction;

    // coming
    int PCSrc;
    int jump;
    int branch;
    int zero;
    int branchTargetAddress;

    /**
     * Constructs a new stage
     *
     * @param simulator the simulator to which this stage is associated
     */
    public InstructionFetchStage(Simulator simulator) {
        super(simulator);
        instruction = 0;
        PC = 0;
        jump = 0;
        PCSrc = 0;
        branchTargetAddress = 0;
    }

    @Override
    public void run() {
        if(simulator.getInstructionNumber(0) == Simulator.EMPTY){
            simulator.setInstructionNumber(1, Simulator.EMPTY);
            return;
        }
        // check if there is BEQ or BNQ instruction
        branch = simulator.getExToMem().getRegister("Branch").getValue();
        zero = simulator.getExToMem().getRegister("Zero").getValue();
        PCSrc = (branch == 1 && zero == 1) ? 1 : 0;
        branchTargetAddress = simulator.getExToMem().getRegister("BranchTargetAddress").getValue();
        jump = simulator.getInstructionDecodeStage().jump;

        simulator.setInstructionNumber(1, PC);
        if(PCSrc == 1 && jump == 0){
            PC = branchTargetAddress;
            simulator.setInstructionNumber(1, PC);
            instruction = simulator.getInstructionMemory().getInstruction(PC);
            PC++;
            PCSrc = 0;
        }else if (PCSrc == 0 && jump == 1){
            PC = simulator.getInstructionDecodeStage().jumpAddress;
            simulator.setInstructionNumber(1, PC);
            instruction = simulator.getInstructionMemory().getInstruction(PC);
            PC++;
            jump = 0;
            simulator.getInstructionDecodeStage().jump = 0;
        }else if (PCSrc == 0 && jump == 0){
            instruction = simulator.getInstructionMemory().getInstruction(PC);
            if(instruction == Simulator.NOP){
                simulator.setInstructionNumber(1, Simulator.NOP);
                PC++;
                if(PC < simulator.getInstructionMemory().getNumberOfInstructions())
                    simulator.setInstructionNumber(0,PC);
                else
                    simulator.setInstructionNumber(0, Simulator.EMPTY);
                return;
            }
            PC++;
        }else {
            System.out.println("impossible, jump and PCSrc are ones");
        }

        // write values to IF/ID pipe
        simulator.getIFtoID().setRegister("PC", PC);
        simulator.getIFtoID().setRegister("Instruction", instruction);

        if(PC < simulator.getInstructionMemory().getNumberOfInstructions())
            simulator.setInstructionNumber(0,PC);
        else
            simulator.setInstructionNumber(0, Simulator.EMPTY);
    }
}