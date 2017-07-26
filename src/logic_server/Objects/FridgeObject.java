package logic_server.Objects;

import avro.distributed.proto.*;
import org.apache.avro.ipc.SaslSocketTransceiver;
import org.apache.avro.ipc.Transceiver;
import org.apache.avro.ipc.specific.SpecificRequestor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Robbe on 7/17/2017.
 */
public class FridgeObject extends ClientObject {

    protected Transceiver client = null;
    protected FridgeProto.Callback proxy = null;
    protected List<CharSequence> items = new ArrayList<>();

    public FridgeObject(int id, CharSequence ip, int port, String name) {
        super(id, ip, port, name, ClientType.FRIDGE);

        try {

            client = new SaslSocketTransceiver(new InetSocketAddress( port));
            proxy = SpecificRequestor.getClient(FridgeProto.Callback.class, client);

        } catch (IOException e) {
            System.err.println("Error Connecting to fridge");
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

    public FridgeObject(FridgeInfo info){
        super(info.getClientInfo().getKey(), info.getClientInfo().getIp(),info.getClientInfo().getPort(),
                info.getClientInfo().getName().toString(),ClientType.FRIDGE);

        try {

            client = new SaslSocketTransceiver(new InetSocketAddress( info.getClientInfo().getPort()));
            proxy = SpecificRequestor.getClient(FridgeProto.Callback.class, client);

        } catch (IOException e) {
            System.err.println("Error Connecting to fridge");
            e.printStackTrace(System.err);
            System.exit(1);
        }

        this.set_inventory(info.getInventory());
    }

    public List<CharSequence> get_inventory(){
        return items;}


    public void set_inventory(List<CharSequence> new_inventory){
        this.items = new ArrayList<>( new_inventory);
    }

    public void replicate_client(ClientObject cliento){
        ClientInfo client = this.convert_client_object(cliento);
        proxy.update_client(client);
    }

    public void replicate_user(UserObject user){

        ClientInfo client = this.convert_client_object(user);
        proxy.update_user(client);

    }

    public void replicate_fridge(FridgeObject fridge){

        ClientInfo client = this.convert_client_object(fridge);
        List<CharSequence> inventory = fridge.get_inventory();

        FridgeInfo info = new FridgeInfo(client,inventory);
        proxy.update_fridge(info);
    }

    public void replicate_light(LightObject light){

        ClientInfo client = this.convert_client_object(light);
        boolean on = light.get_state();

        LightInfo info = new LightInfo(client,on);
        proxy.update_light(info);
    }

    public void replicate_sensor(SensorObject sensor){

        ClientInfo client = this.convert_client_object(sensor);
        List<Float> temperatures = sensor.getTemperature_history();

        SensorInfo info = new SensorInfo(client,temperatures);
        proxy.update_sensor(info);
    }

    public void reconnect(String ip, int port, boolean backup){
        proxy.reconnect(ip, port, backup);
    }


}
