package logic_server.Objects;

import avro.distributed.proto.*;
import org.apache.avro.AvroRemoteException;
import org.apache.avro.ipc.SaslSocketTransceiver;
import org.apache.avro.ipc.Transceiver;
import org.apache.avro.ipc.specific.SpecificRequestor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;


/**
 * Class Represents a server side fridge object.
 */
@SuppressWarnings("Duplicates")
public class FridgeObject extends ClientObject {

    protected Transceiver client = null;
    private FridgeProto.Callback proxy = null;
    protected List<CharSequence> items = new ArrayList<>();

    /**
     * Constructor
     *
     * @param id   id
     * @param ip   ip
     * @param port port
     * @param name name
     */
    public FridgeObject(int id, CharSequence ip, int port, String name) {
        super(id, ip, port, name, ClientType.FRIDGE);

        try {

            client = new SaslSocketTransceiver(new InetSocketAddress(ip.toString(),port));
            proxy = SpecificRequestor.getClient(FridgeProto.Callback.class, client);

        } catch (IOException e) {
            System.err.println("Error Connecting to fridge");
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

    /**
     * Constructor out of a FridgeInfo object
     *
     * @param info a FridgeInfo object. This object contains all replicated data.
     */
    public FridgeObject(FridgeInfo info) {
        super(info.getClientInfo().getKey(), info.getClientInfo().getIp(), info.getClientInfo().getPort(),
                info.getClientInfo().getName().toString(), ClientType.FRIDGE);
        this.online = info.getClientInfo().getOnline();
        try {

            client = new SaslSocketTransceiver(
                    new InetSocketAddress(info.getClientInfo().getIp().toString(), info.getClientInfo().getPort()));
            proxy = SpecificRequestor.getClient(FridgeProto.Callback.class, client);

        } catch (IOException ignored) {

        }

        this.set_inventory(info.getInventory());
    }

    // @return an inventory list
    public List<CharSequence> get_inventory() {
        return items;
    }


    /**
     * Sets the inventory of the fridge.
     *
     * @param new_inventory The new inventory of the fridge.
     */
    public void set_inventory(List<CharSequence> new_inventory) {
        this.items = new ArrayList<>(new_inventory);
    }


    /**
     * The following methods are called by the server to replicate the data.
     *
     * @param client_object The ClientObject that has to be replicated
     */
    public void replicate_client(ClientObject client_object) {
        ClientInfo client = this.convert_client_object(client_object);
        if (isOnline()) {
            try {
                proxy.update_client(client);
            } catch (Exception e) {
                this.log_out();
            }
        }


    }

    // As replicate_client
    public void replicate_user(UserObject user) {

        ClientInfo client = this.convert_client_object(user);
        if (isOnline()) {
            try {
                proxy.update_user(client);
            } catch (Exception e) {
                this.log_out();
            }
        }

    }

    // As replicate_client
    public void replicate_fridge(FridgeObject fridge) {

        ClientInfo client = this.convert_client_object(fridge);
        List<CharSequence> inventory = fridge.get_inventory();
        if (isOnline()) {
            try {
                FridgeInfo fr = new FridgeInfo(client, inventory);
                proxy.update_fridge(fr);
            } catch (Exception e) {
                this.log_out();
            }
        }
    }

    // As replicate_client
    public void replicate_light(LightObject light) {

        ClientInfo client = this.convert_client_object(light);
        boolean on = light.get_state();

        if (isOnline()) {
            try {
                LightInfo info = new LightInfo(client, on);
                proxy.update_light(info);
            } catch (Exception e) {
                this.log_out();
            }
        }
    }

    // As replicate_client
    public void replicate_sensor(SensorObject sensor) {

        ClientInfo client = this.convert_client_object(sensor);
        List<Float> temperatures = sensor.getTemperature_history();

        if (isOnline()) {
            try {
                SensorInfo info = new SensorInfo(client, temperatures);
                proxy.update_sensor(info);
            } catch (Exception e) {
                this.log_out();
            }
        }

    }

    /**
     * Called when the old server comes back online.
     * Sends a message to the correspondingclient.
     */
    public void old_online() {
        proxy.old_online();
    }

    public boolean handshake(){
        try {
            proxy.handshake();
            return true;
        }catch (Exception ignored){

        }
        return false;
    }

    /**
     * Logs the client in.
     *
     * @param port the new port of the device.
     */
    public void log_in(int port) {
        super.log_in(port);
        try {

            client = new SaslSocketTransceiver(new InetSocketAddress(this.getIp().toString(),this.getPort()));
            proxy = SpecificRequestor.getClient(FridgeProto.Callback.class, client);

        } catch (IOException e) {
            System.err.println("Error Connecting to fridge");
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

    /**
     * Reconnect to the new server.
     *
     * @param ip     ip of the new server
     * @param port   port of the new server
     * @param backup is the new server a back up?
     */
    public void reconnect(String ip, int port, boolean backup) {
        try{
            proxy.reconnect(ip, port, backup);
        }catch (Exception ignored){

        }

    }

    public boolean is_alive() {

        try {
            proxy.handshake();
        } catch (AvroRemoteException e) {
            return false;
        }
        return true;
    }


}
