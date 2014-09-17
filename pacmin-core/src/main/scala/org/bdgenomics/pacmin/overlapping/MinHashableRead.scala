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

import org.bdgenomics.formats.avro.AlignmentRecord
import org.bdgenomics.pacmin.minhash.MinHashable

case class MinHashableRead(read: AlignmentRecord,
                           kmerLen: Int) extends MinHashable {

  /**
   * Create hashes for this read by breaking it into shingles by splitting
   * it into equal length k-mers, then take the hash code of each k-mer string.
   *
   * @return Returns an array containing the hash code of each k-mer in the read.
   */
  def provideHashes(): Array[Int] = {
    read.getSequence
      .toString
      .sliding(kmerLen)
      .map(_.hashCode)
      .toArray
  }

  override def toString: String = read.toString
}
