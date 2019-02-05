/*
 * Copyright DataStax, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.datastax.oss.driver.mapper.model.inventory;

import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.BoundStatementBuilder;
import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Insert;
import com.datastax.oss.driver.api.mapper.annotations.SetEntity;

@Dao
public interface ProductDao {

  @SetEntity
  BoundStatement set(Product product, BoundStatement boundStatement);

  @SetEntity
  void set(BoundStatementBuilder builder, Product product);

  @SetEntity
  void set(Dimensions dimensions, UdtValue udtValue);

  @Insert
  void save(Product product);

  @Insert(customClause = "USING TIMESTAMP :timestamp")
  Product saveWithBoundTimestamp(Product product, long timestamp);
}