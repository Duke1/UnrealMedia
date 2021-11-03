package com.qfleng.um.util

import android.content.Context
import android.text.TextUtils
import com.google.gson.*
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.io.IOException
import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target
import java.lang.reflect.Type

class StringConverter : JsonSerializer<String>, JsonDeserializer<String> {
    override fun serialize(src: String?, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return if (src == null) {
            JsonPrimitive("")
        } else {
            JsonPrimitive(src.toString())
        }
    }

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): String {
        return json
                .asJsonPrimitive
                .asString
    }
}

class StringAdapter : TypeAdapter<String>() {
    @Throws(IOException::class)
    override fun read(reader: JsonReader): String {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull()
            return ""
        }
        return reader.nextString()
    }

    @Throws(IOException::class)
    override fun write(writer: JsonWriter, value: String?) {
        if (value == null) {
            writer.jsonValue("\"\"")
            return
        }
        writer.value(value)
    }
}

/**
 * Gson扩展
 */
fun <T> Context.objectFrom(tClass: Class<T>, str: String): T? {
    if (TextUtils.isEmpty(str)) return null

    return gsonObjectFrom(tClass, str)

}

fun <T> gsonObjectFrom(tClass: Class<T>, str: String): T? {
    if (TextUtils.isEmpty(str)) return null
    try {
        return GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(String::class.java, StringConverter())
                .create()
                .fromJson(str, tClass)
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }

}


fun Any?.toJsonString(fast: Boolean = false): String {
    if (null == this) return ""

    try {
        return if (fast) {
            GsonBuilder()
                    .addSerializationExclusionStrategy(object : ExclusionStrategy {
                        override fun shouldSkipField(f: FieldAttributes): Boolean {
                            return null != f.getAnnotation(ObjectFieldExclude::class.java)
                        }

                        override fun shouldSkipClass(clazz: Class<*>): Boolean {
                            return false
                        }
                    })
                    .create().toJson(this)
        } else {
            GsonBuilder()
                    .serializeNulls()
                    .addSerializationExclusionStrategy(object : ExclusionStrategy {
                        override fun shouldSkipField(f: FieldAttributes): Boolean {
                            return null != f.getAnnotation(ObjectFieldExclude::class.java)
                        }

                        override fun shouldSkipClass(clazz: Class<*>): Boolean {
                            return false
                        }
                    })
                    .registerTypeAdapter(String::class.java, StringAdapter())
                    //                    .registerTypeAdapterFactory(new NullStringToEmptyAdapterFactory())
                    .create()
                    .toJson(this)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        return ""
    }

}


/**
 * Created by Duke .
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
annotation class ObjectFieldExclude
