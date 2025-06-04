package br.com.controleestoque.view;

import br.com.controleestoque.dao.CategoriaDAO;
import br.com.controleestoque.dao.ProdutoDAO;
import br.com.controleestoque.model.Categoria;
import br.com.controleestoque.model.Produto;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class FormProduto extends JDialog {

    private final Principal principal;
    private Produto produto;

    private JTextField txtNome;
    private JTextField txtPreco;
    private JTextField txtUnidade;
    private JTextField txtQtdEstoque;
    private JTextField txtQtdMinima;
    private JTextField txtQtdMaxima;
    private JComboBox<Categoria> comboBoxCategoria;

    public FormProduto(Principal principal) {
        this(principal, null);
    }

    public FormProduto(Principal principal, Produto produto) {
        super(principal, true);
        this.principal = principal;
        this.produto = produto;
        initComponents();
        carregarCategorias();
        if (produto != null) {
            preencherFormulario();
        }
    }

    private void initComponents() {
        setTitle(produto == null ? "Novo Produto" : "Editar Produto");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel painel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Linha 1 - Nome
        gbc.gridx = 0;
        gbc.gridy = 0;
        painel.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1;
        txtNome = new JTextField(20);
        painel.add(txtNome, gbc);

        // Linha 2 - Preço Unitário
        gbc.gridx = 0;
        gbc.gridy = 1;
        painel.add(new JLabel("Preço Unitário:"), gbc);
        gbc.gridx = 1;
        txtPreco = new JTextField();
        painel.add(txtPreco, gbc);

        // Linha 3 - Unidade
        gbc.gridx = 0;
        gbc.gridy = 2;
        painel.add(new JLabel("Unidade:"), gbc);
        gbc.gridx = 1;
        txtUnidade = new JTextField();
        painel.add(txtUnidade, gbc);

        // Linha 4 - Quantidade em Estoque
        gbc.gridx = 0;
        gbc.gridy = 3;
        painel.add(new JLabel("Quantidade Estoque:"), gbc);
        gbc.gridx = 1;
        txtQtdEstoque = new JTextField();
        painel.add(txtQtdEstoque, gbc);

        // Linha 5 - Quantidade Mínima
        gbc.gridx = 0;
        gbc.gridy = 4;
        painel.add(new JLabel("Quantidade Mínima:"), gbc);
        gbc.gridx = 1;
        txtQtdMinima = new JTextField();
        painel.add(txtQtdMinima, gbc);

        // Linha 6 - Quantidade Máxima
        gbc.gridx = 0;
        gbc.gridy = 5;
        painel.add(new JLabel("Quantidade Máxima:"), gbc);
        gbc.gridx = 1;
        txtQtdMaxima = new JTextField();
        painel.add(txtQtdMaxima, gbc);

        // Linha 7 - Categoria
        gbc.gridx = 0;
        gbc.gridy = 6;
        painel.add(new JLabel("Categoria:"), gbc);
        gbc.gridx = 1;
        comboBoxCategoria = new JComboBox<>();
        painel.add(comboBoxCategoria, gbc);

        // Linha 8 - Botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.addActionListener(e -> salvar());
        painelBotoes.add(btnSalvar);

        JButton btnAjustarEstoque = new JButton("Ajustar Estoque");
        btnAjustarEstoque.addActionListener(e -> ajustarEstoque());
        painelBotoes.add(btnAjustarEstoque);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> dispose());
        painelBotoes.add(btnCancelar);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        painel.add(painelBotoes, gbc);

        add(painel);
    }

    private void carregarCategorias() {
        try {
            List<Categoria> categorias = new CategoriaDAO().listar();
            for (Categoria c : categorias) {
                comboBoxCategoria.addItem(c);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar categorias:\n" + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void preencherFormulario() {
        txtNome.setText(produto.getNome());
        txtPreco.setText(String.valueOf(produto.getPrecoUnitario()));
        txtUnidade.setText(produto.getUnidade());
        txtQtdEstoque.setText(String.valueOf(produto.getQuantidadeEstoque()));
        txtQtdMinima.setText(String.valueOf(produto.getQuantidadeMinima()));
        txtQtdMaxima.setText(String.valueOf(produto.getQuantidadeMaxima()));
        comboBoxCategoria.setSelectedItem(produto.getCategoria());
    }

    private void ajustarEstoque() {
        String input = JOptionPane.showInputDialog(this,
                "Digite a nova quantidade em estoque:",
                txtQtdEstoque.getText());

        if (input != null) {
            try {
                int novaQuantidade = Integer.parseInt(input);
                if (novaQuantidade < 0) {
                    JOptionPane.showMessageDialog(this, "Quantidade não pode ser negativa.");
                } else {
                    txtQtdEstoque.setText(String.valueOf(novaQuantidade));
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Valor inválido.");
            }
        }
    }

    private void salvar() {
        try {
            String nome = txtNome.getText().trim();
            String unidade = txtUnidade.getText().trim();
            Categoria categoria = (Categoria) comboBoxCategoria.getSelectedItem();

            if (nome.isEmpty() || unidade.isEmpty() || categoria == null) {
                JOptionPane.showMessageDialog(this,
                        "Preencha todos os campos obrigatórios!",
                        "Atenção", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double preco = Double.parseDouble(txtPreco.getText().trim());
            int qtdEstoque = Integer.parseInt(txtQtdEstoque.getText().trim());
            int qtdMinima = Integer.parseInt(txtQtdMinima.getText().trim());
            int qtdMaxima = Integer.parseInt(txtQtdMaxima.getText().trim());

            Produto p = (produto == null) ? new Produto() : produto;
            p.setNome(nome);
            p.setPrecoUnitario(preco);
            p.setUnidade(unidade);
            p.setQuantidadeEstoque(qtdEstoque);
            p.setQuantidadeMinima(qtdMinima);
            p.setQuantidadeMaxima(qtdMaxima);
            p.setCategoria(categoria);

            ProdutoDAO dao = new ProdutoDAO();
            if (produto == null) {
                dao.inserir(p);
                JOptionPane.showMessageDialog(this, "Produto cadastrado com sucesso!");
            } else {
                dao.atualizar(p);
                JOptionPane.showMessageDialog(this, "Produto atualizado com sucesso!");
            }

            principal.atualizarTabelaProdutos(); // Atualiza a tabela na tela principal
            dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Preencha corretamente os campos numéricos (preço, quantidades).",
                    "Erro de Validação", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao salvar o produto:\n" + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
