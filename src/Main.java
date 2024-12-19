import controller.Controller;
import controller.Simulator;

import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        Controller controller = new Controller();
        // demo
        controller.simulator.getInstructionMemory().setInstruction(0, (short) 0b100_000_001_0000111); // addi RF(1), RF(0), 3 <-> RF(1) = RF(0) + 3
        controller.simulator.getInstructionMemory().setInstruction(1, (short) 0b100_000_010_0000111); // addi RF(2), RF(0), 7<-> RF(2) = RF(0) + 7
        controller.simulator.getInstructionMemory().setInstruction(2, (short) 0b100_000_011_0000100); // addi RF(3), RF(0), 4<-> RF(3) = RF(0) + 4
        controller.simulator.getInstructionMemory().setInstruction(3, (short) 0b100_000_101_0000101); // addi RF(3), RF(0), 4<-> RF(5) = RF(0) + 5
        controller.simulator.getInstructionMemory().setInstruction(4, (short) 0b110_000_001_0000000); // MEM(RF(0)+imm) = RF(1) where imm = 0
        controller.simulator.getInstructionMemory().setInstruction(5, (short) 0b110_001_010_0000000); // MEM(RF(0)+imm) = RF(2) where imm = 1
        controller.simulator.getInstructionMemory().setInstruction(6, (short) 0b110_010_011_0000010); // MEM(RF(0)+imm) = RF(3) where imm = 2

        controller.simulator.run();
    }
}
