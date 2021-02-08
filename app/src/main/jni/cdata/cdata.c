#include <jni.h>
#include <string.h>
#include <android/log.h>
#include <stdlib.h>
#include <math.h>

#include "sha256.h"

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

///-------------------------------------------------------------------------------
#undef get16bits
#if (defined(__GNUC__) && defined(__i386__)) || defined(__WATCOMC__) \
  || defined(_MSC_VER) || defined (__BORLANDC__) || defined (__TURBOC__)
#define get16bits(d) (*((const uint16_t *) (d)))
#endif

#if !defined (get16bits)
#define get16bits(d) ((((uint32_t)(((const uint8_t *)(d))[1])) << 8)\
                       +(uint32_t)(((const uint8_t *)(d))[0]) )
#endif

uint32_t _FastHash(const char * data, int len)
{
    uint32_t hash = len, tmp;
    int rem;

    if (len <= 0 || data == NULL) return 0;

    rem = len & 3;
    len >>= 2;

    /* Main loop */
    for (;len > 0; len--)
    {
        hash  += get16bits (data);
        tmp    = (get16bits (data+2) << 11) ^ hash;
        hash   = (hash << 16) ^ tmp;
        data  += 2*sizeof (uint16_t);
        hash  += hash >> 11;
    }

    /* Handle end cases */
    switch (rem)
    {
        case 3: hash += get16bits (data);
                hash ^= hash << 16;
                hash ^= ((signed char)data[sizeof (uint16_t)]) << 18;
                hash += hash >> 11;
                break;
        case 2: hash += get16bits (data);
                hash ^= hash << 11;
                hash += hash >> 17;
                break;
        case 1: hash += (signed char)*data;
                hash ^= hash << 10;
                hash += hash >> 1;
    }

    /* Force "avalanching" of final 127 bits */
    hash ^= hash << 3;
    hash += hash >> 5;
    hash ^= hash << 4;
    hash += hash >> 17;
    hash ^= hash << 25;
    hash += hash >> 6;

    return hash;
}
//-----------------------------------------------------------------------------------
char *xor_6320(char *source, unsigned int len_source, const char *password, unsigned int len_p)
{
    char *result = NULL;
    unsigned int i;

    char _salt[] = "ER#@!AS#OK load";

    unsigned int _len_tmp = 0, offset = 0;
    char* _tmp_str = NULL;
    uint32_t hash = 0;

    _len_tmp  = strlen( _salt );
    _len_tmp += len_p;
    _len_tmp += sizeof(uint32_t);

    //__android_log_print(ANDROID_LOG_DEBUG, "TAG", "_len_tmp: %d %d\n", _len_tmp, strlen( _salt ));

    _tmp_str = (char*) calloc( _len_tmp + 1, 1 );

    offset = 0;
    for(i = 0; i < strlen( _salt ); i++)
    {
        _tmp_str[offset + i] = _salt[i];
    }

    offset += strlen( _salt );

    for(i = 0; i < len_p; i++)
    {
        _tmp_str[offset + i] = password[i];
    }

    offset += len_p;

    hash = _FastHash(_tmp_str, offset);


    memcpy(_tmp_str + offset, &hash, sizeof(uint32_t));

    _tmp_str[ _len_tmp ] = '\0';

    //__android_log_print(ANDROID_LOG_DEBUG, "TAG", "hash: %s %u\n", _tmp_str,  hash);

	BYTE buf[SHA256_BLOCK_SIZE];
	SHA256_CTX ctx;

	sha256_init(&ctx);
	sha256_update(&ctx, (BYTE *)_tmp_str, _len_tmp);
	sha256_final(&ctx, buf);


    result = (char *) malloc( sizeof(char) * (len_source + 1) );

	for(i=0; i < len_source; i++)
	{
		result[i] = source[i] ^ buf[ i % SHA256_BLOCK_SIZE ];
	}

	result[ len_source ] = '\0';

    free(_tmp_str);

    return result;
}

JNIEXPORT jbyteArray JNICALL Java_games2d_com_nards_JniApi_dataEncrypt(JNIEnv * env, jobject  obj, jbyteArray array, jstring password)
{
	jboolean isCopy;
	int pass_length  = (*env)->GetStringUTFLength(env, password);
	const char *pass = (*env)->GetStringUTFChars(env, password, &isCopy);
	//char *pass_m = NULL;
	jbyte *data;
	char *res;
	jbyteArray result;
	unsigned int array_length = (*env)->GetArrayLength(env, array);

	data = (*env)->GetByteArrayElements(env, array, NULL);

	res = xor_6320( (char *) data, array_length, pass, pass_length );

	result = (*env)->NewByteArray(env, array_length);

	(*env)->SetByteArrayRegion(env, result, 0, array_length, (jbyte*)res);

	free(res);
	if (isCopy == JNI_TRUE)
	{
		(*env)->ReleaseStringUTFChars(env, password, pass);
	}
	(*env)->ReleaseByteArrayElements(env, array, data, 0); // release resources

	return result;
}

/*JNIEXPORT jstring JNICALL Java_games2d_com_nards_JniApi_f1(JNIEnv * env, jobject  obj)
{
	//char *szResult = "37.143.15.131;p1.timeproject.ru";
	//char *szResult = " 178.132.206.100; p1.timeproject.ru";
	char *szResult = "p1.timeproject.ru";

	// получаем объект string
    jstring result = (*env)->NewStringUTF(env, szResult);

    // очищаем память
    //free(szResult);

    return result;
}*/

JNIEXPORT jstring JNICALL Java_games2d_com_nards_JniApi_dfp1(JNIEnv * env, jobject  obj)
{
	char *szResult = "FT!@#$()*%%540";

	// получаем объект string
    jstring result = (*env)->NewStringUTF(env, szResult);

    // очищаем память
    //free(szResult);

    return result;
}

//------------------------------------------------------------------------------------------


uint8_t default_xor_key[] = { 0x10, 0xaf, 0x25, 0x36, 0xa8, 0xad, 0xd0, 0x13, 0x55, 0x77, 0x12, 0x08, 0x19, 0xdf, 0x3f, 0x3f };

///-----------------------------------------------------------------------------------------
unsigned char* simple_xor(unsigned char* data, uint32_t len, uint8_t *key, uint8_t key_len)
{
    unsigned char* s = (unsigned char*)calloc( len, 1 );
    uint32_t i = 0;

    memcpy(s, data, len);

    size_t f = 0;
    for(i = 0; i < len; i++)
    {
        s[i] ^= key[f++ % key_len];
    }
    return s;
}

JNIEXPORT jbyteArray JNICALL Java_games2d_com_nards_JniApi_dataEncrypt1(JNIEnv * env, jobject  obj, jbyteArray array)
{
	jbyte *data;
	jbyteArray result;
	unsigned int array_length = (*env)->GetArrayLength(env, array);

	data = (*env)->GetByteArrayElements(env, array, NULL);

	result = (*env)->NewByteArray(env, array_length);

    unsigned char *xor_1 = simple_xor((uint8_t *)data, array_length, default_xor_key, 16 );

	(*env)->SetByteArrayRegion(env, result, 0, array_length, (jbyte*)xor_1);

	free(xor_1);
	(*env)->ReleaseByteArrayElements(env, array, data, 0); // release resources

	return result;
}

JNIEXPORT jbyteArray JNICALL Java_games2d_com_nards_JniApi_dataDecrypt1(JNIEnv * env, jobject  obj, jbyteArray array)
{
	jbyte *data;
	jbyteArray result;
	unsigned int array_length = (*env)->GetArrayLength(env, array);

	data = (*env)->GetByteArrayElements(env, array, NULL);

	result = (*env)->NewByteArray(env, array_length);
	
	unsigned char *xor_2 = simple_xor((uint8_t *)data, array_length, default_xor_key, 16 );

	(*env)->SetByteArrayRegion(env, result, 0, array_length, (jbyte*)xor_2);

	free(xor_2);
	(*env)->ReleaseByteArrayElements(env, array, data, 0); // release resources

	return result;
}

JNIEXPORT jbyteArray JNICALL Java_games2d_com_nards_JniApi_dataEncrypt2(JNIEnv * env, jobject  obj, jbyteArray array, jbyteArray jxor_key)
{
	jbyte *data, *xor_key;
	jbyteArray result;
	unsigned int array_length = (*env)->GetArrayLength(env, array);
	unsigned int xor_key_length = (*env)->GetArrayLength(env, jxor_key);

	data     = (*env)->GetByteArrayElements(env, array, NULL);
	xor_key  = (*env)->GetByteArrayElements(env, jxor_key, NULL);

	result = (*env)->NewByteArray(env, array_length);

    unsigned char *xor_1 = simple_xor((uint8_t *)data, array_length, (uint8_t *)xor_key, 16 );

	(*env)->SetByteArrayRegion(env, result, 0, array_length, (jbyte*)xor_1);

	free(xor_1);
	(*env)->ReleaseByteArrayElements(env, array, data, 0); // release resources
	(*env)->ReleaseByteArrayElements(env, jxor_key, xor_key, 0); // release resources

	return result;
}

JNIEXPORT jbyteArray JNICALL Java_games2d_com_nards_JniApi_dataDecrypt2(JNIEnv * env, jobject  obj, jbyteArray array, jbyteArray jxor_key)
{
	jbyte *data, *xor_key;
	jbyteArray result;
	unsigned int array_length = (*env)->GetArrayLength(env, array);
	unsigned int xor_key_length = (*env)->GetArrayLength(env, jxor_key);

	data     = (*env)->GetByteArrayElements(env, array, NULL);
	xor_key  = (*env)->GetByteArrayElements(env, jxor_key, NULL);

	result = (*env)->NewByteArray(env, array_length);
	
	unsigned char *xor_2 = simple_xor((uint8_t *)data, array_length, (uint8_t *)xor_key, 16 );

	(*env)->SetByteArrayRegion(env, result, 0, array_length, (jbyte*)xor_2);

	free(xor_2);
	(*env)->ReleaseByteArrayElements(env, array, data, 0); // release resources
	(*env)->ReleaseByteArrayElements(env, jxor_key, xor_key, 0); // release resources

	return result;
}

JNIEXPORT jstring JNICALL Java_games2d_com_nards_JniApi_f1(JNIEnv * env, jobject  obj)
{
	//char *szResult = "185.22.232.104";
	//char *szResult = "10.42.0.1";
	char *szResult = "forobots.ru";

	// получаем объект string
    jstring result = (*env)->NewStringUTF(env, szResult);

    // очищаем память
    //free(szResult);

    return result;
}

JNIEXPORT jint JNICALL Java_games2d_com_nards_JniApi_port1(JNIEnv * env, jobject  obj)
{
	return 21540;
}

JNIEXPORT jint JNICALL Java_games2d_com_nards_JniApi_port2(JNIEnv * env, jobject  obj)
{
    return 21544;
}

/*JNIEXPORT jdouble JNICALL Java_games2d_com_nards_JniApi_sqrt(JNIEnv *env, jobject  obj, jdouble d)
{
    return sqrt(d);
}*/
//------------------------------------------------------------------------------------------

static const unsigned int crc32_table[] =
{
  0x00000000, 0x04c11db7, 0x09823b6e, 0x0d4326d9,
  0x130476dc, 0x17c56b6b, 0x1a864db2, 0x1e475005,
  0x2608edb8, 0x22c9f00f, 0x2f8ad6d6, 0x2b4bcb61,
  0x350c9b64, 0x31cd86d3, 0x3c8ea00a, 0x384fbdbd,
  0x4c11db70, 0x48d0c6c7, 0x4593e01e, 0x4152fda9,
  0x5f15adac, 0x5bd4b01b, 0x569796c2, 0x52568b75,
  0x6a1936c8, 0x6ed82b7f, 0x639b0da6, 0x675a1011,
  0x791d4014, 0x7ddc5da3, 0x709f7b7a, 0x745e66cd,
  0x9823b6e0, 0x9ce2ab57, 0x91a18d8e, 0x95609039,
  0x8b27c03c, 0x8fe6dd8b, 0x82a5fb52, 0x8664e6e5,
  0xbe2b5b58, 0xbaea46ef, 0xb7a96036, 0xb3687d81,
  0xad2f2d84, 0xa9ee3033, 0xa4ad16ea, 0xa06c0b5d,
  0xd4326d90, 0xd0f37027, 0xddb056fe, 0xd9714b49,
  0xc7361b4c, 0xc3f706fb, 0xceb42022, 0xca753d95,
  0xf23a8028, 0xf6fb9d9f, 0xfbb8bb46, 0xff79a6f1,
  0xe13ef6f4, 0xe5ffeb43, 0xe8bccd9a, 0xec7dd02d,
  0x34867077, 0x30476dc0, 0x3d044b19, 0x39c556ae,
  0x278206ab, 0x23431b1c, 0x2e003dc5, 0x2ac12072,
  0x128e9dcf, 0x164f8078, 0x1b0ca6a1, 0x1fcdbb16,
  0x018aeb13, 0x054bf6a4, 0x0808d07d, 0x0cc9cdca,
  0x7897ab07, 0x7c56b6b0, 0x71159069, 0x75d48dde,
  0x6b93dddb, 0x6f52c06c, 0x6211e6b5, 0x66d0fb02,
  0x5e9f46bf, 0x5a5e5b08, 0x571d7dd1, 0x53dc6066,
  0x4d9b3063, 0x495a2dd4, 0x44190b0d, 0x40d816ba,
  0xaca5c697, 0xa864db20, 0xa527fdf9, 0xa1e6e04e,
  0xbfa1b04b, 0xbb60adfc, 0xb6238b25, 0xb2e29692,
  0x8aad2b2f, 0x8e6c3698, 0x832f1041, 0x87ee0df6,
  0x99a95df3, 0x9d684044, 0x902b669d, 0x94ea7b2a,
  0xe0b41de7, 0xe4750050, 0xe9362689, 0xedf73b3e,
  0xf3b06b3b, 0xf771768c, 0xfa325055, 0xfef34de2,
  0xc6bcf05f, 0xc27dede8, 0xcf3ecb31, 0xcbffd686,
  0xd5b88683, 0xd1799b34, 0xdc3abded, 0xd8fba05a,
  0x690ce0ee, 0x6dcdfd59, 0x608edb80, 0x644fc637,
  0x7a089632, 0x7ec98b85, 0x738aad5c, 0x774bb0eb,
  0x4f040d56, 0x4bc510e1, 0x46863638, 0x42472b8f,
  0x5c007b8a, 0x58c1663d, 0x558240e4, 0x51435d53,
  0x251d3b9e, 0x21dc2629, 0x2c9f00f0, 0x285e1d47,
  0x36194d42, 0x32d850f5, 0x3f9b762c, 0x3b5a6b9b,
  0x0315d626, 0x07d4cb91, 0x0a97ed48, 0x0e56f0ff,
  0x1011a0fa, 0x14d0bd4d, 0x19939b94, 0x1d528623,
  0xf12f560e, 0xf5ee4bb9, 0xf8ad6d60, 0xfc6c70d7,
  0xe22b20d2, 0xe6ea3d65, 0xeba91bbc, 0xef68060b,
  0xd727bbb6, 0xd3e6a601, 0xdea580d8, 0xda649d6f,
  0xc423cd6a, 0xc0e2d0dd, 0xcda1f604, 0xc960ebb3,
  0xbd3e8d7e, 0xb9ff90c9, 0xb4bcb610, 0xb07daba7,
  0xae3afba2, 0xaafbe615, 0xa7b8c0cc, 0xa379dd7b,
  0x9b3660c6, 0x9ff77d71, 0x92b45ba8, 0x9675461f,
  0x8832161a, 0x8cf30bad, 0x81b02d74, 0x857130c3,
  0x5d8a9099, 0x594b8d2e, 0x5408abf7, 0x50c9b640,
  0x4e8ee645, 0x4a4ffbf2, 0x470cdd2b, 0x43cdc09c,
  0x7b827d21, 0x7f436096, 0x7200464f, 0x76c15bf8,
  0x68860bfd, 0x6c47164a, 0x61043093, 0x65c52d24,
  0x119b4be9, 0x155a565e, 0x18197087, 0x1cd86d30,
  0x029f3d35, 0x065e2082, 0x0b1d065b, 0x0fdc1bec,
  0x3793a651, 0x3352bbe6, 0x3e119d3f, 0x3ad08088,
  0x2497d08d, 0x2056cd3a, 0x2d15ebe3, 0x29d4f654,
  0xc5a92679, 0xc1683bce, 0xcc2b1d17, 0xc8ea00a0,
  0xd6ad50a5, 0xd26c4d12, 0xdf2f6bcb, 0xdbee767c,
  0xe3a1cbc1, 0xe760d676, 0xea23f0af, 0xeee2ed18,
  0xf0a5bd1d, 0xf464a0aa, 0xf9278673, 0xfde69bc4,
  0x89b8fd09, 0x8d79e0be, 0x803ac667, 0x84fbdbd0,
  0x9abc8bd5, 0x9e7d9662, 0x933eb0bb, 0x97ffad0c,
  0xafb010b1, 0xab710d06, 0xa6322bdf, 0xa2f33668,
  0xbcb4666d, 0xb8757bda, 0xb5365d03, 0xb1f740b4
};

unsigned int xcrc32 (const unsigned char *buf, int len, unsigned int init)
{
  unsigned int crc = init;
  while (len--)
    {
      crc = (crc << 8) ^ crc32_table[((crc >> 24) ^ *buf) & 255];
      buf++;
    }
  return crc;
}

JNIEXPORT jint JNICALL Java_games2d_com_nards_JniApi_xcrc32(JNIEnv * env, jobject  obj, jbyteArray array)
{
	jbyte *data;
	jbyteArray result;
	unsigned int array_length = (*env)->GetArrayLength(env, array);

	data = (*env)->GetByteArrayElements(env, array, NULL);

    unsigned int res_crc32 = xcrc32((uint8_t *)data, array_length, 0xffffffff );

	(*env)->ReleaseByteArrayElements(env, array, data, 0); // release resources

	return res_crc32;
}

