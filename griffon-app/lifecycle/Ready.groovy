/*
 * This script is executed inside the UI thread, so be sure to  call
 * long running code in another thread.
 *
 * You have the following options
 * - execOutside { // your code }
 * - execFuture { // your code }
 * - Thread.start { // your code }
 *
 * You have the following options to run code again inside the UI thread
 * - execAsync { // your code }
 * - execSync { // your code }
 */

InputStream.metaClass.eachByte = {int len, Closure c ->
  int read = 0
  byte[] buffer = new byte[len]
  while ((read = delegate.read(buffer)) > 0) {
    c(buffer, read)
  }
}

File.metaClass.md5 = {->
  def digest = java.security.MessageDigest.getInstance("MD5")
  delegate.withInputStream() {is ->
    is.eachByte(8192) {buffer, bytesRead ->
      digest.update(buffer, 0, bytesRead)
    }
  }
  new BigInteger(1, digest.digest()).toString(16).padLeft(32, '0')
}