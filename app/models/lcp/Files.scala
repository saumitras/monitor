package models.lcp

import play.api.Logger

// These values go in OpsTable "processing_state" column
object ProcessingState extends Enumeration {
  type ProcessingState = Value
  val Seen,             // initial file state set by Watcher
      Parsing,          // file is being parsed and added to logvault
      Parsed,           // file has been parsed
      Skip_Parsing,     // file is of type Vault_File, so it is NOT being parsed
      Failed,           // parsing failed for the file due to errors in context or spl
      Duplicate         // there is assertDuplicate in this file's context and this file has been found to be duplicate of an instance thats been already parsed
      = Value
}

object Files {

}
