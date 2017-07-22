package logic_client;

import avro.distributed.proto.ClientType;
import avro.distributed.proto.ServerProto;
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

    Client(CharSequence name, ClientType type) {

        this.name = name;
        this.set_up_server();

        try {
            client = new SaslSocketTransceiver(new InetSocketAddress(6789));
            proxy = SpecificRequestor.getClient(ServerProto.Callback.class, client);

        } catch (IOException e) {
            System.err.println("Error Connecting server");
            e.printStackTrace(System.err);
            System.exit(1);
        }


        try {

            int port = this.server.getPort();
            CharSequence response = proxy.connect(name, "localhost", port, type);
            System.out.println(response);


        } catch (IOException e) {
            System.err.println(" [error] Failed to start server");
            e.printStackTrace(System.err);
            System.exit(1);
        }


    }

    abstract void set_up_server();


    public Void is_alive(){

        return null;
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

