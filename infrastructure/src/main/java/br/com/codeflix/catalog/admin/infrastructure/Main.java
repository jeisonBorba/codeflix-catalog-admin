package br.com.codeflix.catalog.admin.infrastructure;

import br.com.codeflix.catalog.admin.application.UseCase;

public class Main {
    public static void main(String[] args) {
        System.out.println(new UseCase().execute());
    }

}