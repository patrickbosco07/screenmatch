package br.com.alura.screenmatch.service;

import br.com.alura.screenmatch.dto.EpisodioDTO;
import br.com.alura.screenmatch.dto.SerieDTO;
import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SerieService {
    @Autowired
    private SerieRepository serieRepository;

    public List<SerieDTO> obterTodasAsSeries(){
        return converteDadosSerie(serieRepository.findAll());
    }

    public List<SerieDTO> obterTop5Series() {
        return converteDadosSerie(serieRepository.findTop5ByOrderByAvaliacaoDesc());
    }

    public List<SerieDTO> obterLancamentos() {
        return converteDadosSerie(serieRepository.ultimosLancamentos());
    }

    public SerieDTO obterPorId(Long id) {
        Optional<Serie> serie = serieRepository.findById(id);

        if (serie.isPresent()){
            Serie s = serie.get();
            return new SerieDTO(s.getId(), s.getTitulo(),s.getTotalTemporadas(),s.getAvaliacao(),s.getGenero(),
                    s.getAtores(),s.getPoster(),s.getSinopse());
        }
        return null;
    }

    public List<EpisodioDTO> obterTodasTemporadas(Long id) {
        Optional<Serie> serie = serieRepository.findById(id);

        if (serie.isPresent()){
            Serie s = serie.get();
            return s.getEpisodios().stream()
                    .map(e -> new EpisodioDTO(e.getTemporada(), e.getNumeroEpisodio(), e.getTitulo()))
                    .collect(Collectors.toList());
        }
        return null;
    }

    public List<EpisodioDTO> obterTemporadaEspecifica(Long id, Integer numeroTemporada) {
        List<Episodio> episodios = serieRepository.episodiosPorTemporada(id,numeroTemporada);

        if (!episodios.isEmpty()){
            return episodios.stream()
                    .map(e -> new EpisodioDTO(e.getTemporada(), e.getNumeroEpisodio(), e.getTitulo()))
                    .collect(Collectors.toList());
        }
        return null;
    }

    public List<SerieDTO> obterSeriesPelaCategoria(String nome) {
        var enumRetornado = Categoria.fromPortugues(nome);
        return converteDadosSerie(serieRepository.findByGenero(enumRetornado));
    }

    public List<EpisodioDTO> obterTop5EpisodiosSerie(Long id) {
        Optional<Serie> serieRetornada = serieRepository.findById(id);
        if (serieRetornada.isPresent()){
            return converteDadosEpisodio(serieRepository.topEpisodiosPorSerie(serieRetornada.get()));
        }
        return null;

    }

    //MÉTODOS AUXILIARES
    private List<SerieDTO> converteDadosSerie(List<Serie> series){
        return series.stream()
                .map(s -> new SerieDTO(s.getId(), s.getTitulo(),s.getTotalTemporadas(),s.getAvaliacao(),s.getGenero(),
                        s.getAtores(),s.getPoster(),s.getSinopse()))
                .collect(Collectors.toList());
    }

    private List<EpisodioDTO> converteDadosEpisodio(List<Episodio> episodios){
        return episodios.stream()
                .map(e -> new EpisodioDTO(e.getTemporada(), e.getNumeroEpisodio(), e.getTitulo()))
                .collect(Collectors.toList());
    }
}
