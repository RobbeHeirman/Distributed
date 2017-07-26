package logic_server.Objects;

import avro.distributed.proto.*;
import org.apache.avro.AvroRemoteException;
import org.apache.avro.ipc.SaslSocketTransceiver;
import org.apache.avro.ipc.Transceiver;
import org.apache.avro.ipc.specific.SpecificRequestor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

/**
 * Created by Robbe on 7/15/2017.
 */
public class UserObject extends ClientObject {

    protected Transceiver client = null;
    protected UserProto.Callback proxy = null;

   public UserObject(int id, CharSequence ip, int port, String name){
        super(id, ip, port, name, ClientType.USER);

       try {

           client = new SaslSocketTransceiver(new InetSocketAddress(port));
           proxy = SpecificRequestor.getClient(UserProto.Callback.class, client);

       } catch (IOException e) {
           System.err.println("Error Connecting light");
           e.printStackTrace(System.err);
           System.exit(1);
       }

    }

    public UserObject(ClientInfo info){
        super(info.getKey(), info.getIp(),info.getPort(), info.getName().toString(),ClientType.USER);

        try {

            client = new SaslSocketTransceiver(new InetSocketAddress( info.getPort()));
            proxy = SpecificRequestor.getClient(UserProto.Callback.class, client);

        } catch (IOException e) {
            System.err.println("Error Connecting to fridge");
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }


    public boolean is_alive(){

       try{
           proxy.is_alive();
       } catch (AvroRemoteException e) {
           return false;
       }
       return true;
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

