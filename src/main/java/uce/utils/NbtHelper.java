package uce.utils;

import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtList;

import java.util.List;
import java.util.ListIterator;

public class NbtHelper {

    public static NbtList createNbtListFromFloatArray(Float[] arr) {
        List<Float> list = List.of(arr);
        return createNbtListFromFloatArray(list);
    }

    public static NbtList createNbtListFromFloatArray(List<Float> arr) {
        NbtList nbt = new NbtList();
        ListIterator<Float> it = arr.listIterator();
        while (it.hasNext()) {
            nbt.add(it.nextIndex(), NbtFloat.of(it.next()));
        }
        return nbt;
    }
}
