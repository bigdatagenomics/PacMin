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

import org.apache.spark.graphx.{ Edge, Graph, VertexId }
import org.apache.spark.rdd.RDD
import org.bdgenomics.adam.util.SequenceUtils
import org.bdgenomics.avocado.algorithms.hmm.{ Alignment, HMMAligner }
import org.bdgenomics.formats.avro.AlignmentRecord
import org.bdgenomics.pacmin.models.Overlap
import org.bdgenomics.utils.minhash.MinHash
import scala.annotation.tailrec

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
   * @param seed Optional random seed for MinHash.
   * @return Returns a graph which connects overlapping reads together.
   */
  def apply(reads: RDD[AlignmentRecord],
            overlapThreshold: Double,
            signatureLength: Int,
            kmerLength: Int,
            bands: Option[Int],
            seed: Option[Long] = None): Graph[AlignmentRecord, Overlap] = {

    // map reads to minhashable reads
    val readsWithIds = reads.zipWithUniqueId
      .map(kv => (kv._2.asInstanceOf[VertexId], kv._1))
      .cache()
    val minhashableReads = readsWithIds.map(kv => MinHashableRead(kv._1.asInstanceOf[Long], kv._2, kmerLength))

    // perform minhash comparison
    val minhashedOverlaps = if (bands.isDefined) {
      MinHash.approximateMinHash(minhashableReads,
        signatureLength,
        bands.get,
        seed)
    } else {
      MinHash.exactMinHash(minhashableReads,
        signatureLength,
        seed)
    }

    // filter out reads who are insufficently similar
    val filteredOverlaps = minhashedOverlaps.flatMap(kv => {
      val (similarity, readPair) = kv
      if (similarity < overlapThreshold) {
        None
      } else {
        Some(readPair)
      }
    })

    Graph(readsWithIds, filteredOverlaps.flatMap(alignReads))
  }

  /**
   * Aligns two reads together using a HMM based aligner. Only aligns reads where the first
   * read in the pair has a higher ID.
   *
   * @param reads A pair of two reads to align.
   * @return If the two reads should be aligned, the alignment outcome.
   */
  private[overlapping] def alignReads(reads: (MinHashableRead, MinHashableRead)): Option[Edge[Overlap]] = {
    val (r1, r2) = reads

    if (r1.id >= r2.id) {
      None
    } else {
      val r1seq = r1.read.getSequence.toString
      val r2seq = r2.read.getSequence.toString

      // align reads
      val sameStrandAlignment = HMMAligner.align(r1seq,
        r2seq,
        null)
      val diffStrandAlignment = HMMAligner.align(r1seq,
        SequenceUtils.reverseComplement(r2seq),
        null)

      // pick the better alignment
      val (switchStrand, aln) = if (sameStrandAlignment.likelihood >= diffStrandAlignment.likelihood) {
        (false, sameStrandAlignment)
      } else {
        (true, diffStrandAlignment)
      }

      // build the edge
      Some(Edge(r1.id, r2.id, alignmentToOverlap(aln, switchStrand, r1seq.length, r2seq.length)))
    }
  }

  /**
   * Given an alignment from the HMM, this produces a compact representation
   * showing the ranges where the two reads have an alignment match.
   *
   * @param alignment The alignment to analyze.
   * @param strandSwitch True if the two reads are from different strands.
   * @param r1Length The length of the first read.
   * @param r2Length The length of the second read.
   * @return Emits the relevant overlap.
   */
  private[overlapping] def alignmentToOverlap(alignment: Alignment,
                                              strandSwitch: Boolean,
                                              r1Length: Int,
                                              r2Length: Int): Overlap = {

    // get compressed alignment representation
    val states = alignment.generateCompressedStateSequence(alignment.alignmentStateSequence
      .map(c => {
        // we don't care about mismatches; just treat them as matches
        if (c == 'X') {
          '='
        } else {
          c
        }
      }))

    // get the number of matches
    val matches = states.filter(p => p._2 == '=').length

    // start building up ranges
    var overlap = 0
    var r1pos = 0
    var (r2pos, incr) = if (strandSwitch) {
      (r2Length, -1)
    } else {
      (0, 1)
    }

    // construct array
    val stateArray = new Array[(Range, Range)](matches)

    @tailrec def traverseAlignment(iterator: Iterator[(Int, Char)],
                                   idx: Int = 0) {
      if (idx < matches) {
        assert(iterator.hasNext, "Have run out of elements in iterator at " + idx + ".")

        // get next operator
        val (len, op) = iterator.next

        // calculate update parameters
        val (r2f, r2e, r2u) = if (strandSwitch) {
          (-len, 0, -len)
        } else {
          (0, len, len)
        }

        // update array given operator
        val idxUpdate = op match {
          case '=' => {
            overlap += len
            stateArray(idx) = ((r1pos to (r1pos + len)), ((r2pos + r2f) to (r2pos + r2e)))
            r1pos += len
            r2pos += r2u
            idx + 1
          }
          case 'I' => {
            r1pos += len
            idx
          }
          case 'D' => {
            r2pos += r2u
            idx
          }
          case 'P' => {
            r2pos += r2u
            idx
          }
        }

        // recurse
        traverseAlignment(iterator, idxUpdate)
      }
    }

    // traverse the alignment
    traverseAlignment(states.toIterator)

    // build and return the overlap
    Overlap(overlap, strandSwitch, stateArray)
  }
}
