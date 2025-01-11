package test;

import controller.Controller;
import controller.Simulator;
import org.junit.Test;
import units.DataMemory;
import units.RegisterFile;

import java.io.FileNotFoundException;

import static org.junit.Assert.assertEquals;

public class I_TypeInstructionTest {
    Controller controller = new Controller();
    RegisterFile registerFile = controller.simulator.getRegisterFile();
    DataMemory dataMemory = controller.simulator.getDataMemory();


    public I_TypeInstructionTest() throws FileNotFoundException {
    }

    @Test // ADDi (add immediate instruction)
    public void testAddiInstruction() {
        // program instructions
        controller.simulator.getInstructionMemory().setInstruction(0, (short) 0b100_000_001_0000011); // addi RF(1), RF(0), 3 <-> RF(1) = RF(0) + 3 -> RF(1) = 3
        controller.simulator.getInstructionMemory().setInstruction(1, (short) 0b100_001_010_0000101); // addi RF(2), RF(1), 5 <-> RF(2) = RF(1) + 5 -> RF(2) = 8
        controller.simulator.getInstructionMemory().setInstruction(2, (short) 0b100_010_011_1111000); // addi RF(3), RF(1), -8 <-> RF(2) = RF(1) + 5 -> RF(2) = 8-8=0

        controller.simulator.run();
        registerFile = controller.simulator.getRegisterFile();

        assertEquals(0, registerFile.readRegister(0).getValue());
        assertEquals(3, registerFile.readRegister(1).getValue());
        assertEquals(8, registerFile.readRegister(2).getValue());
        assertEquals(0, registerFile.readRegister(3).getValue());
    }

    @Test // SLTi (set on less than immediate instruction)
    public void testSLTiInstruction() {
        // initialize register file
        registerFile.writeRegister(1, 5);  //load RF(1) = 5  = 0b0101
        // program instructions
        controller.simulator.getInstructionMemory().setInstruction(0, (short) 0b001_001_010_0000110); // if(RF(1) < imm) then RF(2) = 1 else RF(2) = 0
        controller.simulator.getInstructionMemory().setInstruction(1, (short) 0b001_001_011_0000011); // if(RF(2) < imm) then RF(3) = 1 else RF(3) = 0

        controller.simulator.run();
        registerFile = controller.simulator.getRegisterFile();
        // loaded register after running the simulation
        assertEquals(1, registerFile.readRegister(2).getValue());
        assertEquals(0, registerFile.readRegister(3).getValue());
    }

    @Test // LW (load word instruction)
    public void testLWInstruction() {
        // initialize data memory and register file
        registerFile.writeRegister(1, 3);  //load RF(1) = 5  = 0b0101
        dataMemory.write(0, 0b1111);     //store in dataMemory M[0] = 15
        dataMemory.write(1, 0b1110);     //store in dataMemory M[1] = 14
        dataMemory.write(3, 0b1101);     //store in dataMemory M[3] = 13
        dataMemory.write(4, 0b1100);     //store in dataMemory M[4] = 12
        // program instructions
        controller.simulator.getInstructionMemory().setInstruction(0, (short) 0b101_000_010_0000000); // RF(2) <- M[RF(0) + imm]  <-> RF(2) = M[0 + 0] = 15
        controller.simulator.getInstructionMemory().setInstruction(1, (short) 0b101_000_011_0000001); // RF(3) <- M[RF(0) + imm]  <-> RF(3) = M[0 + 1] = 14
//        controller.simulator.getInstructionMemory().setInstruction(2, (short) Simulator.NOP);
        controller.simulator.getInstructionMemory().setInstruction(2, (short) 0b101_001_100_0000000); // RF(4) <- M[RF(1) + imm]  <-> RF(4) = M[3 + 0] = 13
        controller.simulator.getInstructionMemory().setInstruction(3, (short) 0b101_001_101_0000001); // RF(5) <- M[RF(1) + imm]  <-> RF(5) = M[3 + 1] = 12

        controller.simulator.run();
        registerFile = controller.simulator.getRegisterFile();
        // loaded registers before running the simulation
        assertEquals(0, registerFile.readRegister(0).getValue());
        assertEquals(3, registerFile.readRegister(1).getValue());
        // loaded register after LW instruction
        assertEquals(15, registerFile.readRegister(2).getValue());
        assertEquals(14, registerFile.readRegister(3).getValue());
        assertEquals(13, registerFile.readRegister(4).getValue());
        assertEquals(12, registerFile.readRegister(5).getValue());
    }

    @Test // SW (store word instruction)
    public void testSWInstruction() {
        // initialize register file
        registerFile.writeRegister(1, 5);  //load RF(1) = 5  = 0b0101
        registerFile.writeRegister(2, 10); //load RF(2) = 10 = 0b1010
        registerFile.writeRegister(3, 3);  //load RF(3) = 3  = 0b0011
        registerFile.writeRegister(4, -15); //load RF(4) = 15 = 0b1111
        // program instructions
        controller.simulator.getInstructionMemory().setInstruction(0, (short) 0b110_000_001_0000000); // M[RF(0) + imm] <- RF(1) <-> M[0 + 0] = 5  = 0b0101
        controller.simulator.getInstructionMemory().setInstruction(1, (short) 0b110_000_010_0000001); // M[RF(0) + imm] <- RF(2) <-> M[0 + 1] = 10 = 0b1010
        controller.simulator.getInstructionMemory().setInstruction(2, (short) 0b110_011_100_0000001); // M[RF(0) + imm] <- RF(4) <-> M[3 + 1] = 15 = 0b1111
        controller.simulator.run();
        dataMemory = controller.simulator.getDataMemory();
        // loaded registers before running the simulation
        assertEquals(5, registerFile.readRegister(1).getValue());
        assertEquals(10, registerFile.readRegister(2).getValue());
        assertEquals(3, registerFile.readRegister(3).getValue());
        assertEquals(-15, registerFile.readRegister(4).getValue());
        // loaded registers before running the simulation
        assertEquals(5, dataMemory.read(0));
        assertEquals(10, dataMemory.read(1));
        assertEquals(0, dataMemory.read(2));
        assertEquals(0, dataMemory.read(3));
        assertEquals(-15, dataMemory.read(4));
    }

    @Test // SW (store word instruction)
    public void testNOPInstruction() {
        // initialize register file
        registerFile.writeRegister(1, 5);  //load RF(1) = 5  = 0b0101
        registerFile.writeRegister(2, 10); //load RF(2) = 10 = 0b1010
        registerFile.writeRegister(3, 3);  //load RF(3) = 3  = 0b0011
        registerFile.writeRegister(4, -15); //load RF(4) = 15 = 0b1111
        // program instructions
        controller.simulator.getInstructionMemory().setInstruction(0, 0b110_000_001_0000000); // M[RF(0) + imm] <- RF(1) <-> M[0 + 0] = 5  = 0b0101
        controller.simulator.getInstructionMemory().setInstruction(1, Simulator.NOP);
        controller.simulator.getInstructionMemory().setInstruction(2, 0b110_000_010_0000001); // M[RF(0) + imm] <- RF(2) <-> M[0 + 1] = 10 = 0b1010
        controller.simulator.getInstructionMemory().setInstruction(3, Simulator.NOP);
        controller.simulator.getInstructionMemory().setInstruction(4, 0b110_011_100_0000001); // M[RF(0) + imm] <- RF(4) <-> M[3 + 1] = 15 = 0b1111
        controller.simulator.run();
        dataMemory = controller.simulator.getDataMemory();
        // loaded registers before running the simulation
        assertEquals(5, registerFile.readRegister(1).getValue());
        assertEquals(10, registerFile.readRegister(2).getValue());
        assertEquals(3, registerFile.readRegister(3).getValue());
        assertEquals(-15, registerFile.readRegister(4).getValue());
        // loaded registers before running the simulation
        assertEquals(5, dataMemory.read(0));
        assertEquals(10, dataMemory.read(1));
        assertEquals(0, dataMemory.read(2));
        assertEquals(0, dataMemory.read(3));
        assertEquals(-15, dataMemory.read(4));
    }
}
