package logic_client;
import avro.distributed.proto.ClientType;
import avro.distributed.proto.LightProto;
import avro.distributed.proto.ServerProto;
import logic_server.SmartServer;
import org.apache.avro.ipc.SaslSocketServer;
import org.apache.avro.ipc.specific.SpecificResponder;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Light extends Client implements LightProto {

    private boolean status = false;

    public Light(CharSequence name){
        super(name, ClientType.LIGHT);

    }

    @Override
    void set_up_server() {
        try {
            server = new SaslSocketServer(new SpecificResponder(LightProto.class,
                    this), new InetSocketAddress(0));
        } catch (IOException e) {
            System.err.println(" [error] Failed to start server");
            e.printStackTrace(System.err);
            System.exit(1);
        }
        server.start();
    }

    @Override
    public void set_light(boolean light_state) {

        status = light_state;
        if (light_state){
            System.out.println("I am now on");
        }

        else{
            System.out.println("I am now off");
        }
    }

    public static void main(String[] args) {

        Light me = new Light("Light0");

        while(true){
            try{
                Thread.sleep(5000);
            } catch(InterruptedException e){
                System.err.println("Thread not sleeping");
            }
        }
    }


}
