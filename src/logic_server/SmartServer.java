package logic_server;

import avro.distributed.proto.*;
import logic_server.Objects.*;
import org.apache.avro.AvroRemoteException;
import org.apache.avro.ipc.SaslSocketServer;
import org.apache.avro.ipc.specific.SpecificResponder;
import org.apache.avro.ipc.Server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmartServer implements ServerProto {

    private Map <Integer, ClientObject> clients = new HashMap<>();
    private Map <Integer, UserObject> users = new HashMap<>();
    private Map <Integer, LightObject> lights = new HashMap<>();
    private Map <Integer, FridgeObject> fridges = new HashMap<>();
    private Map <Integer, SensorObject> sensors = new HashMap<>();
    private Server server = null;

    SmartServer(int port){



        try {
            server = new SaslSocketServer(new SpecificResponder(ServerProto.class,
                    this), new InetSocketAddress(port));
        } catch (IOException e) {
            System.err.println(" [error] Failed to start server");
            e.printStackTrace(System.err);
            System.exit(1);
        }
        server.start();
    }

    public CharSequence connect(CharSequence username, CharSequence ip, int port, ClientType type)
            throws AvroRemoteException {
        int id_counter = 0;
        for (int key : clients.keySet()) {
            id_counter++;
            ClientObject client = clients.get(key);
            if (client.getName().equals(username)) {
                client.log_in();
                System.out.println("user:" + client.getName() + " Logged in");
                return "Logged in succesfully";
            }
        }

        switch(type){
            case USER:
                UserObject new_client = new UserObject(id_counter,ip,port,username.toString());
                users.put(id_counter, new_client);
                clients.put(id_counter, new_client);
                break;

            case LIGHT:
                LightObject new_light = new LightObject(id_counter,ip,port,username.toString());
                lights.put(id_counter, new_light);
                clients.put(id_counter, new_light);
                break;
            case FRIDGE:
                FridgeObject new_fridge = new FridgeObject(id_counter,ip,port,username.toString());
                fridges.put(id_counter, new_fridge);
                clients.put(id_counter, new_fridge);
                break;
            case SENSOR:
                SensorObject new_sensor = new SensorObject(id_counter,ip,port,username.toString());
                sensors.put(id_counter, new_sensor);
                clients.put(id_counter, new_sensor);
                break;
        }

        return "Successfully registered";
    }

    @Override
    public List<ClientDetails> getUsers() throws AvroRemoteException {

        List<ClientDetails> nwlist = new ArrayList<>();

        for (int key: clients.keySet()){
            ClientObject item = clients.get(key);
            ClientDetails record = new ClientDetails();
            record.setName(item.getName());
            record.setType(item.getType());
            record.setStatus(item.isOnline());
            nwlist.add(record);
        }


        return nwlist;
    }

    @Override
    public boolean switchLights(CharSequence lightname) {
        for(int key: lights.keySet()){
            if(lights.get(key).getName().equals(lightname.toString())){

                lights.get(key).switch_light();
                return true;
            }
        }

        return false;
    }

    @Override
    public List<light_status> getLightsStatus() throws AvroRemoteException {
        List<light_status> ret = new ArrayList<>();
        for(int key: lights.keySet()){
            LightObject object = lights.get(key);

            light_status rsp = new light_status();
            rsp.setLightName(object.getName());
            rsp.setOnlineStatus(object.isOnline());
            rsp.setState(object.get_state());

            ret.add(rsp);
        }
        return ret;
    }

    @Override
    public List<CharSequence> getFridgeInventory(CharSequence fridgename) throws AvroRemoteException {


        List<CharSequence> ret = new ArrayList<>();
        for(int key: fridges.keySet()){
            if(fridges.get(key).getName().equals(fridgename.toString()) ){

                ret = fridges.get(key).get_inventory();
                break;
            }
        }
        return ret;
    }

    @Override
    public FridgeDetails openFridge(CharSequence fridgename) throws AvroRemoteException {

        FridgeDetails ret = new FridgeDetails();
        ret.setPort(-1);
        ret.setIp("");

        for(int key: fridges.keySet()){
            FridgeObject item = fridges.get(key);
            if(item.getName().equals(fridgename.toString())){
                ret.setIp(item.getIp());
                ret.setPort(item.getPort());
                break;
            }
        }
        return ret;
    }

    @Override
    public void set_fridge_inventory(CharSequence fridge, List<CharSequence> inventory) {
        for(int key: fridges.keySet()) {
            FridgeObject item = fridges.get(key);
            if (item.getName().equals(fridge.toString())) {
                item.set_inventory(inventory);
                break;
            }
        }
    }

    @Override
    public void temperatureUpdate(CharSequence sensor_name, float temperature) {
        for(int key: sensors.keySet()) {
            SensorObject item = sensors.get(key);
            if (item.getName().equals(sensor_name.toString())) {
                item.add_temperature(temperature);
            }
        }
    }

    @Override
    public List<Float> getTemperatureList(CharSequence sensor_name) throws AvroRemoteException {

        List<Float> ret = new ArrayList<>();
        ret.add((float) -9999);
        for (int key : sensors.keySet()) {
            SensorObject item = sensors.get(key);
            if (item.getName().equals(sensor_name.toString())) {
                ret = item.getTemperature_history();
            }
        }
        return ret;
    }

    public void user_online_check(){

        for (int key : users.keySet()){

            UserObject user = users.get(key);

            if(!user.is_alive()){
                exit_system(key);
            }
        }
    }


    private void exit_system(int key){

        ClientObject client = clients.get(key);
        client.log_out();
    }

    public static void main(String[] args) {

        SmartServer server = new SmartServer(6789);

        while(true){
            try {
                Thread.sleep(5000);
                server.user_online_check();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

