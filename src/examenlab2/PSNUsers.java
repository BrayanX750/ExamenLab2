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
    private RandomAccessFile psn;
    private HashTable users;

    public PSNUsers() {
        try {
            psn = new RandomAccessFile("psn.dat", "rw");
            users = new HashTable();
            reloadHashTable();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al abrir el archivo: " + e.getMessage());
        }
    }

    private void reloadHashTable() {
        try {
            if (psn.length() == 0) return;
            psn.seek(0);
            while (psn.getFilePointer() < psn.length()) {
                long pos = psn.getFilePointer();
                String username = psn.readUTF();
                boolean activo = psn.readBoolean();
                int puntos = psn.readInt();
                int trofeos = psn.readInt();
                int cantidad = psn.readInt();
                if (activo) {
                    users.add(username, pos);
                }
                for (int i = 0; i < cantidad; i++) {
                    psn.readUTF();
                    psn.readUTF();
                    psn.readUTF();
                    psn.readUTF();
                    psn.readUTF();
                }
            }
        } catch (EOFException eof) {
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar usuarios: " + e.getMessage());
        }
    }

    public void addUser(String username) {
        try {
            if (users.search(username) != -1) {
                JOptionPane.showMessageDialog(null, "El usuario ya existe.");
                return;
            }
            psn.seek(psn.length());
            long pos = psn.getFilePointer();
            psn.writeUTF(username);
            psn.writeBoolean(true);
            psn.writeInt(0);
            psn.writeInt(0);
            psn.writeInt(0);
            psn.getChannel().force(true);
            users.add(username, pos);
            updateTextFile();
            JOptionPane.showMessageDialog(null, "Usuario '" + username + "' agregado correctamente.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al agregar usuario: " + e.getMessage());
        }
    }

    public void deactivateUser(String username) {
        try {
            long pos = users.search(username);
            if (pos == -1) {
                JOptionPane.showMessageDialog(null, "Usuario no encontrado.");
                return;
            }
            psn.seek(pos);
            psn.readUTF();
            psn.writeBoolean(false);
            users.remove(username);
            psn.getChannel().force(true);
            updateTextFile();
            JOptionPane.showMessageDialog(null, "Usuario '" + username + "' desactivado.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al desactivar usuario: " + e.getMessage());
        }
    }

    public void addTrophieTo(String username, String trophyGame, String trophyName, Trophy type) {
        try {
            long pos = users.search(username);
            if (pos == -1) {
                JOptionPane.showMessageDialog(null, "Usuario no encontrado.");
                return;
            }
            psn.seek(pos);
            psn.readUTF();
            long posAfterUsername = psn.getFilePointer();
            boolean activo = psn.readBoolean();
            int puntos = psn.readInt();
            int trofeos = psn.readInt();
            int cantidad = psn.readInt();
            puntos += type.getPuntos();
            trofeos++;
            cantidad++;
            psn.seek(posAfterUsername);
            psn.writeBoolean(activo);
            psn.writeInt(puntos);
            psn.writeInt(trofeos);
            psn.writeInt(cantidad);
            psn.seek(psn.length());
            psn.writeUTF(username);
            psn.writeUTF(type.name());
            psn.writeUTF(trophyGame);
            psn.writeUTF(trophyName);
            psn.writeUTF(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
            psn.getChannel().force(true);
            updateTextFile();
            JOptionPane.showMessageDialog(null, "Trofeo '" + trophyName + "' agregado al usuario '" + username + "'.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al agregar trofeo: " + e.getMessage());
        }
    }

    public void playerInfo(String username) {
        try {
            long pos = users.search(username);
            if (pos == -1) {
                JOptionPane.showMessageDialog(null, "Usuario no encontrado.");
                return;
            }
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
                String u = psn.readUTF();
                String tipo = psn.readUTF();
                String juego = psn.readUTF();
                String nombreTrofeo = psn.readUTF();
                String fecha = psn.readUTF();
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

    public void updateTextFile() {
    try (RandomAccessFile raf = new RandomAccessFile("psn.dat", "r");
         FileWriter fw = new FileWriter("usuarios.txt");
         BufferedWriter bw = new BufferedWriter(fw);
         PrintWriter out = new PrintWriter(bw)) {

        while (raf.getFilePointer() < raf.length()) {
            String username = raf.readUTF();
            boolean activo = raf.readBoolean();
            int puntos = raf.readInt();
            int trofeos = raf.readInt();
            int cantidad = raf.readInt();

            out.println("Username: " + username
                    + " | Activo: " + activo
                    + " | Puntos: " + puntos
                    + " | Trofeos: " + trofeos);

            for (int i = 0; i < cantidad; i++) {
                String u = raf.readUTF();
                String tipo = raf.readUTF();
                String juego = raf.readUTF();
                String nombreTrofeo = raf.readUTF();
                String fecha = raf.readUTF();

                out.println("    Trofeo -> Username: " + u
                        + " | Tipo: " + tipo
                        + " | Juego: " + juego
                        + " | Trofeo: " + nombreTrofeo
                        + " | Fecha: " + fecha);
            }
            out.println();
        }

    } catch (EOFException eof) {
       
    } catch (IOException e) {
       
        JOptionPane.showMessageDialog(null, 
            "Error al actualizar archivo de texto: " + e.getMessage());
    }
}

}