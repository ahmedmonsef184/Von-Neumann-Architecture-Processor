import java.util.Arrays;

public class InstructionMemory {

    private final int SIZE = 1024;
    private Instruction[] mem;
    private int noOfInstructions;

    private static InstructionMemory instructionMemory = new InstructionMemory();

    private InstructionMemory(){
        mem = new Instruction[SIZE];
        noOfInstructions = 0;
    }

    public static InstructionMemory getInstructionMemory(){
        return instructionMemory;
    }

    public Instruction getNext(){
        return mem[noOfInstructions - 1];
    }

    public Instruction get(short pc){
        return mem[pc];
    }


    public void add(Instruction instruction){
        mem[noOfInstructions++] = instruction;
    }

    public void print(){
        System.out.println("Contents of instruction memory: \n");
        System.out.println(Arrays.toString(mem));
    }
}
