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

/**
 * An observation inside of a quasitig. This is used for calling the sequence of
 * the finalized multig, and is generated via the transitive reduction of reads.
 *
 * @param alleles The allele strings observed at this site.
 * @likelihoods The likelihoods that each allele was observed.
 */
private[graph] case class ObservedSite(alleles: Array[String],
                                       likelihoods: Array[Double]) {
  assert(likelihoods.length == alleles.length,
    "Likelihood:allele mapping must be 1:1.")
}
