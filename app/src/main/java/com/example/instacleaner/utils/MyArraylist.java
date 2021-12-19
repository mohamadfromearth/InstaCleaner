package com.example.instacleaner.utils;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class MyArraylist extends ArrayList {


    @Override
    public boolean removeAll(@NonNull Collection c) {
        boolean modified = false;
        Iterator<?> e = iterator();
        while (e.hasNext()) {
            if (c.equals(e.next())) {
                e.remove();
                modified = true;
            }
        }
        return modified;
    }
}
