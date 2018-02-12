import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.concurrent.Callable;

public class PortChecker implements Callable {

    private String hostName;
    private int portNumber;
    private Controller c;

    public PortChecker(String hostName, int portNumber, Controller c) {
        this.hostName = hostName;
        this.portNumber = portNumber;
        this.c = c;
    }

    @Override
    public Integer call() {

        // Creates a socket address from a hostname and a port number
        SocketAddress socketAddress = new InetSocketAddress(hostName, portNumber);
        Socket socket = new Socket();

        // Timeout required - it's in milliseconds
        int timeout = 200;
        c.setStatus(Integer.toString(portNumber));
        try {
            socket.connect(socketAddress, timeout);
            socket.close();
            c.setLog("success", "Port: " + portNumber + " is open!");

        } catch (SocketTimeoutException exception) {
//            c.setLog("error", portNumber + " : " + exception.getMessage());
            System.out.println(portNumber + " : " + exception.getMessage());
        } catch (IOException exception) {
//            c.setLog("error", portNumber + " : " + exception.getMessage());
            System.out.println(portNumber + " : " + exception.getMessage());
        }
        return (portNumber);
    }
}
