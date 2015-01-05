/**
 * Licensed to Big Data Genomics (BDG) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The BDG licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bdgenomics.pacmin.graph

import org.apache.spark.graphx.Graph
import org.apache.spark.rdd.RDD
import org.bdgenomics.formats.avro.AlignmentRecord
import org.bdgenomics.pacmin.models.{ Multig, Overlap }

object OverlapGraph {

  /**
   * Takes in an input overlap graph and reduces it down into multigs.
   *
   * @param overlapGraph Input graph of overlapping reads.
   * @return Returns a set of multigs (multi-allele contigs).
   */
  def apply(overlapGraph: Graph[AlignmentRecord, Overlap]): RDD[Multig] = {
    // reduce this string graph down into quasitigs
    val quasitigGraph = transitivelyReduceOverlaps(overlapGraph)

    // finalize our quasitigs into multigs
    finalizeQuasitigs(quasitigGraph)
  }

  /**
   * Takes in an input overlap graph, and converts it into a string graph. String
   * graphs are described in:
   *
   * Myers, Eugene W. "The fragment assembly string graph."
   * Bioinformatics 21.suppl 2 (2005): ii79-ii85.
   *
   * Performs a transitive reduction across the overlap graph, which coalesces
   * overlapping reads down into quasitigs. Quasitigs are a probabilistic
   * equivalent to contigs; specifically, the sequence is expressed as a probability,
   * and the ploidy of the contig is not yet known.
   *
   * @param stringGraph Input string graph.
   * @return Returns a quasitig graph, which provides a set of quasitigs which
   *         are linked by spanning reads.
   */
  private[graph] def transitivelyReduceOverlaps(stringGraph: Graph[AlignmentRecord, Overlap]): Graph[Linkage, Quasitig] = {
    ???
  }

  /**
   * Takes in the quasitig graph and finalizes it into multigs. This function
   * must assign ploidy to the quasitigs, and then call the sequences of the
   * quasitigs.
   *
   * @param quasitigGraph Input quasitig graph.
   * @return An RDD containing finalized multigs.
   */
  private[graph] def finalizeQuasitigs(quasitigGraph: Graph[Linkage, Quasitig]): RDD[Multig] = {
    ???
  }
}
