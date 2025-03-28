/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package examenlab2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception e) {
        }

        SwingUtilities.invokeLater(() -> {
            PSNUsers psn = new PSNUsers();

            JFrame frame = new JFrame("Panel de Control - Usuarios PSN");
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            frame.setSize(600, 500);
            frame.setLocationRelativeTo(null);

            frame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    psn.closeFile();
                    System.exit(0);
                }
            });
  
            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.setBackground(new Color(245, 245, 245));

            JPanel headerPanel = new JPanel();
            headerPanel.setBackground(new Color(220, 220, 220));
            JLabel headerLabel = new JLabel("Gestión de Usuarios PSN");
            headerLabel.setFont(new Font("Verdana", Font.BOLD, 26));
            headerLabel.setForeground(Color.BLACK);
            headerPanel.add(headerLabel);

            JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 15, 15));
            buttonPanel.setBackground(new Color(245, 245, 245));
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            JButton btnAddUser = new JButton("Agregar Usuario");
            styleButton(btnAddUser, new Color(102, 204, 0));

            JButton btnDeactivateUser = new JButton("Desactivar Usuario");
            styleButton(btnDeactivateUser, new Color(204, 0, 0));

            JButton btnAddTrophy = new JButton("Agregar Trofeo");
            styleButton(btnAddTrophy, new Color(0, 153, 153));

            JButton btnShowInfo = new JButton("Mostrar Información");
            styleButton(btnShowInfo, new Color(255, 140, 0));

            buttonPanel.add(btnAddUser);
            buttonPanel.add(btnDeactivateUser);
            buttonPanel.add(btnAddTrophy);
            buttonPanel.add(btnShowInfo);

            JPanel footerPanel = new JPanel();
            footerPanel.setBackground(new Color(220, 220, 220));
            JLabel statusLabel = new JLabel("Listo");
            statusLabel.setFont(new Font("Verdana", Font.ITALIC, 14));
            statusLabel.setForeground(Color.BLACK);
            footerPanel.add(statusLabel);

            mainPanel.add(headerPanel);
            mainPanel.add(Box.createVerticalStrut(20));
            mainPanel.add(buttonPanel);
            mainPanel.add(Box.createVerticalGlue());
            mainPanel.add(footerPanel);

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
                String[] trophyTypes = {"BRONCE", "PLATA", "ORO", "PLATINO"};
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

    private static void styleButton(JButton button, Color bgColor) {
        button.setOpaque(true);
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Verdana", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
    }
}
