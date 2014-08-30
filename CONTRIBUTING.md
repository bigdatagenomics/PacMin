How to contribute to PacMin
=========================

Thank you for sharing your code with the PacMin project. We appreciate your contribution!

## Join our IRC channel

You can find us on Freenode IRC in the #adamdev room.

## Check the issue tracker

Before you write too much code, check the [open issues in the PacMin issue tracker](https://github.com/bigdatagenomics/pacmin/issues?state=open)
to see if someone else has already filed an issue related to your work or is already working on it. If not, go ahead and 
[open a new issue](https://github.com/bigdatagenomics/pacmin/issues/new).

## Submit your pull request

Github provides a nice [overview on how to create a pull request](https://help.github.com/articles/creating-a-pull-request).

Some general rules to follow:

* Do your work in [a fork](https://help.github.com/articles/fork-a-repo) of the PacMin repo.
* Create a branch for each feature/bug in PacMin that you're working on. These branches are often called "feature"
or "topic" branches.
* Use your feature branch in the pull request. Any changes that you push to your feature branch will automatically
be shown in the pull request.  If your feature branch is not based off the latest master, you will be asked to rebase
it before it is merged. This ensures that the commit history is linear, which makes the commit history easier to read.
* Before contributing code to PacMin, check the [Github issue tracker](https://github.com/bigdatagenomics/pacmin/issues).
If there is not an open ticket for what you would like to work on, please open it. When you submit your changes to PacMin,
reference the issue from the pull request so that it will [be closed when your pull request is merged](https://github.com/blog/1506-closing-issues-via-pull-requests).
Also, please reference the issue number in your commit.
* Run the ./scripts/format-source script in order to format the code and ensure correct license headers
  ```
  $ ./scripts/format-source
  ```
* Please alphabetize your imports. We follow the following approach: first, alphabetize by package name (e.g., `org.apache.spark`
should be before `org.bigdatagenomics.pacmin`). Within a project, "lower level" packages should be sorted ahead (e.g.,
`org.apache.spark.SparkContext` should be before `org.apache.spark.rdd.RDD`). Within a single package, sort alphabetically,
but put object implicit imports first (e.g., put `org.apache.spark.SparkContext._` before `org.apache.spark.Logging`, and
`org.apache.spark.Logging` before `org.apache.spark.SparkContext`).
* Keep your pull requests as small as possible. Large pull requests are hard to review. Try to break up your changes
into self-contained and incremental pull requests, if need be, and reference dependent pull requests, e.g. "This pull
request builds on request #92. Please review #92 first."
* The first line of commit messages should start by referencing the issue number they fix (i.e., "[PacMin-307]" indicates that
this commit fixes PacMin issue #307), followed by a short (<80 character) summary, followed by an empty line and then,
optionally, any details that you want to share about the commit.
* Include unit tests with your pull request. We love tests and [use Jenkins](https://amplab.cs.berkeley.edu/jenkins/)
to check every pull request and commit. Just look for files in the PacMin repo that end in "*Suite.scala", 
