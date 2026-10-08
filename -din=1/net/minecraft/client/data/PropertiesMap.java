package net.minecraft.client.data;

import com.google.common.collect.ImmutableList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.state.property.Property.Value;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public record PropertiesMap(List<Value<?>> values) {
   public static final PropertiesMap EMPTY = new PropertiesMap(List.of());
   private static final Comparator<Value<?>> COMPARATOR = Comparator.comparing(value -> value.property().getName());

   public PropertiesMap withValue(Value<?> value) {
      return new PropertiesMap(Util.withAppended(this.values, value));
   }

   public PropertiesMap copyOf(PropertiesMap propertiesMap) {
      return new PropertiesMap(ImmutableList.builder().addAll(this.values).addAll(propertiesMap.values).build());
   }

   public static PropertiesMap withValues(Value<?>... values) {
      return new PropertiesMap(List.of(values));
   }

   public String asString() {
      return this.values.stream().sorted(COMPARATOR).<CharSequence>map(Value::toString).collect(Collectors.joining(","));
   }

   @Override
   public String toString() {
      return (R)this.asString();
   }
}
