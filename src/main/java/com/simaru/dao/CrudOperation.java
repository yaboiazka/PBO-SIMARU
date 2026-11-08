package com.simaru.dao;
import java.util.List;

public interface CrudOperation<T> {
    void tambah(T data);
    void ubah(T data);
    void hapus(int id);
    List<T> tampilkan();

}
