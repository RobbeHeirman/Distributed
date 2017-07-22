package logic_server.Objects;

import avro.distributed.proto.ClientType;
import avro.distributed.proto.FridgeProto;
import avro.distributed.proto.LightProto;
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
public class FridgeObject extends ClientObject {

    protected Transceiver client = null;
    protected FridgeProto.Callback proxy = null;
    protected List<CharSequence> items = new ArrayList<>();

    public FridgeObject(int id, CharSequence ip, int port, String name) {
        super(id, ip, port, name, ClientType.FRIDGE);

        try {

            client = new SaslSocketTransceiver(new InetSocketAddress( port));
            proxy = SpecificRequestor.getClient(FridgeProto.Callback.class, client);

        } catch (IOException e) {
            System.err.println("Error Connecting to fridge");
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

    public List<CharSequence> get_inventory(){
        System.out.println("Requesting inventory of " + this.getName() + "...");
        return items;}


    public void set_inventory(List<CharSequence> new_inventory){
        System.out.println("Updating inventory of " + this.getName());
        this.items = new ArrayList<>( new_inventory);}
}
