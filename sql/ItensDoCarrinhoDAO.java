package sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Conection.Conexao;
import model.CarrinhoDeCompras;
import model.ItensDoCarrinho;
import model.Produto;

public class ItensDoCarrinhoDAO {


    public void adicionarProdutos(ItensDoCarrinho itens_do_carrinho) { 

        int quantidadeNoEstoque = 0;
        String quantidadeEstoque = "SELECT quantidade_estoque FROM PRODUTO WHERE ID_PRODUTO = ?";     
        String updateQuantidadeEstoque = "UPDATE Produto SET quantidade_estoque = ? WHERE ID_PRODUTO = ?";
        String adicionaProdutos = "INSERT INTO ITENS_DO_CARRINHO (id_carrinho, id_produto, quantidade) VALUES(?, ?, ?)";

        try(PreparedStatement stmtQuantEstoque = Conexao.getConexao().prepareStatement(quantidadeEstoque);) {

            stmtQuantEstoque.setInt(1, itens_do_carrinho.getId_produto());

            ResultSet rsQuantEstoque = stmtQuantEstoque.executeQuery();

            if(rsQuantEstoque.next()){
                
                quantidadeNoEstoque = rsQuantEstoque.getInt("quantidade_estoque");
            
            }
        
            //se a consulta sql retornar pelo menos 1 registro e a quantidade desse produto for maior que o cliente vai comprar

            if(quantidadeNoEstoque >= itens_do_carrinho.getQuantidade()) {

                try(PreparedStatement stmtAdicionaProdutos = Conexao.getConexao().prepareStatement(adicionaProdutos)) {

                    stmtAdicionaProdutos.setInt(1, itens_do_carrinho.getId_carrinho());
                    stmtAdicionaProdutos.setInt(2, itens_do_carrinho.getId_produto());
                    stmtAdicionaProdutos.setInt(3, itens_do_carrinho.getQuantidade());
                    
                    int produtoAdicionado = stmtAdicionaProdutos.executeUpdate();

                    if(produtoAdicionado == 1) {

                        try(PreparedStatement stmtUpdateQuantidadeEstoque = Conexao.getConexao().prepareStatement(updateQuantidadeEstoque)) {

                            stmtUpdateQuantidadeEstoque.setInt(1, quantidadeNoEstoque - itens_do_carrinho.getQuantidade());
                            stmtUpdateQuantidadeEstoque.setInt(2, itens_do_carrinho.getId_produto());

                            stmtUpdateQuantidadeEstoque.executeUpdate();

                            System.out.println("Produto adicionado ao carrinho!");

                        }

                    }

                }
                



            }
            
            else{

                throw new IllegalArgumentException("Quantidade do produto não disponível!");

            }
            
            rsQuantEstoque.close();
            
        }
        catch(Exception e) {

            System.out.println(e);

        }


    }


    public HashMap<Produto, Integer> produtosItensDoCarrinho(int id_carrinho) {

        HashMap<Produto, Integer> produtos = new HashMap();

        String itensDoCarrinho = "SELECT id_produto, COUNT(*) AS quantidade\n" + //
                        "FROM ITENS_DO_CARRINHO\n" + //
                        "WHERE id_carrinho = ?\n" + //
                        "GROUP BY id_produto;";
        String produtosSQL = "sELECT * FROM PRODUTO WHERE ID_PRODUTO = ?";

        try 
        {
            PreparedStatement stmtItensDoCarrinho = Conexao.getConexao().prepareStatement(itensDoCarrinho);

            stmtItensDoCarrinho.setInt(1, id_carrinho);

            ResultSet rsItensDoCarrinho = stmtItensDoCarrinho.executeQuery();

            while(rsItensDoCarrinho.next()) 
            {

                try
                {
                    int id_produto = rsItensDoCarrinho.getInt("id_produto");
                    int quantidade = rsItensDoCarrinho.getInt("quantidade");

                PreparedStatement stmtProdutosSQL = Conexao.getConexao().prepareStatement(produtosSQL);

                stmtProdutosSQL.setInt(1, id_produto);

                ResultSet rsProdutosSQL = stmtProdutosSQL.executeQuery();

                if(rsProdutosSQL.next()) {

                    Produto produto = new Produto();

                    produto.setNome(rsProdutosSQL.getString("nome"));
                    produto.setDescricao(rsProdutosSQL.getString("descricao"));
                    produto.setPreco(rsProdutosSQL.getDouble("preco"));
                    produto.setId_produto(id_produto);

                    produtos.put(produto, quantidade);

                    }

                }
                catch(SQLException e) 
                {

                    System.out.println(e);
                
                }

            }

            stmtItensDoCarrinho.close();
            rsItensDoCarrinho.close();

        }

        catch(Exception e) 
        {

            System.out.println(e);
        
        }

        return produtos;

    }

}
