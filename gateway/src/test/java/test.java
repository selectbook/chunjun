import com.iiot.common.bytes.HexStr;

public class test {
    public static void main(String[] args) {
        byte[] array = HexStr.toArray("553A001C2A58543336352D3030303033340601060707E40616151015AA");
        byte xor = array[28];
        System.out.println(xor);
        System.out.println(Byte.toUnsignedInt(xor));
    }
}
