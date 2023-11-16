public class CPU {
    private RegisterSet registers;
    private Instruction[] current;
    private boolean isFlushed;

    private static CPU cpu = new CPU();

    public CPU(){
        registers = new RegisterSet();
        current = new Instruction[3];
        isFlushed = false;
    }

    public static CPU getCPU(){
        return cpu;
    }

    public RegisterSet getRegisters() {
        return registers;
    }

    public Instruction fetch(){
        Instruction i = InstructionMemory.getInstructionMemory().get(registers.readPC());
        registers.incrementPC();
        return i;
    }


    public void decode(Instruction i){
        i.decode();
    }

    public void execute(Instruction i){
        i.execute();
    }

    public void run() {
        int cycles = 1;
        while (true) {
            if (isFlushed())
                current = new Instruction[3];
            current[2] = current[1];
            current[1] = current[0];
            current[0] = fetch();

            if (current[0] == null && current[1] == null && current[2] == null) {
                System.out.println("Execution finished\n\n--------------------------");
                break;
            }
            System.out.println("Cycle: " + cycles);

            if (current[2] != null) {
                System.out.println("Instruction " + current[2].getBinary() + " is executing");
                System.out.println("----\nInputs:\nR1: " + current[2].getFirstRegisterNumber() +
                        (current[2].getType() == InstructionType.R_TYPE ? "\nR2: " : "\nIMM: ") + current[2].getSecondRegisterNumber()+"\n----");
                execute(current[2]);
//                System.out.println("\n Outputs:\n");
                System.out.println("**********\n");

            }

            if (current[1] != null) {
                System.out.println("Instruction " + current[1].getBinary() + " is being decoded");
                System.out.println("----\nInputs:\nBinary Instruction: " + current[1].getBinary()+"\n----");
                decode(current[1]);
                System.out.println("Outputs:\nOP Code: " + current[1].getOpcode() + "\nR1: " + current[1].getFirstRegisterNumber()
                        + (current[1].getType() == InstructionType.R_TYPE ? "\nR2: " : "\nIMM: ") + current[1].getSecondRegisterNumber()+"\n----");
                System.out.println("**********\n");
            }


            if (current[0] != null) {
                System.out.println("Instruction " + current[0].getBinary() + " is being fetched");
                System.out.println("----\nInputs:\nPC: " + (registers.readPC() - 1)+"\n----");
                System.out.println("Outputs:\nBinary Instruction: " + current[0].getBinary()+"\n----");
            }

            cycles++;
            System.out.println("--------------------------\n");

        }

        registers.print();

        InstructionMemory.getInstructionMemory().print();

        DataMemory.getDataMemory().print();

    }

    public boolean isFlushed(){
        return isFlushed;
    }

    public void setFlushed(boolean f){
        isFlushed = f;
    }
}
