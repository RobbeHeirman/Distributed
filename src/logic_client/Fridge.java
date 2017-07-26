package logic_client;

import avro.distributed.proto.*;
import org.apache.avro.AvroRemoteException;
import org.apache.avro.ipc.SaslSocketServer;
import org.apache.avro.ipc.specific.SpecificResponder;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Robbe on 7/17/2017.
 */
public class Fridge extends ServerClient implements FridgeProto {

    List<CharSequence> items = new ArrayList<>();

    Fridge(CharSequence name) {
        super(name, ClientType.FRIDGE);
    }

    @Override
    void set_up_server() {
        try {
            server = new SaslSocketServer(new SpecificResponder(FridgeProto.class,
                    this), new InetSocketAddress(0));
        } catch (IOException e) {
            System.err.println(" [error] Failed to start server");
            e.printStackTrace(System.err);
            System.exit(1);
        }
        server.start();
    }

    @Override
    public void addItem(CharSequence item) {
        this.items.add(item);
    }

    @Override
    public List<CharSequence> contains() throws AvroRemoteException {
        return this.items;
    }

    @Override
    public void removeItem(CharSequence item) {
        this.items.remove(item);
    }

    @Override
    public void close_fridge() {

        proxy.set_fridge_inventory(this.name, this.items);
        System.out.println("Getting closed.");
    }

    @Override
    public void update_client(ClientInfo client_info) {
        this.add_client(client_info);
    }

    @Override
    public void update_user(ClientInfo fridge_info) {
        this.add_user(fridge_info);
    }

    @Override
    public void update_fridge(FridgeInfo fridge_info) {

        this.add_fridge(fridge_info);
    }

    @Override
    public void update_light(LightInfo fridge_info) {

        this.add_light(fridge_info);
    }

    @Override
    public void update_sensor(SensorInfo fridge_info) {

        this.add_sensor(fridge_info);
    }

    @Override
    public void send_UID(int UID) {
        super.send_UID(UID);
    }

    @Override
    public void reconnect(CharSequence ip, int port, boolean back_up) {
        super.reconnect(ip.toString(),port, back_up);
    }


    public static void main(String args[]) {

        Fridge fridge = new Fridge("Fridge0");
        fridge.connect();
        while (true) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}


