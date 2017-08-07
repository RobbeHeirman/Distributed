package logic_client;

import avro.distributed.proto.ClientType;
import avro.distributed.proto.SensorProto;
import org.apache.avro.AvroRemoteException;
import org.apache.avro.ipc.SaslSocketServer;
import org.apache.avro.ipc.specific.SpecificResponder;
import reader.readfile;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.util.Map;


public class Sensor extends Client implements SensorProto {
    private Sensor(String s_ip, int s_port, String ip, CharSequence name,float temp, float drift) {
        super(s_ip, s_port,ip, name, ClientType.SENSOR);

        this.temperature = temp;
        this.drift = drift;

    }

    private float temperature;
    private float clock = 0;
    private float drift;
    private int counter = 0;


    @Override
    void set_up_server() {
        try {
            server = new SaslSocketServer(new SpecificResponder(SensorProto.class,
                    this), new InetSocketAddress(ip,0));
        } catch (IOException e) {
            System.err.println(" [error] Failed to start server");
            e.printStackTrace(System.err);
            System.exit(1);
        }
        server.start();
    }

    private void updateTemperature() {
        /*
         * Updates our temperature to a new value.
		 * Original temp + [-1,1]
		 */

        if (counter == 5) {
            counter = 0;
            float random1 = (float) Math.random();

            float random2 = (float) Math.random();

            if (random2 < 0.5) {

                temperature -= random1;
            } else {
                temperature += random1;
            }

            try {
                float send_time = System.nanoTime(); //Cristian's algorithm
                float server_time = proxy.temperatureUpdate(this.name, temperature);
                float receive_time = System.nanoTime();

                float delta = receive_time - send_time;

                clock = server_time + (delta / 2);

            } catch (AvroRemoteException ignored) {

            }
            System.out.println("Temperature updated to " + this.temperature + "/ set clock to " + clock);

        }
        counter++;
        clock += 1 + drift;
    }


    public static void main(String args[]) {

        PrintStream out = null;
        try {
            out = new PrintStream(new FileOutputStream("test2_output.txt"));
        } catch (FileNotFoundException ignored) {

        }
        System.setErr(out);

        readfile r = new readfile();
        Map<String,String> inf = r.readFile("info.txt");

        String s_ip = inf.get("ip_server");
        int s_port = Integer.parseInt(inf.get("port_server"));
        String c_ip = inf.get("ip_client");

        String name = "default_sensor";
        float initial_temp = 20;
        float thrift = (float) 0.25;



        try{
            name = args[0];
            initial_temp = Float.parseFloat(args[1]);
            thrift=  Float.parseFloat(args[2]);
        }catch (Exception ignored){
            System.out.println("Not all values were given. Filling in with defaults...");
        }



        Sensor sensor = new Sensor(s_ip,s_port,c_ip,name,initial_temp,thrift);
        sensor.connect();
        //noinspection InfiniteLoopStatement
        while (true) {
            try {
                Thread.sleep(1000);
                sensor.updateTemperature();
                if (sensor.ping()) {
                    sensor.server_back_up();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void reconnect(CharSequence ip, int port, boolean back_up) {
        super.reconnect(ip.toString(), port, back_up);
    }
}
