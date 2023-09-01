package br.com.codeflix.catalog.admin.application;

import br.com.codeflix.catalog.admin.domain.category.Category;

public abstract class UseCase<IN, OUT> {

    public abstract OUT execute(IN in);

}