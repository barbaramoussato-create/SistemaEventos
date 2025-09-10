package sistemaventos;

import java.util.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SistemaEventos {
    private List<Usuario> usuarios = new ArrayList<>();
    private List<Evento> eventos = new ArrayList<>();
    private Map<Usuario, List<Evento>> participacoes = new HashMap<>();
    private static final String ARQUIVO = "events.data";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public void cadastrarUsuario(Usuario u) {
        usuarios.add(u);
    }

    public void cadastrarEvento(Evento e) {
        eventos.add(e);
        eventos.sort(Comparator.comparing(Evento::getHorario));
    }

    public List<Evento> listarEventos() {
        return new ArrayList<>(eventos);
    }

    public boolean participarEvento(Usuario u, Evento e) {
        participacoes.putIfAbsent(u, new ArrayList<>());
        List<Evento> lista = participacoes.get(u);
        if (!lista.contains(e)) {
            lista.add(e);
            return true;
        }
        return false;
    }

    public boolean cancelarParticipacao(Usuario u, Evento e) {
        if (participacoes.containsKey(u)) {
            return participacoes.get(u).remove(e);
        }
        return false;
    }

    public List<Evento> eventosParticipando(Usuario u) {
        return participacoes.getOrDefault(u, new ArrayList<>());
    }

    public List<Evento> eventosOcorrendo() {
        LocalDateTime agora = LocalDateTime.now();
        List<Evento> acontecendo = new ArrayList<>();
        for (Evento e : eventos) {
            if (e.getHorario().isBefore(agora) && e.getHorario().plusHours(3).isAfter(agora)) {
                acontecendo.add(e);
            }
        }
        return acontecendo;
    }

    public List<Evento> eventosPassados() {
        LocalDateTime agora = LocalDateTime.now();
        List<Evento> passados = new ArrayList<>();
        for (Evento e : eventos) {
            if (e.getHorario().plusHours(3).isBefore(agora)) {
                passados.add(e);
            }
        }
        return passados;
    }

    public void salvarEventos() throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ARQUIVO))) {
            for (Evento e : eventos) {
                String linha = String.join("|",
                        e.getNome(),
                        e.getEndereco(),
                        e.getCategoria(),
                        e.getHorario().format(formatter),
                        e.getDescricao()
                );
                writer.println(linha);
            }
        }
    }

    public void carregarEventos() throws IOException {
        eventos.clear();
        File file = new File(ARQUIVO);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(ARQUIVO))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] partes = linha.split("\\|");
                if (partes.length == 5) {
                    LocalDateTime horario = LocalDateTime.parse(partes[3], formatter);
                    Evento e = new Evento(
                        partes[0],      // nome
                        partes[1],      // endereco
                        partes[2],      // categoria
                        horario,        // horario
                        partes[4]       // descricao
                    );
                    eventos.add(e);
                }
            }
        }
        eventos.sort(Comparator.comparing(Evento::getHorario));
    }
}
