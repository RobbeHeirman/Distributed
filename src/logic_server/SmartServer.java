/*
  Author: Robbe Heirman
  Version: 1.1
  Description: Implementation of the server component. Brief list of what the server can do is found in the project
  description.
 */
package logic_server;

import avro.distributed.proto.*;
import logic_server.Objects.*;

import org.apache.avro.AvroRemoteException;
import org.apache.avro.ipc.SaslSocketServer;
import org.apache.avro.ipc.Server;
import org.apache.avro.ipc.specific.SpecificResponder;
import reader.readfile;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implements SmartServer. SmartServer handles the main logic of our system.
 * Devices will send and retrieve information through smartserver. Smartserver will handle
 * those request in a macro managing way by giving commands to the corresponding objects.
 * Those objects (all derrived from ClientObject) will handle their specific logic.
 */
public class SmartServer implements ServerProto {

    private String ip;

    private Map<Integer, ClientObject> clients = new HashMap<>();
    private Map<Integer, UserObject> users = new HashMap<>();
    private Map<Integer, LightObject> lights = new HashMap<>();
    private Map<Integer, FridgeObject> fridges = new HashMap<>();
    private Map<Integer, SensorObject> sensors = new HashMap<>();

    private Server server = null;
    private int backup_key;
    private int clock = 0;

    /**
     * The constructor of smartserver
     *
     * @param ip The ip of the server
     * @param port port the server will use
     */
    public SmartServer(String ip,int port) {

        this.ip = ip;
        try {
            server = new SaslSocketServer(new SpecificResponder(ServerProto.class,
                    this), new InetSocketAddress(ip,port));
        } catch (IOException e) {
            System.err.println(" [error] Failed to start server");
            e.printStackTrace(System.err);
            System.exit(1);
        }
        server.start();
        System.out.println("Server: Server has been started || ip: " +ip + " || port: " + server.getPort());
    }


    /**
     * Handles Connection information. Every device that connect with the server must call this method.
     * It registers/logs in a device. Keeping track of username, ip and port (needed for push based communication)
     * and type. Will return the key of the object.
     *
     * @param username the name of the device that connects. Note that every device needs a unique name.
     * @param ip       the ip of the device connecting to the system.
     * @param port     the port of the device connecting to the system.
     * @param type     Type of device connection to the system (USER, FRIDGE, LIGHT and sensor).
     * @return the key of the device.
     */
    @Override
    public int connect(CharSequence username, CharSequence ip, int port, ClientType type) throws AvroRemoteException {

        int id_counter = 0;
        for (int key : clients.keySet()) {
            id_counter++;
            ClientObject client = clients.get(key);
            if (client.getName().equals(username.toString())) { // device already in the system so log in
                System.out.println("Server: device " + client.getName() + " Logged in");
                this.client_update(client);
                if (type == ClientType.USER) { //mainly to restore lights to their original state if a user enters the
                    for (int ignored : lights.keySet()) { // building
                        lights.get(ignored).user_up();
                    }
                    for (int ignored : users.keySet()) {
                        if (ignored != client.getKey()) {
                            users.get(ignored).enters_the_building(username); // sending to the other users
                        }                                                     // that this user entered the building
                    }
                }
                client.log_in(port); // handles log_in details on the clientObject
                return id_counter;
            }
        }
        // not an existing user so we register it
        switch (type) {
            case USER:
                UserObject new_client = new UserObject(id_counter, ip, port, username.toString());
                users.put(id_counter, new_client);
                clients.put(id_counter, new_client);
                this.user_update(new_client); //replication call
                this.update_new_item(new_client); // replication call
                for (int ignored : lights.keySet()) { // a user is active in the building so restore lights
                    lights.get(ignored).user_up();
                }

                for (int ignored : users.keySet()) { // sending a notification to the other users someone entered
                    if (ignored != id_counter)
                        users.get(ignored).enters_the_building(new_client.getName());
                }
                break;

            case LIGHT:
                LightObject new_light = new LightObject(id_counter, ip, port, username.toString());
                lights.put(id_counter, new_light);
                clients.put(id_counter, new_light);
                this.light_update(new_light);
                break;

            case FRIDGE:
                FridgeObject new_fridge = new FridgeObject(id_counter, ip, port, username.toString());
                fridges.put(id_counter, new_fridge);
                clients.put(id_counter, new_fridge);
                this.fridge_update(new_fridge);
                this.update_new_item(new_fridge);
                break;

            case SENSOR:
                SensorObject new_sensor = new SensorObject(id_counter, ip, port, username.toString());
                sensors.put(id_counter, new_sensor);
                clients.put(id_counter, new_sensor);
                this.sensor_update(new_sensor);
                break;
        }
        System.out.println("Server: device " + clients.get(id_counter).getName() + " now registered with id " + id_counter);
        return id_counter;
    }

    /**
     * Returns a list of all devices registered and their status.
     *
     * @return a list of ClientDetails. ClientDetails is a record holding info about a client
     */
    @Override
    public List<ClientDetails> getUsers() {

        System.out.println("Server: List of devices is requested...");

        List<ClientDetails> nwlist = new ArrayList<>();

        for (int key : clients.keySet()) {
            ClientObject item = clients.get(key);
            ClientDetails record = new ClientDetails();
            record.setName(item.getName());
            record.setType(item.getType());
            record.setStatus(item.isOnline());
            nwlist.add(record);
        }


        return nwlist;
    }

    /**
     * returns a list of lights and their state.
     *
     * @return a list of light status, light_status is a record with name of light and the corresponding status.
     */
    @Override
    public List<light_status> getLightsStatus() {
        System.out.println("Server: requesting the state of all lights...");
        List<light_status> ret = new ArrayList<>();
        for (int key : lights.keySet()) {
            LightObject object = lights.get(key);

            light_status rsp = new light_status();
            rsp.setLightName(object.getName());
            rsp.setOnlineStatus(object.isOnline());
            rsp.setState(object.get_state());

            ret.add(rsp);
        }
        return ret;
    }

    /**
     * Switches given light to on/off.
     *
     * @param lightname the name of the light
     * @return the new state of the light
     */
    @Override
    public boolean switchLights(CharSequence lightname) {
        for (int key : lights.keySet()) {
            if (lights.get(key).getName().equals(lightname.toString())) {

                if(lights.get(key).switch_light()){
                    this.light_update(lights.get(key));
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns the inventory of a given fridge
     *
     * @param fridgename name of the drige.
     * @return list of CharSequence. Each list item represents an item in the fridge.
     */
    @Override
    public List<CharSequence> getFridgeInventory(CharSequence fridgename) {


        List<CharSequence> ret = new ArrayList<>();
        for (int key : fridges.keySet()) {
            if (fridges.get(key).getName().equals(fridgename.toString())) {

                ret = fridges.get(key).get_inventory();
                break;
            }
        }
        return ret;
    }

    /**
     * A fridge will call this method to inform the server it is empty.
     * The server will forward this message to all users.
     *
     * @param fridge_name name of the fridge that is empty
     */
    @Override
    public void fridgeEmpty(CharSequence fridge_name) {
        System.out.println("Server: Fridge " + fridge_name + " is now empty.");
        for (int key : users.keySet()) {
            users.get(key).fridgeEmpty(fridge_name);
        }
    }

    /**
     * Method is called by a user to open a fridge.
     * The server retrieves the info needed by the user to establish a connection.
     *
     * @param fridge_name name of the fridge we the user wants to connect with.
     * @return A FridgeDetails object. Contains the ip and port of the fridge the user wants to connect with.
     * Port -1 is returned when the fridge is not found. User has to handle this correctly.
     */
    @Override
    public FridgeDetails openFridge(CharSequence fridge_name) {

        FridgeDetails ret = new FridgeDetails();
        ret.setPort(-1);
        ret.setIp("");

        for (int key : fridges.keySet()) {
            FridgeObject item = fridges.get(key);
            if(item.handshake()){
                if (item.getName().equals(fridge_name.toString())) {
                    ret.setIp(item.getIp());
                    ret.setPort(item.getPort());
                    break;
                }
            }
            else{
                item.log_out();
            }
        }
        return ret;
    }

    /**
     * After the fridge is closed again. The server has to update the new inventory state of the fridge.
     * The fridge will call this method to handle that.
     *
     * @param fridge    name of the fridge that wants to update it's inventory.
     * @param inventory a list of CharSequence's, representing the inventory of the fridge.
     */
    @Override
    public void set_fridge_inventory(CharSequence fridge, List<CharSequence> inventory) {
        for (int key : fridges.keySet()) {
            FridgeObject item = fridges.get(key);
            if (item.getName().equals(fridge.toString())) {
                item.set_inventory(inventory);
                this.fridge_update(item);
                break;
            }
        }
    }

    /**
     * A sensor will call this to send a temperature update. Temperatures ar kept track of in a queue
     * for each sensor. If the queue size is capped, the oldest temperature will be removed.
     * The server will also send it's logical time to the sensor as a response.
     *
     * @param sensor_name Name of the senor that sends an update.
     * @param temperature the value of the temperature.
     * @return the logical time of the server.
     */
    @Override
    public float temperatureUpdate(CharSequence sensor_name, float temperature) {
        for (int key : sensors.keySet()) {
            SensorObject item = sensors.get(key);
            if (item.getName().equals(sensor_name.toString())) {
                item.add_temperature(temperature);
                this.sensor_update(item);
            }
        }
        return clock;
    }

    /**
     * A user will ask for the list of temperatures. If there are multiple sensors in the house.
     * An average will be calculated.
     *
     * @return A list of float, the average history of temperatures in the house.
     */
    @Override
    public List<Float> getTemperatureList() {

        int k_l = 0;
        for(int key: clients.keySet()){
            if(clients.get(key).getType() == ClientType.SENSOR){
                k_l = key;
                break;
            }
        }

        List<Float> ret = sensors.get(k_l).getTemperature_history();
        for (int i = 0; i < ret.size(); i++) {
            for (int key : sensors.keySet()) {
                if (sensors.get(0) != sensors.get(key)) {
                    float add = 0;
                    if (sensors.get(key).getTemperature_history().size() > i) {
                        add = sensors.get(key).getTemperature_history().get(i);
                    }
                    ret.set(i, ret.get(i) + add);
                }
            }
        }

        int divider = sensors.size();
        if (divider != 0) {
            for (int k = 0; k < ret.size(); k++) {
                ret.set(k, ret.get(k) / divider);
            }
        }
        return ret;
    }

    /**
     * Converts replicated user data into usable data for the server. All following methods do this for their
     * corresponding device. This is used when the system switches to a new server
     * @param object a ClientInfo object. Object contains all info about a user.
     */
    @Override
    public void install_user(ClientInfo object) {
        UserObject user = new UserObject(object);
        users.put(object.getKey(), user);
        clients.put(object.getKey(), user);
    }

    // As install_user.
    @Override
    public void install_fridge(FridgeInfo object) {

        FridgeObject fridge = new FridgeObject(object);
        fridges.put(object.getClientInfo().getKey(), fridge);
        clients.put(object.getClientInfo().getKey(), fridge);
    }

    // As install_user.
    @Override
    public void install_light(LightInfo object) {

        LightObject light = new LightObject(object);
        lights.put(object.getClientInfo().getKey(), light);
        clients.put(object.getClientInfo().getKey(), light);
    }

    // As install_user.
    @Override
    public void install_sensor(SensorInfo object) {

        SensorObject sensor = new SensorObject(object);
        sensors.put(object.getClientInfo().getKey(), sensor);
        clients.put(object.getClientInfo().getKey(), sensor);
    }

    /**
     * If a client takes the role of a server. The server registers it's key. This is needed when we want to roll back
     * to the old server.
     *
     * @param key of client that takes on the server role.
     */
    public void set_client_server(int key) {
        backup_key = key;
    }

    /**
     * This is called when the old server is back online. This method looks for the device that took over the server
     * role and will send a message that the old server is back up. The device will handle the logic needed to install
     * the old server.
     */
    @Override
    public void old_up() {

        if (fridges.containsKey(backup_key)) { // Either a fridge or a user is the back up server.
            fridges.get(backup_key).old_online();
        } else {
            users.get(backup_key).old_online();
        }

    }

    /**
     * Sends a reconnection message to all the devices.
     * Those devices will connect this server as the new server and also record if this is a back up server.
     *
     * @param is_backup a boolean that is true if this is a back up server.
     */
    @Override
    public void broadcast_reconnection(boolean is_backup) {

        int port = this.server.getPort();
        for (int key : clients.keySet()) {
            clients.get(key).reconnect(this.ip, port, is_backup);
        }
    }

    //Will close the server.
    public void close() {

        server.close();
    }

    private void user_online_check() {

        for (int key : users.keySet()) {

            UserObject user = users.get(key);

            if (!user.is_alive() && user.isOnline()) {
                exit_system(key);
            }
        }

        for (int key : fridges.keySet()) {

            FridgeObject fridge = fridges.get(key);

            if (!fridge.is_alive() && fridge.isOnline()) {
                exit_system(key);
            }
        }
    }

    private void client_update(ClientObject client) {

        int no_key = client.getKey();
        for (int key : users.keySet()) {
            if (no_key != key) {
                users.get(key).replicate_client(client);
            }

        }

        for (int key : fridges.keySet()) {
            if (no_key != key) {
                fridges.get(key).replicate_client(client);
            }
        }
    }


    private void user_update(UserObject user) {
        int no_key = user.getKey();
        for (int key : users.keySet()) {
            if (no_key != key) {
                users.get(key).replicate_user(user);
            }
        }

        for (int key : fridges.keySet()) {
            if (no_key != key) {
                fridges.get(key).replicate_user(user);
            }

        }

    }


    private void fridge_update(FridgeObject fridge) {

        for (int key : users.keySet()) {
            users.get(key).replicate_fridge(fridge);
        }

        for (int key : fridges.keySet()) {
            fridges.get(key).replicate_fridge(fridge);
        }

    }

    private void light_update(LightObject light) {

        for (int key : users.keySet()) {
            users.get(key).replicate_light(light);
        }

        for (int key : fridges.keySet()) {
            fridges.get(key).replicate_light(light);
        }

    }

    private void sensor_update(SensorObject sensor) {

        for (int key : users.keySet()) {
            users.get(key).replicate_sensor(sensor);
        }

        for (int key : fridges.keySet()) {
            fridges.get(key).replicate_sensor(sensor);
        }

    }

    private void update_new_item(FridgeObject object) {
        for (int key : this.users.keySet()) {
            UserObject user = this.users.get(key);
            object.replicate_user(user);
        }

        for (int key : this.fridges.keySet()) {
            FridgeObject user = this.fridges.get(key);
            object.replicate_fridge(user);
        }

        for (int key : this.lights.keySet()) {
            LightObject user = this.lights.get(key);
            object.replicate_light(user);
        }

        for (int key : this.sensors.keySet()) {
            SensorObject user = this.sensors.get(key);
            object.replicate_sensor(user);
        }
    }

    private void update_new_item(UserObject object) {
        for (int key : this.users.keySet()) {
            UserObject user = this.users.get(key);
            object.replicate_user(user);
        }

        for (int key : this.fridges.keySet()) {
            FridgeObject user = this.fridges.get(key);
            object.replicate_fridge(user);
        }

        for (int key : this.lights.keySet()) {
            LightObject user = this.lights.get(key);
            object.replicate_light(user);
        }

        for (int key : this.sensors.keySet()) {
            SensorObject user = this.sensors.get(key);
            object.replicate_sensor(user);
        }

    }


    private void exit_system(int key) {
        ClientObject client = clients.get(key);
        client.log_out();
        client_update(client);

        boolean no_users = true;
        for (int ignored : users.keySet()) {

            if (users.get(ignored).isOnline()) {
                no_users = false;
                if (client.getType() == ClientType.USER) {
                    users.get(ignored).left_the_building(client.getName());
                }
            }
        }

        if (no_users) {
            for (int light_key : lights.keySet()) {
                lights.get(light_key).all_users_down();
            }
        }
    }



    private void progress_interval() {
        clock++;
    }

    public static void main(String[] args) {

        readfile r = new readfile();
        Map<String,String> inf = r.readFile("info.txt");


      /*  PrintStream out = null;
        try {
            out = new PrintStream(new FileOutputStream("test2_output.txt"));
        } catch (FileNotFoundException e) {

        }
        System.setErr(out); */
        SmartServer server = new SmartServer(inf.get("ip_server"),Integer.parseInt(inf.get("port_server")));

        //noinspection InfiniteLoopStatement
        while (true) {
            try {
                Thread.sleep(1000);
                server.user_online_check();
                server.progress_interval();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

