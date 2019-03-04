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
package com.datastax.driver.examples.basic;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.api.core.metadata.Metadata;
import com.datastax.oss.driver.api.core.metadata.Node;
import com.datastax.oss.driver.api.core.metadata.schema.KeyspaceMetadata;
import com.datastax.oss.driver.api.core.metadata.schema.TableMetadata;

/**
 * Gathers information about a Cassandra cluster's topology (which nodes belong to the cluster) and
 * schema (what keyspaces, tables, etc. exist in this cluster).
 *
 * <p>Preconditions: - a Cassandra cluster is running and accessible through the contacts points
 * identified by basic.contact-points (see application.conf)
 *
 * <p>Side effects: none.
 *
 * @see <a href="http://datastax.github.io/java-driver/manual/">Java driver online manual</a>
 */
public class ReadTopologyAndSchemaMetadata {

  public static void main(String[] args) {

    try (CqlSession session = new CqlSessionBuilder().build()) {

      Metadata metadata = session.getMetadata();
      System.out.printf(
          "Connected to cluster: %s%n", session.getName()); // todo metadata.getClusterName()

      for (Node node : metadata.getNodes().values()) {
        System.out.printf(
            "Datatacenter: %s; Host: %s; Rack: %s%n",
            node.getDatacenter(), node.getConnectAddress(), node.getRack());
      }

      for (KeyspaceMetadata keyspace : metadata.getKeyspaces().values()) {
        for (TableMetadata table : keyspace.getTables().values()) {
          System.out.printf("Keyspace: %s; Table: %s%n", keyspace.getName(), table.getName());
        }
      }
    }
  }
}
