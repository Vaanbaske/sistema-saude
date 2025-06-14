package br.com.saude.ui;

import br.com.saude.dao.PacienteDAO;
import br.com.saude.model.Paciente;
import br.com.saude.util.Conexao;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.List;

public class TelaInserirPressaoAdmin extends JFrame {
    private final TelaInicialAdmin telaAnterior;
    private JComboBox<Paciente> comboPacientes;
    private JTextField tfSistolica, tfDiastolica;
    private JDateChooser campoData;

    public TelaInserirPressaoAdmin(TelaInicialAdmin telaAnterior) {
        this.telaAnterior = telaAnterior;

        setTitle("Inserir Pressão - Administrador");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(5, 2, 10, 10));

        comboPacientes = new JComboBox<>();
        carregarPacientes();

        tfSistolica = new JTextField();
        tfDiastolica = new JTextField();
        campoData = new JDateChooser();

        // Validação para aceitar apenas números até 3 dígitos
        tfSistolica.setDocument(new PlainDocument() {
            @Override
            public void insertString(int offset, String str, javax.swing.text.AttributeSet attr) throws javax.swing.text.BadLocationException {
                if (str != null && str.matches("\\d+") && (getLength() + str.length() <= 3)) {
                    super.insertString(offset, str, attr);
                }
            }
        });

        tfDiastolica.setDocument(new PlainDocument() {
            @Override
            public void insertString(int offset, String str, javax.swing.text.AttributeSet attr) throws javax.swing.text.BadLocationException {
                if (str != null && str.matches("\\d+") && (getLength() + str.length() <= 3)) {
                    super.insertString(offset, str, attr);
                }
            }
        });

        JButton btnSalvar = new JButton("Salvar");
        JButton btnVoltar = new JButton("Voltar");

        btnSalvar.addActionListener(e -> salvarRegistro());
        btnVoltar.addActionListener(e -> {
            dispose();
            new TelaInicialAdmin(telaAnterior.getAdmin());
        });

        add(new JLabel("Paciente:")); add(comboPacientes);
        add(new JLabel("Sistólica:")); add(tfSistolica);
        add(new JLabel("Diastólica:")); add(tfDiastolica);
        add(new JLabel("Data:")); add(campoData);
        add(btnSalvar); add(btnVoltar);

        setVisible(true);
    }

    private void carregarPacientes() {
        List<Paciente> pacientes = new PacienteDAO().listarTodos();
        for (Paciente p : pacientes) {
            comboPacientes.addItem(p);
        }
    }

    private void salvarRegistro() {
        Paciente paciente = (Paciente) comboPacientes.getSelectedItem();
        String sistolica = tfSistolica.getText().trim();
        String diastolica = tfDiastolica.getText().trim();
        Date data = campoData.getDate();

        if (paciente == null || sistolica.isEmpty() || diastolica.isEmpty() || data == null) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos!", "Erro", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = Conexao.getConexao();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO pressao (id_paciente, sistolica, diastolica, data) VALUES (?, ?, ?, ?)")) {
            stmt.setInt(1, paciente.getId());
            stmt.setInt(2, Integer.parseInt(sistolica));
            stmt.setInt(3, Integer.parseInt(diastolica));
            stmt.setDate(4, new java.sql.Date(data.getTime()));
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Registro salvo com sucesso!");
            tfSistolica.setText("");
            tfDiastolica.setText("");
            campoData.setDate(null);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao salvar: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
