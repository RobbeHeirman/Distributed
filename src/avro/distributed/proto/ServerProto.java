/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package avro.distributed.proto;

@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public interface ServerProto {
  public static final org.apache.avro.Protocol PROTOCOL = org.apache.avro.Protocol.parse("{\"protocol\":\"ServerProto\",\"namespace\":\"avro.distributed.proto\",\"types\":[{\"type\":\"enum\",\"name\":\"ClientType\",\"symbols\":[\"USER\",\"FRIDGE\",\"LIGHT\",\"SENSOR\"]},{\"type\":\"record\",\"name\":\"light_status\",\"fields\":[{\"name\":\"light_name\",\"type\":\"string\"},{\"name\":\"online_status\",\"type\":\"boolean\"},{\"name\":\"state\",\"type\":\"boolean\"}]},{\"type\":\"record\",\"name\":\"FridgeDetails\",\"fields\":[{\"name\":\"ip\",\"type\":\"string\"},{\"name\":\"port\",\"type\":\"int\"}]},{\"type\":\"record\",\"name\":\"ClientDetails\",\"fields\":[{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"type\",\"type\":\"ClientType\"},{\"name\":\"status\",\"type\":\"boolean\"}]}],\"messages\":{\"connect\":{\"request\":[{\"name\":\"username\",\"type\":\"string\"},{\"name\":\"IPaddres\",\"type\":\"string\"},{\"name\":\"portNumber\",\"type\":\"int\"},{\"name\":\"userType\",\"type\":\"ClientType\"}],\"response\":\"string\"},\"getUsers\":{\"request\":[],\"response\":{\"type\":\"array\",\"items\":\"ClientDetails\"}},\"switchLights\":{\"request\":[{\"name\":\"lightname\",\"type\":\"string\"}],\"response\":\"boolean\"},\"getLightsStatus\":{\"request\":[],\"response\":{\"type\":\"array\",\"items\":\"light_status\"}},\"getFridgeInventory\":{\"request\":[{\"name\":\"fridgename\",\"type\":\"string\"}],\"response\":{\"type\":\"array\",\"items\":\"string\"}},\"openFridge\":{\"request\":[{\"name\":\"fridgename\",\"type\":\"string\"}],\"response\":\"FridgeDetails\"},\"set_fridge_inventory\":{\"request\":[{\"name\":\"fridge\",\"type\":\"string\"},{\"name\":\"inventory\",\"type\":{\"type\":\"array\",\"items\":\"string\"}}],\"response\":\"null\",\"one-way\":true},\"temperatureUpdate\":{\"request\":[{\"name\":\"sensor_name\",\"type\":\"string\"},{\"name\":\"temperature\",\"type\":\"float\"}],\"response\":\"null\",\"one-way\":true},\"getTemperatureList\":{\"request\":[{\"name\":\"sensor_name\",\"type\":\"string\"}],\"response\":{\"type\":\"array\",\"items\":\"float\"}}}}");
  java.lang.CharSequence connect(java.lang.CharSequence username, java.lang.CharSequence IPaddres, int portNumber, avro.distributed.proto.ClientType userType) throws org.apache.avro.AvroRemoteException;
  java.util.List<avro.distributed.proto.ClientDetails> getUsers() throws org.apache.avro.AvroRemoteException;
  boolean switchLights(java.lang.CharSequence lightname) throws org.apache.avro.AvroRemoteException;
  java.util.List<avro.distributed.proto.light_status> getLightsStatus() throws org.apache.avro.AvroRemoteException;
  java.util.List<java.lang.CharSequence> getFridgeInventory(java.lang.CharSequence fridgename) throws org.apache.avro.AvroRemoteException;
  avro.distributed.proto.FridgeDetails openFridge(java.lang.CharSequence fridgename) throws org.apache.avro.AvroRemoteException;
  void set_fridge_inventory(java.lang.CharSequence fridge, java.util.List<java.lang.CharSequence> inventory);
  void temperatureUpdate(java.lang.CharSequence sensor_name, float temperature);
  java.util.List<java.lang.Float> getTemperatureList(java.lang.CharSequence sensor_name) throws org.apache.avro.AvroRemoteException;

  @SuppressWarnings("all")
  public interface Callback extends ServerProto {
    public static final org.apache.avro.Protocol PROTOCOL = avro.distributed.proto.ServerProto.PROTOCOL;
    void connect(java.lang.CharSequence username, java.lang.CharSequence IPaddres, int portNumber, avro.distributed.proto.ClientType userType, org.apache.avro.ipc.Callback<java.lang.CharSequence> callback) throws java.io.IOException;
    void getUsers(org.apache.avro.ipc.Callback<java.util.List<avro.distributed.proto.ClientDetails>> callback) throws java.io.IOException;
    void switchLights(java.lang.CharSequence lightname, org.apache.avro.ipc.Callback<java.lang.Boolean> callback) throws java.io.IOException;
    void getLightsStatus(org.apache.avro.ipc.Callback<java.util.List<avro.distributed.proto.light_status>> callback) throws java.io.IOException;
    void getFridgeInventory(java.lang.CharSequence fridgename, org.apache.avro.ipc.Callback<java.util.List<java.lang.CharSequence>> callback) throws java.io.IOException;
    void openFridge(java.lang.CharSequence fridgename, org.apache.avro.ipc.Callback<avro.distributed.proto.FridgeDetails> callback) throws java.io.IOException;
    void getTemperatureList(java.lang.CharSequence sensor_name, org.apache.avro.ipc.Callback<java.util.List<java.lang.Float>> callback) throws java.io.IOException;
  }
}