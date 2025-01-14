package test;

import controller.Controller;
import controller.Simulator;
import org.junit.Test;
import units.RegisterFile;

import java.io.FileNotFoundException;

import static org.junit.Assert.assertEquals;

public class BranchInstructionTest {
    Controller controller = new Controller();
    RegisterFile registerFile = controller.simulator.getRegisterFile();


    public BranchInstructionTest() throws FileNotFoundException {
    }

    @Test
    public void testSignUnit(){
        // Example 7-bit input (as an integer)
        int sevenBitInput = 0b1111101; // Example input, must fit in 7 bits (-64 to 63) , -1

        // Sign extend to 32 bits
        int extendedValue = controller.simulator.getInstructionDecodeStage().signExtend7to32(sevenBitInput);
        assertEquals(0xfffffffd, extendedValue);
    }

    @Test
    public void testBranchOnNotEqualInstruction() {
        registerFile.writeRegister(2, 0); // RF(2) = 0
        registerFile.writeRegister(1, 3);
        controller.simulator.getInstructionMemory().setInstruction(0, 0b100_010_010_0000001);// RF(2) = RF(2) + 1
        controller.simulator.getInstructionMemory().setInstruction(1, 0b010_010_001_1111110);// if (RF(2) != RF(1)) jump to address (pc+1+imm) where imm = -2
        controller.simulator.getInstructionMemory().setInstruction(2, Simulator.NOP);
        controller.simulator.getInstructionMemory().setInstruction(3, Simulator.NOP);
        controller.simulator.getInstructionMemory().setInstruction(4, 0b100_010_111_0000001);// RF(7) = RF(2)+1 = 3+1 = 4

        controller.simulator.run();
        registerFile = controller.simulator.getRegisterFile();

        assertEquals(3, registerFile.readRegister(2).getValue());
        assertEquals(3, registerFile.readRegister(1).getValue());
        assertEquals(4, registerFile.readRegister(7).getValue());
    }
    @Test
    public void testBranchOnEqualInstruction() {
        registerFile.writeRegister(2, 3); // RF(2) = 0
        registerFile.writeRegister(1, 1);
        controller.simulator.getInstructionMemory().setInstruction(0, 0b100_010_010_1111111);// RF(2) = RF(2) - 1
        controller.simulator.getInstructionMemory().setInstruction(1, 0b011_010_001_0000011);// if (RF(2) == RF(1)) jump to address (pc+1+imm) where imm = 5
        controller.simulator.getInstructionMemory().setInstruction(2, Simulator.NOP);
        controller.simulator.getInstructionMemory().setInstruction(3, Simulator.NOP);
        controller.simulator.getInstructionMemory().setInstruction(4, 0b111_0000000000000);// jump to PC = 0
        controller.simulator.getInstructionMemory().setInstruction(5, Simulator.NOP);

        controller.simulator.run();
        registerFile = controller.simulator.getRegisterFile();

        assertEquals(1, registerFile.readRegister(2).getValue());
        assertEquals(1, registerFile.readRegister(1).getValue());
    }

    @Test
    public void testJumpInstruction() {
        // initialize register file
        registerFile.writeRegister(7, 3); //load RF(7) = 3
        registerFile.writeRegister(1, 7); //load RF(1) = 7
        // program instructions
        controller.simulator.getInstructionMemory().setInstruction(0,  0b000_001_111_010_0_000); // add RF(2), RF(1), RF(0) <-> RF(2) = RF(1) + RF(7) = 10
        controller.simulator.getInstructionMemory().setInstruction(1, 0b011_001_010_0000101);// if (RF(2) == RF(1)) jump to address (pc+1+imm) where imm
        controller.simulator.getInstructionMemory().setInstruction(2, Simulator.NOP);
        controller.simulator.getInstructionMemory().setInstruction(3, Simulator.NOP);
        controller.simulator.getInstructionMemory().setInstruction(4,  0b100_010_010_1111111); // addi RF(2), RF(2), imm <-> RF(2) = RF(2) - 1
        controller.simulator.getInstructionMemory().setInstruction(5, Simulator.NOP);
        controller.simulator.getInstructionMemory().setInstruction(6,  0b111_0000000000001); // jump 0b10
        controller.simulator.getInstructionMemory().setInstruction(7, Simulator.NOP);

        controller.simulator.run();
        registerFile = controller.simulator.getRegisterFile();

        // loaded register before running the simulation
        assertEquals(3, registerFile.readRegister(7).getValue());
        assertEquals(7, registerFile.readRegister(1).getValue());
        // loaded register after running the simulation
        assertEquals(7, registerFile.readRegister(2).getValue());

    }
}
