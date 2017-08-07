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
 * Sensor objects. Contains the logic for all server side sensor instances.
 */
public class SensorObject extends ClientObject {

    private List<Float> temperature_history = new ArrayList<>();

    protected Transceiver client = null;
    private SensorProto.Callback proxy = null;

    // Constructor
    public SensorObject(int id, CharSequence ip, int port, String name) {
        super(id, ip, port, name, ClientType.SENSOR);

        try {

            client = new SaslSocketTransceiver(new InetSocketAddress(ip.toString(),port));
            proxy = SpecificRequestor.getClient(SensorProto.Callback.class, client);

        } catch (IOException e) {
            System.err.println("Error Connecting to fridge");
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

    // Constructor out of an info object
    public SensorObject(SensorInfo info){
        super(info.getClientInfo().getKey(), info.getClientInfo().getIp(),info.getClientInfo().getPort(),
                info.getClientInfo().getName().toString(),ClientType.SENSOR);
        this.online = info.getClientInfo().getOnline();
        try {

            client = new SaslSocketTransceiver(
                    new InetSocketAddress(info.getClientInfo().getIp().toString(), info.getClientInfo().getPort()));
            proxy = SpecificRequestor.getClient(SensorProto.Callback.class, client);

        } catch (IOException e) {
            System.err.println("Error Connecting to fridge");
            e.printStackTrace(System.err);
            System.exit(1);
        }

        this.temperature_history = info.getTemperatureList();
    }

    /**
     * Add's a temperature to the temperature history
     * @param temperature temperature that needs to be added
     */
    public void add_temperature(float temperature){

        System.out.println("Server: Temperature of sensor: " + this.getName() +" updated to " + temperature);
        temperature_history.add(temperature);
        if(this.temperature_history.size() > 10){
            this.temperature_history.remove(0);
        }
    }

    // temperature_history getter
    public List<Float> getTemperature_history(){
        return temperature_history;
    }

    public void reconnect(String ip, int port, boolean backup){
        proxy.reconnect(ip, port,backup);
    }

    public void log_in(int port){
        super.log_in(port);
        try {

            client = new SaslSocketTransceiver(new InetSocketAddress(this.getIp().toString(), this.getPort()));
            proxy = SpecificRequestor.getClient(SensorProto.Callback.class, client);

        } catch (IOException e) {
            System.err.println("Error Connecting to fridge");
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }
}
