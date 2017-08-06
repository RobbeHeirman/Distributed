package logic_server.Objects;

import avro.distributed.proto.*;

import org.apache.avro.ipc.SaslSocketTransceiver;
import org.apache.avro.ipc.Transceiver;
import org.apache.avro.ipc.specific.SpecificRequestor;

import java.io.IOException;
import java.net.InetSocketAddress;


/**
 * LightObject class. All logic for the server side instances of a light.
 */
public class LightObject extends ClientObject {

    private boolean on = false;

    protected Transceiver client = null;
    private LightProto.Callback proxy = null;

    /**
     * Constructor
     *
     * @param id   id
     * @param ip   ip
     * @param port port
     * @param name name
     */
    public LightObject(int id, CharSequence ip, int port, String name) {
        super(id, ip, port, name, ClientType.LIGHT);

        try {

            client = new SaslSocketTransceiver(new InetSocketAddress(ip.toString(),port));
            proxy = SpecificRequestor.getClient(LightProto.Callback.class, client);

        } catch (IOException e) {
            System.err.println("Error Connecting light");
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

    /**
     * Alternate constructor out of a LightInfo object
     *
     * @param info A LightInfo object.
     */
    public LightObject(LightInfo info) {
        super(info.getClientInfo().getKey(), info.getClientInfo().getIp(), info.getClientInfo().getPort(),
                info.getClientInfo().getName().toString(), ClientType.LIGHT);
        this.online = info.getClientInfo().getOnline();
        try {

            client = new SaslSocketTransceiver(
                    new InetSocketAddress(info.getClientInfo().getIp().toString(), info.getClientInfo().getPort()));
            proxy = SpecificRequestor.getClient(LightProto.Callback.class, client);

        } catch (IOException e) {
            System.err.println("Error Connecting to fridge");
            e.printStackTrace(System.err);
            System.exit(1);
        }

        this.on = info.getState();
    }

    /**
     * Method called for switching the light
     */
    public boolean switch_light() {

        on = !on;
        try{
            proxy.set_light(on);
            return true;
        } catch (Exception e){
            this.log_out();
        }
        return false;
    }

    public boolean get_state() {
        return on;
    }

    // If all users are down. This is called and the lights are turned off
    public void all_users_down() {
        proxy.set_light(false);
    }

    // If a user is up. Lights are set to their original state
    public void user_up() {
        proxy.set_light(on);
    }

    public void log_in(int port) {
        super.log_in(port);
        try {

            client = new SaslSocketTransceiver(new InetSocketAddress(this.getIp().toString(),this.getPort()));
            proxy = SpecificRequestor.getClient(LightProto.Callback.class, client);

        } catch (IOException e) {
            System.err.println("Error Connecting to fridge");
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

    public void reconnect(String ip, int port, boolean backup) {
        proxy.reconnect(ip, port, backup);
    }
}
