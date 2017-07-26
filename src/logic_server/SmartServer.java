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

    boolean is_backup;

    public SmartServer(int port, boolean is_backup ){

        this.is_backup = is_backup;

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

    public int connect(CharSequence username, CharSequence ip, int port, ClientType type)
            throws AvroRemoteException {
        int id_counter = 0;
        for (int key : clients.keySet()) {
            id_counter++;
            ClientObject client = clients.get(key);
            if (client.getName().equals(username)) {
                client.log_in();
                System.out.println("user:" + client.getName() + " Logged in");
                this.client_update(client);
                return id_counter;
            }
        }

        switch(type){
            case USER:
                UserObject new_client = new UserObject(id_counter,ip,port,username.toString());
                users.put(id_counter, new_client);
                clients.put(id_counter, new_client);
                this.user_update(new_client);
                this.update_new_item(new_client);
                break;

            case LIGHT:
                LightObject new_light = new LightObject(id_counter,ip,port,username.toString());
                lights.put(id_counter, new_light);
                clients.put(id_counter, new_light);
                this.light_update(new_light);
                break;
            case FRIDGE:
                FridgeObject new_fridge = new FridgeObject(id_counter,ip,port,username.toString());
                fridges.put(id_counter, new_fridge);
                clients.put(id_counter, new_fridge);
                this.fridge_update(new_fridge);
                this.update_new_item(new_fridge);
                break;
            case SENSOR:
                SensorObject new_sensor = new SensorObject(id_counter,ip,port,username.toString());
                sensors.put(id_counter, new_sensor);
                clients.put(id_counter, new_sensor);
                this.sensor_update(new_sensor);
                break;
        }

        return id_counter;
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
                this.light_update(lights.get(key));
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
                this.fridge_update(item);
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
                this.sensor_update(item);
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

    private void client_update(ClientObject client){

        for(int key : users.keySet()){
            users.get(key).replicate_client(client);
        }

        for(int key: fridges.keySet()){
            fridges.get(key).replicate_client(client);
        }

    }



    private void user_update(UserObject user){

        for(int key : users.keySet()){
            users.get(key).replicate_user(user);
        }

        for(int key: fridges.keySet()){
            fridges.get(key).replicate_user(user);
        }

    }



    private void fridge_update(FridgeObject fridge){

        for(int key : users.keySet()){
            System.out.println("bla");
            users.get(key).replicate_fridge(fridge);
        }

        for(int key: fridges.keySet()){
            fridges.get(key).replicate_fridge(fridge);
        }

    }

    private void light_update(LightObject light){

        for(int key : users.keySet()){
            users.get(key).replicate_light(light);
        }

        for(int key: fridges.keySet()){
            fridges.get(key).replicate_light(light);
        }

    }

    private void sensor_update(SensorObject sensor){

        for(int key : users.keySet()){
            users.get(key).replicate_sensor(sensor);
        }

        for(int key: fridges.keySet()){
            fridges.get(key).replicate_sensor(sensor);
        }

    }

    private void update_new_item(FridgeObject object){
        for(int key: this.users.keySet()){
            UserObject user = this.users.get(key);
            object.replicate_user(user);
        }

        for(int key: this.fridges.keySet()){
            FridgeObject user = this.fridges.get(key);
            object.replicate_fridge(user);
        }

        for(int key: this.lights.keySet()){
            LightObject user = this.lights.get(key);
            object.replicate_light(user);
        }

        for(int key: this.sensors.keySet()){
            SensorObject user = this.sensors.get(key);
            object.replicate_sensor(user);
        }
    }

    private void update_new_item(UserObject object){
        for(int key: this.users.keySet()){
            UserObject user = this.users.get(key);
            object.replicate_user(user);
        }

        for(int key: this.fridges.keySet()){
            FridgeObject user = this.fridges.get(key);
            object.replicate_fridge(user);
        }

        for(int key: this.lights.keySet()){
            LightObject user = this.lights.get(key);
            object.replicate_light(user);
        }

        for(int key: this.sensors.keySet()){
            SensorObject user = this.sensors.get(key);
            object.replicate_sensor(user);
        }

    }


    public void install_user(ClientInfo object){

        UserObject user = new UserObject(object);
        users.put(object.getKey(), user);
        clients.put(object.getKey(),user);
    }

    public void install_fridge(FridgeInfo object){

        FridgeObject fridge = new FridgeObject(object);
        fridges.put(object.getClientInfo().getKey(), fridge);
        clients.put(object.getClientInfo().getKey(),fridge);
    }

    public void install_light(LightInfo object){

        LightObject light = new LightObject(object);
        lights.put(object.getClientInfo().getKey(), light);
        clients.put(object.getClientInfo().getKey(),light);
    }

    public void install_sensor(SensorInfo object){

        SensorObject sensor = new SensorObject(object);
        sensors.put(object.getClientInfo().getKey(), sensor);
        clients.put(object.getClientInfo().getKey(),sensor);
    }

    public void broadcast_reconnection(){

        int port = this.server.getPort();
        for(int key: clients.keySet()){
            clients.get(key).reconnect("localhost", port, this.is_backup);
        }
    }

    private void exit_system(int key){

        ClientObject client = clients.get(key);
        client.log_out();
        client_update(client);
    }

    void switch_to_backup(){
        this.is_backup = true;
    }

    public static void main(String[] args) {

        SmartServer server = new SmartServer(6789, false);

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

