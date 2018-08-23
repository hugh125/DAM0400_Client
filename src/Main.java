import java.io.IOException;

public class Main {

    public static void main(String[] args) throws Exception {
        System.out.println("Hello World!");

        String strIP = "127.0.0.1";
        int iPort = 6001;
        Client client =new Client();
        client.OpenClient(strIP, iPort);
    }
}

