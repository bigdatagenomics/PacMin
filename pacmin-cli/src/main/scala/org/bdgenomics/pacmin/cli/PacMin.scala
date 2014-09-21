/**
 * Licensed to Big Data Genomics (BDG) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The BDG licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bdgenomics.pacmin.cli

import org.apache.hadoop.mapreduce.Job
import org.apache.spark.rdd.RDD
import org.apache.spark.{ SparkContext, Logging }
import org.kohsuke.args4j.{ Option => option, Argument }
import org.bdgenomics.formats.avro.{
  AlignmentRecord,
  NucleotideContigFragment
}
import org.bdgenomics.adam.cli.{
  ADAMCommandCompanion,
  ADAMSparkCommand,
  ParquetArgs,
  Args4j,
  Args4jBase
}
import org.bdgenomics.adam.rdd.ADAMContext._
import org.bdgenomics.pacmin.graph.OverlapGraph
import org.bdgenomics.pacmin.overlapping.Overlapper

object PacMin extends ADAMCommandCompanion {

  val commandName = "PacMin"
  val commandDescription = "Assemble contigs from PacBio reads."

  def apply(args: Array[String]) = {
    new PacMin(Args4j[PacMinArgs](args))
  }
}

class PacMinArgs extends Args4jBase with ParquetArgs {
  @Argument(metaVar = "READS", required = true, usage = "ADAM read-oriented data", index = 0)
  var readInput: String = _

  @Argument(metaVar = "CONTIGS", required = true, usage = "Contig output", index = 1)
  var contigOutput: String = _

  @option(name = "-debug", usage = "If set, prints a higher level of debug output.")
  var debug = false

  @option(required = true, name = "-overlap_threshold", usage = "Sets the similarity threshold for considering two reads to overlap.")
  var overlapThreshold: Double = _

  @option(required = true, name = "-signature_length", usage = "The length of MinHash signature to use.")
  var signatureLength: Int = _

  @option(required = true, name = "-kmer_length", usage = "The length of k-mers to use when shingling reads.")
  var kmerLength: Int = _

  @option(required = false, name = "-minhash_lsh_bands", usage = "If using approximate LSH for MinHashing, the number of bands to use.")
  var bands: Int = -1
}

class PacMin(protected val args: PacMinArgs) extends ADAMSparkCommand[PacMinArgs] with Logging {

  // companion object to this class - needed for ADAMCommand framework
  val companion = PacMin

  /**
   * Main method. Assembles an overlap graph by overlapping reads, then finds
   * the multigs in the graph.
   *
   * @param sc SparkContext for RDDs.
   * @param job Hadoop Job container for file I/O.
   */
  def run(sc: SparkContext, job: Job) {

    // load reads
    val reads: RDD[AlignmentRecord] = sc.adamLoad(args.readInput)

    // if bands is set, wrap
    val bands: Option[Int] = if (args.bands != -1) {
      Some(args.bands)
    } else {
      None
    }

    // overlap reads
    val overlapGraph = Overlapper(reads,
      args.overlapThreshold,
      args.signatureLength,
      args.kmerLength,
      bands)

    // go from an overlap graph to multigs, via a string graph
    val multigs = OverlapGraph(overlapGraph)

    // save multigs as a text fastg
    multigs.map(_.toFastg)
      .saveAsTextFile(args.contigOutput)
  }
}
