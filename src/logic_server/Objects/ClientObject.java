package logic_server.Objects;

import avro.distributed.proto.ClientInfo;
import avro.distributed.proto.ClientType;

/**
 * Created by Robbe on 7/13/2017.
 */


public abstract class ClientObject {

    private int id;
    private CharSequence ip;
    private int port;
    private String name;
    private ClientType type;
    private boolean online;

    public ClientObject(int id, CharSequence ip, int port, String name, ClientType type){

        this.id = id;
        this.ip = ip;
        this.port = port;
        this.name = name;
        this.type = type;
        online = true;
    }

    public String getName(){
        return name;
    }

    public CharSequence getIp() {return ip;}

    public int getPort() {return port;}

    public boolean isOnline(){return online;}

    public ClientType getType(){return this.type;}

    public void log_in(){
        online = true;
    }

    public void log_out(){ online = false;}

    public int getKey(){return this.id;}

    protected ClientInfo convert_client_object(ClientObject client){

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
