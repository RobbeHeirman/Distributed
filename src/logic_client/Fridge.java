package logic_client;

import avro.distributed.proto.*;
import org.apache.avro.AvroRemoteException;
import org.apache.avro.ipc.SaslSocketServer;
import org.apache.avro.ipc.specific.SpecificResponder;
import reader.readfile;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Fridge extends ServerClient implements FridgeProto {

    private List<CharSequence> items = new ArrayList<>();

    private Fridge(String server_ip, int server_port,String ip, CharSequence name) {
        super(server_ip, server_port,ip, name, ClientType.FRIDGE);

        System.out.println("Fridge: I am now online.");
    }

    @Override
    void set_up_server() {
        try {
            server = new SaslSocketServer(new SpecificResponder(FridgeProto.class,
                    this), new InetSocketAddress(ip,0));
        } catch (IOException e) {
            System.err.println(" [error] Failed to start server");
            e.printStackTrace(System.err);
            System.exit(1);
        }
        server.start();
    }

    @Override
    public void addItem(CharSequence item) {
        this.items.add(item);
    }

    @Override
    public List<CharSequence> contains() throws AvroRemoteException {
        return this.items;
    }

    @Override
    public void removeItem(CharSequence item) {

        this.items.remove(item);

        if(this.items.isEmpty()){
            proxy.fridgeEmpty(this.name);
        }
    }

    @Override
    public void close_fridge() {
        proxy.set_fridge_inventory(this.name, this.items);
        System.out.println("Getting closed.");
    }

    @Override
    public void update_client(ClientInfo client_info) {
        this.add_client(client_info);
    }

    @Override
    public void update_user(ClientInfo fridge_info) {
        this.add_user(fridge_info);
    }

    @Override
    public void update_fridge(FridgeInfo fridge_info) {

        this.add_fridge(fridge_info);
    }

    @Override
    public void update_light(LightInfo fridge_info) {

        this.add_light(fridge_info);
    }

    @Override
    public void update_sensor(SensorInfo fridge_info) {

        this.add_sensor(fridge_info);
    }

    @Override
    public void send_UID(int UID) {
        super.send_UID(UID);
    }

    @Override
    public void reconnect(CharSequence ip, int port, boolean back_up) {
        super.reconnect(ip.toString(),port, back_up);
    }

    @Override
    public void old_online(){
        super.old_online();
    }

    @Override
    public int handshake() throws AvroRemoteException {
        return 0;
    }


    public static void main(String args[]) {

        readfile r = new readfile();
        Map<String,String> inf = r.readFile("info.txt");

        String s_ip = inf.get("ip_server");
        int s_port = Integer.parseInt(inf.get("port_server"));
        String c_ip = inf.get("ip_client");

        String name = "default_fridge";
        for(String s: args){
            name = s;
        }


        Fridge fridge = new Fridge(s_ip, s_port,c_ip,name);
        fridge.connect();
        while (true) {
            try {
                Thread.sleep(5000);
                if(fridge.is_backup()){
                    if(fridge.ping()){
                        System.out.println("Server back up!");
                        fridge.server_back_up();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}


