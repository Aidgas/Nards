LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := cdata
LOCAL_CFLAGS    := -Werror
LOCAL_SRC_FILES := cdata.c sx.cpp sha256.c
LOCAL_LDLIBS    := -llog 

include $(BUILD_SHARED_LIBRARY)
