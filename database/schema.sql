CREATE DATABASE IF NOT EXISTS manajemen_ruangan;
USE manajemen_ruangan;

-- =====================================================
-- TABEL
-- =====================================================

-- program_studi harus dibuat lebih dulu karena direferensikan oleh pengguna
CREATE TABLE program_studi (
    id_prodi INT AUTO_INCREMENT PRIMARY KEY,
    fakultas VARCHAR(100) NOT NULL,
    program_studi VARCHAR(100) NOT NULL
);

CREATE TABLE pengguna (
    id_pengguna INT AUTO_INCREMENT PRIMARY KEY,
    nama VARCHAR(100) NOT NULL,
    nim_nip VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('MAHASISWA','ADMIN','DOSEN') NOT NULL,
    id_prodi INT NULL,
    CONSTRAINT fk_pengguna_prodi FOREIGN KEY (id_prodi) REFERENCES program_studi(id_prodi)
);

CREATE TABLE ruangan (
    id_ruangan INT AUTO_INCREMENT PRIMARY KEY,
    nama_ruangan VARCHAR(50) NOT NULL,
    lokasi VARCHAR(100) NOT NULL,
    kapasitas INT NOT NULL,
    fasilitas VARCHAR(150),
    status ENUM('Tersedia','Dipakai','Perbaikan') DEFAULT 'Tersedia'
);

CREATE TABLE peminjaman (
    id_peminjaman INT AUTO_INCREMENT PRIMARY KEY,
    id_pengguna INT NOT NULL,
    id_ruangan INT NOT NULL,
    tanggal_pinjam DATE NOT NULL,
    jam_mulai TIME NOT NULL,
    jam_selesai TIME NOT NULL,
    keperluan VARCHAR(200),
    status_verifikasi ENUM('Pending','Disetujui','Ditolak') DEFAULT 'Pending',
    tanggal_pengajuan DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_pengguna) REFERENCES pengguna(id_pengguna),
    FOREIGN KEY (id_ruangan) REFERENCES ruangan(id_ruangan)
);

CREATE TABLE verifikasi (
    id_verifikasi INT AUTO_INCREMENT PRIMARY KEY,
    id_peminjaman INT NOT NULL,
    id_admin INT NOT NULL,
    status_verifikasi ENUM('Pending','Disetujui','Ditolak') NOT NULL,
    catatan VARCHAR(200),
    tanggal_verifikasi DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_peminjaman) REFERENCES peminjaman(id_peminjaman) ON DELETE CASCADE,
    FOREIGN KEY (id_admin) REFERENCES pengguna(id_pengguna)
);

CREATE TABLE jadwal (
    id_jadwal INT AUTO_INCREMENT PRIMARY KEY,
    id_ruangan INT NOT NULL,
    id_peminjaman INT NOT NULL,
    tanggal DATE NOT NULL,
    jam_mulai TIME NOT NULL,
    jam_selesai TIME NOT NULL,
    FOREIGN KEY (id_ruangan) REFERENCES ruangan(id_ruangan),
    FOREIGN KEY (id_peminjaman) REFERENCES peminjaman(id_peminjaman) ON DELETE CASCADE
);

-- =====================================================
-- DATA AWAL UNTUK UJI COBA
-- =====================================================

-- Program Studi & Fakultas (harus diisi sebelum pengguna)
INSERT INTO program_studi (fakultas, program_studi) VALUES
('Fakultas Matematika dan Ilmu Pengetahuan Alam', 'Informatika'),   -- id_prodi = 1
('Fakultas Matematika dan Ilmu Pengetahuan Alam', 'Matematika'),    -- id_prodi = 2
('Fakultas Matematika dan Ilmu Pengetahuan Alam', 'Fisika'),        -- id_prodi = 3
('Fakultas Teknik', 'Teknik Elektro'),                              -- id_prodi = 4
('Fakultas Teknik', 'Teknik Mesin');                                -- id_prodi = 5

-- Pengguna (password disimpan sebagai SHA-256, sama dengan yang dipakai aplikasi)
-- Admin memakai id_prodi NULL karena pengelola sistem universal.
-- Mahasiswa dan dosen Informatika memakai id_prodi = 1.
INSERT INTO pengguna (nama, nim_nip, email, password, role, id_prodi) VALUES
('Administrator', 'ADM001', 'admin@email.com', SHA2('admin123', 256), 'ADMIN', NULL),
('Ida Bagus Rama Baskarayana', '2508561002', 'rama.baskarayana@email.com', SHA2('rama12345', 256), 'MAHASISWA', 1),
('I Putu Satria Mahatama', '2508561017', 'satria.mahatama@email.com', SHA2('satria12345', 256), 'MAHASISWA', 1),
('Azka Nafarrel Kagata', '2508561012', 'kagatazka@email.com', SHA2('Keisuke3S', 256), 'MAHASISWA', 1),
('Ida Bagus Gede Juana Praditya', '2508561008', 'gussjuan@email.com', SHA2('juana12345', 256), 'MAHASISWA', 1),
('I Made Satria Bimantara', '198801012015041001', 'satria.bimantara@email.com', SHA2('dosen12345', 256), 'DOSEN', 1);

-- Ruangan
INSERT INTO ruangan (nama_ruangan, lokasi, kapasitas, fasilitas, status) VALUES
('MIPA 2.1', 'Gedung MIPA Lantai 2', 40, 'Proyektor, AC, Whiteboard', 'Tersedia'),
('MIPA 1.4', 'Gedung MIPA Lantai 1', 30, 'Proyektor, AC', 'Tersedia'),
('Lab Komputer', 'Gedung Informatika Lantai 3', 25, 'Komputer, Proyektor, AC', 'Tersedia');
