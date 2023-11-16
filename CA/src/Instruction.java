public class Instruction {

    private String binaryInstruction;
    private int opcode;
    private int firstRegisterNumber;
    private int secondRegisterNumber;
    private byte firstRegisterData;
    private byte secondRegisterData;
    private InstructionType type;

    public Instruction(String instruction){
        binaryInstruction = instruction;
    }

    public void decode() {
        StringBuilder sb = new StringBuilder(binaryInstruction);
        opcode = Integer.parseInt(binaryInstruction.substring(0, 4), 2);
        setType();
        firstRegisterNumber = Integer.parseInt(binaryInstruction.substring(4, 10), 2);
        if (sb.charAt(10) == '1') {
            sb = new StringBuilder(sb.substring(10));
            sb.reverse();
            sb.append("11111111111111111111111111");
            sb.reverse();
            long tmp = Long.parseLong(sb.toString(), 2);
            if (tmp > 0x7fffffffL)
                tmp = -(0x100000000L - tmp);
            secondRegisterNumber = (int) tmp;
        } else
            secondRegisterNumber = Integer.parseInt(binaryInstruction.substring(10), 2);
        firstRegisterData = CPU.getCPU().getRegisters().readRegister(firstRegisterNumber);
        if (type == InstructionType.R_TYPE)
            secondRegisterData = CPU.getCPU().getRegisters().readRegister(secondRegisterNumber);
    }

    public void setType() {
        switch (opcode){
            case 0:
            case 1:
            case 2:
            case 5:
            case 6:
            case 7: type = InstructionType.R_TYPE; return;
            case 3:
            case 4:
            case 8:
            case 9:
            case 10:
            case 11: type = InstructionType.I_TYPE; return;
        }
    }

    public void execute(){
        switch (opcode){
            case 0: executeADD(); return;
            case 1: executeSUB(); return;
            case 2: executeMUL(); return;
            case 3: executeLDI(); return;
            case 4: executeBEQZ(); return;
            case 5: executeAND(); return;
            case 6: executeOR(); return;
            case 7: executeJR(); return;
            case 8: executeSLC(); return;
            case 9: executeSRC(); return;
            case 10: executeLB(); return;
            case 11: executeSB(); return;
        }
    }

    public void executeADD(){
        byte res = (byte) (firstRegisterData + secondRegisterData);
        CPU.getCPU().getRegisters().setRegister(firstRegisterNumber, res);
        int temp1 = firstRegisterData & 0x000000FF;
        int temp2 = (secondRegisterData) & 0x000000FF;
        int carry;
        if(((temp1 + temp2) & (1<<8)) == (1<<8))
            carry = 1;
        else
            carry = 0;
        int c1 = ((1 << 8) & (temp1+temp2)) == 0 ? 0 : 1;
        int c2 = (((1 << 7) & (temp1+temp2))) == 0 ? 0 : 1;
        int c3 = (((1 << 7) & (temp1))) == 0 ? 0 : 1;
        int c4 = (((1 << 7) & (temp2))) == 0 ? 0 : 1;
        int overflow = c1 ^ c2 ^ c3 ^ c4;
        int negative = res < 0 ? 1 : 0;
        int zero = res < 0 ? 1 : 0;
        CPU.getCPU().getRegisters().setSREG((byte) (CPU.getCPU().getRegisters().readSREG() | (carry << 4)));
        CPU.getCPU().getRegisters().setSREG((byte) (CPU.getCPU().getRegisters().readSREG() | (overflow << 3)));
        CPU.getCPU().getRegisters().setSREG((byte) (CPU.getCPU().getRegisters().readSREG() | (negative << 2)));
        CPU.getCPU().getRegisters().setSREG((byte) (CPU.getCPU().getRegisters().readSREG() | ((overflow^negative) << 1)));
        CPU.getCPU().getRegisters().setSREG((byte) (CPU.getCPU().getRegisters().readSREG() | (zero)));
        printOutputs();
        printCarry(carry);
        printOverflow(overflow);
        printNegative(negative);
        printSign(overflow ^ negative);
        printZero(zero);
        printRegisterChange(res);
    }

    public void executeSUB(){
        byte res = (byte) (firstRegisterData - secondRegisterData);
        CPU.getCPU().getRegisters().setRegister(firstRegisterNumber, res);
        int temp1 = firstRegisterData & 0x000000FF;
        int temp2 = (secondRegisterData) & 0x000000FF;
        int c1 = ((1 << 8) & (temp1-temp2)) == 0 ? 0 : 1;
        int c2 = (((1 << 7) & (temp1-temp2))) == 0 ? 0 : 1;
        int c3 = (((1 << 7) & (temp1))) == 0 ? 0 : 1;
        int c4 = (((1 << 7) & (temp2))) == 0 ? 0 : 1;
        int overflow = c1 ^ c2 ^ c3 ^ c4;
        int negative = res < 0 ? 1 : 0;
        int zero = res == 0 ? 1 : 0;
        CPU.getCPU().getRegisters().setSREG((byte) (CPU.getCPU().getRegisters().readSREG() | (overflow << 3)));
        CPU.getCPU().getRegisters().setSREG((byte) (CPU.getCPU().getRegisters().readSREG() | (negative << 2)));
        CPU.getCPU().getRegisters().setSREG((byte) (CPU.getCPU().getRegisters().readSREG() | ((overflow^negative) << 1)));
        CPU.getCPU().getRegisters().setSREG((byte) (CPU.getCPU().getRegisters().readSREG() | (zero)));
        printOutputs();
        printOverflow(overflow);
        printNegative(negative);
        printSign(overflow ^ negative);
        printZero(zero);
        printRegisterChange(res);
    }

    public void executeMUL(){
        byte res = (byte) (firstRegisterData * secondRegisterData);
        CPU.getCPU().getRegisters().setRegister(firstRegisterNumber, res);
        int negative = res < 0 ? 1 : 0;
        int zero = res < 0 ? 1 : 0;
        CPU.getCPU().getRegisters().setSREG((byte) (CPU.getCPU().getRegisters().readSREG() | (negative << 2)));
        CPU.getCPU().getRegisters().setSREG((byte) (CPU.getCPU().getRegisters().readSREG() | (zero)));
        printOutputs();
        printNegative(negative);
        printZero(zero);
        printRegisterChange(res);
    }

    public void executeAND(){
        byte res = (byte) (firstRegisterData & secondRegisterData);
        CPU.getCPU().getRegisters().setRegister(firstRegisterNumber, res);
        int negative = res < 0 ? 1 : 0;
        int zero = res < 0 ? 1 : 0;
        CPU.getCPU().getRegisters().setSREG((byte) (CPU.getCPU().getRegisters().readSREG() | (negative << 2)));
        CPU.getCPU().getRegisters().setSREG((byte) (CPU.getCPU().getRegisters().readSREG() | (zero)));
        printOutputs();
        printNegative(negative);
        printZero(zero);
        printRegisterChange(res);
    }

    public void executeOR(){
        byte res = (byte) (firstRegisterData | secondRegisterData);
        CPU.getCPU().getRegisters().setRegister(firstRegisterNumber, res);
        int negative = res < 0 ? 1 : 0;
        int zero = res < 0 ? 1 : 0;
        CPU.getCPU().getRegisters().setSREG((byte) (CPU.getCPU().getRegisters().readSREG() | (negative << 2)));
        CPU.getCPU().getRegisters().setSREG((byte) (CPU.getCPU().getRegisters().readSREG() | (zero)));
        printOutputs();
        printNegative(negative);
        printZero(zero);
        printRegisterChange(res);
    }

    public void executeSLC(){
        byte res = (byte) (firstRegisterData << secondRegisterNumber | firstRegisterData >>> (Byte.SIZE - secondRegisterNumber));
        CPU.getCPU().getRegisters().setRegister(firstRegisterNumber, res);
        int negative = res < 0 ? 1 : 0;
        int zero = res < 0 ? 1 : 0;
        CPU.getCPU().getRegisters().setSREG((byte) (CPU.getCPU().getRegisters().readSREG() | (negative << 2)));
        CPU.getCPU().getRegisters().setSREG((byte) (CPU.getCPU().getRegisters().readSREG() | (zero)));
        printOutputs();
        printNegative(negative);
        printZero(zero);
        printRegisterChange(res);
    }

    public void executeSRC(){
        byte res = (byte) (firstRegisterData >>> secondRegisterNumber | firstRegisterData << (Byte.SIZE - secondRegisterNumber));
        CPU.getCPU().getRegisters().setRegister(firstRegisterNumber, res);
        int negative = res < 0 ? 1 : 0;
        int zero = res < 0 ? 1 : 0;
        CPU.getCPU().getRegisters().setSREG((byte) (CPU.getCPU().getRegisters().readSREG() | (negative << 2)));
        CPU.getCPU().getRegisters().setSREG((byte) (CPU.getCPU().getRegisters().readSREG() | (zero)));
        printOutputs();
        printNegative(negative);
        printZero(zero);
        printRegisterChange(res);
    }

    public void executeLB(){
        CPU.getCPU().getRegisters().setRegister(firstRegisterNumber, DataMemory.getDataMemory().getByte(secondRegisterNumber));
        printRegisterChange(DataMemory.getDataMemory().getByte(secondRegisterNumber));
    }

    public void executeSB(){
        DataMemory.getDataMemory().setByte(secondRegisterNumber, CPU.getCPU().getRegisters().readRegister(firstRegisterNumber));
        printDataChange(secondRegisterNumber, CPU.getCPU().getRegisters().readRegister(firstRegisterNumber));
    }

    public void executeBEQZ(){
        if (firstRegisterData == 0) {
            CPU.getCPU().getRegisters().setPC((short) (CPU.getCPU().getRegisters().readPC() - 2 + secondRegisterNumber));
            CPU.getCPU().setFlushed(true);
        }
    }

    public void executeJR(){
        CPU.getCPU().getRegisters().setPC((short) ((firstRegisterData << 8) | secondRegisterData & 0x0000FF));
        CPU.getCPU().setFlushed(true);
    }

    public void executeLDI(){
        CPU.getCPU().getRegisters().setRegister(firstRegisterNumber, (byte) secondRegisterNumber);
        printRegisterChange((byte) secondRegisterNumber);
    }

    public String getBinary(){
        return binaryInstruction;
    }

    public void printRegisterChange(byte data){
        System.out.println("Register "+firstRegisterNumber+" value has been changed to "+ data+"\n----");
    }

    public void printOutputs(){
        System.out.print("Outputs:\n");
    }

    public void printDataChange(int idx, byte data){
        System.out.println("Byte "+ idx +" in the data memory was changed to "+ data+"\n----");
    }

    public int getOpcode() {
        return opcode;
    }

    public int getFirstRegisterNumber() {
        return firstRegisterNumber;
    }

    public int getSecondRegisterNumber() {
        return secondRegisterNumber;
    }

    public InstructionType getType() {
        return type;
    }

    public String toString(){
        return binaryInstruction;
    }

    public void printCarry(int bit){
        System.out.println("Carry bit was set to "+bit);
    }

    public void printOverflow(int bit){
        System.out.println("Overflow bit was set to "+bit);
    }

    public void printNegative(int bit){
        System.out.println("Negative bit was set to "+bit);
    }

    public void printSign(int bit){
        System.out.println("Sign bit was set to "+bit);
    }

    public void printZero(int bit){
        System.out.println("Zero bit was set to "+bit);
    }
}
