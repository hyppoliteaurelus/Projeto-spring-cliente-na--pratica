package br.com.dio.service.impl;

import br.com.dio.model.Cliente;
import br.com.dio.model.ClienteRepository;
import br.com.dio.model.Endereco;
import br.com.dio.model.EnderecoRepository;
import br.com.dio.service.ClienteService;
import br.com.dio.service.ViaCepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

///**
// * Implementação da <b>Strategy</b> {@link ClienteService}, a qual pode ser
// * injetada pelo Spring (via {@link Autowired}). Com isso, como essa classe é um
// * {@link Service}, ela será tratada como um <b>Singleton</b>.
// *
// * @author falvojr
// */
@Service
public class ClienteServiceImpl implements ClienteService {

    // Singleton: Injetar os componentes do Spring com @Autowired.
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private EnderecoRepository enderecoRepository;
    @Autowired
    private ViaCepService viaCepService;

    // Strategy: Implementar os métodos definidos na interface.
    // Facade: Abstrair integrações com subsistemas, provendo uma interface simples.

    @Override
    public Iterable<Cliente> buscarTodos() {
        // Buscar todos os Clientes.
        return clienteRepository.findAll();
    }

    @Override
    public Cliente buscarPorId(Long id) {
        // Buscar Cliente por ID.
        Optional<Cliente> cliente = clienteRepository.findById(id);
        return cliente.orElse(null);
    }

    @Override
    public void inserir(Cliente cliente) {

        salvarClienteComCep(cliente);
    }

    @Override
    public void atualizar(Long id, Cliente cliente) {
        // Buscar Cliente por ID, caso exista:
        Optional<Cliente> clienteBd = clienteRepository.findById(id);
        if (clienteBd.isPresent()) {
            Cliente clienteExistente = clienteBd.get();
            clienteExistente.setNome(cliente.getNome()); // Atualiza apenas o nome por exemplo
            salvarClienteComCep(clienteExistente);
            salvarClienteComCep(cliente);
        }
    }

    @Override
    public void deletar(Long id) {
        // Deletar Cliente por ID.
        clienteRepository.deleteById(id);
    }

    private void salvarClienteComCep(Cliente cliente) {
        String cep = cliente.getEndereco().getCep();
        Endereco endereco = enderecoRepository.findById(cep).orElseGet(() -> {
            Endereco novoEndereco = viaCepService.consultarCep(cep);
            novoEndereco.setCep(cep); // Definir manualmente o identificador
            return enderecoRepository.save(novoEndereco);
        });
        cliente.setEndereco(endereco);
        clienteRepository.save(cliente);
    }
}