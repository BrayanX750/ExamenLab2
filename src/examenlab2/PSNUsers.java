/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package examenlab2;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;

public class PSNUsers {
    private RandomAccessFile file;
    private HashTable users;

    public PSNUsers() {
        try {
            file = new RandomAccessFile("psn.dat", "rw");
            users = new HashTable();
            reloadHashTable();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al abrir el archivo.");
        }
    }

    private void reloadHashTable() {
        try {
            file.seek(0);
            while (file.getFilePointer() < file.length()) {
                long pos = file.getFilePointer();
                String username = file.readUTF();
                boolean activo = file.readBoolean();
                int puntos = file.readInt();
                int trofeos = file.readInt();
                int cantidad = file.readInt();

                if (activo) {
                    users.add(username, pos);
                }

                // Saltar los trofeos
                for (int i = 0; i < cantidad; i++) {
                    file.readUTF(); // username
                    file.readUTF(); // tipo
                    file.readUTF(); // juego
                    file.readUTF(); // nombre
                    file.readUTF(); // fecha
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar usuarios.");
        }
    }

    public void addUser(String username) {
        try {
            if (users.search(username) != -1) {
                JOptionPane.showMessageDialog(null, "El usuario ya existe.");
                return;
            }

            file.seek(file.length());
            long pos = file.getFilePointer();
            file.writeUTF(username);
            file.writeBoolean(true); // activo
            file.writeInt(0); // puntos
            file.writeInt(0); // cantidad trofeos
            file.writeInt(0); // cantidad trofeos escritos
            users.add(username, pos);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al agregar usuario.");
        }
    }

    public void deactivateUser(String username) {
        try {
            long pos = users.search(username);
            if (pos == -1) {
                JOptionPane.showMessageDialog(null, "Usuario no encontrado.");
                return;
            }

            file.seek(pos);
            file.readUTF(); // saltar username
            file.writeBoolean(false); // cambiar activo
            users.remove(username);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al desactivar usuario.");
        }
    }

    public void addTrophieTo(String username, String trophyGame, String trophyName, Trophy type) {
        try {
            long pos = users.search(username);
            if (pos == -1) {
                JOptionPane.showMessageDialog(null, "Usuario no encontrado.");
                return;
            }

            file.seek(pos);
            file.readUTF(); // username
            file.readBoolean(); // activo
            int puntos = file.readInt();
            int trofeos = file.readInt();
            int cantidad = file.readInt();

            puntos += type.getPuntos();
            trofeos++;
            cantidad++;

            long datosInicio = file.getFilePointer();
            file.seek(pos + username.length() * 2 + 2); // UTF + boolean
            file.writeBoolean(true);
            file.writeInt(puntos);
            file.writeInt(trofeos);
            file.writeInt(cantidad);
            file.seek(datosInicio);

            file.seek(file.length());
            file.writeUTF(username);
            file.writeUTF(type.name());
            file.writeUTF(trophyGame);
            file.writeUTF(trophyName);
            file.writeUTF(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al agregar trofeo.");
        }
    }

    public void playerInfo(String username) {
        try {
            long pos = users.search(username);
            if (pos == -1) {
                JOptionPane.showMessageDialog(null, "Usuario no encontrado.");
                return;
            }

            file.seek(pos);
            String name = file.readUTF();
            boolean activo = file.readBoolean();
            int puntos = file.readInt();
            int trofeos = file.readInt();
            int cantidad = file.readInt();

            StringBuilder sb = new StringBuilder();
            sb.append("Username: ").append(name).append("\n")
              .append("Puntos: ").append(puntos).append("\n")
              .append("Trofeos: ").append(trofeos).append("\n\n");

            for (int i = 0; i < cantidad; i++) {
                String u = file.readUTF();
                String tipo = file.readUTF();
                String juego = file.readUTF();
                String nombre = file.readUTF();
                String fecha = file.readUTF();

                sb.append(fecha).append(" – ").append(tipo)
                  .append(" – ").append(juego)
                  .append(" – ").append(nombre).append("\n");
            }

            JOptionPane.showMessageDialog(null, sb.toString());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al mostrar la info.");
        }
    }
}

