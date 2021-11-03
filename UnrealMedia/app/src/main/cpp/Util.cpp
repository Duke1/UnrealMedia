//
// Created by Duke
//

#include "Util.h"

JNIEnv *getJNIEnv(JavaVM *vm, int *needsDetach) {
  JNIEnv *env = NULL;
  if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
    int status = vm->AttachCurrentThread(&env, 0);
    if (status < 0) {
      LOGD("failed to attach current thread");
      return NULL;
    }
    *needsDetach = 1;
  }
  //LOGD("GetEnv Success");
  return env;
}
