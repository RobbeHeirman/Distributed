/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package avro.distributed.proto;  
@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class ClientInfo extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"ClientInfo\",\"namespace\":\"avro.distributed.proto\",\"fields\":[{\"name\":\"ip\",\"type\":\"string\"},{\"name\":\"port\",\"type\":\"int\"},{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"type\",\"type\":{\"type\":\"enum\",\"name\":\"ClientType\",\"symbols\":[\"USER\",\"FRIDGE\",\"LIGHT\",\"SENSOR\"]}},{\"name\":\"key\",\"type\":\"int\"},{\"name\":\"online\",\"type\":\"boolean\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
  @Deprecated public java.lang.CharSequence ip;
  @Deprecated public int port;
  @Deprecated public java.lang.CharSequence name;
  @Deprecated public avro.distributed.proto.ClientType type;
  @Deprecated public int key;
  @Deprecated public boolean online;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>. 
   */
  public ClientInfo() {}

  /**
   * All-args constructor.
   */
  public ClientInfo(java.lang.CharSequence ip, java.lang.Integer port, java.lang.CharSequence name, avro.distributed.proto.ClientType type, java.lang.Integer key, java.lang.Boolean online) {
    this.ip = ip;
    this.port = port;
    this.name = name;
    this.type = type;
    this.key = key;
    this.online = online;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call. 
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return ip;
    case 1: return port;
    case 2: return name;
    case 3: return type;
    case 4: return key;
    case 5: return online;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
  // Used by DatumReader.  Applications should not call. 
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: ip = (java.lang.CharSequence)value$; break;
    case 1: port = (java.lang.Integer)value$; break;
    case 2: name = (java.lang.CharSequence)value$; break;
    case 3: type = (avro.distributed.proto.ClientType)value$; break;
    case 4: key = (java.lang.Integer)value$; break;
    case 5: online = (java.lang.Boolean)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'ip' field.
   */
  public java.lang.CharSequence getIp() {
    return ip;
  }

  /**
   * Sets the value of the 'ip' field.
   * @param value the value to set.
   */
  public void setIp(java.lang.CharSequence value) {
    this.ip = value;
  }

  /**
   * Gets the value of the 'port' field.
   */
  public java.lang.Integer getPort() {
    return port;
  }

  /**
   * Sets the value of the 'port' field.
   * @param value the value to set.
   */
  public void setPort(java.lang.Integer value) {
    this.port = value;
  }

  /**
   * Gets the value of the 'name' field.
   */
  public java.lang.CharSequence getName() {
    return name;
  }

  /**
   * Sets the value of the 'name' field.
   * @param value the value to set.
   */
  public void setName(java.lang.CharSequence value) {
    this.name = value;
  }

  /**
   * Gets the value of the 'type' field.
   */
  public avro.distributed.proto.ClientType getType() {
    return type;
  }

  /**
   * Sets the value of the 'type' field.
   * @param value the value to set.
   */
  public void setType(avro.distributed.proto.ClientType value) {
    this.type = value;
  }

  /**
   * Gets the value of the 'key' field.
   */
  public java.lang.Integer getKey() {
    return key;
  }

  /**
   * Sets the value of the 'key' field.
   * @param value the value to set.
   */
  public void setKey(java.lang.Integer value) {
    this.key = value;
  }

  /**
   * Gets the value of the 'online' field.
   */
  public java.lang.Boolean getOnline() {
    return online;
  }

  /**
   * Sets the value of the 'online' field.
   * @param value the value to set.
   */
  public void setOnline(java.lang.Boolean value) {
    this.online = value;
  }

  /** Creates a new ClientInfo RecordBuilder */
  public static avro.distributed.proto.ClientInfo.Builder newBuilder() {
    return new avro.distributed.proto.ClientInfo.Builder();
  }
  
  /** Creates a new ClientInfo RecordBuilder by copying an existing Builder */
  public static avro.distributed.proto.ClientInfo.Builder newBuilder(avro.distributed.proto.ClientInfo.Builder other) {
    return new avro.distributed.proto.ClientInfo.Builder(other);
  }
  
  /** Creates a new ClientInfo RecordBuilder by copying an existing ClientInfo instance */
  public static avro.distributed.proto.ClientInfo.Builder newBuilder(avro.distributed.proto.ClientInfo other) {
    return new avro.distributed.proto.ClientInfo.Builder(other);
  }
  
  /**
   * RecordBuilder for ClientInfo instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<ClientInfo>
    implements org.apache.avro.data.RecordBuilder<ClientInfo> {

    private java.lang.CharSequence ip;
    private int port;
    private java.lang.CharSequence name;
    private avro.distributed.proto.ClientType type;
    private int key;
    private boolean online;

    /** Creates a new Builder */
    private Builder() {
      super(avro.distributed.proto.ClientInfo.SCHEMA$);
    }
    
    /** Creates a Builder by copying an existing Builder */
    private Builder(avro.distributed.proto.ClientInfo.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.ip)) {
        this.ip = data().deepCopy(fields()[0].schema(), other.ip);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.port)) {
        this.port = data().deepCopy(fields()[1].schema(), other.port);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.name)) {
        this.name = data().deepCopy(fields()[2].schema(), other.name);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.type)) {
        this.type = data().deepCopy(fields()[3].schema(), other.type);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.key)) {
        this.key = data().deepCopy(fields()[4].schema(), other.key);
        fieldSetFlags()[4] = true;
      }
      if (isValidValue(fields()[5], other.online)) {
        this.online = data().deepCopy(fields()[5].schema(), other.online);
        fieldSetFlags()[5] = true;
      }
    }
    
    /** Creates a Builder by copying an existing ClientInfo instance */
    private Builder(avro.distributed.proto.ClientInfo other) {
            super(avro.distributed.proto.ClientInfo.SCHEMA$);
      if (isValidValue(fields()[0], other.ip)) {
        this.ip = data().deepCopy(fields()[0].schema(), other.ip);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.port)) {
        this.port = data().deepCopy(fields()[1].schema(), other.port);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.name)) {
        this.name = data().deepCopy(fields()[2].schema(), other.name);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.type)) {
        this.type = data().deepCopy(fields()[3].schema(), other.type);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.key)) {
        this.key = data().deepCopy(fields()[4].schema(), other.key);
        fieldSetFlags()[4] = true;
      }
      if (isValidValue(fields()[5], other.online)) {
        this.online = data().deepCopy(fields()[5].schema(), other.online);
        fieldSetFlags()[5] = true;
      }
    }

    /** Gets the value of the 'ip' field */
    public java.lang.CharSequence getIp() {
      return ip;
    }
    
    /** Sets the value of the 'ip' field */
    public avro.distributed.proto.ClientInfo.Builder setIp(java.lang.CharSequence value) {
      validate(fields()[0], value);
      this.ip = value;
      fieldSetFlags()[0] = true;
      return this; 
    }
    
    /** Checks whether the 'ip' field has been set */
    public boolean hasIp() {
      return fieldSetFlags()[0];
    }
    
    /** Clears the value of the 'ip' field */
    public avro.distributed.proto.ClientInfo.Builder clearIp() {
      ip = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /** Gets the value of the 'port' field */
    public java.lang.Integer getPort() {
      return port;
    }
    
    /** Sets the value of the 'port' field */
    public avro.distributed.proto.ClientInfo.Builder setPort(int value) {
      validate(fields()[1], value);
      this.port = value;
      fieldSetFlags()[1] = true;
      return this; 
    }
    
    /** Checks whether the 'port' field has been set */
    public boolean hasPort() {
      return fieldSetFlags()[1];
    }
    
    /** Clears the value of the 'port' field */
    public avro.distributed.proto.ClientInfo.Builder clearPort() {
      fieldSetFlags()[1] = false;
      return this;
    }

    /** Gets the value of the 'name' field */
    public java.lang.CharSequence getName() {
      return name;
    }
    
    /** Sets the value of the 'name' field */
    public avro.distributed.proto.ClientInfo.Builder setName(java.lang.CharSequence value) {
      validate(fields()[2], value);
      this.name = value;
      fieldSetFlags()[2] = true;
      return this; 
    }
    
    /** Checks whether the 'name' field has been set */
    public boolean hasName() {
      return fieldSetFlags()[2];
    }
    
    /** Clears the value of the 'name' field */
    public avro.distributed.proto.ClientInfo.Builder clearName() {
      name = null;
      fieldSetFlags()[2] = false;
      return this;
    }

    /** Gets the value of the 'type' field */
    public avro.distributed.proto.ClientType getType() {
      return type;
    }
    
    /** Sets the value of the 'type' field */
    public avro.distributed.proto.ClientInfo.Builder setType(avro.distributed.proto.ClientType value) {
      validate(fields()[3], value);
      this.type = value;
      fieldSetFlags()[3] = true;
      return this; 
    }
    
    /** Checks whether the 'type' field has been set */
    public boolean hasType() {
      return fieldSetFlags()[3];
    }
    
    /** Clears the value of the 'type' field */
    public avro.distributed.proto.ClientInfo.Builder clearType() {
      type = null;
      fieldSetFlags()[3] = false;
      return this;
    }

    /** Gets the value of the 'key' field */
    public java.lang.Integer getKey() {
      return key;
    }
    
    /** Sets the value of the 'key' field */
    public avro.distributed.proto.ClientInfo.Builder setKey(int value) {
      validate(fields()[4], value);
      this.key = value;
      fieldSetFlags()[4] = true;
      return this; 
    }
    
    /** Checks whether the 'key' field has been set */
    public boolean hasKey() {
      return fieldSetFlags()[4];
    }
    
    /** Clears the value of the 'key' field */
    public avro.distributed.proto.ClientInfo.Builder clearKey() {
      fieldSetFlags()[4] = false;
      return this;
    }

    /** Gets the value of the 'online' field */
    public java.lang.Boolean getOnline() {
      return online;
    }
    
    /** Sets the value of the 'online' field */
    public avro.distributed.proto.ClientInfo.Builder setOnline(boolean value) {
      validate(fields()[5], value);
      this.online = value;
      fieldSetFlags()[5] = true;
      return this; 
    }
    
    /** Checks whether the 'online' field has been set */
    public boolean hasOnline() {
      return fieldSetFlags()[5];
    }
    
    /** Clears the value of the 'online' field */
    public avro.distributed.proto.ClientInfo.Builder clearOnline() {
      fieldSetFlags()[5] = false;
      return this;
    }

    @Override
    public ClientInfo build() {
      try {
        ClientInfo record = new ClientInfo();
        record.ip = fieldSetFlags()[0] ? this.ip : (java.lang.CharSequence) defaultValue(fields()[0]);
        record.port = fieldSetFlags()[1] ? this.port : (java.lang.Integer) defaultValue(fields()[1]);
        record.name = fieldSetFlags()[2] ? this.name : (java.lang.CharSequence) defaultValue(fields()[2]);
        record.type = fieldSetFlags()[3] ? this.type : (avro.distributed.proto.ClientType) defaultValue(fields()[3]);
        record.key = fieldSetFlags()[4] ? this.key : (java.lang.Integer) defaultValue(fields()[4]);
        record.online = fieldSetFlags()[5] ? this.online : (java.lang.Boolean) defaultValue(fields()[5]);
        return record;
      } catch (Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }
}