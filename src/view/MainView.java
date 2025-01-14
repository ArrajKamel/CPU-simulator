package view;

import controller.Controller;
import controller.Simulator;
import units.InstructionMemory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainView extends JFrame {
    private int pc = 0 ;
    InstructionMemory instructions = new InstructionMemory(100);
    private int clockCycle = 0;
    private boolean isRunning = false ;
    private int instructionNumber = 0 ;
    private Controller controller;
    public MainView(Controller controller) {
        this.controller = controller;
    }
    JPanel bottomRightPanel = new JPanel();
    JFrame mainFrame = new JFrame("CPU simulator: MIPS Pipeline Architecture");
    JPanel mainPanel = new JPanel();
    JLabel[] pipesLabel = new JLabel[4];
    JPanel[] verticalPanels = new JPanel[4];
    JTable instructionsTable;
    DefaultTableModel tableModel;
    JButton addButton, deleteButton, updateButton, nopButton, uploadButton, resetButton;

    JPanel registerFilePanel = new JPanel();
    JLabel rfLabel = new JLabel("Register File");
    JLabel registerFileLabel = new JLabel();

    JPanel dataMemoryPanel = new JPanel();
    JLabel dmLabel = new JLabel("Data Memory");
    JLabel dataMemoryLabel = new JLabel();

    JPanel instructionStatusPanel = new JPanel();
    JLabel[] instructionStatusLabel = new JLabel[5];

    JPanel pcPanel = new JPanel();
    JLabel pcLabel = new JLabel("PC: ");
    JLabel pcValueLabel = new JLabel();
    JLabel clockCycleLabel = new JLabel("Clock Cycle: ");
    JLabel clockCycleValueLabel = new JLabel();


    public void view(){
        //init mainFrame
        mainFrame.setSize(1650 , 950);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setLayout(null);
        mainFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);

        //inti mainPanel
        mainPanel.setBounds(0 , 0  ,1650 , 950);
        mainPanel.setBackground(Color.darkGray);
        mainPanel.setLayout(null);
        mainFrame.add(mainPanel);

        // Define font for labels
        Font labelFont = new Font("Arial", Font.BOLD, 16);
        String[] labelsText = {"IF/ID", "ID/EX", "EX/MEM", "MEM/WB"};
        int xPosition = 50;
        for (int i = 0; i < pipesLabel.length; i++) {
            // Create a panel for each section
            if(i == 0){
                verticalPanels[i] = new JPanel();
                verticalPanels[i].setBounds(xPosition, 50, 250, 150);
                verticalPanels[i].setBackground(Color.LIGHT_GRAY);
                verticalPanels[i].setLayout(null);
            }else {
                verticalPanels[i] = new JPanel();
                verticalPanels[i].setBounds(xPosition, 50, 250, 650);
                verticalPanels[i].setBackground(Color.LIGHT_GRAY);
                verticalPanels[i].setLayout(null);
            }

            // Add the label at the top
            pipesLabel[i] = new JLabel(labelsText[i], SwingConstants.CENTER);
            pipesLabel[i].setBounds(0, 0, 250, 30);
            pipesLabel[i].setForeground(Color.BLACK);
            pipesLabel[i].setFont(labelFont);
            verticalPanels[i].add(pipesLabel[i]);

            mainPanel.add(verticalPanels[i]);
            xPosition += 300;
        }

        // Create the table for program instructions
        JLabel tableTitle = new JLabel("Program Instructions", SwingConstants.CENTER);
        tableTitle.setBounds(1250, 50, 380, 30);

        tableTitle.setForeground(Color.WHITE);
        tableTitle.setFont(new Font("Arial", Font.BOLD, 20));
        mainPanel.add(tableTitle);

        tableModel = new DefaultTableModel(new Object[]{"Nr.", "Instruction", "Assembly"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        instructionsTable = new JTable(tableModel);

        instructionsTable.getColumnModel().getColumn(0).setPreferredWidth(30);
        instructionsTable.getColumnModel().getColumn(1).setPreferredWidth(180);
        instructionsTable.getColumnModel().getColumn(2).setPreferredWidth(150);

        instructionsTable.setRowHeight(25);
        instructionsTable.getTableHeader().setReorderingAllowed(false);
        instructionsTable.getTableHeader().setResizingAllowed(false);

        JScrollPane tableScrollPane = new JScrollPane(instructionsTable);
        tableScrollPane.setBounds(1250, 100, 350, 400);
        mainPanel.add(tableScrollPane);

        // Add buttons below the table
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBounds(1250, 520, 350, 200);
        buttonPanel.setLayout(new GridLayout(2, 3, 10, 10));
        buttonPanel.setBackground(Color.DARK_GRAY);

        addButton = createStyledButton("Add", new Color(34, 139, 34), new Color(50, 205, 50));
        deleteButton = createStyledButton("Delete", new Color(139, 0, 0), new Color(205, 0, 0));
        updateButton = createStyledButton("Update", new Color(0, 0, 139), new Color(65, 105, 225));
        nopButton = createStyledButton("NOP", new Color(184, 134, 11), new Color(255, 215, 0));
        uploadButton = createStyledButton("Upload", new Color(25, 25, 112), new Color(100, 149, 237));
        resetButton = createStyledButton("Reset", new Color(155, 0,0), new Color(255, 105, 30));

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(nopButton);
        buttonPanel.add(uploadButton);
        buttonPanel.add(resetButton);

        mainPanel.add(buttonPanel);

        addButton.addActionListener(e -> openAddInstructionDialog());
        nopButton.addActionListener(e -> nopButtonClickHandel());
        deleteButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = instructionsTable.getSelectedRow();

                if (selectedRow >= 0) {
                    tableModel.removeRow(selectedRow);
                    for (int i = 0; i < tableModel.getRowCount(); i++) {
                        tableModel.setValueAt(i , i, 0); // Update the "Nr." column
                    }
                } else {
                    JOptionPane.showMessageDialog(
                            mainFrame,
                            "Please select a row to delete.",
                            "No Row Selected",
                            JOptionPane.WARNING_MESSAGE
                    );
                }
            }
        });
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Confirm before clearing the table
                int confirm = JOptionPane.showConfirmDialog(
                        mainFrame,
                        "Are you sure you want to clear the table?",
                        "Clear Table",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    // Clear all rows from the table
                    tableModel.setRowCount(0);
                    controller.simulator.getInstructionMemory().clear();
                    instructions.clear();
                    instructionNumber = 0 ;
                }
            }
        });
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = instructionsTable.getSelectedRow();

                if (selectedRow >= 0) {
                    // Get current values of the selected row
                    String currentNr = tableModel.getValueAt(selectedRow, 0).toString();
                    String currentInstruction = tableModel.getValueAt(selectedRow, 1).toString();
                    String currentAssembly = tableModel.getValueAt(selectedRow, 2).toString();

                    if(currentAssembly.equals("NOP")){
                        JOptionPane.showMessageDialog(
                                mainFrame,
                                "you cannot update NOP instruction.",
                                "Be careful",
                                JOptionPane.WARNING_MESSAGE
                        );
                        return;
                    }
                    // Open a dialog to update the values
                    openUpdateDialog(selectedRow, currentNr, currentInstruction, currentAssembly);
                } else {
                    // Show a warning if no row is selected
                    JOptionPane.showMessageDialog(
                            mainFrame,
                            "Please select a row to update.",
                            "No Row Selected",
                            JOptionPane.WARNING_MESSAGE
                    );
                }
            }
        });
        uploadButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                controller.simulator.getRegisterFile().writeRegister(2, 3); // RF(2) = 0
                controller.simulator.getRegisterFile().writeRegister(1, 1);
                instructions.setInstruction(0, 0b100_010_010_1111111);// RF(2) = RF(2) - 1
                instructions.setInstruction(1, 0b011_010_001_0000011);// if (RF(2) == RF(1)) jump to address (pc+1+imm) where imm = 5
                instructions.setInstruction(2, Simulator.NOP);
                instructions.setInstruction(3, Simulator.NOP);
                instructions.setInstruction(4, 0b111_0000000000000);// jump to PC = 0
                instructions.setInstruction(5, Simulator.NOP);
                controller.simulator.getInstructionMemory().clear();
                controller.simulator.setInstructionMemory(instructions);
                instructionNumber = 0 ;
                for (int i =0 ; i < instructions.getNumberOfInstructions() ; i++){
                    if(instructions.getInstruction(instructionNumber) < 0){
                        tableModel.addRow(new Object[]{instructionNumber,"NOP"});
                        instructionNumber++;
                    }
                    else {
                        tableModel.addRow(new Object[]{instructionNumber, Integer.toBinaryString(instructions.getInstruction(instructionNumber))});
                        instructionNumber++;
                    }
                }
                JOptionPane.showMessageDialog(
                        mainFrame,
                        "done",
                        "instructions are set.",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        });

        // Panel for bottom-right buttons
        bottomRightPanel.setBounds(1250, 850, 350, 50);
        bottomRightPanel.setLayout(new GridLayout(1, 3, 10, 10));
        bottomRightPanel.setBackground(Color.DARK_GRAY);
        JButton clockButton = createStyledButton("Clock++", new Color(0, 128, 128), new Color(0, 255, 255));
        JButton runButton = createStyledButton("Run", new Color(34, 139, 34), new Color(50, 205, 50));
        JButton terminateButton = createStyledButton("Terminate", new Color(139, 0, 0), new Color(205, 0, 0));

        bottomRightPanel.add(clockButton);
        bottomRightPanel.add(runButton);
        bottomRightPanel.add(terminateButton);

        runButton.addActionListener(e -> {
            if(controller.simulator.getInstructionMemory().getNumberOfInstructions() != 0 ) {
                if (!isRunning) {
                    System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
                    System.out.println("                Start of Program");
                    System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
                    isRunning = true;
                    //add the instruction status
                    instructionStatusPanel.setBounds(50, 770, 850, 70);
                    instructionStatusPanel.setBackground(Color.LIGHT_GRAY);
                    instructionStatusPanel.setLayout(null);
                    mainPanel.add(instructionStatusPanel);
                } else {
                    JOptionPane.showMessageDialog(
                            mainFrame,
                            "the program is already running",
                            "miss click?",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }
            }
            else {
                JOptionPane.showMessageDialog(
                        mainFrame,
                        "please set some instructions.n",
                        "miss click?",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        });
        clockButton.addActionListener(e -> {
            if (isRunning && controller.simulator.isBusy()) {
                controller.simulator.getInstructionFetchStage().run();
                controller.simulator.getInstructionDecodeStage().run();
                controller.simulator.getExecutionStage().run();
                controller.simulator.getMemoryStage().run();
                controller.simulator.getWriteBackStage().run();

                controller.simulator.updatePipelines();
                controller.simulator.print(clockCycle++);

                controller.simulator.updateInstructionNumbers();

                // get the pc value
                pc = controller.simulator.getInstructionFetchStage().PC;
                pcValueLabel.setBounds(70, 10, 40, 40);
                pcValueLabel.setForeground(Color.BLACK);
                pcValueLabel.setFont(labelFont);
                pcValueLabel.setText(String.valueOf(pc));
                pcPanel.add(pcValueLabel);

                clockCycleValueLabel.setBounds(110, 45, 30, 40);
                clockCycleValueLabel.setForeground(Color.BLACK);
                clockCycleValueLabel.setFont(labelFont);
                clockCycleValueLabel.setText(String.valueOf(pc));
                pcPanel.add(clockCycleValueLabel);

                // write the pipeline content to the panels
                updateVerticalPanelContent(0,controller.simulator.getIFtoID().toString());
                updateVerticalPanelContent(1,controller.simulator.getIDtoEx().toString());
                updateVerticalPanelContent(2,controller.simulator.getExToMem().toString());
                updateVerticalPanelContent(3,controller.simulator.getMemToWb().toString());

                for(int i = 0 ; i < 4 ; i++){
                    verticalPanels[i].add(pipesLabel[i]);
                }

                // Update Register File content directly
                String registerFileContent = "<html>" + controller.simulator.getRegisterFile().toString().replace("\n", "<br>") + "</html>";
                registerFileLabel.setText(registerFileContent);

                registerFilePanel.revalidate();
                registerFilePanel.repaint();

                // Update data memory content directly
                String dataMemoryContent = "<html>" + controller.simulator.getDataMemory().toString().replace("\n", "<br>") + "</html>";
                dataMemoryLabel.setText(dataMemoryContent);

                dataMemoryPanel.revalidate();
                dataMemoryPanel.repaint();

                instructionStatusPanel.removeAll();
                int x = 10;
                for (int i = 0; i < 5; i++) {
                    if (controller.simulator.getInstructionNumber(i) >= 0) {
                        instructionStatusLabel[i] = new JLabel();
                        instructionStatusLabel[i].setBounds(x, 10, 170, 30); // Adjust width for longer text
                        instructionStatusLabel[i].setForeground(Color.BLACK);
                        String instructionStatus = String.format(
                                "  %d: %s  ",
                                controller.simulator.getInstructionNumber(i),
                                controller.simulator.getInstructionAction()[i]
                        );
                        instructionStatusLabel[i].setText(instructionStatus);
                        instructionStatusPanel.add(instructionStatusLabel[i]);
                        x += 170;
                    }
                }
                instructionStatusPanel.revalidate();
                instructionStatusPanel.repaint();
            }
            else if (!controller.simulator.isBusy()) {
                isRunning = false;
                JOptionPane.showMessageDialog(
                        mainFrame,
                        "No instructions to execute. The simulation is idle.",
                        "Clock++",
                        JOptionPane.INFORMATION_MESSAGE
                );
                terminationMessage();
            }

        });
        terminateButton.addActionListener(e -> {
            if(isRunning) {
                isRunning = false;
                terminationMessage();
            }
            else {
                JOptionPane.showMessageDialog(
                        mainFrame,
                        "the program is not running",
                        "miss click??",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
            // reset the simulator
            instructions = controller.simulator.getInstructionMemory();
            controller.simulator = new Simulator();
            controller.simulator.setInstructionMemory(instructions);
            // clear the instruction status panel
            instructionStatusPanel.removeAll();
            instructionStatusPanel.revalidate();
            instructionStatusPanel.repaint();

        });
        mainPanel.add(bottomRightPanel);

        // add register file panel
        registerFilePanel.setBounds(50, 230, 270, 250);
        registerFilePanel.setBackground(Color.LIGHT_GRAY);
        registerFilePanel.setLayout(null);

        rfLabel.setBounds(10, 10, 200, 50);
        rfLabel.setFont(labelFont);
        rfLabel.setForeground(Color.BLACK);
        registerFilePanel.add(rfLabel);

        registerFileLabel.setBounds(10, 60, 230, 140);
        registerFileLabel.setForeground(Color.BLACK);
        String registerFileContent = "<html>" + controller.simulator.getRegisterFile().toString().replace("\n", "<br>") + "</html>";
        registerFileLabel.setText(registerFileContent);
        registerFilePanel.add(registerFileLabel);

        mainPanel.add(registerFilePanel);

        // add data memory panel
        dataMemoryPanel.setBounds(50, 500, 270, 250);
        dataMemoryPanel.setBackground(Color.LIGHT_GRAY);
        dataMemoryPanel.setLayout(null);

        dmLabel.setBounds(10, 10, 200, 50);
        dmLabel.setFont(labelFont);
        dmLabel.setForeground(Color.BLACK);
        dataMemoryPanel.add(dmLabel);

        dataMemoryLabel.setBounds(10, 60, 230, 140);
        dataMemoryLabel.setForeground(Color.BLACK);
        String dataMemoryContent = "<html>" + controller.simulator.getDataMemory().toString().replace("\n", "<br>") + "</html>";
        dataMemoryLabel.setText(dataMemoryContent);
        dataMemoryPanel.add(dataMemoryLabel);

        mainPanel.add(dataMemoryPanel);

        // add the PC panel
        pcPanel.setBounds(920, 750, 200, 90 );
        pcPanel.setBackground(Color.LIGHT_GRAY);
        pcPanel.setLayout(null);
        mainPanel.add(pcPanel);

        pcLabel.setBounds(10, 10, 55, 40);
        pcLabel.setFont(labelFont);
        pcLabel.setForeground(Color.BLACK);
        pcPanel.add(pcLabel);

        clockCycleLabel.setBounds(10, 45, 100, 40);
        clockCycleLabel.setFont(labelFont);
        clockCycleLabel.setForeground(Color.BLACK);
        pcPanel.add(clockCycleLabel);







        mainFrame.setVisible(true);
    }




    private void terminationMessage(){
        System.out.println("##############################################\n");
        System.out.printf("Register File\n^^^^^^^^^^^^^\n%s==============================================\n", controller.simulator.getRegisterFile());
        System.out.printf("Data Memory\n^^^^^^^^^^^\n%s\n", controller.simulator.getDataMemory());
        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        System.out.println("               End of Program");
        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
    }

    // Helper method to create styled buttons with hover effects
    private JButton createStyledButton(String text, Color defaultColor, Color hoverColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18)); // Larger font
        button.setForeground(Color.WHITE); // Text color
        button.setBackground(defaultColor); // Default background color
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor); // Change to hover color
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(defaultColor); // Reset to default color
            }
        });

        return button;
    }

    private void openAddInstructionDialog() {
        // Instruction type and details dialog
        JDialog dialog = new JDialog(mainFrame, "Add Instruction", true);
        dialog.setSize(500, 400);
        dialog.setLayout(new FlowLayout());

        // Panel for dropdown and inputs
        JPanel inputsPanel = new JPanel();
        inputsPanel.setLayout(new GridLayout(10, 2, 10, 10));
        dialog.add(inputsPanel, BorderLayout.CENTER);
        // Instruction Type Dropdown
        JLabel typeLabel = new JLabel("Instruction Type:");
        String[] instructionTypes = {"R-type", "I-type", "J-type"};
        JComboBox<String> typeComboBox = new JComboBox<>(instructionTypes);
        inputsPanel.add(typeLabel);
        inputsPanel.add(typeComboBox);

        // Dynamic input panel
        JPanel dynamicPanel = new JPanel(new GridLayout(10, 2, 10, 10));
        dialog.add(dynamicPanel, BorderLayout.CENTER);

        // Add dynamic inputs based on instruction type
        typeComboBox.addActionListener(e -> {
            dynamicPanel.removeAll();
            String selectedType = (String) typeComboBox.getSelectedItem();

//        // Dropdown for instruction types
//        JLabel typeLabel = new JLabel("Instruction Type:");
//        String[] instructionTypes = {"R-type", "I-type", "J-type"};
//        JComboBox<String> typeComboBox = new JComboBox<>(instructionTypes);
//        dialog.add(typeLabel);
//        dialog.add(typeComboBox);
//
//        // Panel for dynamic inputs
//        JPanel dynamicPanel = new JPanel();
//        dynamicPanel.setLayout(new GridLayout(10, 2, 10, 10));
//        dialog.add(dynamicPanel);
//
//        // Handle instruction type selection
//        typeComboBox.addActionListener(e -> {
//            dynamicPanel.removeAll();
//            String selectedType = (String) typeComboBox.getSelectedItem();
            if ("R-type".equals(selectedType)) {
                addRTypeInputs(dynamicPanel);
            } else if ("I-type".equals(selectedType)) {
                addITypeInputs(dynamicPanel);
            } else if ("J-type".equals(selectedType)) {
                addJTypeInputs(dynamicPanel);
            }
            dynamicPanel.revalidate();
            dynamicPanel.repaint();
        });

        // Add or cancel buttons
        JButton addButton = new JButton("Add");
        JButton cancelButton = new JButton("Cancel");
        addButton.addActionListener(e -> handleAddInstruction(
                typeComboBox,
                findComboBox(dynamicPanel), // Instruction combo box
                findTextField(dynamicPanel, "RSource"), // Source field
                findTextField(dynamicPanel, "RTarget"), // Target field
                findTextField(dynamicPanel, "RDestination"), // Destination field
                findTextField(dynamicPanel, "Immediate"), // Immediate field
                findTextField(dynamicPanel, "JumpAddress") // Jump address field

        ));
        cancelButton.addActionListener(e -> dialog.dispose());
        dialog.add(addButton);
        dialog.add(cancelButton);

        dialog.setVisible(true);
    }

    // Helper Method to Find a JTextField by Name in the Dynamic Panel
    private JTextField findTextField(JPanel panel, String name) {
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JTextField && name.equals(comp.getName())) {
                return (JTextField) comp;
            }
        }
        return null; // Return null if not found
    }
    // Helper Method to Find a JComboBox in the Dynamic Panel
    private JComboBox<String> findComboBox(JPanel panel) {
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JComboBox) {
                return (JComboBox<String>) comp;
            }
        }
        return null; // Return null if not found
    }

    private void addRTypeInputs(JPanel panel) {
        JLabel instructionLabel = new JLabel("Instruction:");
        String[] rTypeInstructions = {"ADD", "SUB", "SLL", "SRL", "AND", "OR", "XOR", "SLT"};
        JComboBox<String> instructionComboBox = new JComboBox<>(rTypeInstructions);

        JLabel sourceLabel = new JLabel("RSource (0-7):");
        JTextField sourceField = new JTextField(5);
        sourceField.setName("RSource");

        JLabel targetLabel = new JLabel("RTarget (0-7):");
        JTextField targetField = new JTextField(5);
        targetField.setName("RTarget");

        JLabel destinationLabel = new JLabel("RDestination (0-7):");
        JTextField destinationField = new JTextField(5);
        destinationField.setName("RDestination");



        panel.add(instructionLabel);
        panel.add(instructionComboBox);
        panel.add(sourceLabel);
        panel.add(sourceField);
        panel.add(targetLabel);
        panel.add(targetField);
        panel.add(destinationLabel);
        panel.add(destinationField);
    }

    private void addITypeInputs(JPanel panel) {
        JLabel instructionLabel = new JLabel("Instruction:");
        String[] iTypeInstructions = {"SLTi", "ADDi", "SW", "LW", "BEQ", "BNQ"};
        JComboBox<String> instructionComboBox = new JComboBox<>(iTypeInstructions);

        JLabel sourceLabel = new JLabel("RSource (0-7):");
        JTextField sourceField = new JTextField(5);
        sourceField.setName("RSource");

        JLabel targetLabel = new JLabel("RTarget (0-7):");
        JTextField targetField = new JTextField(5);
        targetField.setName("RTarget");

        JLabel immediateLabel = new JLabel("Immediate (-64 to 63):");
        JTextField immediateField = new JTextField(5);
        immediateField.setName("Immediate");

        panel.add(instructionLabel);
        panel.add(instructionComboBox);
        panel.add(sourceLabel);
        panel.add(sourceField);
        panel.add(targetLabel);
        panel.add(targetField);
        panel.add(immediateLabel);
        panel.add(immediateField);
    }

    private void addJTypeInputs(JPanel panel) {
        JLabel instructionLabel = new JLabel("Instruction:");
        JComboBox<String> instructionComboBox = new JComboBox<>(new String[]{"JUMP"});

        JLabel addressLabel = new JLabel("Jump Address (0-1600):");
        JTextField addressField = new JTextField(10);
        addressField.setName("JumpAddress");

        panel.add(instructionLabel);
        panel.add(instructionComboBox);
        panel.add(addressLabel);
        panel.add(addressField);
    }

    private void handleAddInstruction(
            JComboBox<String> instructionTypeComboBox,
            JComboBox<String> instructionComboBox,
            JTextField rSourceField,
            JTextField rTargetField,
            JTextField rDestinationField,
            JTextField immediateField,
            JTextField jumpAddressField
    ) {
        String assemblyForm = "";
        String type = instructionTypeComboBox.getSelectedItem().toString();
        String instructionName = instructionComboBox.getSelectedItem().toString();
        String Imm;
        String OP = "";
        String RS;
        String RT;
        String RD;
        String fun;
        int function = 0;
        String shiftAmount = "0";
        String jumpAddress = "";


        switch (type){
            case "R-type" :
                // get the instruction
                switch (instructionName){
                    case "ADD":
                        function = 0;
                        shiftAmount = "0";
                        assemblyForm = " RF(" + rDestinationField.getText() + ") =" + " RF(" + rSourceField.getText() + ") +" +" RF(" + rTargetField.getText() + ")";
                        break;
                    case "SUB":
                        function = 1;
                        shiftAmount = "0";
                        assemblyForm = " RF(" + rDestinationField.getText() + ") =" + " RF(" + rSourceField.getText() + ") -" +" RF(" + rTargetField.getText() + ")";
                        break;
                    case "SLL":
                        function = 2;
                        shiftAmount = "1";
                        assemblyForm = " RF(" + rDestinationField.getText() + ") =" + " RF(" + rTargetField.getText() + ") << " + shiftAmount;
                        break;
                    case "SRL":
                        function = 3;
                        shiftAmount = "1";
                        assemblyForm = " RF(" + rDestinationField.getText() + ") =" + " RF(" + rTargetField.getText() + ") >> " + shiftAmount;
                        break;
                    case "AND":
                        function = 4;
                        shiftAmount = "0";
                        assemblyForm = " RF(" + rDestinationField.getText() + ") =" + " RF(" + rSourceField.getText() + ") &" +" RF(" + rTargetField.getText() + ")";
                        break;
                    case "OR":
                        function = 5;
                        shiftAmount = "0";
                        assemblyForm = " RF(" + rDestinationField.getText() + ") =" + " RF(" + rSourceField.getText() + ") |" +" RF(" + rTargetField.getText() + ")";
                        break;
                    case "XOR":
                        function = 6;
                        shiftAmount = "0";
                        assemblyForm = " RF(" + rDestinationField.getText() + ") =" + " RF(" + rSourceField.getText() + ") ^" +" RF(" + rTargetField.getText() + ")";
                        break;
                    case "SLT":
                        function = 7;
                        shiftAmount = "0";
                        assemblyForm = "slt";
                        break;
                    default:
                        System.out.println("Unknown instruction");
                }

                OP = String.format("%3s", Integer.toBinaryString(0)).replace(' ', '0'); // "101"
                RS = String.format("%3s", Integer.toBinaryString(Integer.parseInt(rSourceField.getText()))).replace(' ', '0'); // "101"
                RT = String.format("%3s", Integer.toBinaryString(Integer.parseInt(rTargetField.getText()))).replace(' ', '0'); // "011"
                RD = String.format("%3s", Integer.toBinaryString(Integer.parseInt(rDestinationField.getText()))).replace(' ', '0'); // "011"
                fun = String.format("%3s", Integer.toBinaryString(function)).replace(' ', '0');

                String instructionForTable = OP + "_"+ RS + "_"+ RT + "_"+ RD + "_"+ shiftAmount + "_"+ fun;
                String instructionString = OP + RS +  RT +  RD + shiftAmount + fun;

                tableModel.addRow(new Object[]{instructionNumber, instructionForTable, assemblyForm});
                controller.setInstruction(instructionNumber, Integer.parseInt(instructionString, 2));
                instructionNumber++;
                break;

            case "I-type" :

                // get the instruction
                switch (instructionName){
                    case "SLTi":
                        OP = "001";
                        assemblyForm = "SLTi";
                        break;
                    case "BNQ":
                        assemblyForm = "BNQ";
                        OP = "010";
                        break;
                    case "BEQ":
                        assemblyForm = "BEQ";
                        OP = "011";
                        break;
                    case "ADDi":
                        assemblyForm = " RF(" + rTargetField.getText() + ") =" + " RF(" + rSourceField.getText() + ") +" + immediateField.getText();
                        OP = "100";
                        break;
                    case "LW":
                        assemblyForm = " RF(" + rTargetField.getText() + ") =" + "M["+ " RF(" + rSourceField.getText() + ") +" +immediateField.getText() + "]";
                        OP = "101";
                        break;
                    case "SW":
                        assemblyForm = "M["+ " RF(" + rSourceField.getText() + ") +" +immediateField.getText() + "]" + " RF(" + rTargetField.getText() + ") =";
                        OP = "110";
                        break;
                    default:
                        System.out.println("Unknown instruction");
                }
                RS = String.format("%3s", Integer.toBinaryString(Integer.parseInt(rSourceField.getText()))).replace(' ', '0'); // "101"
                RT = String.format("%3s", Integer.toBinaryString(Integer.parseInt(rTargetField.getText()))).replace(' ', '0'); // "011"
                Imm = String.format("%7s", Integer.toBinaryString(Integer.parseInt(immediateField.getText()))).replace(' ', '0');
                instructionForTable = OP + "_" + RS + "_" + RT + "_"+ Imm;
                instructionString = OP + RS +  RT +  Imm;
                tableModel.addRow(new Object[]{instructionNumber, instructionForTable, assemblyForm});
                controller.setInstruction(instructionNumber, Integer.parseInt(instructionString, 2));
                instructionNumber++;
                if(OP == "010" || OP == "011"){
                    // automatically add the nop instruction after the branch instructions
                    assemblyForm = "NOP";
                    tableModel.addRow(new Object[]{instructionNumber, "0XFFFF0000", assemblyForm});
                    controller.setInstruction(instructionNumber, Simulator.NOP);
                    instructionNumber++;

                    tableModel.addRow(new Object[]{instructionNumber, "0XFFFF0000", assemblyForm});
                    controller.setInstruction(instructionNumber, Simulator.NOP);
                    instructionNumber++;
                }
                break;
            case "J-type" :
                OP = "111";
                jumpAddress = String.format("%13s", Integer.toBinaryString(Integer.parseInt(jumpAddressField.getText()))).replace(' ', '0');
                instructionForTable = OP + "_" + jumpAddress;

                instructionString = OP + jumpAddress;
                assemblyForm = "jump " + jumpAddressField.getText();
                tableModel.addRow(new Object[]{instructionNumber, instructionForTable, assemblyForm});
                controller.setInstruction(instructionNumber, Integer.parseInt(instructionString, 2));
                instructionNumber++;
                // automatically add the nop instruction after the jump
                assemblyForm = "NOP";
                tableModel.addRow(new Object[]{instructionNumber, "0XFFFF0000", assemblyForm});
                controller.setInstruction(instructionNumber, Simulator.NOP);
                instructionNumber++;
                break;
        }

    }


    private void nopButtonClickHandel(){
        String assemblyForm = "NOP";
        tableModel.addRow(new Object[]{instructionNumber, "0XFFFF0000", assemblyForm});
        controller.setInstruction(instructionNumber,Simulator.NOP);
        instructionNumber++;
    }

    private void openUpdateDialog(int row, String currentNr, String currentInstruction, String currentAssembly) {
        JDialog dialog = new JDialog(mainFrame, "Update Instruction", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new GridLayout(4, 2, 10, 10));

        JLabel nrLabel = new JLabel("Nr.:");
        JTextField nrField = new JTextField(currentNr);
        nrField.setEnabled(false); // Disable editing for the Nr. field

        JLabel instructionLabel = new JLabel("Instruction:");
        JTextField instructionField = new JTextField(currentInstruction);

        JLabel assemblyLabel = new JLabel("Assembly:");
        JTextField assemblyField = new JTextField(currentAssembly);

        dialog.add(nrLabel);
        dialog.add(nrField);
        dialog.add(instructionLabel);
        dialog.add(instructionField);
        dialog.add(assemblyLabel);
        dialog.add(assemblyField);

        JButton updateButton = new JButton("Update");
        JButton cancelButton = new JButton("Cancel");

        dialog.add(updateButton);
        dialog.add(cancelButton);

        updateButton.addActionListener(e -> {
            String updatedInstruction = instructionField.getText();
            String updatedAssembly = assemblyField.getText();

            // Update the table model
            tableModel.setValueAt(updatedInstruction, row, 1);
            tableModel.setValueAt(updatedAssembly, row, 2);
            // overwrite the instruction in the instruction memory
            controller.simulator.setInstructionNumber(Integer.parseInt(nrField.getText()) , Integer.parseInt(instructionField.getText()));

            dialog.dispose();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void updateVerticalPanelContent(int panelIndex, String content) {
        if (panelIndex < 0 || panelIndex >= verticalPanels.length) {
            throw new IllegalArgumentException("Invalid panel index");
        }

        // Get the target panel
        JPanel targetPanel = verticalPanels[panelIndex];

        // Clear the panel before adding new content
        targetPanel.removeAll();

        // Create a new JLabel for the content
        JLabel contentLabel = new JLabel("<html>" + content.replace("\n", "<br>") + "</html>");
        contentLabel.setBounds(10, 40, 230, 600); // Adjust bounds as needed
        contentLabel.setVerticalAlignment(SwingConstants.TOP); // Align text to the top
        contentLabel.setFont(new Font("Arial", Font.PLAIN, 14)); // Set font
        contentLabel.setForeground(Color.BLACK); // Text color

        // Add the label to the panel
        targetPanel.add(contentLabel);

        // Refresh the panel
        targetPanel.revalidate();
        targetPanel.repaint();
    }

}
