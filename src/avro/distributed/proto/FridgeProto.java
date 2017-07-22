/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package avro.distributed.proto;

@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public interface FridgeProto {
  public static final org.apache.avro.Protocol PROTOCOL = org.apache.avro.Protocol.parse("{\"protocol\":\"FridgeProto\",\"namespace\":\"avro.distributed.proto\",\"types\":[],\"messages\":{\"addItem\":{\"request\":[{\"name\":\"item\",\"type\":\"string\"}],\"response\":\"null\",\"one-way\":true},\"contains\":{\"request\":[],\"response\":{\"type\":\"array\",\"items\":\"string\"}},\"removeItem\":{\"request\":[{\"name\":\"item\",\"type\":\"string\"}],\"response\":\"null\",\"one-way\":true},\"close_fridge\":{\"request\":[],\"response\":\"null\",\"one-way\":true}}}");
  void addItem(java.lang.CharSequence item);
  java.util.List<java.lang.CharSequence> contains() throws org.apache.avro.AvroRemoteException;
  void removeItem(java.lang.CharSequence item);
  void close_fridge();

  @SuppressWarnings("all")
  public interface Callback extends FridgeProto {
    public static final org.apache.avro.Protocol PROTOCOL = avro.distributed.proto.FridgeProto.PROTOCOL;
    void contains(org.apache.avro.ipc.Callback<java.util.List<java.lang.CharSequence>> callback) throws java.io.IOException;
  }
}