// Copyright (c) VMware, Inc. 2025.
// All rights reserved. SPDX-License-Identifier: Apache-2.0

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vmware.gemfire.examples.transactionsMixedWithNon;

import java.util.HashSet;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.RegionFunctionContext;

/**
 * Function that searches any entries older than a certain age. If it finds one, it
 * will check to see if that entry exists in the primary. If the primary does not
 * contain the entry, the key will be returned from the function.
 * <p/>
 * This function can be invoked through gfsh. The first parameter is the region
 * name to examine. The second parameter the age in minutes of the entries. Only
 * entries with a last modified older than this age will be considered.
 * <p/>
 * Example of finding entries older than 1 hour and checking them.
 * {code}
 * execute function --id=FindOldEntriesFunction --arguments=example-region,60
 * {code}
 */
public class FindAndUpdateEntriesFunction implements Function {
  public static final String ID = FindAndUpdateEntriesFunction.class.getSimpleName();

  @Override
  public void execute(FunctionContext context) {
    RegionFunctionContext rfc = (RegionFunctionContext) context;

    Region dataSet = rfc.getDataSet();
    HashSet<Object> toReturn = new HashSet<>(dataSet.keySet());
    for (Object key : toReturn) {

      dataSet.put(key, "IN_PROGRESS");
      dataSet.remove(key);
      // Transactional update
      // doInTransaction(context.getCache(), () -> dataSet.put(key, "IN_PROGRESS"));
    }

    rfc.getResultSender().lastResult(toReturn);
  }

  @Override
  public String getId() {
    return ID;
  }

}
