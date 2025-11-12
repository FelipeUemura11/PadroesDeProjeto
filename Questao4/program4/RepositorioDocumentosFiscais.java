import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class RepositorioDocumentosFiscais {
    private final Set<String> numerosRegistrados = Collections.synchronizedSet(new HashSet<>());

    public boolean existeNumero(String numero) {
        return numerosRegistrados.contains(numero);
    }

    public void registrarNumero(String numero) {
        numerosRegistrados.add(numero);
    }

    public void removerNumero(String numero) {
        numerosRegistrados.remove(numero);
    }
}


