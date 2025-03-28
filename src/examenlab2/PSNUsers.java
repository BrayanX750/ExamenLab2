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
    // 1) Variable "psn" para gestionar el archivo
    private RandomAccessFile psn;
    // 2) Variable "users" (HashTable) para manejar todos los usuarios en memoria
    private HashTable users;

    public PSNUsers() {
        try {
            // Se abre (o crea) el archivo "psn.dat"
            psn = new RandomAccessFile("psn.dat", "rw");
            users = new HashTable();
            reloadHashTable();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al abrir el archivo: " + e.getMessage());
        }
    }

    /**
     * Carga la tabla de usuarios en memoria desde el archivo "psn.dat".
     */
    private void reloadHashTable() {
        try {
            if (psn.length() == 0) {
                // Si el archivo está vacío, no hay nada que cargar
                return;
            }
            psn.seek(0);
            while (psn.getFilePointer() < psn.length()) {
                long pos = psn.getFilePointer();
                String username = psn.readUTF();
                boolean activo = psn.readBoolean();
                int puntos = psn.readInt();
                int trofeos = psn.readInt();
                int cantidad = psn.readInt();

                // Solo se agrega a la tabla en memoria si está activo
                if (activo) {
                    users.add(username, pos);
                }

                // Saltar la información de trofeos
                for (int i = 0; i < cantidad; i++) {
                    psn.readUTF(); // username (trofeo)
                    psn.readUTF(); // tipo
                    psn.readUTF(); // juego
                    psn.readUTF(); // nombre del trofeo
                    psn.readUTF(); // fecha
                }
            }
        } catch (EOFException eof) {
            // Se llegó al final del archivo normalmente, sin problemas
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar usuarios: " + e.getMessage());
        }
    }

    /**
     * Agrega un usuario nuevo al archivo y a la tabla en memoria.
     */
    public void addUser(String username) {
        try {
            // Primero se busca en la HashTable (users)
            if (users.search(username) != -1) {
                JOptionPane.showMessageDialog(null, "El usuario ya existe.");
                return;
            }

            // Si no existe en memoria, se agrega al final del archivo
            psn.seek(psn.length());
            long pos = psn.getFilePointer();
            psn.writeUTF(username);
            psn.writeBoolean(true);  // activo
            psn.writeInt(0);         // puntos
            psn.writeInt(0);         // trofeos
            psn.writeInt(0);         // cantidad trofeos escritos

            // Forzamos que se escriban los cambios en disco
            psn.getChannel().force(true);

            // Se actualiza la tabla en memoria
            users.add(username, pos);

            // (Opcional) Agregar registro en archivo de texto usuarios.txt
            try (FileWriter fw = new FileWriter("usuarios.txt", true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {
                out.println("Username: " + username + " | Activo: true | Puntos: 0 | Trofeos: 0");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error al escribir en archivo de texto: " + e.getMessage());
            }

            JOptionPane.showMessageDialog(null, "Usuario '" + username + "' agregado correctamente.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al agregar usuario: " + e.getMessage());
        }
    }

    /**
     * Desactiva un usuario en el archivo y lo quita de la tabla en memoria.
     */
    public void deactivateUser(String username) {
        try {
            // Se busca en la tabla en memoria
            long pos = users.search(username);
            if (pos == -1) {
                JOptionPane.showMessageDialog(null, "Usuario no encontrado.");
                return;
            }

            // Si está en memoria, se actualiza el archivo para desactivarlo
            psn.seek(pos);
            psn.readUTF();       // saltar el username
            psn.writeBoolean(false);  // marcar activo como false
            users.remove(username);   // quitar de la tabla en memoria

            psn.getChannel().force(true);

            JOptionPane.showMessageDialog(null, "Usuario '" + username + "' desactivado.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al desactivar usuario: " + e.getMessage());
        }
    }

    /**
     * Agrega un trofeo a un usuario. Se buscan los datos en memoria (HashTable),
     * luego se actualiza el archivo y, si todo está bien, se mantiene la referencia
     * en memoria.
     */
    public void addTrophieTo(String username, String trophyGame, String trophyName, Trophy type) {
        try {
            // Se busca la posición del usuario en memoria
            long pos = users.search(username);
            if (pos == -1) {
                JOptionPane.showMessageDialog(null, "Usuario no encontrado.");
                return;
            }

            // Actualizar la información en el archivo
            psn.seek(pos);
            psn.readUTF();  // username
            long posAfterUsername = psn.getFilePointer();
            boolean activo = psn.readBoolean();
            int puntos = psn.readInt();
            int trofeos = psn.readInt();
            int cantidad = psn.readInt();

            puntos += type.getPuntos();
            trofeos++;
            cantidad++;

            // Escribir los datos actualizados
            psn.seek(posAfterUsername);
            psn.writeBoolean(activo);
            psn.writeInt(puntos);
            psn.writeInt(trofeos);
            psn.writeInt(cantidad);

            // Escribir la info del trofeo al final del archivo
            psn.seek(psn.length());
            psn.writeUTF(username);
            psn.writeUTF(type.name());
            psn.writeUTF(trophyGame);
            psn.writeUTF(trophyName);
            psn.writeUTF(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));

            psn.getChannel().force(true);

            JOptionPane.showMessageDialog(null, 
                "Trofeo '" + trophyName + "' agregado al usuario '" + username + "'.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al agregar trofeo: " + e.getMessage());
        }
    }

    /**
     * Muestra la información de un usuario (búsqueda en la tabla en memoria).
     */
    public void playerInfo(String username) {
        try {
            // Se busca la posición del usuario en memoria
            long pos = users.search(username);
            if (pos == -1) {
                JOptionPane.showMessageDialog(null, "Usuario no encontrado.");
                return;
            }

            // Se leen los datos del archivo en esa posición
            psn.seek(pos);
            String name = psn.readUTF();
            boolean activo = psn.readBoolean();
            int puntos = psn.readInt();
            int trofeos = psn.readInt();
            int cantidad = psn.readInt();

            StringBuilder sb = new StringBuilder();
            sb.append("=== INFORMACIÓN DEL USUARIO ===\n")
              .append("Username: ").append(name).append("\n")
              .append("Activo: ").append(activo ? "Sí" : "No").append("\n")
              .append("Puntos: ").append(puntos).append("\n")
              .append("Trofeos Totales: ").append(trofeos).append("\n\n")
              .append("=== TROFEOS DEL USUARIO ===\n");

            for (int i = 0; i < cantidad; i++) {
                String u = psn.readUTF();        // username del trofeo
                String tipo = psn.readUTF();     // tipo de trofeo
                String juego = psn.readUTF();    // nombre del juego
                String nombreTrofeo = psn.readUTF(); // nombre del trofeo
                String fecha = psn.readUTF();    // fecha

                sb.append("Username: ").append(u).append("\n")
                  .append("Tipo del trofeo: ").append(tipo).append("\n")
                  .append("Juego: ").append(juego).append("\n")
                  .append("Trofeo: ").append(nombreTrofeo).append("\n")
                  .append("Fecha: ").append(fecha).append("\n\n");
            }
            JOptionPane.showMessageDialog(null, sb.toString());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al mostrar la información: " + e.getMessage());
        }
    }

    /**
     * Cierra el archivo "psn.dat" para liberar recursos.
     */
    public void closeFile() {
        if (psn != null) {
            try {
                psn.getChannel().force(true);
                psn.close();
            } catch (IOException e) {
                System.err.println("Error al cerrar el archivo: " + e.getMessage());
            }
        }
    }
}
