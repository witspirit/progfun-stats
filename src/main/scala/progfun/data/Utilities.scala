package progfun.data

import java.io.{File, PrintWriter}

trait Utilities {

  /** N-th column from a list of (CSV-style) lines.
   */
  def getColumn(n: Int, lines: List[String]): List[String] =
    lines.map(line => line.split("\t")(n)).tail

  /** Create a `PrintWriter` for writing to file `f`.
   *  Apply argument function `op` to `PrintWriter`, thereby
   *  printing to the given file.
   */
  def printToFile(f: File)(op: PrintWriter => Unit) {
    val p = new PrintWriter(f)
    try { op(p) } finally { p.close() }
  }

  /** Determine frequency of occurrence for each element in a list.
   */
  def getFreqs[T](list: List[T]): List[(T, Int)] =
    list.groupBy(x => x)
        .map { case (k, v) => (k, v.length) }
        .toList

  /** Determine frequency of occurrence for each element in a list
   *  when one possible choice is a user filled-in "Other" field.
   */
  def getFreqWithOther(choices: List[String], xs: List[String]) = {
    val (ys, other) = xs.partition(x => choices.contains(x))
    (getFreqs(ys), other)
  }

  def percentOf(count: Int, total: Int): Int =
    (count.toDouble / total * 100).round.toInt
}
