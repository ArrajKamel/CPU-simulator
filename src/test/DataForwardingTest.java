package test;

import controller.Controller;
import org.junit.Test;
import units.DataMemory;
import units.RegisterFile;

import java.io.FileNotFoundException;

import static org.junit.Assert.assertEquals;

public class DataForwardingTest {
    Controller controller = new Controller();
    RegisterFile registerFile = controller.simulator.getRegisterFile();
    DataMemory dataMemory = controller.simulator.getDataMemory();


    public DataForwardingTest() throws FileNotFoundException {
    }

    @Test // Arithmetic Dependency
    public void TestForwardingFromTheEXStage() {
        registerFile.writeRegister(0,4);
        registerFile.writeRegister(1,7);
        registerFile.writeRegister(4,8);
        registerFile.writeRegister(6,5);

        // add RF(2), RF(1), RF(0) -> RF(2) = RF(1) + RF(0) = 11
        controller.simulator.getInstructionMemory().setInstruction(0, (short) 0b000_001_000_010_0_000);
        // sub RF(3), RF(2), RF(4) -> RF(3) = RF(2) - RF(4) = 11 - 8 = 3
        controller.simulator.getInstructionMemory().setInstruction(1, (short) 0b000_010_100_011_0_001);
        // and RF(5), RF(3), RF(6) -> RF(5) = RF(3) & RF(6) = 0b011 & 0b101 = 0b001 = 1
        controller.simulator.getInstructionMemory().setInstruction(2, (short) 0b000_011_110_101_0_100);

        controller.simulator.run();
        registerFile = controller.simulator.getRegisterFile();

        assertEquals(11, registerFile.readRegister(2).getValue());
        assertEquals(3, registerFile.readRegister(3).getValue());
        assertEquals(1, registerFile.readRegister(5).getValue());

    }

}
