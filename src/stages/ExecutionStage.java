package stages;

import controller.Simulator;


public class ExecutionStage extends Stage{
    //values to be written to the next pipe
    int branchTargetAddress;
    int zero ;
    int ALUResult;
    int writeAddress;
    //and the readDate2 which is the same from the previous pipe

    // control signals go forward
    int memToReg;
    int regWrite;
    int memWrite;
    int branch;


    // control signals come from the previous pipe
    int ALUOp; //3 bits
    int ALUSrc;
    int regDst;

    //values from the previous pipe
    int PC;
    int readData1;
    int readData2;
    int Ext_imm;
    int function;
    int rTarget;
    int rDestination;
    int shiftAmount;

    // rs (RSource) for forwarding
    int rSource;

    /**
     * Constructs a new stage
     *
     * @param simulator the simulator to which this stage is associated
     */
    public ExecutionStage(Simulator simulator) {
        super(simulator);
        branchTargetAddress = 0;
        zero = 0 ;
        ALUResult = 0;
        writeAddress = 0;

        memToReg = 0;
        regWrite = 0;
        memWrite = 0;
        branch = 0;

        ALUOp = 0; //3 bits
        ALUSrc = 0;
        regDst = 0;

        PC = 0;
        readData1 = 0;
        readData2 = 0;
        Ext_imm = 0;
        function = 0;
        rTarget = 0;
        rDestination = 0;
        shiftAmount = 0;

        rSource = 0 ;
    }

    @Override
    public void run() {

        if(simulator.getInstructionNumber(2) == Simulator.EMPTY)
        {
            simulator.setInstructionNumber(3, Simulator.EMPTY);
            return;
        }
        if(simulator.getInstructionNumber(2) == Simulator.NOP){
            simulator.setInstructionNumber(3, Simulator.NOP);
            simulator.getExToMem().clear();
            return;
        }

        readFromIDToEX();
        int firstOperand ;
        int secondOperand;

        //calculate the BranchTargetAddress
        branchTargetAddress = PC + Ext_imm;

        // set the operand of the ALU
        // briefly forwarding is get the value, which will be written in the RF, before it is written
        // by bringing it from the memory stage or write back stage, where this value is ready but not written in RF yet
        //and we do not want to wait for it to be written, we need it immediately
        // of course we need the value of rs to check if we should forward data for readData1
        // and we need to check the value of RegWrite s, where we have two of them,
        // one from EXToMem and the second form MemToWB
        // handling forwarding for readData1
        if(simulator.getExToMem().getRegister("RegWrite").getValue() == 1
                && simulator.getExToMem().getRegister("WriteAddress").getValue() == rSource)
            readData1 = simulator.getExToMem().getRegister("ALUResult").getValue();
        else if (simulator.getMemToWb().getRegister("RegWrite").getValue() == 1
                && simulator.getMemToWb().getRegister("WriteAddress").getValue() == rSource)
            readData1 = simulator.getMemToWb().getRegister("MemToReg").getValue() == 0
                    ? simulator.getMemToWb().getRegister("ALUResult").getValue()
                    : simulator.getMemToWb().getRegister("MemoryData").getValue();

        // handling forwarding for readData2
        if(simulator.getExToMem().getRegister("RegWrite").getValue() == 1
                && simulator.getExToMem().getRegister("WriteAddress").getValue() == rTarget)
            readData2 = simulator.getExToMem().getRegister("ALUResult").getValue();
        else if (simulator.getMemToWb().getRegister("RegWrite").getValue() == 1
                && simulator.getMemToWb().getRegister("WriteAddress").getValue() == rTarget)
            readData2 = simulator.getMemToWb().getRegister("MemToReg").getValue() == 0
                    ? simulator.getMemToWb().getRegister("ALUResult").getValue()
                    : simulator.getMemToWb().getRegister("MemoryData").getValue();

        firstOperand = readData1;
        secondOperand = ALUSrc == 1 ? Ext_imm : readData2;

        //fine the required operation for the ALU and perform it and return the result
        Operation operation = findOperation(ALUOp, function);
        ALUResult = ALU(firstOperand, secondOperand, shiftAmount, operation);

        // find the write back address
        writeAddress = regDst == 1 ? rDestination : rTarget;

        //set values to the next pipe
        // values from the previous pipe
        simulator.getExToMem().setRegister("MemToReg", memToReg);
        simulator.getExToMem().setRegister("RegWrite", regWrite);
        simulator.getExToMem().setRegister("MemWrite", memWrite);
        simulator.getExToMem().setRegister("Branch", branch);
        simulator.getExToMem().setRegister("ReadData2", readData2);
        // values from the current stage
        simulator.getExToMem().setRegister("BranchTargetAddress", branchTargetAddress);
        simulator.getExToMem().setRegister("Zero", zero);
        simulator.getExToMem().setRegister("ALUResult", ALUResult);
        simulator.getExToMem().setRegister("WriteAddress", writeAddress);

        simulator.setInstructionNumber(3, simulator.getInstructionNumber(2));
    }

    /**
     * Simulates the behavior of an Arithmetic Logic Unit (ALU) by performing specified
     * operations on given operands. The operation to be performed is determined by the
     * provided {@link Operation} enum value.
     *
     * @param firstOperand The first input operand for the ALU operation.
     * @param secondOperand The second input operand for the ALU operation.
     * @param shiftAmount The shift amount used for shift operations (e.g., SLL, SRL).
     * @param operation The operation to be performed, specified as an {@link Operation} enum.
     *                  Possible values include ADD, SUB, SLL, SRL, AND, OR, XOR, and NON.
     * @return The result of the ALU operation. For the NON operation, it returns 0.
     */
    private int ALU(int firstOperand, int secondOperand, int shiftAmount, Operation operation) {
        int result = 0;
        switch (operation){
            case ADD:
                result = firstOperand + secondOperand;
                break;
            case SUB:
                result = firstOperand - secondOperand;
                break;
            case SLL:
                result = secondOperand << shiftAmount;
                break;
            case SRL:
                result = secondOperand >> shiftAmount;
                break;
            case AND:
                result = firstOperand & secondOperand;
                break;
            case OR:
                result = firstOperand | secondOperand;
                break;
            case XOR:
                result = firstOperand ^ secondOperand;
                break;
            case SLT:
                if((firstOperand - secondOperand) < 0)
                    result = 1 ;
                else
                    result = 0;
                break;
            case BEQ:
                 result = Simulator.DOESNTMATTER;
                if((firstOperand - secondOperand) == 0)
                    zero = 1 ;
                else
                    zero = 0 ;
                break;
            case BNQ:
                result = Simulator.DOESNTMATTER;
                if((firstOperand - secondOperand) != 0)
                    zero = 1 ;
                else
                    zero = 0 ;
                break;
            case NON:
                break;
            default:
                System.out.println("Unknown operation");
        }
        return result;
    }

    /**
     * Determines the appropriate operation to be performed in an Arithmetic Logic Unit (ALU)
     * based on the provided op code and function code.
     *
     * @param op The operation code that specifies the type of instruction.
     * @param function The function code that specifies the detailed operation in the instruction.
     * @return An {@link Operation} enum that represents the ALU operation to be performed.
     *         Possible values include ADD, SUB, SLL, SRL, AND, OR, XOR, SLT, or NON.
     */
    private Operation findOperation(int op, int function) {
        Operation operation = Operation.NON;
        operation = switch (op) {
            case 0b000 -> // R_type we have to check the function
                    switch (function) {
                        case 0b000 -> // addition
                                Operation.ADD;
                        case 0b001 -> // subtraction
                                Operation.SUB;
                        case 0b010 -> // shift left logic SLL
                                Operation.SLL;
                        case 0b011 -> // shift right logic SRL
                                Operation.SRL;
                        case 0b100 -> // AND operation
                                Operation.AND;
                        case 0b101 -> // OR operation
                                Operation.OR;
                        case 0b110 -> // XOR operation
                                Operation.XOR;
                        case 0b111 -> // set on less than SLT
                                Operation.SLT;// first operand is rs, second operand is rt, if the result <0 jump, if not do nothing
                        default -> operation;
                    };
            case 0b001 -> // set on less than immediate
                    Operation.SLT;
            case 0b010 -> // branch on not equal BNQ
                    Operation.BNQ; // in this instruction, to apply the branch moving, the result must not be zero
            case 0b011 -> // branch on equal BEQ
                    Operation.BEQ; // here the result must be zero to apply branch moving
            case 0b100 -> // add immediate addi
                    Operation.ADD;
            case 0b101 -> // load word LW
                    Operation.ADD; // the result is the address to be read from the memory and written on the RF
            case 0b110 -> // store word SW
                    Operation.ADD; // the result is the address to be written over in the memory
            case 0b111 -> // jump nothing for alu to do
                    Operation.NON;
            default -> operation;
        };
        return operation;
    }


    /**
     * Reads and loads various values from the ID to EX pipeline register into the corresponding
     * fields of the execution stage. The method retrieves the control signals, immediate values,
     * register data, and other relevant fields required for execution.
     *
     * The data is sourced from the simulator's ID to EX pipeline register using the specified
     * register names and stored in the appropriate fields of the execution stage.
     *
     * Specifically, it retrieves and stores:
     * - Control signals for ALU operation (`ALUOp`), ALU source (`ALUSrc`), and register destination (`regDst`).
     * - Program counter (`PC`), operand values (`readData1`, `readData2`), immediate value (`Ext_imm`),
     *   function code (`function`), and register target/destination/source.
     * - Shift amount (`shiftAmount`) used in shift operations.
     * - Control signals forwarded to the next pipeline stage such as memory-to-register (`memToReg`),
     *   register write (`regWrite`), memory write (`memWrite`), and branch instruction control (`branch`).
     */
    private void readFromIDToEX() {
        ALUOp = simulator.getIDtoEx().getRegister("ALUOp").getValue();
        ALUSrc = simulator.getIDtoEx().getRegister("ALUSrc").getValue();
        regDst = simulator.getIDtoEx().getRegister("RegDst").getValue();

        PC = simulator.getIDtoEx().getRegister("PC").getValue();
        readData1 = simulator.getIDtoEx().getRegister("ReadData1").getValue();
        readData2 = simulator.getIDtoEx().getRegister("ReadData2").getValue();
        Ext_imm = simulator.getIDtoEx().getRegister("Ext_imm").getValue();
        function = simulator.getIDtoEx().getRegister("Function").getValue();
        rTarget = simulator.getIDtoEx().getRegister("RTarget").getValue();
        rDestination = simulator.getIDtoEx().getRegister("RDestination").getValue();
        shiftAmount = simulator.getIDtoEx().getRegister("ShiftAmount").getValue();

        // signals to be forwarded to the next pipe
        memToReg = simulator.getIDtoEx().getRegister("MemToReg").getValue();
        regWrite = simulator.getIDtoEx().getRegister("RegWrite").getValue();
        memWrite = simulator.getIDtoEx().getRegister("MemWrite").getValue();
        branch = simulator.getIDtoEx().getRegister("Branch").getValue();

        rSource = simulator.getIDtoEx().getRegister("RSource").getValue();
    }

}
