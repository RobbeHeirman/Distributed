package logic_server.Objects;

import avro.distributed.proto.*;
import org.apache.avro.ipc.SaslSocketTransceiver;
import org.apache.avro.ipc.Transceiver;
import org.apache.avro.ipc.specific.SpecificRequestor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Robbe on 7/17/2017.
 */
public class SensorObject extends ClientObject {

    List<Float> temperature_history = new ArrayList<>();

    protected Transceiver client = null;
    protected SensorProto.Callback proxy = null;

    public SensorObject(int id, CharSequence ip, int port, String name) {
        super(id, ip, port, name, ClientType.SENSOR);

        try {

            client = new SaslSocketTransceiver(new InetSocketAddress(port));
            proxy = SpecificRequestor.getClient(SensorProto.Callback.class, client);

        } catch (IOException e) {
            System.err.println("Error Connecting to fridge");
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

    public SensorObject(SensorInfo info){
        super(info.getClientInfo().getKey(), info.getClientInfo().getIp(),info.getClientInfo().getPort(),
                info.getClientInfo().getName().toString(),ClientType.SENSOR);

        try {

            client = new SaslSocketTransceiver(new InetSocketAddress( info.getClientInfo().getPort()));
            proxy = SpecificRequestor.getClient(SensorProto.Callback.class, client);

        } catch (IOException e) {
            System.err.println("Error Connecting to fridge");
            e.printStackTrace(System.err);
            System.exit(1);
        }

        this.temperature_history = info.getTemperatureList();
    }


    public void add_temperature(float temperature){

        System.out.println("Temperature of sensor: " + this.getName() +" updated to " + temperature);
        temperature_history.add(temperature);
        if(this.temperature_history.size() > 10){
            this.temperature_history.remove(0);
        }
    }

    public List<Float> getTemperature_history(){
        return temperature_history;
    }

    public void reconnect(String ip, int port, boolean backup){
        proxy.reconnect(ip, port,backup);
    }
}
