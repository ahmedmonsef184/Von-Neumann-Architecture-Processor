import java.util.Arrays;

public class DataMemory {

    private byte[] mem;

    private static DataMemory dataMemory = new DataMemory();

    public DataMemory(){
        mem = new byte[2048];
    }

    public static DataMemory getDataMemory() {
        return dataMemory;
    }

    public void setByte(int byteNum, byte data){
        mem[byteNum] = data;
    }

    public byte getByte(int byteNum){
        return mem[byteNum];
    }

    public void print(){
        System.out.println("Contents of data memory: \n");
        System.out.println(Arrays.toString(mem));
    }
}
