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

                // Saltar los registros de trofeos de este usuario
                for (int i = 0; i < cantidad; i++) {
                    file.readUTF(); // username
                    file.readUTF(); // tipo
                    file.readUTF(); // juego
                    file.readUTF(); // nombre trofeo
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

            // Escribir en el archivo binario psn.dat
            file.seek(file.length());
            long pos = file.getFilePointer();
            file.writeUTF(username);
            file.writeBoolean(true); // activo
            file.writeInt(0);       // puntos
            file.writeInt(0);       // trofeos
            file.writeInt(0);       // cantidad trofeos escritos
            users.add(username, pos);

            // Escribir en el archivo de texto "usuarios.txt"
            // Se abre en modo append para que cada nuevo usuario se agregue al final.
            try (FileWriter fw = new FileWriter("usuarios.txt", true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {
                // Formateamos la información del usuario a guardar
                out.println("Username: " + username + " | Activo: true | Puntos: 0 | Trofeos: 0");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error al escribir en el archivo de texto.");
            }

            JOptionPane.showMessageDialog(null, "Usuario '" + username + "' agregado correctamente.");
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
            file.readUTF();           // saltar username
            file.writeBoolean(false);  // cambiar activo a false
            users.remove(username);

            JOptionPane.showMessageDialog(null, "Usuario '" + username + "' desactivado correctamente.");
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
            String userInFile = file.readUTF();  // leer username
            long posAfterUsername = file.getFilePointer(); // posición donde está el boolean
            boolean activo = file.readBoolean();
            int puntos = file.readInt();
            int trofeos = file.readInt();
            int cantidad = file.readInt();

            // Actualizar valores
            puntos += type.getPuntos();
            trofeos++;
            cantidad++;

            // Regresar a la posición después del username para actualizar el registro
            file.seek(posAfterUsername);
            file.writeBoolean(activo); 
            file.writeInt(puntos);
            file.writeInt(trofeos);
            file.writeInt(cantidad);

            // Mover el puntero al final del archivo para agregar la información del trofeo
            file.seek(file.length());
            file.writeUTF(username);
            file.writeUTF(type.name());
            file.writeUTF(trophyGame);
            file.writeUTF(trophyName);
            file.writeUTF(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));

            JOptionPane.showMessageDialog(null, 
                "Trofeo '" + trophyName + "' agregado correctamente al usuario '" + username + "'.");
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
            String name = file.readUTF();   // username
            boolean activo = file.readBoolean();
            int puntos = file.readInt();
            int trofeos = file.readInt();
            int cantidad = file.readInt();

            // Construir mensaje con info general
            StringBuilder sb = new StringBuilder();
            sb.append("=== INFORMACIÓN DEL USUARIO ===\n")
              .append("Username: ").append(name).append("\n")
              .append("Activo: ").append(activo ? "Sí" : "No").append("\n")
              .append("Puntos: ").append(puntos).append("\n")
              .append("Trofeos Totales: ").append(trofeos).append("\n\n");

            // Agregar la información de cada trofeo en el orden solicitado
            sb.append("=== TROFEOS DEL USUARIO ===\n");
            for (int i = 0; i < cantidad; i++) {
                String u = file.readUTF();
                String tipo = file.readUTF();
                String juego = file.readUTF();
                String nombreTrofeo = file.readUTF();
                String fecha = file.readUTF();

                sb.append("username: ").append(u).append("\n")
                  .append("tipo del trofeo: ").append(tipo).append("\n")
                  .append("nombre del juego: ").append(juego).append("\n")
                  .append("nombre del trofeo: ").append(nombreTrofeo).append("\n")
                  .append("fecha en que se ganó: ").append(fecha).append("\n\n");
            }

            JOptionPane.showMessageDialog(null, sb.toString());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al mostrar la info.");
        }
    }

    /**
     * Cierra el archivo para asegurarnos de que los datos queden
     * guardados correctamente.
     */
    public void closeFile() {
        if (file != null) {
            try {
                file.close();
            } catch (IOException e) {
                System.err.println("Error al cerrar el archivo: " + e.getMessage());
            }
        }
    }
}
