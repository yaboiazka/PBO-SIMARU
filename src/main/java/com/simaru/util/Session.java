package com.simaru.util;

import com.simaru.model.Admin;
import com.simaru.model.Pengguna;

public class Session {
    private static Pengguna current;

    public Session(Pengguna pengguna) {
        this.current = pengguna;
    }

    public static Pengguna getCurrent() {
        return current;
    }

    public static boolean isAdmin() {
        return current instanceof Admin;
    }

    public static void clear() {
        current = null;
    }
}
