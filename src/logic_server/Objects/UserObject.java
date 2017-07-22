package logic_server.Objects;

import avro.distributed.proto.ClientType;
import avro.distributed.proto.LightProto;
import avro.distributed.proto.UserProto;
import org.apache.avro.AvroRemoteException;
import org.apache.avro.ipc.SaslSocketTransceiver;
import org.apache.avro.ipc.Transceiver;
import org.apache.avro.ipc.specific.SpecificRequestor;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by Robbe on 7/15/2017.
 */
public class UserObject extends ClientObject {

    protected Transceiver client = null;
    protected UserProto.Callback proxy = null;

   public UserObject(int id, CharSequence ip, int port, String name){
        super(id, ip, port, name, ClientType.USER);

       try {

           client = new SaslSocketTransceiver(new InetSocketAddress(port));
           proxy = SpecificRequestor.getClient(UserProto.Callback.class, client);

       } catch (IOException e) {
           System.err.println("Error Connecting light");
           e.printStackTrace(System.err);
           System.exit(1);
       }

    }

    public boolean is_alive(){

       try{
           proxy.is_alive();
       } catch (AvroRemoteException e) {
           return false;
       }
       return true;
    }


}

