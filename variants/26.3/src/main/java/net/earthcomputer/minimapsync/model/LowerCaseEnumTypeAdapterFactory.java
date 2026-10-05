package net.earthcomputer.minimapsync.model;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.*;
import java.io.IOException;
import java.util.*;
/** Retain vanilla's legacy lowercase enum JSON format on versions that removed its helper. */
public final class LowerCaseEnumTypeAdapterFactory implements TypeAdapterFactory {
 public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
  if(!type.getRawType().isEnum()) return null;
  Map<String,T> values=new HashMap<>();
  for(Object value:type.getRawType().getEnumConstants()) values.put(((Enum<?>)value).name().toLowerCase(Locale.ROOT),(T)value);
  return new TypeAdapter<T>() {
   public void write(JsonWriter out,T value)throws IOException {if(value==null)out.nullValue();else out.value(((Enum<?>)value).name().toLowerCase(Locale.ROOT));}
   public T read(JsonReader in)throws IOException {if(in.peek()==JsonToken.NULL){in.nextNull();return null;}return values.get(in.nextString());}
  };
 }
}
