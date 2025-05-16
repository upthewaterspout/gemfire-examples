package com.vmware.gemfire.examples.transactionsMixedWithNon;

import org.apache.geode.cache.CommitConflictException;
import org.apache.geode.cache.GemFireCache;

public class TransactionUtil {
  public static void doInTransaction(GemFireCache cache, Runnable r) {
    boolean done = false;
    while (!done) {
      try {
        cache.getCacheTransactionManager().begin();
        r.run();
        cache.getCacheTransactionManager().commit();
        done = true;
      } catch (CommitConflictException e) {
        // retry
      }
    }
  }
}
