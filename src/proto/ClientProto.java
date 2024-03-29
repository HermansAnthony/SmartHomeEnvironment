/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package proto;

@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public interface ClientProto {
  public static final org.apache.avro.Protocol PROTOCOL = org.apache.avro.Protocol.parse("{\"protocol\":\"ClientProto\",\"namespace\":\"proto\",\"types\":[],\"messages\":{\"getInventory\":{\"request\":[],\"response\":{\"type\":\"array\",\"items\":\"string\"}},\"isOpen\":{\"request\":[],\"response\":\"int\"},\"setFridgeUser\":{\"request\":[{\"name\":\"id\",\"type\":\"int\"}],\"response\":\"boolean\"},\"addFridgeItem\":{\"request\":[{\"name\":\"item\",\"type\":\"string\"}],\"response\":\"boolean\"},\"removeFridgeItem\":{\"request\":[{\"name\":\"item\",\"type\":\"string\"}],\"response\":\"boolean\"},\"getState\":{\"request\":[],\"response\":\"boolean\"},\"switchState\":{\"request\":[],\"response\":\"null\",\"one-way\":true},\"changeState\":{\"request\":[{\"name\":\"state\",\"type\":\"boolean\"}],\"response\":\"null\",\"one-way\":true},\"ping\":{\"request\":[],\"response\":\"null\",\"one-way\":true},\"election\":{\"request\":[{\"name\":\"i\",\"type\":\"int\"},{\"name\":\"id\",\"type\":\"int\"}],\"response\":\"null\",\"one-way\":true},\"elected\":{\"request\":[{\"name\":\"i\",\"type\":\"int\"},{\"name\":\"IPAddress\",\"type\":\"string\"},{\"name\":\"portNumber\",\"type\":\"int\"}],\"response\":\"null\",\"one-way\":true},\"setNewController\":{\"request\":[{\"name\":\"IPAddress\",\"type\":\"string\"},{\"name\":\"portNumber\",\"type\":\"int\"}],\"response\":\"null\",\"one-way\":true},\"settleConnection\":{\"request\":[],\"response\":\"null\"},\"announceEnter\":{\"request\":[{\"name\":\"userId\",\"type\":\"int\"},{\"name\":\"enter\",\"type\":\"boolean\"}],\"response\":\"null\",\"one-way\":true},\"announceEmpty\":{\"request\":[{\"name\":\"fridgeId\",\"type\":\"int\"}],\"response\":\"null\",\"one-way\":true}}}");
  java.util.List<java.lang.CharSequence> getInventory() throws org.apache.avro.AvroRemoteException;
  int isOpen() throws org.apache.avro.AvroRemoteException;
  boolean setFridgeUser(int id) throws org.apache.avro.AvroRemoteException;
  boolean addFridgeItem(java.lang.CharSequence item) throws org.apache.avro.AvroRemoteException;
  boolean removeFridgeItem(java.lang.CharSequence item) throws org.apache.avro.AvroRemoteException;
  boolean getState() throws org.apache.avro.AvroRemoteException;
  void switchState();
  void changeState(boolean state);
  void ping();
  void election(int i, int id);
  void elected(int i, java.lang.CharSequence IPAddress, int portNumber);
  void setNewController(java.lang.CharSequence IPAddress, int portNumber);
  java.lang.Void settleConnection() throws org.apache.avro.AvroRemoteException;
  void announceEnter(int userId, boolean enter);
  void announceEmpty(int fridgeId);

  @SuppressWarnings("all")
  public interface Callback extends ClientProto {
    public static final org.apache.avro.Protocol PROTOCOL = proto.ClientProto.PROTOCOL;
    void getInventory(org.apache.avro.ipc.Callback<java.util.List<java.lang.CharSequence>> callback) throws java.io.IOException;
    void isOpen(org.apache.avro.ipc.Callback<java.lang.Integer> callback) throws java.io.IOException;
    void setFridgeUser(int id, org.apache.avro.ipc.Callback<java.lang.Boolean> callback) throws java.io.IOException;
    void addFridgeItem(java.lang.CharSequence item, org.apache.avro.ipc.Callback<java.lang.Boolean> callback) throws java.io.IOException;
    void removeFridgeItem(java.lang.CharSequence item, org.apache.avro.ipc.Callback<java.lang.Boolean> callback) throws java.io.IOException;
    void getState(org.apache.avro.ipc.Callback<java.lang.Boolean> callback) throws java.io.IOException;
    void settleConnection(org.apache.avro.ipc.Callback<java.lang.Void> callback) throws java.io.IOException;
  }
}