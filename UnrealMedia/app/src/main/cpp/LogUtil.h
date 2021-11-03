#include "android/log.h"
#include <assert.h>

#ifndef __LOG__
#define __LOG__
#ifdef __cplusplus
extern "C" {
#endif


#define  LOG_TAG    "Unreal"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)


#ifdef __cplusplus
}
#endif
#endif
