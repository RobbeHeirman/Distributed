package logic_server.Objects;

import avro.distributed.proto.ClientType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Robbe on 7/17/2017.
 */
public class SensorObject extends ClientObject {

    List<Float> temperature_history = new ArrayList<>();

    public SensorObject(int id, CharSequence ip, int port, String name) {
        super(id, ip, port, name, ClientType.SENSOR);
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
}
