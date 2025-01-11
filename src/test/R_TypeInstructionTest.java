package test;
import controller.Controller;
import controller.Simulator;
import org.junit.Test;
import units.DataMemory;
import units.RegisterFile;
import java.io.FileNotFoundException;
import static org.junit.Assert.assertEquals;

public class R_TypeInstructionTest {
    Controller controller = new Controller();
    RegisterFile registerFile = controller.simulator.getRegisterFile();
    DataMemory dataMemory = controller.simulator.getDataMemory();


    public R_TypeInstructionTest() throws FileNotFoundException {
    }

    @Test // ADD (addition instruction)
    public void testAddInstruction()  {
        // initialize register file
        registerFile.writeRegister(0, 3); //load RF(0) = 3
        registerFile.writeRegister(1, 7); //load RF(1) = 7
        registerFile.writeRegister(5, -7); //load RF(1) = 7
        // program instructions
        controller.simulator.getInstructionMemory().setInstruction(0,  0b000_001_000_010_0_000); // add RF(2), RF(1), RF(0) <-> RF(2) = RF(1) + RF(0) = 10
        controller.simulator.getInstructionMemory().setInstruction(1,  0b000_101_001_011_0_000); // add RF(3), RF(1), RF(5) <-> RF(3) = RF(1) + RF(5) = 0
        controller.simulator.getInstructionMemory().setInstruction(2,  0b000_101_000_100_0_000); // add RF(4), RF(0), RF(5) <-> RF(4) = RF(0) + RF(0) = -4

        controller.simulator.run();
        registerFile = controller.simulator.getRegisterFile();

        // loaded register before running the simulation
        assertEquals(3, registerFile.readRegister(0).getValue());
        assertEquals(7, registerFile.readRegister(1).getValue());
        assertEquals(-7, registerFile.readRegister(5).getValue());
        // loaded register after running the simulation
        assertEquals(10, registerFile.readRegister(2).getValue());
        assertEquals(0, registerFile.readRegister(3).getValue());
        assertEquals(-4, registerFile.readRegister(4).getValue());
    }

    @Test // SUB (subtraction instruction)
    public void testSubInstruction() {
        // initialize register file
        registerFile.writeRegister(0, 3); //load RF(0) = 3
        registerFile.writeRegister(1, 7); //load RF(1) = 7
        registerFile.writeRegister(5, -7);
        // program instructions
        controller.simulator.getInstructionMemory().setInstruction(0, (short) 0b000_000_001_010_0_001); // sub RF(2), RF(0), RF(1) <-> RF(2) = RF(0) - RF(1) = -4
        controller.simulator.getInstructionMemory().setInstruction(1, (short) 0b000_000_101_011_0_001); // sub RF(3), RF(0), RF(5) <-> RF(3) = RF(0) - RF(5) = 10
        controller.simulator.getInstructionMemory().setInstruction(2, (short) 0b000_010_101_100_0_001); // sub RF(4), RF(2), RF(5) <-> RF(4) = RF(2) - RF(5) = 3

        controller.simulator.run();
        registerFile = controller.simulator.getRegisterFile();
        // loaded register before running the simulation
        assertEquals(3, registerFile.readRegister(0).getValue());
        assertEquals(7, registerFile.readRegister(1).getValue());
        assertEquals(-7, registerFile.readRegister(5).getValue());
        // loaded register after running the simulation
        assertEquals(-4, registerFile.readRegister(2).getValue());
        assertEquals(10, registerFile.readRegister(3).getValue());
        assertEquals(3, registerFile.readRegister(4).getValue());
    }
    @Test // SLL (shift left logic instruction)
    public void testSLLInstruction() {
        // initialize register file
        registerFile.writeRegister(0, 3); //load RF(0) = 3
        // program instructions
        controller.simulator.getInstructionMemory().setInstruction(0, (short) 0b000_000_000_001_1_010); // sll RF(1), RF(0) << 1 <-> RF(1) = RF(0) << RF(0)
        controller.simulator.run();
        registerFile = controller.simulator.getRegisterFile();
        assertEquals(3, registerFile.readRegister(0).getValue());
        assertEquals(6, registerFile.readRegister(1).getValue());
    }

    @Test //SRL (shift right logic instruction)
    public void testSRLInstruction() {
        // note : shifting right a negative number will result
        //      • number/2     if the number is even
        //      • number/2 +1  if the number is odd

        // initialize register file
        registerFile.writeRegister(0, 3);   //load RF(0) =  3 = 0b0000000000000011
        registerFile.writeRegister(2, -3);  //load RF(0) = -3 = 0b1111111111111101
        registerFile.writeRegister(4, -16); //load RF(0) = -16= 0b1111111111110000

        // program instructions
        controller.simulator.getInstructionMemory().setInstruction(0, (short) 0b000_000_000_001_1_011); // srl RF(1), RF(0) >> 1 <-> RF(1) = RF(0) >> 1
        controller.simulator.getInstructionMemory().setInstruction(1, (short) 0b000_000_010_011_1_011); // srl RF(3), RF(2) >> 1 <-> RF(3) = RF(2) >> 1
        controller.simulator.getInstructionMemory().setInstruction(2, (short) 0b000_000_100_101_1_011); // srl RF(5), RF(4) >> 1 <-> RF(5) = RF(4) >> 1

        controller.simulator.run();
        registerFile = controller.simulator.getRegisterFile();
        // loaded register before running the simulation
        assertEquals(3, registerFile.readRegister(0).getValue());
        assertEquals(-3, registerFile.readRegister(2).getValue());
        assertEquals(-16, registerFile.readRegister(4).getValue());
        // loaded register after running the simulation
        assertEquals(1, registerFile.readRegister(1).getValue());
        assertEquals(-2, registerFile.readRegister(3).getValue());
        assertEquals(-8, registerFile.readRegister(5).getValue());

    }

    @Test // AND (and instruction)
    public void testANDInstruction() {
        // initialize register file
        registerFile.writeRegister(1, 3);  //load RF(1) = 3  = 0b0011
        registerFile.writeRegister(2, 5);  //load RF(2) = 5  = 0b0101
        registerFile.writeRegister(3, 12); //load RF(3) = 12 = 0b1100
        // program instructions
        controller.simulator.getInstructionMemory().setInstruction(0, (short) 0b000_010_001_100_0_100); // and RF(4) RF(1) RF(2) <-> RF(4) = RF(1) & RF(2)
        controller.simulator.getInstructionMemory().setInstruction(1, (short) 0b000_011_001_101_0_100); // and RF(5) RF(1) RF(3) <-> RF(5) = RF(1) & RF(3)
        controller.simulator.getInstructionMemory().setInstruction(2, (short) 0b000_011_000_110_0_100); // and RF(6) RF(0) RF(3) <-> RF(6) = RF(0) & RF(3)
        controller.simulator.getInstructionMemory().setInstruction(3, (short) 0b000_011_010_111_0_100); // and RF(7) RF(2) RF(3) <-> RF(7) = RF(2) & RF(3)

        controller.simulator.run();
        registerFile = controller.simulator.getRegisterFile();
        // loaded register before running the simulation
        assertEquals(3, registerFile.readRegister(1).getValue());
        assertEquals(5, registerFile.readRegister(2).getValue());
        assertEquals(12, registerFile.readRegister(3).getValue());
        // loaded register after running the simulation
        assertEquals(1, registerFile.readRegister(4).getValue());
        assertEquals(0, registerFile.readRegister(5).getValue());
        assertEquals(0, registerFile.readRegister(6).getValue());
        assertEquals(4, registerFile.readRegister(7).getValue());
    }

    @Test // OR (or instruction)
    public void testORInstruction() {
        // initialize register file
        registerFile.writeRegister(1, 3);  //load RF(1) = 3  = 0b0011
        registerFile.writeRegister(2, 5);  //load RF(2) = 5  = 0b0101
        registerFile.writeRegister(3, 12); //load RF(3) = 12 = 0b1100
        // program instructions
        controller.simulator.getInstructionMemory().setInstruction(0, (short) 0b000_010_001_100_0_101); // or RF(4) RF(1) RF(2) <-> RF(4) = RF(1) | RF(2)
        controller.simulator.getInstructionMemory().setInstruction(1, (short) 0b000_011_001_101_0_101); // or RF(5) RF(1) RF(3) <-> RF(5) = RF(1) | RF(3)
        controller.simulator.getInstructionMemory().setInstruction(2, (short) 0b000_011_000_110_0_101); // or RF(6) RF(0) RF(3) <-> RF(6) = RF(0) | RF(3)
        controller.simulator.getInstructionMemory().setInstruction(3, (short) 0b000_011_010_111_0_101); // or RF(7) RF(2) RF(3) <-> RF(7) = RF(2) | RF(3)

        controller.simulator.run();
        registerFile = controller.simulator.getRegisterFile();
        // loaded register before running the simulation
        assertEquals(3, registerFile.readRegister(1).getValue());
        assertEquals(5, registerFile.readRegister(2).getValue());
        assertEquals(12, registerFile.readRegister(3).getValue());
        // loaded register after running the simulation
        assertEquals(7, registerFile.readRegister(4).getValue());
        assertEquals(15, registerFile.readRegister(5).getValue());
        assertEquals(12, registerFile.readRegister(6).getValue());
        assertEquals(13, registerFile.readRegister(7).getValue());
    }

    @Test // XOR (xor instruction)
    public void testXORInstruction() {
        // initialize register file
        registerFile.writeRegister(1, 3);  //load RF(1) = 3  = 0b0011
        registerFile.writeRegister(2, 5);  //load RF(2) = 5  = 0b0101
        registerFile.writeRegister(3, 13); //load RF(3) = 13 = 0b1101
        // program instructions
        controller.simulator.getInstructionMemory().setInstruction(0, (short) 0b000_010_001_100_0_110); // xor RF(4) RF(1) RF(2) <-> RF(4) = RF(1) ^ RF(2)
        controller.simulator.getInstructionMemory().setInstruction(1, (short) 0b000_011_001_101_0_110); // xor RF(5) RF(1) RF(3) <-> RF(5) = RF(1) ^ RF(3)
        controller.simulator.getInstructionMemory().setInstruction(2, (short) 0b000_011_000_110_0_110); // xor RF(6) RF(0) RF(3) <-> RF(6) = RF(0) ^ RF(3)
        controller.simulator.getInstructionMemory().setInstruction(3, (short) 0b000_011_010_111_0_110); // xor RF(7) RF(2) RF(3) <-> RF(7) = RF(2) ^ RF(3)

        controller.simulator.run();
        registerFile = controller.simulator.getRegisterFile();
        // loaded register after running the simulation
        assertEquals(3, registerFile.readRegister(1).getValue());
        assertEquals(5, registerFile.readRegister(2).getValue());
        assertEquals(13, registerFile.readRegister(3).getValue());
        // loaded register after running the simulation
        assertEquals(6, registerFile.readRegister(4).getValue());
        assertEquals(14, registerFile.readRegister(5).getValue());
        assertEquals(13, registerFile.readRegister(6).getValue());
        assertEquals(8, registerFile.readRegister(7).getValue());
    }

    @Test // SLT (set on less than instruction)
    public void testSLTInstruction() {
        // initialize register file
        registerFile.writeRegister(1, 3);  //load RF(1) = 3  = 0b0011
        registerFile.writeRegister(2, 5);  //load RF(2) = 5  = 0b0101
        // program instructions
        controller.simulator.getInstructionMemory().setInstruction(0, (short) 0b000_001_010_011_0_111); // if(RF(1) < RF(2)) then RF(3) = 1 else RF(3) = 0
        controller.simulator.getInstructionMemory().setInstruction(1, (short) 0b000_010_001_100_0_111); // if(RF(2) < RF(1)) then RF(4) = 1 else RF(4) = 0

        controller.simulator.run();
        registerFile = controller.simulator.getRegisterFile();
        // loaded register before running the simulation
        assertEquals(3, registerFile.readRegister(1).getValue());
        assertEquals(5, registerFile.readRegister(2).getValue());
        // loaded register after running the simulation
        assertEquals(1, registerFile.readRegister(3).getValue());
        assertEquals(0, registerFile.readRegister(4).getValue());
    }

    @Test // ADD (addition instruction)
    public void testNOPInstruction()  {
        // initialize register file
        registerFile.writeRegister(0, 3); //load RF(0) = 3
        registerFile.writeRegister(1, 7); //load RF(1) = 7
        registerFile.writeRegister(5, -7); //load RF(1) = 7
        // program instructions
        controller.simulator.getInstructionMemory().setInstruction(0,  0b000_001_000_010_0_000); // add RF(2), RF(1), RF(0) <-> RF(2) = RF(1) + RF(0) = 10
        controller.simulator.getInstructionMemory().setInstruction(1,  Simulator.NOP); // add RF(2), RF(1), RF(0) <-> RF(2) = RF(1) + RF(0) = 10
        controller.simulator.getInstructionMemory().setInstruction(2,  Simulator.NOP); // add RF(2), RF(1), RF(0) <-> RF(2) = RF(1) + RF(0) = 10
        controller.simulator.getInstructionMemory().setInstruction(3,  Simulator.NOP); // add RF(2), RF(1), RF(0) <-> RF(2) = RF(1) + RF(0) = 10
        controller.simulator.getInstructionMemory().setInstruction(4,  0b000_101_001_011_0_000); // add RF(3), RF(1), RF(5) <-> RF(3) = RF(1) + RF(5) = 0
        controller.simulator.getInstructionMemory().setInstruction(5,  Simulator.NOP); // add RF(2), RF(1), RF(0) <-> RF(2) = RF(1) + RF(0) = 10
        controller.simulator.getInstructionMemory().setInstruction(6,  0b000_101_000_100_0_000); // add RF(4), RF(0), RF(5) <-> RF(4) = RF(0) + RF(0) = -4

        controller.simulator.run();
        registerFile = controller.simulator.getRegisterFile();

        // loaded register before running the simulation
        assertEquals(3, registerFile.readRegister(0).getValue());
        assertEquals(7, registerFile.readRegister(1).getValue());
        assertEquals(-7, registerFile.readRegister(5).getValue());
        // loaded register after running the simulation
        assertEquals(10, registerFile.readRegister(2).getValue());
        assertEquals(0, registerFile.readRegister(3).getValue());
        assertEquals(-4, registerFile.readRegister(4).getValue());
    }

}

// overflow is not handled
// immediate value is not designed to accept negative values
