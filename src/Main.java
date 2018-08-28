import java.io.IOException;

public class Main {

    public static void main(String[] args) throws Exception {
       // System.out.println("Hello World!");

        String strIP = "192.168.1.170";
        int iPort = 60001;
        Client client =new Client();
        client.OpenClient(strIP, iPort, true);  //模拟4端口开
        client.OpenClient(strIP, iPort, false);  //模拟4端口关
    }
}

