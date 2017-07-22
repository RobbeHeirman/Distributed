package logic_client;

import avro.distributed.proto.ClientType;

/**
 * Created by Robbe on 7/22/2017.
 */
public abstract class ServerClient extends Client {

    ServerClient(CharSequence name, ClientType type) {
        super(name, type);
    }
}
