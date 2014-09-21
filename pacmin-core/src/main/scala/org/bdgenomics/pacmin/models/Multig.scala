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
package org.bdgenomics.pacmin.models

/**
 * A multig is a "multi-allelic" contig. Specifically, a multig represents
 * a contig that occurs more than once (e.g., ploidy > 1, or is repeated), where
 * there may be slight divergence between the two occurrences (e.g., there is a
 * SNP contained in the multig).
 *
 * @param sample The name of this sample.
 * @param id The ID of this multig in this sample.
 * @param rightMultigs The multigs to the right of this multig, if any.
 * @param leftMultigs The multigs to the left of this multig, if any.
 * @param sequences The base strings in this multig.
 * @param qualities The phred scaled qualities for all bases called in this multig.
 */
case class Multig(sample: String,
                  id: Long,
                  rightMultigs: Array[Long],
                  leftMultigs: Array[Long],
                  sequences: Array[String],
                  qualities: Array[Array[Int]]) {

  val ploidy = sequences.length
  assert((rightMultigs.length == ploidy || rightMultigs.length == 0) &&
    (leftMultigs.length == ploidy || leftMultigs.length == 0) &&
    qualities.length == ploidy,
    "Ploidy must be consistent across data structure.")
  (0 until ploidy).foreach(i => {
    assert(sequences(i).length == qualities(i).length,
      "Sequence/quality length mismatch for #" + i + ".")
  })

  /**
   * @return Returns the description of this multig in FASTG format.
   */
  def toFastg(): String = {
    ???
  }
}
