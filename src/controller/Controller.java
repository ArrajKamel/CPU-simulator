package controller;

import java.io.FileNotFoundException;

public class Controller {
    public Simulator simulator;

    public Controller() throws FileNotFoundException {
        simulator = new Simulator();
    }

    public void setInstruction(int instructionNumber, int instructionValue) {
        simulator.getInstructionMemory().setInstruction(instructionNumber, instructionValue);
    }
}