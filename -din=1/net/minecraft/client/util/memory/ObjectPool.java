package net.minecraft.client.util.memory;

import com.google.common.annotations.VisibleForTesting;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.ClosableFactory;

@Environment(EnvType.CLIENT)
public class ObjectPool implements ObjectAllocator, AutoCloseable {
   private final int lifespan;
   private final Deque<ObjectPool.Entry<?>> entries = new ArrayDeque<>();

   public ObjectPool(int lifespan) {
      this.lifespan = lifespan;
   }

   public void decrementLifespan() {
      Iterator<? extends ObjectPool.Entry<?>> iterator = this.entries.iterator();

      while (iterator.hasNext()) {
         ObjectPool.Entry<?> entry = (ObjectPool.Entry<?>)iterator.next();
         if (entry.lifespan-- == 0) {
            entry.close();
            iterator.remove();
         }
      }
   }

   @Override
   public <T> T acquire(ClosableFactory<T> factory) {
      T object = this.acquireUnprepared(factory);
      factory.prepare(object);
      return object;
   }

   private <T> T acquireUnprepared(ClosableFactory<T> factory) {
      Iterator<? extends ObjectPool.Entry<?>> iterator = this.entries.iterator();

      while (iterator.hasNext()) {
         ObjectPool.Entry<?> entry = (ObjectPool.Entry<?>)iterator.next();
         if (factory.equals(entry.factory)) {
            iterator.remove();
            return (T)entry.object;
         }
      }

      return factory.create();
   }

   @Override
   public <T> void release(ClosableFactory<T> factory, T value) {
      this.entries.addFirst(new ObjectPool.Entry<>(factory, value, this.lifespan));
   }

   public void clear() {
      this.entries.forEach(ObjectPool.Entry::close);
      this.entries.clear();
   }

   @Override
   public void close() {
      this.clear();
   }

   @VisibleForTesting
   protected Collection<ObjectPool.Entry<?>> getEntries() {
      return this.entries;
   }

   @Environment(EnvType.CLIENT)
   @VisibleForTesting
   protected static final class Entry<T> implements AutoCloseable {
      final ClosableFactory<T> factory;
      final T object;
      int lifespan;

      Entry(ClosableFactory<T> factory, T object, int lifespan) {
         this.factory = factory;
         this.object = object;
         this.lifespan = lifespan;
      }

      @Override
      public void close() {
         this.factory.close(this.object);
      }
   }
}
