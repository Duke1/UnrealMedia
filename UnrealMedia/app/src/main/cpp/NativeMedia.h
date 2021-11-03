//
// Created by Duke
//

#ifndef UM_NATIVEMEDIA_H
#define UM_NATIVEMEDIA_H

#include "LogUtil.h"
#include <jni.h>

#define RNN(env, className, gMethods)   clazz = env->FindClass(className);\
                                        if (!clazz) {\
                                            LOGE("JNI_OnLoad->FindClass (%s) error!",className);\
                                            return -1;\
                                        }\
                                        if (env->RegisterNatives(clazz, gMethods, sizeof(gMethods) / sizeof(gMethods[0])) < 0) {\
                                            LOGE("JNI_OnLoad->RegisterNatives error!");\
                                            return -1;\
                                        }\



#endif //UM_NATIVEMEDIA_H
