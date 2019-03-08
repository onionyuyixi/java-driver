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
package com.datastax.oss.driver.api.core.loadbalancing;

import com.datastax.oss.driver.api.core.metadata.Node;
import com.datastax.oss.driver.api.core.metadata.NodeState;
import com.datastax.oss.driver.api.core.session.Request;
import com.datastax.oss.driver.api.core.session.Session;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;

/** Decides which Cassandra nodes to contact for each query. */
public interface LoadBalancingPolicy extends AutoCloseable {

  /**
   * Initializes this policy with the nodes discovered during driver initialization.
   *
   * <p>This method is guaranteed to be called exactly once per instance, and before any other
   * method in this class. At this point, the driver has successfully connected to one of the
   * contact points, and performed a first refresh of topology information (by default, the contents
   * of {@code system.peers}) to find out which other nodes exist in the cluster.
   *
   * @param nodes the nodes in the cluster. This method must call {@link
   *     DistanceReporter#setDistance(Node, NodeDistance) distanceReporter.setDistance} for each one
   *     of them (otherwise it will stay at {@link NodeDistance#IGNORED} and the driver won't open
   *     connections to it). Note that the {@link Node#getState() state} can be either {@link
   *     NodeState#UP} (for the successful contact point), {@link NodeState#DOWN} (for contact
   *     points that were tried unsuccessfully), or {@link NodeState#UNKNOWN} (for contact points
   *     that weren't tried, or any other node discovered from the topology refresh). Node states
   *     may be updated concurrently while this method executes, but if so this policy will get
   *     notified after this method has returned, through other methods such as {@link #onUp(Node)}
   *     or {@link #onDown(Node)}.
   * @param distanceReporter an object that will be used by the policy to signal distance changes.
   */
  void init(@NonNull Map<UUID, Node> nodes, @NonNull DistanceReporter distanceReporter);

  /**
   * Returns the coordinators to use for a new query.
   *
   * <p>Each new query will call this method, and try the returned nodes sequentially.
   *
   * @param request the request that is being routed. Note that this can be null for some internal
   *     uses.
   * @param session the session that is executing the request. Note that this can be null for some
   *     internal uses.
   * @return the list of coordinators to try. <b>This must be a concurrent queue</b>; {@link
   *     java.util.concurrent.ConcurrentLinkedQueue} is a good choice.
   */
  @NonNull
  Queue<Node> newQueryPlan(@Nullable Request request, @Nullable Session session);

  /**
   * Called when a node is added to the cluster.
   *
   * <p>The new node will have the state {@link NodeState#UNKNOWN}. The actual state will be known
   * when:
   *
   * <ul>
   *   <li>the load balancing policy signals an active distance for the node, and the driver tries
   *       to connect to it.
   *   <li>or a topology event is received from the cluster.
   * </ul>
   */
  void onAdd(@NonNull Node node);

  /** Called when a node is determined to be up. */
  void onUp(@NonNull Node node);

  /** Called when a node is determined to be down. */
  void onDown(@NonNull Node node);

  /** Called when a node is removed from the cluster. */
  void onRemove(@NonNull Node node);

  /** Called when the cluster that this policy is associated with closes. */
  @Override
  void close();

  /** An object that the policy uses to signal decisions it makes about node distances. */
  interface DistanceReporter {
    void setDistance(@NonNull Node node, @NonNull NodeDistance distance);
  }
}
