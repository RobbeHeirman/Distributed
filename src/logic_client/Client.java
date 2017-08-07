package logic_client;

import avro.distributed.proto.ClientType;
import avro.distributed.proto.ServerProto;
import org.apache.avro.AvroRemoteException;
import org.apache.avro.ipc.SaslSocketTransceiver;
import org.apache.avro.ipc.Server;
import org.apache.avro.ipc.Transceiver;
import org.apache.avro.ipc.specific.SpecificRequestor;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * The abstract class of all client side logic classes.
 * al general logic is written here.
 */
public abstract class Client {

    protected String ip;
    protected Transceiver client = null;
    ServerProto.Callback proxy = null;

    String server_ip;
    int server_port;

    private ServerProto.Callback old_proxy = null;

    protected Server server;
    protected CharSequence name;

    protected int key;
    private ClientType type;
    private boolean is_backup = false;

    /**
     * Constructor
     *
     * @param name name of the client
     * @param type of the client.
     */
    Client(String ip_server,int port_server,String ip_client, CharSequence name, ClientType type) {

        this.server_ip = ip_server;
        this.server_port = port_server;
        this.ip = ip_client;
        this.name = name;
        this.type = type;
        this.set_up_server();

        try {
            client = new SaslSocketTransceiver(new InetSocketAddress(ip_server,port_server));
            proxy = SpecificRequestor.getClient(ServerProto.Callback.class, client);

        } catch (IOException e) {
            System.err.println("Error Connecting server");
            e.printStackTrace(System.err);
            System.exit(1);
        }

    }


    public void reconnect(String ip, int port, boolean back_up) {
        is_backup = back_up;

        try {
            old_proxy = proxy;
            SaslSocketTransceiver cl = new SaslSocketTransceiver(new InetSocketAddress(ip,port));
            proxy = SpecificRequestor.getClient(ServerProto.Callback.class, cl);
        } catch (IOException ignored) {
        }
    }

    abstract void set_up_server();


    public Void is_alive() {

        return null;
    }

    protected void server_down() {
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void connect() {
        try {

            int port = this.server.getPort();
            key = proxy.connect(name, ip, port, type);


        } catch (IOException e) {
            System.err.println("Server down!");
            server_down();
        }
    }

    boolean ping() {

        if (is_backup) {
            try {
                client = new SaslSocketTransceiver(new InetSocketAddress(this.server_ip,this.server_port));
                old_proxy = SpecificRequestor.getClient(ServerProto.Callback.class, client);
                old_proxy.getUsers();
                System.out.println("Succesful ping");
                return true;
            } catch (AvroRemoteException e) {
                System.out.println("Failed ping");
            } catch (IOException ignored) {

            }
        }
        return false;
    }

    protected boolean is_backup() {
        return this.is_backup;
    }


    void server_back_up() {
        proxy.old_up();
        is_backup = false;


    }

    void close_connection() {
        try {
            client.close();
        } catch (IOException e) {
            System.err.println("Could not close the connection");
            System.exit(1);
        }

    }


}

