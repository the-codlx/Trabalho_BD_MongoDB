package utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Utils {
    
    public static Scanner entrada = new Scanner(System.in);

    
    public static String Opcoes() {
        return "------------------------------------" + "\n1 - FAZER LOGIN\n2 - REALIZAR CADASTRO\n3 - CRIAR AS COLEÇÕES NO BANCO DE DADOS MONGODB\n4 - SAIR" + "\n ------------------------------------";
    }

    public static int Opcao() {
        int opcao = entrada.nextInt();
        entrada.nextLine();
        return opcao;
    }


    public static void mostraOpcoes() {
        System.out.println(Utils.Opcoes());
    }


    
}
