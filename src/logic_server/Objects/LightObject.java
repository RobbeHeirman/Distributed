package logic_server.Objects;

/**
 * Created by Robbe on 7/15/2017.
 */

import avro.distributed.proto.*;
import org.apache.avro.ipc.SaslSocketTransceiver;
import org.apache.avro.ipc.Server;
import org.apache.avro.ipc.Transceiver;
import org.apache.avro.ipc.specific.SpecificRequestor;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by Robbe on 7/15/2017.
 */
public class LightObject extends ClientObject {

    private boolean on = false;

    protected Transceiver client = null;
    protected LightProto.Callback proxy = null;

    public LightObject(int id, CharSequence ip, int port, String name) {
        super(id, ip, port, name, ClientType.LIGHT);

        try {

            client = new SaslSocketTransceiver(new InetSocketAddress(port));
            proxy = SpecificRequestor.getClient(LightProto.Callback.class, client);

        } catch (IOException e) {
            System.err.println("Error Connecting light");
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

    public LightObject(LightInfo info){
        super(info.getClientInfo().getKey(), info.getClientInfo().getIp(),info.getClientInfo().getPort(),
                info.getClientInfo().getName().toString(),ClientType.LIGHT);

        try {

            client = new SaslSocketTransceiver(new InetSocketAddress( info.getClientInfo().getPort()));
            proxy = SpecificRequestor.getClient(LightProto.Callback.class, client);

        } catch (IOException e) {
            System.err.println("Error Connecting to fridge");
            e.printStackTrace(System.err);
            System.exit(1);
        }

        this.on = info.getState();
    }

    public void switch_light() {

        if (on == false) {
            on = true;
        } else {
            on = false;
        }
        proxy.set_light(on);
    }

    public boolean get_state(){return on;}

    public void reconnect(String ip, int port, boolean backup){
        proxy.reconnect(ip, port, backup);
    }
}
