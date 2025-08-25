package br.com.alura.screenmatch.repository;

import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, Long> {
    Optional<Serie> findFirstByTituloContainingIgnoreCase(String nome);

    List<Serie> findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(String nomeAtor, Double avaliacao);

    List<Serie> findTop5ByOrderByAvaliacaoDesc();

    List<Serie> findByGenero(Categoria categoria);
    //Consulta série pela qtd temporadas e avaliação (sem JPQL)
    List<Serie> findByTotalTemporadasLessThanEqualAndAvaliacaoLessThanEqual(Integer qtdTemporadas, Double avaliacao);
    //Consulta série pela qtd temporadas e avaliação (com JPQL)
    @Query("select s from Serie s WHERE s.totalTemporadas <= :qtdTemporadas AND s.avaliacao >= :avaliacao")
    List<Serie> seriesPorTemporadaEAvaliacao(Integer qtdTemporadas, Double avaliacao);
    @Query("select e from Serie s JOIN s.episodios e WHERE e.titulo ILIKE %:trecho")
    List<Episodio> episodioPorTrecho(String trecho);
    @Query("select e from Serie s JOIN s.episodios e WHERE s = :serie ORDER BY e.avaliacao DESC LIMIT 5")
    List<Episodio> topEpisodiosPorSerie(Serie serie);
    @Query("select e from Serie s JOIN s.episodios e WHERE s = :serie AND YEAR(e.dataLancamento) >= :anoLancamento")
    List<Episodio> episodiosPorSerieEAno(Serie serie, int anoLancamento);
    @Query("select s from Serie s JOIN s.episodios e ORDER BY e.dataLancamento DESC LIMIT 5")
    List<Serie> ultimosLancamentos();
}
