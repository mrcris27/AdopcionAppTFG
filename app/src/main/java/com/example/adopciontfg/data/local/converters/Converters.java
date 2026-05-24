package com.example.adopciontfg.data.local.converters;

import android.text.TextUtils;

import androidx.room.TypeConverter;

import com.example.adopciontfg.model.Characteristic;
import com.example.adopciontfg.model.Species;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Converters {

    // ─── List<String> (fotos) ───────────────────────────────────────────
    @TypeConverter
    public static String fromStringList(List<String> list) {
        if (list == null) return null;
        return TextUtils.join(",", list);
    }

    @TypeConverter
    public static List<String> toStringList(String value) {
        if (value == null || value.isEmpty()) return new ArrayList<>();
        return new ArrayList<>(Arrays.asList(value.split(",")));
    }

    // ─── Species (Enum) ─────────────────────────────────────────────────
    @TypeConverter
    public static String fromSpecies(Species species) {
        if (species == null) return null;
        return species.name();
    }

    @TypeConverter
    public static Species toSpecies(String value) {
        if (value == null) return null;
        return Species.valueOf(value);
    }

    // ─── List<Characteristic> (Enum) ────────────────────────────────────
    @TypeConverter
    public static String fromCharacteristicList(List<Characteristic> list) {
        if (list == null) return null;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i).name());
            if (i < list.size() - 1) sb.append(",");
        }
        return sb.toString();
    }

    @TypeConverter
    public static List<Characteristic> toCharacteristicList(String value) {
        if (value == null || value.isEmpty()) return new ArrayList<>();
        List<Characteristic> list = new ArrayList<>();
        for (String s : value.split(",")) {
            list.add(Characteristic.valueOf(s));
        }
        return list;
    }
}
