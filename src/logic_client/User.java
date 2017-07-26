package logic_client;

import asg.cliche.ShellFactory;
import avro.distributed.proto.*;
import org.apache.avro.AvroRemoteException;
import org.apache.avro.ipc.SaslSocketServer;
import org.apache.avro.ipc.SaslSocketTransceiver;
import org.apache.avro.ipc.Transceiver;
import org.apache.avro.ipc.specific.SpecificRequestor;
import org.apache.avro.ipc.specific.SpecificResponder;
import ui.ShellClosed;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Robbe on 7/15/2017.
 */
public class User extends ServerClient implements UserProto {

    protected Transceiver fridge_transceiver = null;
    protected FridgeProto.Callback fridge_client = null;

    User(CharSequence name) {
        super(name, ClientType.USER);
    }

    @Override
    void set_up_server() {
        try {
            server = new SaslSocketServer(new SpecificResponder(UserProto.class,
                    this), new InetSocketAddress(0));
        } catch (IOException e) {
            System.err.println(" [error] Failed to start server");
            e.printStackTrace(System.err);
            System.exit(1);
        }
        server.start();
    }


    public List<ClientDetails> getDevicesUsers() {
        /*
         * Sends a request to the server asking for all connected devices.
		 * (fridge's,lights,sensors and other users)
		 *
		 * @return a CharSequence of all the devices connected to the server.
		 */
        List<ClientDetails> response = new ArrayList<>();

        try {

            response = proxy.getUsers();

        } catch (IOException e) {
            System.err.println("Error asking for devices");
            this.server_down();
        }

        return response;
    }

    public boolean switch_light(CharSequence light_name) {
        try {

            return proxy.switchLights(light_name);
        } catch (IOException e) {
            System.out.println("Could not switch light");
            this.server_down();
        }
        return false;
    }


    public CharSequence getLightsStatus() {

        CharSequence response = "";


        try {
            List<light_status> lstat = proxy.getLightsStatus();

            for (light_status light : lstat) {

                response = response + light.getLightName().toString() + "\t";
                if (light.getState()) {
                    response = response + "Aan\t";
                } else {
                    response = response + "Uit\t";
                }

                if (light.getOnlineStatus()) {
                    response = response + "Online\t";
                } else {
                    response = response + "Offline\t";
                }
                response = response + "\n";
            }
        } catch (IOException e) {
            System.err.println("Could not get status");
            this.server_down();
        }


        return response;
    }

    public CharSequence getFridgeInventory(CharSequence fridge){
        CharSequence rsp = "";
        try {
            List<CharSequence> inv = proxy.getFridgeInventory(fridge);
            for(CharSequence item : inv) rsp = rsp + item.toString() +"\n";
        } catch (AvroRemoteException e) {
            System.err.println("Could not get Fridge inventory");
            this.server_down();
        }
        return rsp;
    }

    public CharSequence getFridgeInventoryOpen(){
        CharSequence rsp = "";
        try {
            List<CharSequence> inv = fridge_client.contains();
            for(CharSequence item : inv) rsp = rsp + item.toString() +"\n";
        } catch (AvroRemoteException e) {
            System.err.println("Could not get Fridge inventory");
            this.server_down();
        }
        return rsp;
    }


    public boolean open_fridge(CharSequence fridge){

        try{
            FridgeDetails det = this.proxy.openFridge(fridge);
            if(det.getPort() == -1){
                return false;
            }
            else{
                this.fridge_transceiver = new SaslSocketTransceiver(
                        new InetSocketAddress(det.getIp().toString(),det.getPort()));

                this.fridge_client = SpecificRequestor.getClient(FridgeProto.Callback.class, fridge_transceiver);
                return true;
            }
        } catch (AvroRemoteException e) {
            System.err.println("problems opening the fridge");
        } catch (IOException e) {
            System.err.println("Problems estabilishing fridge connection");
        }
        return false;
    }

    public void add_item(CharSequence item){

        fridge_client.addItem(item);
    }

    public void remove_item(CharSequence item){
        fridge_client.removeItem(item);
    }

    public void close_fridge(){

        fridge_client.close_fridge();
        try {
            fridge_transceiver.close();
        } catch (IOException e) {
            System.err.println("Cannot close fridge transceiver");
            e.printStackTrace();
        }
    }

    public float current_temperature(CharSequence sensor_name){
            float ret = -9999;
        try {
            List<Float> temperature_list = proxy.getTemperatureList(sensor_name);
            ret = temperature_list.get(0);
        } catch (AvroRemoteException e) {
            System.err.println("Could not retrieve temperature");
            this.server_down();
        }
        return ret;
    }

    public List<Float> get_temperature_history(CharSequence sensor_name){
        List<Float> temperature_list = null;
        try {
            temperature_list = proxy.getTemperatureList(sensor_name);
            ;
        } catch (AvroRemoteException e) {
            System.err.println("Could not retrieve temperature history");
            this.server_down();
        }
        return temperature_list;
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
    public Void is_alive(){
        return null;
    }




    public static void main(String[] args) {

        User me = new User("Robbe");
        me.connect();

        try {
            ShellFactory.createConsoleShell("SmartHome/", "", new ShellClosed(me)).commandLoop();
            me.close_connection();
        } catch (IOException e) {
            System.err.println("Problems with the console Shell");
        }

    }


}

