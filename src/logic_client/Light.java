package logic_client;
import avro.distributed.proto.ClientType;
import avro.distributed.proto.LightProto;
import org.apache.avro.ipc.SaslSocketServer;
import org.apache.avro.ipc.specific.SpecificResponder;
import reader.readfile;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;

public class Light extends Client implements LightProto {

    private Light(String s_ip, int s_port, String ip, CharSequence name){
        super(s_ip, s_port,ip, name, ClientType.LIGHT);

        System.out.println("Light: Light connected to server.");

    }

    @Override
    void set_up_server() {
        try {
            server = new SaslSocketServer(new SpecificResponder(LightProto.class,
                    this), new InetSocketAddress(ip,0));
        } catch (IOException e) {
            System.err.println(" [error] Failed to start server");
            e.printStackTrace(System.err);
            System.exit(1);
        }
        server.start();
    }

    @Override
    public void set_light(boolean light_state) {

        if (light_state){
            System.out.println("Light: I am now on");
        }

        else{
            System.out.println("Light: I am now off");
        }
    }

    @Override
    public void reconnect(CharSequence ip, int port, boolean back_up) {
        super.reconnect(ip.toString(),port, back_up);
    }

    public static void main(String[] args) {

        readfile r = new readfile();
        Map<String,String> inf = r.readFile("info.txt");

        String s_ip = inf.get("ip_server");
        int s_port = Integer.parseInt(inf.get("port_server"));
        String c_ip = inf.get("ip_client");

        String name = "default_light";
        for(String s: args){
            name = s;
        }

        Light me = new Light(s_ip, s_port,c_ip,name);
        me.connect();

        //noinspection InfiniteLoopStatement
        while(true){
            try{
                Thread.sleep(5000);
                if(me.ping()){
                    me.server_back_up();
                }
            } catch(InterruptedException e){
                System.err.println("Thread not sleeping");
            }
        }
    }


}
