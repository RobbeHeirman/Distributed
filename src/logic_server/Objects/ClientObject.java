/*
  Description: Contains the ClientObject class.
  @Author: Robbe Heirman
  @Version: 1.1
 */

package logic_server.Objects;

import avro.distributed.proto.ClientInfo;
import avro.distributed.proto.ClientType;


/**
 * The ClientObject class is the abstract class for all Server side objects. These are the models in the system.
 * Instances of those classes will contain all information about the objects in the system.
 */
public abstract class ClientObject {

    private int id;
    private CharSequence ip;
    private int port;
    private String name;
    private ClientType type;
    protected boolean online;

    /**
     * The constructor (package private)
     * @param id id of the class. Also referred as key
     * @param ip  ip address of the device where the client of the object is located.
     * @param port Port of the address of the device where the client is located.
     * @param name Unique name of the object.
     * @param type A clientType enumerator. One for each kind of object.
     */
    ClientObject(int id, CharSequence ip, int port, String name, ClientType type) {

        this.id = id;
        this.ip = ip;
        this.port = port;
        this.name = name;
        this.type = type;
        online = true;
    }

    // @return returns the name of the object as a string.
    public String getName() {
        return name;
    }


    // @return returns the ip of the ip of the object.
    public CharSequence getIp() {
        return ip;
    }

    // @return returns the port as an iteger of the client side device.
    public int getPort() {
        return port;
    }

    // @return if the device is online
    public boolean isOnline() {
        return online;
    }

    // @return the type of the device
    public ClientType getType() {
        return this.type;
    }

    // Returns the key
    public int getKey() {
        return this.id;
    }

    /**
     * Log in a device. This device also needs to re send his port. All devices their ports binded dynamicly.
     * so this needs to switch.
     *
     * @param port the new port of the device.
     */
    public void log_in(int port) {
        online = true;
        this.port = port;
    }

    // Logs the device out.
    public void log_out() {
        System.out.println("Server: " + this.getName() + " went offline.");
        online = false;
    }

    ClientInfo convert_client_object(ClientObject client) {

        ClientInfo ret = new ClientInfo();
        ret.setIp(client.getIp());
        ret.setPort(client.getPort());
        ret.setType(client.getType());
        ret.setKey(client.getKey());
        ret.setName(client.getName());
        ret.setOnline(client.isOnline());
        return ret;
    }

    public abstract void reconnect(String ip, int port, boolean backup);
}
