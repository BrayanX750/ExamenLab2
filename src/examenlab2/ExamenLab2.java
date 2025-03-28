/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package examenlab2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ExamenLab2 {
    public static void main(String[] args) {
        // Usamos el Look and Feel del sistema para un aspecto nativo
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception e) {
            // Si ocurre algún error, se utiliza el look and feel por defecto
        }

        SwingUtilities.invokeLater(() -> {
            PSNUsers psn = new PSNUsers();

            JFrame frame = new JFrame("Panel de Control - Usuarios PSN");
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            frame.setSize(600, 500);
            frame.setLocationRelativeTo(null); // Centrar la ventana

            // Al cerrar, se cierra el archivo y luego se finaliza la aplicación
            frame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    psn.closeFile();
                    System.exit(0);
                }
            });

            // Panel principal con BoxLayout vertical
            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            // Fondo de un gris medio-oscuro para buen contraste
            mainPanel.setBackground(new Color(60, 60, 60));

            // Encabezado
            JPanel headerPanel = new JPanel();
            headerPanel.setBackground(new Color(80, 80, 80));
            JLabel headerLabel = new JLabel("Gestión de Usuarios PSN");
            headerLabel.setFont(new Font("Verdana", Font.BOLD, 28));
            headerLabel.setForeground(Color.WHITE);
            headerPanel.add(headerLabel);

            // Panel de botones con GridLayout
            JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 15, 15));
            buttonPanel.setBackground(new Color(60, 60, 60));
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            // Botones en español
            JButton btnAddUser = new JButton("Agregar Usuario");
            styleButton(btnAddUser, new Color(56, 155, 60)); // Verde algo más oscuro

            JButton btnDeactivateUser = new JButton("Desactivar Usuario");
            styleButton(btnDeactivateUser, new Color(224, 47, 34)); // Rojo

            JButton btnAddTrophy = new JButton("Agregar Trofeo");
            styleButton(btnAddTrophy, new Color(13, 130, 223)); // Azul

            JButton btnShowInfo = new JButton("Mostrar Información");
            styleButton(btnShowInfo, new Color(235, 183, 0)); // Ámbar

            buttonPanel.add(btnAddUser);
            buttonPanel.add(btnDeactivateUser);
            buttonPanel.add(btnAddTrophy);
            buttonPanel.add(btnShowInfo);

            // Pie de página con etiqueta de estado
            JPanel footerPanel = new JPanel();
            footerPanel.setBackground(new Color(80, 80, 80));
            JLabel statusLabel = new JLabel("Listo");
            statusLabel.setFont(new Font("Verdana", Font.ITALIC, 14));
            statusLabel.setForeground(Color.WHITE);
            footerPanel.add(statusLabel);

            // Agregar componentes al mainPanel
            mainPanel.add(headerPanel);
            mainPanel.add(Box.createVerticalStrut(20));
            mainPanel.add(buttonPanel);
            mainPanel.add(Box.createVerticalGlue());
            mainPanel.add(footerPanel);

            // Acciones para cada botón
            btnAddUser.addActionListener(e -> {
                String user = JOptionPane.showInputDialog(frame, "Ingrese el nombre de usuario:");
                if(user != null && !user.trim().isEmpty()){
                    psn.addUser(user.trim());
                    statusLabel.setText("Usuario '" + user.trim() + "' agregado.");
                } else {
                    statusLabel.setText("Operación cancelada o campo vacío.");
                }
            });

            btnDeactivateUser.addActionListener(e -> {
                String user = JOptionPane.showInputDialog(frame, "Ingrese el usuario a desactivar:");
                if(user != null && !user.trim().isEmpty()){
                    psn.deactivateUser(user.trim());
                    statusLabel.setText("Usuario '" + user.trim() + "' desactivado.");
                } else {
                    statusLabel.setText("Operación cancelada o campo vacío.");
                }
            });

            btnAddTrophy.addActionListener(e -> {
                String user = JOptionPane.showInputDialog(frame, "Ingrese el usuario:");
                if(user == null || user.trim().isEmpty()){
                    statusLabel.setText("Operación cancelada o campo vacío.");
                    return;
                }
                String game = JOptionPane.showInputDialog(frame, "Ingrese el nombre del juego:");
                if(game == null || game.trim().isEmpty()){
                    statusLabel.setText("Operación cancelada o campo vacío.");
                    return;
                }
                String trophyName = JOptionPane.showInputDialog(frame, "Ingrese el nombre del trofeo:");
                if(trophyName == null || trophyName.trim().isEmpty()){
                    statusLabel.setText("Operación cancelada o campo vacío.");
                    return;
                }
                String[] trophyTypes = {"BRONZE", "SILVER", "GOLD", "PLATINUM"};
                String type = (String) JOptionPane.showInputDialog(frame, "Seleccione el tipo de trofeo:",
                        "Tipo de Trofeo", JOptionPane.QUESTION_MESSAGE, null, trophyTypes, trophyTypes[0]);
                if(type == null || type.trim().isEmpty()){
                    statusLabel.setText("Operación cancelada o campo vacío.");
                    return;
                }
                psn.addTrophieTo(user.trim(), game.trim(), trophyName.trim(), Trophy.valueOf(type));
                statusLabel.setText("Trofeo '" + trophyName.trim() + "' agregado al usuario '" + user.trim() + "'.");
            });

            btnShowInfo.addActionListener(e -> {
                String user = JOptionPane.showInputDialog(frame, "Ingrese el usuario para ver información:");
                if(user != null && !user.trim().isEmpty()){
                    psn.playerInfo(user.trim());
                    statusLabel.setText("Mostrando información de '" + user.trim() + "'.");
                } else {
                    statusLabel.setText("Operación cancelada o campo vacío.");
                }
            });

            frame.setContentPane(mainPanel);
            frame.setVisible(true);
        });
    }

    // Método para estilizar los botones
    private static void styleButton(JButton button, Color bgColor) {
        button.setOpaque(true);                  // Para que respete el color de fondo en LookAndFeel
        button.setBackground(bgColor);           // Color de fondo
        button.setForeground(Color.WHITE);       // Color de texto
        button.setFont(new Font("Verdana", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
    }
}
