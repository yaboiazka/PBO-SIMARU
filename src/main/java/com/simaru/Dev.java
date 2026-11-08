package com.simaru;

import java.util.Scanner;

import com.simaru.model.Mahasiswa;
import com.simaru.model.Pengguna;

public class Dev {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your email: ");
        String email = scanner.nextLine();
        System.out.print("Enter your Passcode: ");
        Pengguna pengguna = new Mahasiswa(0, "Dev", email, "", "123456789", "Computer Science");
        String passcode = scanner.nextLine();
        System.out.println(pengguna.login(email, passcode) ? "Login successful!" : "Login failed!");
        scanner.close();
    }
}
