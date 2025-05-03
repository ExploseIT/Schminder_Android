package uk.co.explose.schminder.android.utils

import androidx.compose.material3.ExperimentalMaterial3Api
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import java.time.OffsetDateTime
import androidx.compose.material3.TimePickerState

class OffsetDateTimeAdapter : JsonDeserializer<OffsetDateTime>, JsonSerializer<OffsetDateTime> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): OffsetDateTime {
        return OffsetDateTime.parse(json.asString)
    }

    override fun serialize(
        src: OffsetDateTime,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return JsonPrimitive(src.toString()) // e.g. "2025-04-23T16:45:12Z"
    }
}


@OptIn(ExperimentalMaterial3Api::class)
fun TimePickerState.setTime(hour: Int, minute: Int) {
    this.hour = hour
    this.minute = minute
}
