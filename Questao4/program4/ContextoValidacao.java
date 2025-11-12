import java.time.Clock;

public class ContextoValidacao {
    
    private final Clock relogio;
    private final RepositorioDocumentosFiscais repositorio;
    private final ServicoSefazCliente servicoSefazCliente;

    public ContextoValidacao(Clock relogio,
                             RepositorioDocumentosFiscais repositorio,
                             ServicoSefazCliente servicoSefazCliente) {
        this.relogio = relogio;
        this.repositorio = repositorio;
        this.servicoSefazCliente = servicoSefazCliente;
    }

    public Clock getRelogio() {
        return relogio;
    }

    public RepositorioDocumentosFiscais getRepositorio() {
        return repositorio;
    }

    public ServicoSefazCliente getServicoSefazCliente() {
        return servicoSefazCliente;
    }
}


