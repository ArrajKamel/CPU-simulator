import controller.Controller;
import controller.Simulator;

import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        Controller controller = new Controller();
        controller.simulator.getIFtoID().setRegister("Instruction", 5);
        controller.simulator.getIFtoID().setRegister("PC", 10);
        controller.simulator.getIFtoID().update();
        System.out.println(controller.simulator.getIFtoID());
        controller.simulator.getIFtoID().clear();
        controller.simulator.getIFtoID().update();
        System.out.println(controller.simulator.getIFtoID());


//        // demo
//        controller.simulator.getInstructionMemory().setInstruction(0, (short) 0b100_000_001_0000111); // addi RF(1), RF(0), 3 <-> RF(1) = RF(0) + 3
//        controller.simulator.getInstructionMemory().setInstruction(1, (short) 0b100_000_010_0010000); // addi RF(2), RF(1), 1 <-> RF(2) = RF(1) + 1
//        controller.simulator.getInstructionMemory().setInstruction(2, (short) 0b100_010_011_0000101); // addi RF(2), RF(1), 1 <-> RF(2) = RF(1) + 1
//        controller.simulator.getInstructionMemory().setInstruction(3, (short) 0b000_011_010_100_0_000); // add RF(0), RF(1), RF(2) <-> RF(0) = RF(1) + RF(2)
//        controller.simulator.getInstructionMemory().setInstruction(4, (short) 0b100_000_101_0000001); // addi RF(2), RF(1), 1 <-> RF(2) = RF(1) + 1
//        controller.simulator.getInstructionMemory().setInstruction(5, (short) 0b000_100_101_110_0_001); // add RF(0), RF(1), RF(2) <-> RF(0) = RF(1) + RF(2)
//        controller.simulator.getInstructionMemory().setInstruction(6, (short) 0b000_000_101_101_1_010); // add RF(0), RF(1), RF(2) <-> RF(0) = RF(1) + RF(2)
//        controller.simulator.getInstructionMemory().setInstruction(7, (short) 0b000_000_001_001_1_011); // add RF(0), RF(1), RF(2) <-> RF(0) = RF(1) + RF(2)
//        controller.simulator.getInstructionMemory().setInstruction(8, (short) 0b000_100_011_111_0_100); // add RF(0), RF(1), RF(2) <-> RF(0) = RF(1) + RF(2)
//        controller.simulator.getInstructionMemory().setInstruction(9, (short) 0b000_110_111_111_0_101); // add RF(0), RF(1), RF(2) <-> RF(0) = RF(1) + RF(2)
//        controller.simulator.getInstructionMemory().setInstruction(10, (short) 0b000_100_011_111_0_110); // add RF(0), RF(1), RF(2) <-> RF(0) = RF(1) + RF(2)
//        controller.simulator.getInstructionMemory().setInstruction(11, (short) 0b000_110_111_000_0_111); // add RF(0), RF(1), RF(2) <-> RF(0) = RF(1) + RF(2)
//
////        controller.simulator.getInstructionMemory().setInstruction(3, (short) 0b000_010_001_000_0_000); // add RF(0), RF(1), RF(2) <-> RF(0) = RF(1) + RF(2)
////        controller.simulator.getInstructionMemory().setInstruction(4, (short) 0b000_000_001_011_0_001); // add RF(0), RF(1), RF(2) <-> RF(0) = RF(1) + RF(2)
//
////        controller.simulator.getInstructionMemory().setInstruction(1, (short) 0b100_000_010_0000111); // addi RF(2), RF(0), 7<-> RF(2) = RF(0) + 7
//
////        controller.simulator.getInstructionMemory().setInstruction(2, (short) 0b100_000_011_0000100); // addi RF(3), RF(0), 4<-> RF(3) = RF(0) + 4
////        controller.simulator.getInstructionMemory().setInstruction(3, (short) 0b100_000_101_0000101); // addi RF(3), RF(0), 4<-> RF(5) = RF(0) + 5
////        controller.simulator.getInstructionMemory().setInstruction(4, (short) 0b110_000_001_0000000); // MEM(RF(0)+imm) = RF(1) where imm = 0
////        controller.simulator.getInstructionMemory().setInstruction(5, (short) 0b110_001_010_0000000); // MEM(RF(0)+imm) = RF(2) where imm = 1
////        controller.simulator.getInstructionMemory().setInstruction(6, (short) 0b110_010_011_0000010); // MEM(RF(0)+imm) = RF(3) where imm = 2
//
//        controller.simulator.run();
    }
}
