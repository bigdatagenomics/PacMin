PacMin
======

Assembler for PacBio reads.

# Methods

We'll overlap the PacBio reads using the MinHash sketch method proposed in:

```
Berlin, Konstantin, et al. "Assembling Large Genomes with Single-Molecule Sequencing and Locality Sensitive Hashing." bioRxiv (2014): 008003.
```

Once the reads are overlapped, we will assemble the reads into a string graph. String graphs are described in:

```
Myers, Eugene W. "The fragment assembly string graph." Bioinformatics 21.suppl 2 (2005): ii79-ii85.
```

We do not assume that reads are "correct"; instead, we will maintain "probabilistic" overlaps between the fragments in the string graph.
Once we have obtained these probabilistic overlaps, we can estimate the ploidy of each overlap by normalizing the overlap coverage by length
and can then apply traditional genotyping methods (e.g., the likelihood estimation stages used in SAMTools) to find the concensus sequences at each overlap.

# Getting Started

## Building PacMin

PacMin uses [Maven](http://maven.apache.org/) to build. To build PacMin, cd into the repository and run "mvn package".

## Running PacMin

ADAM is packaged via [appassembler](http://mojo.codehaus.org/appassembler/appassembler-maven-plugin/) and includes all necessary
dependencies

You might want to add the following to your `.bashrc` to make running `adam` easier:

```
alias pacmin=". $PACMIN_HOME/pacmin-cli/target/appassembler/bin/pacmin"
```

`$PACMIN_HOME` should be the path to where you have checked PacMin out on your local filesystem.
To change any Java options (e.g., the memory settings --> "-Xmx4g", or to pass Java properties)
set the `$JAVA_OPTS` environment variable. Additional details about customizing the appassembler
runtime can be found [here](http://mojo.codehaus.org/appassembler/appassembler-maven-plugin/usage-script.html).

Once this alias is in place, you can run adam by simply typing `pacmin` at the commandline.

# Getting In Touch

# License

PacMin is released under an [Apache 2.0 license](https://github.com/bigdatagenomics/PacMin/blob/master/LICENSE).

# Distribution

Snapshots of PacMin are available from the [Sonatype OSS](https://oss.sonatype.org) repository:

```
<groupId>org.bdgenomics.pacmin</groupId>
<artifactId>pacmin-core</artifactId>
<version>0.0.1-SNAPSHOT</version>
```

Once we've got a release, we will publish to [Maven Central](http://search.maven.org).