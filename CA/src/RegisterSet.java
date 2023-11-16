public class RegisterSet {

    private byte[] gprs;
    private byte sreg;
    private short pc;

    public RegisterSet(){
        gprs = new byte[64];
        sreg = 0;
        pc = 0;
    }

    public byte readRegister(int registerNumber){
        return gprs[registerNumber];
    }

    public byte readSREG(){
        return sreg;
    }

    public short readPC(){
        return pc;
    }

    public void setPC(short pc) {
        this.pc = pc;
    }

    public short incrementPC() { return ++pc; }

    public void setSREG(byte sreg) {
        this.sreg = sreg;
    }

    public void setRegister(int registerNumber, byte data){
        gprs[registerNumber] = data;
    }

    public void print(){
        for (int i = 0; i < 64; i++)
            System.out.println("Content of register "+i+" is: "+gprs[i]);
        System.out.println("Content of statud register is: "+sreg);
        System.out.println("Content of program counter is: "+pc);
    }
}
