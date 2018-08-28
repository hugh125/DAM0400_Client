import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Client {
    boolean isOK=false;

    /**
     * 输入IP地址、端口号、开关命令，实现对控制器的开关控制
     * @param strIP 控制器IP地址 具体信息找控制器设置人员，询问IP地址
     * @param iPort 控制器端口号 默认60001 具体信息找控制器设置人员，询问端口号
     * @param isOpen 控制器开关
     * @throws Exception
     */
    public void OpenClient(String strIP, int iPort, boolean isOpen) throws Exception {
        //客户端请求与本机在 iPort 端口建立TCP连接
        Socket client = new Socket(strIP, iPort);
        client.setSoTimeout(10 * 1000);

        //创建收发数据流
        InputStream in = client.getInputStream();
        OutputStream out = client.getOutputStream();
        try {
            //发送字节流
            byte[] bytesSend = WriteAllDO(0xfe, isOpen);
            out.write(bytesSend);

            //接收字节流
            byte[] bytesRecv = new byte[10];
            in.read(bytesRecv);
            isOK = ReadAllDO(bytesRecv);

        } finally {
            //3.使用Socet类的close（）方法关闭连接
            out.close();
            in.close();
            client.close();
        }
    }

    /**
     * 发送开关指令
     * @param addr      IP地址最后一位
     * @param io        要控制继电器寄存器地址
     * @param openclose 开关状态
     * @return
     */
    public byte[] WriteDO(int addr,int io,boolean openclose)
    {
        byte[] src = new byte[8];
        src[0] = (byte)addr;
        src[1] = 0x05;
        src[2] = 0x00;
        src[3] = (byte)io;
        src[4] = (byte)((openclose) ? 0xff : 0x00);
        src[5] = 0x00;
        char crc = CMBRTU.CalculateCrc(src,6);
        src[6] = (byte)(crc & 0xff);
        src[7] = (byte)(crc>>8);
        return src;
    }

    /**
     * 发送开关指令
     * @param addr      IP地址最后一位
     * @param openclose 开关状态
     * @return 返回要发送的字节流
     */
    public byte[] WriteAllDO(int addr, boolean openclose) {
        byte[] src = new byte[10];
        src[0] = (byte) addr;
        src[1] = 0x0f;
        src[2] = 0x00;
        src[3] = 0x00;
        src[4] = 0x00;
        src[5] = 0x04;
        src[6] = 0x01;
        src[7] = (byte) ((openclose) ? 0xff : 0x00);
        char crc = CMBRTU.CalculateCrc(src, 8);
        src[8] = (byte) (crc & 0xff);
        src[9] = (byte) (crc >> 8);
        return src;
    }

    /**
     * 接收状态
     *
     * @param bytesRecv 接收到的数据包
     * @return 返回处理后的结果
     */
    public boolean ReadAllDO(byte[] bytesRecv) {
        for (int i = 0; i < bytesRecv.length; i++){
            System.out.printf(Integer.toHexString(bytesRecv[i]&0xff));
        }
        System.out.println();
        char crcRecv, crcCurrent;
        crcRecv = CMBRTU.CalculateCrc(bytesRecv, 8);
        crcCurrent = (char) (bytesRecv[9] << 8 | bytesRecv[8]);
        //如果CRC验证失败，返回false
        if (crcCurrent != crcRecv)
            return false;

        //如果返回码第二字节不是0X0F，返回false
        if (bytesRecv[1] != 0x0F)
            return false;
        return true;
    }

}


