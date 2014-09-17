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
  var configFile: String = _

  @option(name = "-debug", usage = "If set, prints a higher level of debug output.")
  var debug = false

  @option(required = false, name = "-fragment_length", usage = "Sets maximum fragment length. Default value is 10,000. Values greater than 1e9 should be avoided.")
  var fragmentLength: Long = 10000L
}

class PacMin(protected val args: PacMinArgs) extends ADAMSparkCommand[PacMinArgs] with Logging {

  // companion object to this class - needed for ADAMCommand framework
  val companion = PacMin

  /**
   * Main method. Does nothing at the moment!
   *
   * @param sc SparkContext for RDDs.
   * @param job Hadoop Job container for file I/O.
   */
  def run(sc: SparkContext, job: Job) {
    log.error("I don't do anything yet.")
  }
}
