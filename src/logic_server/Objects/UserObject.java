package logic_server.Objects;

import avro.distributed.proto.*;
import org.apache.avro.AvroRemoteException;
import org.apache.avro.ipc.SaslSocketTransceiver;
import org.apache.avro.ipc.Transceiver;
import org.apache.avro.ipc.specific.SpecificRequestor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

@SuppressWarnings("Duplicates")
public class UserObject extends ClientObject {

    protected Transceiver client = null;
    private UserProto.Callback proxy = null;

    public UserObject(int id, CharSequence ip, int port, String name) {
        super(id, ip, port, name, ClientType.USER);

        try {

            client = new SaslSocketTransceiver(new InetSocketAddress(ip.toString(),port));
            proxy = SpecificRequestor.getClient(UserProto.Callback.class, client);

        } catch (IOException e) {
            System.err.println("Error Connecting light");
            e.printStackTrace(System.err);
            System.exit(1);
        }

    }

    public UserObject(ClientInfo info) {
        super(info.getKey(), info.getIp(), info.getPort(), info.getName().toString(), ClientType.USER);
        this.online = info.getOnline();
        try {

            client = new SaslSocketTransceiver(new InetSocketAddress(info.getIp().toString(),info.getPort()));
            proxy = SpecificRequestor.getClient(UserProto.Callback.class, client);

        } catch (IOException e) {
            System.err.println("Error Connecting to fridge");
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

    public void fridgeEmpty(CharSequence fridge_name) {
        proxy.fridgeEmpty(fridge_name);
    }


    public boolean is_alive() {

        try {
            proxy.is_alive();
        } catch (AvroRemoteException e) {
            return false;
        }
        return true;
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

    public void left_the_building(CharSequence user) {
        proxy.user_leaves(user);
    }

    public void enters_the_building(CharSequence user) {
        if(this.isOnline()){
            try{
                proxy.user_enters(user);
            }catch (Exception ignored){

            }
        }

    }


    public void reconnect(String ip, int port, boolean backup) {
        proxy.reconnect(ip, port, backup);
    }

    public void old_online() {
        proxy.old_online();
    }


    public void log_in(int port) {
        super.log_in(port);

        try {
            client.close();
            client = new SaslSocketTransceiver(new InetSocketAddress(this.getIp().toString(),this.getPort()));
            proxy = SpecificRequestor.getClient(UserProto.Callback.class, client);

        } catch (IOException e) {
            System.err.println("Error Connecting to fridge");
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }


}

