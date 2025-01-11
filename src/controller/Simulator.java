package controller;

import stages.*;
import units.DataMemory;
import units.InstructionMemory;
import units.PipelineRegister;
import units.RegisterFile;

import java.util.Arrays;

public class Simulator {
    public static final int NOP = 0xffff0000, EMPTY = -2, DOESNTMATTER = 0xffff0000;

    PipelineRegister IFtoID;
    PipelineRegister IDtoEx;
    PipelineRegister ExToMem;
    PipelineRegister MemToWb;

    InstructionMemory instructionMemory;
    RegisterFile registerFile;
    DataMemory dataMemory;

    InstructionFetchStage instructionFetchStage;
    InstructionDecodeStage instructionDecodeStage;
    ExecutionStage executionStage;
    MemoryStage memoryStage;
    WriteBackStage writeBackStage;

    int[] instructionsNumbers = new int[5];
    int[] tmpInstructionsNumbers = new int[5];
    String[] instructionAction = new String[] { "Fetched", "Decoded", "Executed", "In memory stage", "In write back stage" };


    /**
     * Constructs a new simulator that has four pipeline registers, three data units
     * and five stages (helper simulators for each pipeline stage)
     */
    public Simulator()
    {
        IFtoID = new PipelineRegister(0);
        IDtoEx = new PipelineRegister(1);
        ExToMem = new PipelineRegister(2);
        MemToWb = new PipelineRegister(3);

        instructionMemory = new InstructionMemory(1000);	//1000 instructions, 1000 * 16bit = 16KB (but technically it is 1000 * sizeof(int)
        registerFile = new RegisterFile();
        dataMemory = new DataMemory(16000);			    //16000 * 16 bit = 256 KB (but technically it is 16000 * sizeof(int)

        instructionFetchStage = new InstructionFetchStage(this);
        instructionDecodeStage = new InstructionDecodeStage(this);
        executionStage = new ExecutionStage(this);
        memoryStage = new MemoryStage(this);
        writeBackStage = new WriteBackStage(this);

        Arrays.fill(instructionsNumbers, EMPTY);
        Arrays.fill(tmpInstructionsNumbers, EMPTY);
        instructionsNumbers[0] = 0; // the spark
    }

    /**
     * Runs the simulator
     */
    public void run()
    {
        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        System.out.println("                Start of Program");
        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        int clockCycle = 0;

        while (isBusy())
//        while (clockCycle < 30)
        {
            //Run Stages one by one where in which they do not influence each others until the pipelines get updated by the function updatePipelines()
            instructionFetchStage.run();
            instructionDecodeStage.run();
            executionStage.run();
            memoryStage.run();
            writeBackStage.run();

            updatePipelines();

            print(clockCycle++);

            updateInstructionNumbers();
        }
        System.out.println("##############################################\n");
        System.out.printf("Register File\n^^^^^^^^^^^^^\n%s==============================================\n", registerFile);
        System.out.printf("Data Memory\n^^^^^^^^^^^\n%s\n", dataMemory);
        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        System.out.println("               End of Program");
        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
    }

    public PipelineRegister getIFtoID() { return IFtoID; }
    public PipelineRegister getIDtoEx() { return IDtoEx; }
    public PipelineRegister getExToMem() { return ExToMem;}
    public PipelineRegister getMemToWb() { return MemToWb; }
    public InstructionMemory getInstructionMemory() { return instructionMemory; }
    public RegisterFile getRegisterFile() {	return registerFile; }
    public DataMemory getDataMemory() {	return dataMemory; }
    public InstructionDecodeStage getInstructionDecodeStage() {	return instructionDecodeStage; }
    public MemoryStage getMemoryStage() { return memoryStage; }


    /**
     * Gets the instruction number of a certain stage in the current clock cycle
     * @param stageID the id of the stage to get its current instruction number
     * @return the instruction number of the specified stage
     */
    public int getInstructionNumber(int stageID) { return instructionsNumbers[stageID]; }

    /**
     * Sets the instruction number of a certain stage for the next clock cycle
     * @param stageID the id of the stage to set its next instruction
     * @param instructionNumber the instruction number to be set
     */
    public void setInstructionNumber(int stageID, int instructionNumber)
    {
        tmpInstructionsNumbers[stageID] = instructionNumber;
    }

    /**
     * Updates the instruction numbers of all stages.
     * This method is called when moving to a new clock cycle.
     */
    public void updateInstructionNumbers()
    {
        for(int i = 0; i < 5; ++i)
        {
            instructionsNumbers[i] = tmpInstructionsNumbers[i];
            tmpInstructionsNumbers[i] = EMPTY;
        }
    }

    /**
     * Updates the values of the pipeline registers.
     * This method is called when moving to a new clock cycle.
     */
    private void updatePipelines()
    {
        IFtoID.update();
        IDtoEx.update();
        ExToMem.update();
        MemToWb.update();
    }

    /**
     * Checks whether the processor is executing an instruction in any of its stages.
     * @return
     */
    private boolean isBusy()
    {
        for(int instructionNumber: instructionsNumbers)
            if(instructionNumber != EMPTY)
                return true;
        return false;
    }

    private void print(int clockCycle){
        if(clockCycle > 1)
            System.out.println("##############################################\n");
        System.out.printf("Clock Cycle %2d\n^^^^^^^^^^^^^^\n", clockCycle);
        System.out.println("the current instruction is : \n" + Integer.toBinaryString(instructionMemory.getInstruction(clockCycle)));

        for(int i = 0; i < 5; ++i)
            if(instructionsNumbers[i] >= 0)
                System.out.printf("Instruction %d %s\n", instructionsNumbers[i] + 1, instructionAction[i]);

        System.out.println("\nPipeline Registers\n^^^^^^^^^^^^^^^^^^");
        System.out.printf("Pipeline IF/ID\n***************\n%s================================================\n", IFtoID);
        System.out.printf("Pipeline ID/EX\n***************\n%s================================================\n", IDtoEx);
        System.out.printf("Pipeline EX/MEM\n****************\n%s================================================\n", ExToMem);
        System.out.printf("Pipeline MEM/WB\n****************\n%s================================================\n", MemToWb);
    }
}