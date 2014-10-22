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
package org.bdgenomics.pacmin.overlapping

import org.apache.spark.graphx.Graph
import org.apache.spark.rdd.RDD
import org.bdgenomics.formats.avro.AlignmentRecord
import org.bdgenomics.pacmin.models.Overlap

object Overlapper extends Serializable {

  /**
   * Takes in reads, and uses a MinHash-based Jaccard similarity estimator to
   * find overlaps between reads.
   *
   * @param reads Set of reads to overlap.
   * @param overlapThreshold Minimum threshold for emitting an overlap.
   * @param signatureLength MinHash signature length to use.
   * @param kmerLength Length of k-mers to use when shingling reads.
   * @param bands Optional band parameter. See MinHash implementation for details.
   * @return Returns a graph which connects overlapping reads together.
   */
  def apply(reads: RDD[AlignmentRecord],
            overlapThreshold: Double,
            signatureLength: Int,
            kmerLength: Int,
            bands: Option[Int]): Graph[AlignmentRecord, Overlap] = {
    ???
  }
}
