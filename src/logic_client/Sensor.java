package logic_client;

import avro.distributed.proto.ClientType;
import avro.distributed.proto.LightProto;
import avro.distributed.proto.SensorProto;
import org.apache.avro.AvroRemoteException;
import org.apache.avro.ipc.SaslSocketServer;
import org.apache.avro.ipc.specific.SpecificResponder;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by Robbe on 7/17/2017.
 */
public class Sensor extends Client implements SensorProto {
    Sensor(CharSequence name) {
        super(name, ClientType.SENSOR);
    }

    float temperature = 20;

    @Override
    void set_up_server() {
        try {
            server = new SaslSocketServer(new SpecificResponder(SensorProto.class,
                    this), new InetSocketAddress(0));
        } catch (IOException e) {
            System.err.println(" [error] Failed to start server");
            e.printStackTrace(System.err);
            System.exit(1);
        }
        server.start();
    }

    public void updateTemperature(){
		/*
		 * Updates our temperature to a new value.
		 * Original temp + [-1,1]
		 */

        float random1 = (float) Math.random();

        float random2 = (float) Math.random();

        if(random2 < 0.5){

            temperature -= random1;
        }

        else{
            temperature += random1;
        }

        proxy.temperatureUpdate(this.name, temperature);
        System.out.println("Temperature updated to " + this.temperature);

    }
    public static void main(String args[]) {

        Sensor sensor = new Sensor("Sensor0");
        while (true) {
            try {
                Thread.sleep(5000);
                sensor.updateTemperature();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
