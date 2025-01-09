package com.currencyconverter.jsonadapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson
import java.util.AbstractMap

object MapEntryJsonAdapter: JsonAdapter<Map.Entry<String, String>>() {

    @FromJson
    override fun fromJson(reader: JsonReader): Map.Entry<String, String> {
        reader.beginObject()
        var name = reader.nextName();
        if (name != "key") throw JsonDataException("Unknown name: $name");
        val key = reader.nextString();
        name = reader.nextName();
        if (name != "value") throw JsonDataException("Unknown name: $name");
        val value = reader.nextString();
        reader.endObject();
        return AbstractMap.SimpleEntry(key, value);
    }

    @ToJson
    override fun toJson(writer: JsonWriter, data: Map.Entry<String, String>?) {
        writer.beginObject().name("key").value(data?.key.toString()).name("value").value(data?.value.toString()).endObject()
    }
}
