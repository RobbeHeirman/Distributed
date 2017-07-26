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

public abstract class Client{

    protected Transceiver client = null;
    protected ServerProto.Callback proxy = null;
    protected Server server;
    protected CharSequence name;

    protected int key;
    int port;
    ClientType type;
    boolean is_backup = false;

    Client(CharSequence name, ClientType type) {

        this.name = name;
        this.type= type;
        this.set_up_server();

        try {
            client = new SaslSocketTransceiver(new InetSocketAddress(6789));
            proxy = SpecificRequestor.getClient(ServerProto.Callback.class, client);

        } catch (IOException e) {
            System.err.println("Error Connecting server");
            e.printStackTrace(System.err);
            System.exit(1);
        }

    }

    public void connect(){
        try {

            this.port = this.server.getPort();
            key = proxy.connect(name, "localhost", port, type);



        } catch (IOException e) {
            System.err.println("Server down!");
            server_down();
        }
    }

    public void reconnect(String ip, int port, boolean back_up){
        is_backup = back_up;
        try {
            client.close();
        } catch (IOException e) {

        }

        try {
            client = new SaslSocketTransceiver(new InetSocketAddress(port));
            proxy = SpecificRequestor.getClient(ServerProto.Callback.class, client);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    abstract void set_up_server();


    public Void is_alive(){

        return null;
    }

    protected void server_down(){
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected  boolean ping(){
        try{
            proxy.getUsers();
            return true;
        } catch (AvroRemoteException e) {

        }

        return false;
    }

    protected boolean is_backup(){
        return this.is_backup;
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

