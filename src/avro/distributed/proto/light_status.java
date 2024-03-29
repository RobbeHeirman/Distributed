/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package avro.distributed.proto;  
@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class light_status extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"light_status\",\"namespace\":\"avro.distributed.proto\",\"fields\":[{\"name\":\"light_name\",\"type\":\"string\"},{\"name\":\"online_status\",\"type\":\"boolean\"},{\"name\":\"state\",\"type\":\"boolean\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
  @Deprecated public java.lang.CharSequence light_name;
  @Deprecated public boolean online_status;
  @Deprecated public boolean state;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>. 
   */
  public light_status() {}

  /**
   * All-args constructor.
   */
  public light_status(java.lang.CharSequence light_name, java.lang.Boolean online_status, java.lang.Boolean state) {
    this.light_name = light_name;
    this.online_status = online_status;
    this.state = state;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call. 
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return light_name;
    case 1: return online_status;
    case 2: return state;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
  // Used by DatumReader.  Applications should not call. 
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: light_name = (java.lang.CharSequence)value$; break;
    case 1: online_status = (java.lang.Boolean)value$; break;
    case 2: state = (java.lang.Boolean)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'light_name' field.
   */
  public java.lang.CharSequence getLightName() {
    return light_name;
  }

  /**
   * Sets the value of the 'light_name' field.
   * @param value the value to set.
   */
  public void setLightName(java.lang.CharSequence value) {
    this.light_name = value;
  }

  /**
   * Gets the value of the 'online_status' field.
   */
  public java.lang.Boolean getOnlineStatus() {
    return online_status;
  }

  /**
   * Sets the value of the 'online_status' field.
   * @param value the value to set.
   */
  public void setOnlineStatus(java.lang.Boolean value) {
    this.online_status = value;
  }

  /**
   * Gets the value of the 'state' field.
   */
  public java.lang.Boolean getState() {
    return state;
  }

  /**
   * Sets the value of the 'state' field.
   * @param value the value to set.
   */
  public void setState(java.lang.Boolean value) {
    this.state = value;
  }

  /** Creates a new light_status RecordBuilder */
  public static avro.distributed.proto.light_status.Builder newBuilder() {
    return new avro.distributed.proto.light_status.Builder();
  }
  
  /** Creates a new light_status RecordBuilder by copying an existing Builder */
  public static avro.distributed.proto.light_status.Builder newBuilder(avro.distributed.proto.light_status.Builder other) {
    return new avro.distributed.proto.light_status.Builder(other);
  }
  
  /** Creates a new light_status RecordBuilder by copying an existing light_status instance */
  public static avro.distributed.proto.light_status.Builder newBuilder(avro.distributed.proto.light_status other) {
    return new avro.distributed.proto.light_status.Builder(other);
  }
  
  /**
   * RecordBuilder for light_status instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<light_status>
    implements org.apache.avro.data.RecordBuilder<light_status> {

    private java.lang.CharSequence light_name;
    private boolean online_status;
    private boolean state;

    /** Creates a new Builder */
    private Builder() {
      super(avro.distributed.proto.light_status.SCHEMA$);
    }
    
    /** Creates a Builder by copying an existing Builder */
    private Builder(avro.distributed.proto.light_status.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.light_name)) {
        this.light_name = data().deepCopy(fields()[0].schema(), other.light_name);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.online_status)) {
        this.online_status = data().deepCopy(fields()[1].schema(), other.online_status);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.state)) {
        this.state = data().deepCopy(fields()[2].schema(), other.state);
        fieldSetFlags()[2] = true;
      }
    }
    
    /** Creates a Builder by copying an existing light_status instance */
    private Builder(avro.distributed.proto.light_status other) {
            super(avro.distributed.proto.light_status.SCHEMA$);
      if (isValidValue(fields()[0], other.light_name)) {
        this.light_name = data().deepCopy(fields()[0].schema(), other.light_name);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.online_status)) {
        this.online_status = data().deepCopy(fields()[1].schema(), other.online_status);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.state)) {
        this.state = data().deepCopy(fields()[2].schema(), other.state);
        fieldSetFlags()[2] = true;
      }
    }

    /** Gets the value of the 'light_name' field */
    public java.lang.CharSequence getLightName() {
      return light_name;
    }
    
    /** Sets the value of the 'light_name' field */
    public avro.distributed.proto.light_status.Builder setLightName(java.lang.CharSequence value) {
      validate(fields()[0], value);
      this.light_name = value;
      fieldSetFlags()[0] = true;
      return this; 
    }
    
    /** Checks whether the 'light_name' field has been set */
    public boolean hasLightName() {
      return fieldSetFlags()[0];
    }
    
    /** Clears the value of the 'light_name' field */
    public avro.distributed.proto.light_status.Builder clearLightName() {
      light_name = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /** Gets the value of the 'online_status' field */
    public java.lang.Boolean getOnlineStatus() {
      return online_status;
    }
    
    /** Sets the value of the 'online_status' field */
    public avro.distributed.proto.light_status.Builder setOnlineStatus(boolean value) {
      validate(fields()[1], value);
      this.online_status = value;
      fieldSetFlags()[1] = true;
      return this; 
    }
    
    /** Checks whether the 'online_status' field has been set */
    public boolean hasOnlineStatus() {
      return fieldSetFlags()[1];
    }
    
    /** Clears the value of the 'online_status' field */
    public avro.distributed.proto.light_status.Builder clearOnlineStatus() {
      fieldSetFlags()[1] = false;
      return this;
    }

    /** Gets the value of the 'state' field */
    public java.lang.Boolean getState() {
      return state;
    }
    
    /** Sets the value of the 'state' field */
    public avro.distributed.proto.light_status.Builder setState(boolean value) {
      validate(fields()[2], value);
      this.state = value;
      fieldSetFlags()[2] = true;
      return this; 
    }
    
    /** Checks whether the 'state' field has been set */
    public boolean hasState() {
      return fieldSetFlags()[2];
    }
    
    /** Clears the value of the 'state' field */
    public avro.distributed.proto.light_status.Builder clearState() {
      fieldSetFlags()[2] = false;
      return this;
    }

    @Override
    public light_status build() {
      try {
        light_status record = new light_status();
        record.light_name = fieldSetFlags()[0] ? this.light_name : (java.lang.CharSequence) defaultValue(fields()[0]);
        record.online_status = fieldSetFlags()[1] ? this.online_status : (java.lang.Boolean) defaultValue(fields()[1]);
        record.state = fieldSetFlags()[2] ? this.state : (java.lang.Boolean) defaultValue(fields()[2]);
        return record;
      } catch (Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }
}
