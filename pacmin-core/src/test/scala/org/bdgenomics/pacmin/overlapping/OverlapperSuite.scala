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

import org.bdgenomics.adam.util.SequenceUtils
import org.bdgenomics.formats.avro.AlignmentRecord
import org.bdgenomics.pacmin.utils.PacMinFunSuite
import scala.math.abs
import scala.util.Random

class OverlapperSuite extends PacMinFunSuite {

  val kmerLength = 15

  def randomString(seed: Int, len: Int): (String, Random) = {
    val r = new Random(seed)

    ((0 until len).map(i => r.nextInt(4))
      .map(i => i match {
        case 0 => "A"
        case 1 => "C"
        case 2 => "G"
        case _ => "T"
      }).reduceLeft(_ + _), r)
  }

  test("compute overlap between two strings from the same strand") {
    val rString = randomString(10, 1100)._1

    val r1 = MinHashableRead(0L, AlignmentRecord.newBuilder()
      .setSequence(rString.take(1000))
      .build(), kmerLength)
    val r2 = MinHashableRead(1L, AlignmentRecord.newBuilder()
      .setSequence(rString.takeRight(1000))
      .build(), kmerLength)

    val readAlignmentOpt = Overlapper.alignReads((r1, r2))

    assert(readAlignmentOpt.isDefined)
    val readAlignment = readAlignmentOpt.get

    assert(readAlignment.srcId === 0L)
    assert(readAlignment.dstId === 1L)
    assert(readAlignment.attr.estimatedSize === 900)
    assert(!readAlignment.attr.switchesStrands)
    assert(readAlignment.attr.correspondance.filter(p => p._1.length > 10).length === 1)
    assert(readAlignment.attr.correspondance.head._1 === (0 to 867))
    assert(readAlignment.attr.correspondance.head._2 === (100 to 967))
  }

  test("only test two reads if they meet ordering restrictions") {
    val r1 = MinHashableRead(0L, AlignmentRecord.newBuilder()
      .build(), kmerLength)
    val r2 = MinHashableRead(1L, AlignmentRecord.newBuilder()
      .build(), kmerLength)

    assert(Overlapper.alignReads(r2, r1).isEmpty)
    assert(Overlapper.alignReads(r1, r1).isEmpty)
    assert(Overlapper.alignReads(r2, r2).isEmpty)
  }

  test("compute overlap between two strings from different strands") {
    val rString = randomString(10, 1100)._1

    val r1 = MinHashableRead(0L, AlignmentRecord.newBuilder()
      .setSequence(rString.take(1000))
      .build(), kmerLength)
    val r2 = MinHashableRead(1L, AlignmentRecord.newBuilder()
      .setSequence(SequenceUtils.reverseComplement(rString.takeRight(1000)))
      .build(), kmerLength)

    val readAlignmentOpt = Overlapper.alignReads((r1, r2))

    assert(readAlignmentOpt.isDefined)
    val readAlignment = readAlignmentOpt.get

    assert(readAlignment.srcId === 0L)
    assert(readAlignment.dstId === 1L)
    assert(readAlignment.attr.estimatedSize === 900)
    assert(readAlignment.attr.switchesStrands)
    assert(readAlignment.attr.correspondance.filter(p => p._1.length > 10).length === 1)
    assert(readAlignment.attr.correspondance.head._1 === (0 to 867))
    assert(readAlignment.attr.correspondance.head._2 === (33 to 900))
  }

  sparkTest("compute overlaps for ten 1000 bp reads, all drawn from the same strand") {
    val baseString = randomString(123, 2000)._1
    var read = -1
    val reads = sc.parallelize(baseString
      .sliding(1000, 100)
      .toSeq
      .map(s => {
        read += 1
        AlignmentRecord.newBuilder()
          .setStart(read)
          .setSequence(s)
          .build()
      }))

    val overlapGraph = Overlapper(reads, 0.5, 256, kmerLength, None, Some(123456L))

    val edges = overlapGraph.edges
    assert(edges.count === 27)
    assert(!edges.map(ev => ev.attr.switchesStrands).reduce(_ || _))
  }

  sparkTest("compute overlaps for ten 1000 bp reads, drawn from different strands") {
    val (baseString, rv) = randomString(123, 2000)
    var read = -1
    val reads = sc.parallelize(baseString
      .sliding(1000, 100)
      .toSeq
      .map(s => {
        read += 1
        val flipStrand = rv.nextBoolean()
        val fs = if (flipStrand) {
          SequenceUtils.reverseComplement(s)
        } else {
          s
        }
        AlignmentRecord.newBuilder()
          .setStart(read)
          .setSequence(fs)
          .build()
      }))

    val overlapGraph = Overlapper(reads, 0.5, 256, kmerLength, None, Some(123456L))

    val edges = overlapGraph.edges
    assert(edges.count === 27)
    assert(edges.map(ev => ev.attr.switchesStrands).reduce(_ || _))
    assert(!edges.map(ev => ev.attr.switchesStrands).reduce(_ && _))
  }
}
