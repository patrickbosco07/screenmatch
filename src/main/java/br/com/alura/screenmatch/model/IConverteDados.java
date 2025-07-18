package br.com.alura.screenmatch.model;

public interface IConverteDados {
    public <T> T obterDados(String json, Class<T> classe);
}
