#include <jni.h>
#include <stdlib.h>
#include <string.h>
#include <android/log.h>

/** нормализация типов данных */
typedef signed char         int8_t;
typedef unsigned char       uint8_t;
typedef signed short        int16_t;
typedef unsigned short      uint16_t;
typedef signed int          int32_t;
typedef unsigned int        uint32_t;
//typedef unsigned int        size_t;
//typedef unsigned long       uintptr_t;
//typedef signed long long    int64_t;
//typedef unsigned long long  uint64_t;

unsigned char* simple_xor(unsigned char* data, uint32_t len, const char *key)
{
    unsigned char* s = (unsigned char*)calloc( len, 1 );
    uint32_t i = 0;

    memcpy(s, data, len);

    size_t length = strlen(key), f = 0;
    for(i = 0; i < len; i++)
    {
            s[i] ^= key[f++ % length];
    }
    return s;
}

extern "C" jbyteArray JNICALL Java_com_onlineradio_messenger_MainActivity_SXOR(JNIEnv * env, jobject  obj, jbyteArray array)
{
	jbyteArray result;
	jbyte *data;
	unsigned int array_length = env->GetArrayLength( array);

	data = env->GetByteArrayElements( array, NULL);

	unsigned char* res_encode = simple_xor( (unsigned char *)data, array_length, "REAQ@!#RR&#Daq17" );

	result = env->NewByteArray(array_length);

	env->SetByteArrayRegion(result, 0, array_length, (jbyte*)res_encode);

	free(res_encode);
	env->ReleaseByteArrayElements( array, data, 0); // release resources

	return result;
}

extern "C" jbyteArray JNICALL Java_com_onlineradio_messenger_MyService_SXOR(JNIEnv * env, jobject  obj, jbyteArray array)
{
	jbyteArray result;
	jbyte *data;
	unsigned int array_length = env->GetArrayLength( array);

	data = env->GetByteArrayElements( array, NULL);

	unsigned char* res_encode = simple_xor( (unsigned char *)data, array_length, "REAQ@!#RR&#Daq17" );

	result = env->NewByteArray(array_length);

	env->SetByteArrayRegion(result, 0, array_length, (jbyte*)res_encode);

	free(res_encode);
	env->ReleaseByteArrayElements( array, data, 0); // release resources

	return result;
}
