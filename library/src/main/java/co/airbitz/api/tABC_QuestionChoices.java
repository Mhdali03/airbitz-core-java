/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.7
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package co.airbitz.api;

public class tABC_QuestionChoices {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected tABC_QuestionChoices(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(tABC_QuestionChoices obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        coreJNI.delete_tABC_QuestionChoices(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setNumChoices(long value) {
    coreJNI.tABC_QuestionChoices_numChoices_set(swigCPtr, this, value);
  }

  public long getNumChoices() {
    return coreJNI.tABC_QuestionChoices_numChoices_get(swigCPtr, this);
  }

  public void setAChoices(SWIGTYPE_p_p_sABC_QuestionChoice value) {
    coreJNI.tABC_QuestionChoices_aChoices_set(swigCPtr, this, SWIGTYPE_p_p_sABC_QuestionChoice.getCPtr(value));
  }

  public SWIGTYPE_p_p_sABC_QuestionChoice getAChoices() {
    long cPtr = coreJNI.tABC_QuestionChoices_aChoices_get(swigCPtr, this);
    return (cPtr == 0) ? null : new SWIGTYPE_p_p_sABC_QuestionChoice(cPtr, false);
  }

  public tABC_QuestionChoices() {
    this(coreJNI.new_tABC_QuestionChoices(), true);
  }

}
