package stages;
import controller.Simulator;

public class InstructionFetchStage extends Stage{
    int PC ;
    int instruction;

    // incoming
    int PCSrc;
    int jump;
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

        simulator.setInstructionNumber(1, PC);
        //get instruction
        instruction = simulator.getInstructionMemory().getInstruction(PC);
        // get the value of the PC
        PCSrc = simulator.getMemoryStage().PCSrc;
        jump = simulator.getInstructionDecodeStage().jump;
        if(PCSrc == 1 && jump == 0){
            // TODO BRANCH INSTRUCTION
            PC = branchTargetAddress;
        }else if (PCSrc == 0 && jump == 1){
            PC = simulator.getInstructionDecodeStage().jumpAddress;
            // TODO JUMP INSTRUCTION
        }else if (PCSrc == 0 && jump == 0){
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
