package br.com.instapromo.instapromo.model;

import java.util.List;

public class Back4AppResponse {

    List<Product> results;

    public List<Product> getResults() {
        return results;
    }

    public void setResults(List<Product> results) {
        this.results = results;
    }
}