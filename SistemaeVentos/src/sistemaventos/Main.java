package sistemaventos;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static SistemaEventos sistema = new SistemaEventos();
    private static Scanner scanner = new Scanner(System.in);
    private static Usuario usuarioLogado = null;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static void main(String[] args) {
        try { sistema.carregarEventos(); } catch (Exception e) {
            System.out.println("Erro ao carregar eventos: " + e.getMessage());
        }
        menuPrincipal();
    }

    private static void menuPrincipal() {
        while (true) {
            System.out.println("\n=== Sistema de Eventos da Cidade ===");
            System.out.println("1 - Cadastrar Usuário");
            System.out.println("2 - Cadastrar Evento");
            System.out.println("3 - Listar Eventos");
            System.out.println("4 - Participar de Evento");
            System.out.println("5 - Cancelar participação");
            System.out.println("6 - Meus Eventos");
            System.out.println("7 - Eventos no momento");
            System.out.println("8 - Eventos passados");
            System.out.println("9 - Salvar e Sair");
            System.out.print("Escolha uma opção: ");
            int op = Integer.parseInt(scanner.nextLine());
            switch (op) {
                case 1: cadastrarUsuario(); break;
                case 2: cadastrarEvento(); break;
                case 3: listarEventos(); break;
                case 4: participarEvento(); break;
                case 5: cancelarParticipacao(); break;
                case 6: eventosDoUsuario(); break;
                case 7: eventosOcorrendo(); break;
                case 8: eventosPassados(); break;
                case 9: salvarESair(); return;
                default: System.out.println("Opção inválida");
            }
        }
    }

    private static void cadastrarUsuario() {
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Idade: ");
        int idade = Integer.parseInt(scanner.nextLine());
        usuarioLogado = new Usuario(nome, email, idade);
        sistema.cadastrarUsuario(usuarioLogado);
        System.out.println("Usuário cadastrado e logado com sucesso!");
    }

    private static void cadastrarEvento() {
        System.out.print("Nome do evento: ");
        String nome = scanner.nextLine();
        System.out.print("Endereço: ");
        String endereco = scanner.nextLine();
        System.out.println("Categorias disponíveis: Festa, Esporte, Show, Cultura");
        System.out.print("Selecione a categoria: ");
        String categoria = scanner.nextLine();
        if (!categoria.equalsIgnoreCase("Festa") && !categoria.equalsIgnoreCase("Esporte")
                && !categoria.equalsIgnoreCase("Show") && !categoria.equalsIgnoreCase("Cultura")) {
            System.out.println("Categoria inválida. Evento não cadastrado.");
            return;
        }
        System.out.print("Horário (dd/MM/yyyy HH:mm): ");
        String horarioStr = scanner.nextLine();
        LocalDateTime horario;
        try { horario = LocalDateTime.parse(horarioStr, formatter); }
        catch (Exception e) {
            System.out.println("Formato de data/hora inválido. Evento não cadastrado.");
            return;
        }
        System.out.print("Descrição: ");
        String descricao = scanner.nextLine();
        Evento ev = new Evento(nome, endereco, categoria, horario, descricao);
        sistema.cadastrarEvento(ev);
        System.out.println("Evento cadastrado com sucesso!");
    }

    private static void listarEventos() {
        List<Evento> eventos = sistema.listarEventos();
        if (eventos.isEmpty()) {
            System.out.println("Nenhum evento cadastrado.");
            return;
        }
        System.out.println("\nEventos cadastrados:");
        for (int i = 0; i < eventos.size(); i++) {
            Evento e = eventos.get(i);
            System.out.printf("%d) %s - %s - %s às %s\n", i + 1, e.getNome(), e.getCategoria(),
                    e.getEndereco(), e.getHorario().format(formatter));
        }
    }

    private static void participarEvento() {
        if (usuarioLogado == null) {
            System.out.println("Cadastre e faça login primeiro.");
            return;
        }
        listarEventos();
        System.out.print("Digite o número do evento para participar: ");
        int num = Integer.parseInt(scanner.nextLine()) - 1;
        List<Evento> eventos = sistema.listarEventos();
        if (num < 0 || num >= eventos.size()) {
            System.out.println("Evento inválido.");
            return;
        }
        Evento evento = eventos.get(num);
        if (sistema.participarEvento(usuarioLogado, evento)) {
            System.out.println("Participação confirmada.");
        } else {
            System.out.println("Você já está participando deste evento.");
        }
    }

    private static void cancelarParticipacao() {
        if (usuarioLogado == null) {
            System.out.println("Cadastre e faça login primeiro.");
            return;
        }
        List<Evento> participando = sistema.eventosParticipando(usuarioLogado);
        if (participando.isEmpty()) {
            System.out.println("Você não está participando de nenhum evento.");
            return;
        }
        System.out.println("Eventos que você está participando:");
        for (int i = 0; i < participando.size(); i++) {
            Evento e = participando.get(i);
            System.out.printf("%d) %s - %s - %s às %s\n", i + 1, e.getNome(), e.getCategoria(),
                    e.getEndereco(), e.getHorario().format(formatter));
        }
        System.out.print("Digite o número do evento para cancelar participação: ");
        int num = Integer.parseInt(scanner.nextLine()) - 1;
        if (num < 0 || num >= participando.size()) {
            System.out.println("Entrada inválida.");
            return;
        }
        Evento evento = participando.get(num);
        if (sistema.cancelarParticipacao(usuarioLogado, evento)) {
            System.out.println("Participação cancelada.");
        } else {
            System.out.println("Erro ao cancelar participação.");
        }
    }

    private static void eventosDoUsuario() {
        if (usuarioLogado == null) {
            System.out.println("Cadastre e faça login primeiro.");
            return;
        }
        List<Evento> participando = sistema.eventosParticipando(usuarioLogado);
        if (participando.isEmpty()) {
            System.out.println("Você não está participando de nenhum evento.");
            return;
        }
        System.out.println("Eventos em que você está participando:");
        for (Evento e : participando) {
            System.out.printf("%s às %s\n", e.getNome(), e.getHorario().format(formatter));
        }
    }

    private static void eventosOcorrendo() {
        List<Evento> ocorrendo = sistema.eventosOcorrendo();
        if (ocorrendo.isEmpty()) {
            System.out.println("Não há eventos ocorrendo no momento.");
            return;
        }
        System.out.println("Eventos ocorrendo agora:");
        for (Evento e : ocorrendo) {
            System.out.printf("%s - %s às %s\n", e.getNome(), e.getCategoria(), e.getHorario().format(formatter));
        }
    }

    private static void eventosPassados() {
        List<Evento> passados = sistema.eventosPassados();
        if (passados.isEmpty()) {
            System.out.println("Não há eventos passados.");
            return;
        }
        System.out.println("Eventos passados:");
        for (Evento e : passados) {
            System.out.printf("%s - %s às %s\n", e.getNome(), e.getCategoria(), e.getHorario().format(formatter));
        }
    }

    private static void salvarESair() {
        try {
            sistema.salvarEventos();
            System.out.println("Eventos salvos com sucesso. Saindo...");
        } catch (Exception e) {
            System.out.println("Erro ao salvar eventos: " + e.getMessage());
        }
    }
}
