package br.com.alura.screenmatch;

import br.com.alura.screenmatch.model.*;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoAPI;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private  ConverterDado converterDado = new ConverterDado();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=ddb7a783";
    private List<DadosSerie> dadosSeries =new ArrayList<>();
    private SerieRepository serieRepository;
    private List<Serie> series = new ArrayList<>();
    private Optional<Serie> serieBuscada;

    public Principal(SerieRepository serieRepository) {
        this.serieRepository = serieRepository;
    }

    public void exibirMenu() {

        var opcao = -1;
        while (opcao != 0) {
            var menu = """
                    1 - Buscar séries
                    2 - Buscar episódios
                    3 - Listar séries buscadas
                    4 - Buscar série pelo nome 
                    5 - Buscar séries por ator
                    6 - Buscar Top 5 séries              
                    7 - Buscar séries por categoria
                    8 - Buscar séries por número de temporadas e avaliação
                    9 - Buscar episódios pelo trecho
                    10 - Buscar top 5 episódios por série
                    11 - Buscar episódios a partir de uma data
                    0 - Sair                                 
                    """;
            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    listarSeriesBuscadas();
                    break;
                case 4:
                    listarSeriePeloNome();
                    break;
                case 5:
                    buscarSeriesPorAtor();
                    break;
                case 6:
                    buscarTopSeries();
                    break;
                case 7:
                    buscarSeriesPorCategoria();
                    break;
                case 8:
                    buscarSeriesPorTemporadaAvaliacao();
                    break;
                case 9:
                    buscarEpisodioPorTrecho();
                    break;
                case 10:
                    topEpisodiosPorSerie();
                    break;
                case 11:
                    buscarEpisodiosDepoisDeUmaData();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }
    }

    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        Serie serie = new Serie(dados);
        serieRepository.save(serie);
        System.out.println(dados);
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();
        var json = consumoAPI.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = converterDado.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void buscarEpisodioPorSerie(){
        listarSeriesBuscadas();
        System.out.println("Insira o nome da série que deseja pesquisar: ");
        var nomeSerie = leitura.nextLine();

        Optional<Serie> first = series.stream()
                .filter(s -> s.getTitulo().toLowerCase().contains(nomeSerie.toLowerCase()))
                .findFirst();
        if (first.isPresent()){
            var  serieEncontrada = first.get();
            List<DadosTemporada> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumoAPI.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = converterDado.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodioList =  temporadas.stream()
                    .flatMap(d -> d.episodios().stream().map(e -> new Episodio(d.numero(), e)))
                    .collect(Collectors.toList());
            serieEncontrada.setEpisodios(episodioList);
            serieRepository.save(serieEncontrada);

        } else {
            System.out.println("Série não encontrada");
        }


    }

    private void listarSeriesBuscadas(){
        series = serieRepository.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }
    private void listarSeriePeloNome(){
        listarSeriesBuscadas();
        System.out.println("Insira o nome da série que deseja pesquisar: ");
        var nomeSerie = leitura.nextLine();

        serieBuscada = serieRepository.findFirstByTituloContainingIgnoreCase(nomeSerie);
        if (serieBuscada.isPresent()){
            System.out.println("Dados da série: \n" + serieBuscada.get());
        } else {
            System.out.println("Série não encontrada");
        }
    }
    private void buscarSeriesPorAtor(){
        System.out.println("Insira o nome do ator: ");
        var nomeAtor = leitura.nextLine();
        System.out.println("Insira a avaliação: ");
        var avaliacao = leitura.nextDouble();
        List<Serie> seriesEncontradas = serieRepository.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor,avaliacao);
        seriesEncontradas.forEach(System.out::println);
    }
    private void buscarTopSeries(){
        List<Serie> seriesTop = serieRepository.findTop5ByOrderByAvaliacaoDesc();
        seriesTop.forEach(serie ->
                System.out.println(serie.getTitulo() + " avaliação: " + serie.getAvaliacao()));
    }
    private void buscarSeriesPorCategoria(){
        System.out.println("Deseja buscar séries de que categoria/gênero? ");
        var nomeGenero = leitura.nextLine();
        Categoria categoria = Categoria.fromPortugues(nomeGenero);
        List<Serie> seriesPorCategoria = serieRepository.findByGenero(categoria);
        System.out.println("Séries da categoria: " + nomeGenero + ": ");
        seriesPorCategoria.forEach(System.out::println);
    }
    private void buscarSeriesPorTemporadaAvaliacao(){
        System.out.println("Digite o número máximo de temporadas que deseja: ");
        var qtdTemporadas = Integer.valueOf(leitura.nextInt());
        System.out.println("Digite a avaliação mínima para consulta: ");
        var avaliacao = leitura.nextDouble();
        List<Serie> listaSeries = serieRepository.seriesPorTemporadaEAvaliacao(qtdTemporadas,avaliacao);
        System.out.println("Lista de séries com número e avaliação exigidos: ");
        listaSeries.forEach(System.out::println);
    }
    private void buscarEpisodioPorTrecho(){
        System.out.println("Qual o nome do episódio para busca? ");
        var trechoEpisodio = leitura.nextLine();
        List<Episodio> episodiosEncontrados = serieRepository.episodioPorTrecho(trechoEpisodio);
        if (episodiosEncontrados.isEmpty()){
            System.out.println("Nenhum episódio encontrado");
        } else {
            episodiosEncontrados.forEach(e -> System.out.printf("Série: %s Temporada %s - Episódio %s - %s\n",
                    e.getSerie().getTitulo(), e.getTemporada(), e.getNumeroEpisodio(), e.getTitulo()));

        }
    }
    private void topEpisodiosPorSerie(){
        listarSeriePeloNome();
        if (serieBuscada.isPresent()){
            Serie serie = serieBuscada.get();
            List<Episodio> topEpisodios = serieRepository.topEpisodiosPorSerie(serie);
        }

    }
    private void buscarEpisodiosDepoisDeUmaData(){
        listarSeriePeloNome();
        if (serieBuscada.isPresent()){
            Serie serie = serieBuscada.get();
            System.out.println("Pesquisar a partir de qual ano?");
            var anoLancamento = leitura.nextInt();
            leitura.nextLine();

            List<Episodio> episodiosAno = serieRepository.episodiosPorSerieEAno(serie,anoLancamento);
            episodiosAno.forEach(System.out::println);
        }
    }
//        System.out.println("Digite o nome da série para busca");
//        String nomeSerie = leitura.nextLine();
//        var json = consumoAPI.obterDados(ENDERECO + nomeSerie.replace(" ","+") + API_KEY);
//        DadosSerie dadosSerie = converterDado.obterDados(json, DadosSerie.class);
//        System.out.println(dadosSerie);
//
//        List<DadosTemporada> temporadas = new ArrayList<>();
//        for (int i = 1; i < dadosSerie.totalTemporadas(); i++) {
//            json = consumoAPI.obterDados(ENDERECO + nomeSerie.replace(" ","+") + "&Season=" + i + API_KEY);
//            DadosTemporada dadosTemporada = converterDado.obterDados(json, DadosTemporada.class);
//            temporadas.add(dadosTemporada);
//        }
//        temporadas.forEach(System.out::println);
//
//        //Modo raiz de fazer
////        for (int i = 0; i < temporadas.size(); i++) {
////            List<DadosEpisodio> episodios = temporadas.get(i).episodios();
////            for (int j = 0; j < episodios.size(); j++) {
////                System.out.println(episodios.get(i).titulo());
////            }
////        }
//
//        //Printandi episódios sem vincular a qual temporada pertencem
//        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));
//
//        List<DadosEpisodio> dadosEpisodioList = temporadas.stream()
//                .flatMap(t -> t.episodios().stream())
//                .collect(Collectors.toList());
//
//        System.out.println("\n Top 5 episódios: ");
//        dadosEpisodioList.stream()
//                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
//                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
//                .limit(5)
//                .forEach(System.out::println);
//
//
//        List<Episodio> episodios = temporadas.stream()
//                .flatMap(t -> t.episodios().stream().map(d -> new Episodio(t.numero(), d))
//                ).collect(Collectors.toList());
//        //Printando episódios vinculando as temporadas que eles pertencem
//        episodios.forEach(System.out::println);
//
//        System.out.println("Digite o trecho do episódio que deseja buscar");
//        var trechoEpisodio = leitura.nextLine();
//        Optional<Episodio> episodioBuscado = episodios.stream()
//                        .filter(e -> e.getTitulo().toUpperCase().contains(trechoEpisodio.toUpperCase()))
//                                .findFirst();
//        if (episodioBuscado.isPresent()){
//            System.out.println("Temporada: " + episodioBuscado.get().getTemporada());
//        } else {
//            System.out.println("Episódio não encontrado");
//        }
//
//        System.out.println("A partir de que ano deseja ver os episódios?");
//        var ano = leitura.nextInt();
//        leitura.nextLine();
//
//        LocalDate dataBusca = LocalDate.of(ano, 1, 1);
//
//        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
//        episodios.stream()
//                .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca))
//                .forEach(e -> System.out.println(
//                        "Temporada: " + e.getTemporada() +
//                                "Episódio: " + e.getTitulo() +
//                                "Data lançamento: " + e.getDataLancamento().format(dateTimeFormatter)
//                ));
//
//        Map<Integer, Double> classificacaoTemp = episodios.stream()
//                .filter(e -> e.getAvaliacao() > 0.0)
//                .collect(Collectors.groupingBy(Episodio::getTemporada,
//                        Collectors.averagingDouble(Episodio::getAvaliacao)));
//        System.out.println("Classificação por temporada: ");
//        System.out.println(classificacaoTemp);
//
//        DoubleSummaryStatistics est = episodios.stream()
//                .filter(e -> e.getAvaliacao() > 0.0)
//                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));
//        System.out.println("Summary: ");
//        System.out.println(est);

}
