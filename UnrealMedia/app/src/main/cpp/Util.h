//
// Created by Duke
//

#ifndef UM_UTIL_H
#define UM_UTIL_H
#include <jni.h>
#include "LogUtil.h"

JNIEnv *getJNIEnv(JavaVM *vm, int *needsDetach);

#endif //UM_UTIL_H
