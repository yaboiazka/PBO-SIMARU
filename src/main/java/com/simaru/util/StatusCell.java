package com.simaru.util;

import javafx.scene.control.TableCell;

/**
 * Sel tabel status dengan warna. Teks tetap ditampilkan sehingga warna bukan
 * satu-satunya penanda.
 */
public class StatusCell<S> extends TableCell<S, String> {
    @Override
    protected void updateItem(String item, boolean kosong) {
        super.updateItem(item, kosong);
        getStyleClass().removeAll("status-pending", "status-disetujui", "status-ditolak");
        if (kosong || item == null) {
            setText(null);
            return;
        }
        setText(item);
        if (Status.PENDING.equalsIgnoreCase(item)) {
            getStyleClass().add("status-pending");
        } else if (Status.DISETUJUI.equalsIgnoreCase(item)) {
            getStyleClass().add("status-disetujui");
        } else if (Status.DITOLAK.equalsIgnoreCase(item)) {
            getStyleClass().add("status-ditolak");
        }
    }
}
