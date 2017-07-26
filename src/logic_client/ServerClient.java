package logic_client;

import avro.distributed.proto.*;
import logic_server.SmartServer;
import org.apache.avro.ipc.SaslSocketTransceiver;
import org.apache.avro.ipc.Transceiver;
import org.apache.avro.ipc.specific.SpecificRequestor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Robbe on 7/22/2017.
 */
public abstract class ServerClient extends Client {

    private Map<Integer,ClientInfo> client_rep;
    private Map<Integer,ClientInfo> user_rep;
    private Map<Integer,FridgeInfo> fridge_rep;
    private Map<Integer,LightInfo> light_rep;
    private Map<Integer,SensorInfo> sensor_rep;

    private boolean participant = false;
    private ClientInfo neighbour_info = null;
    private SmartServer back_up = null;

    private Transceiver neighbour_transceiver = null;
    private UserProto.Callback neighbour_proxy_user = null;
    private FridgeProto.Callback neighbour_proxy_fridge = null;

    ServerClient(CharSequence name, ClientType type) {
        super(name, type);

        user_rep = new HashMap<>();
        client_rep = new HashMap<>();
        fridge_rep = new HashMap<>();
        light_rep = new HashMap<>();
        sensor_rep = new HashMap<>();
    }

    protected void add_client(ClientInfo client){
        int key = client.getKey();
        ClientInfo cl = client_rep.get(key);

        cl.setOnline(client.getOnline());
    }

    protected void add_user(ClientInfo user){

        int key = user.getKey();
        client_rep.put(key,user);
        user_rep.put(key, user);
    }

    protected void add_fridge(FridgeInfo fridge){

        ClientInfo client  = fridge.getClientInfo();
        int key = client.getKey();
        client_rep.put(key,client);

        fridge_rep.put(key,fridge);
    }

    protected void add_light(LightInfo light){

        ClientInfo client  = light.getClientInfo();
        int key = client.getKey();
        client_rep.put(key,client);

        light_rep.put(key,light);
    }

    protected void add_sensor(SensorInfo sensor){

        ClientInfo client  = sensor.getClientInfo();
        int key = client.getKey();
        client_rep.put(key,client);

        sensor_rep.put(key,sensor);
    }

    protected ClientInfo find_neighbour (){

        ClientInfo cl ;
        int key = this.key + 1;
        while(true){

            if(key >= this.client_rep.size()){
                key = 0 ;
            }
            if(fridge_rep.containsKey(key) || client_rep.containsKey(key)){
                cl = client_rep.get(key);
                System.out.println("check");
                neighbour_info = cl;
                return cl;
            }
            key++;
        }
    }

    protected void server_down(){
        System.out.println("Server down! starting the election");
        participant = true;
        neighbour_info = this.find_neighbour();
        this.connect_with_neighbour(neighbour_info);
        forward_int(this.key);
    }

    protected void forward_int(int num){
        switch(neighbour_info.getType()){

            case USER:
                neighbour_proxy_user.send_UID(num);
                break;
            case FRIDGE:
                neighbour_proxy_fridge.send_UID(num);
                break;
            default:
                System.err.println("This client is not a fridge or a USER!");
                System.exit(1);

        }
    }

    protected void connect_with_neighbour(ClientInfo info){
        try {
            neighbour_transceiver = new SaslSocketTransceiver(new InetSocketAddress(info.getPort()));

            switch(info.getType()){

                case USER:
                    neighbour_proxy_user = SpecificRequestor.getClient(UserProto.Callback.class, neighbour_transceiver);
                    break;
                case FRIDGE:

                    neighbour_proxy_fridge = SpecificRequestor.getClient(FridgeProto.Callback.class, neighbour_transceiver);
                    break;
                default:
                    System.err.println("This client is not a fridge or a USER!");
                    System.exit(1);
            }

        } catch (IOException e) {
            System.err.println("Error Connecting server");
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

    protected void send_UID(int UID) {

        boolean was_participant = participant;
        if(participant == false){
            ClientInfo cl = find_neighbour();
            connect_with_neighbour(cl);
            participant = true;
        }

        if(UID > key){
            forward_int(UID);


        }
        else if(UID < key && !was_participant){
            forward_int(key);
        }

        else if(UID == key){
            System.out.println("I am the new server!");
            participant = false;

            back_up = new SmartServer(0, true);

            for (int key: user_rep.keySet()){
                back_up.install_user(user_rep.get(key));
            }

            for (int key: fridge_rep.keySet()){
                back_up.install_fridge(fridge_rep.get(key));
            }

            for (int key: light_rep.keySet()){
                back_up.install_light(light_rep.get(key));
            }

            for (int key: sensor_rep.keySet()){
                back_up.install_sensor(sensor_rep.get(key));
            }
        }

        back_up.broadcast_reconnection();

    }

    public void reconnect(String ip, int port, boolean back_up){
        participant = false;
        super.reconnect(ip, port, back_up);
    }
}
