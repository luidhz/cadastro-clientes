package com.luiz.cadastroclientes.service;

import com.luiz.cadastroclientes.entity.Cliente;
import com.luiz.cadastroclientes.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository repository;

    public Cliente salvar(Cliente cliente) {
        return repository.save(cliente);
    }

    public List<Cliente> listarTodos() {
        return repository.findAll();
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }

    public Cliente atualizar(Long id, Cliente cliente){
        Cliente clienteExistente =
                repository.findById(id).orElseThrow();
        clienteExistente.setNome(cliente.getNome());
        clienteExistente.setEmail(cliente.getEmail());
        clienteExistente.setIdade(cliente.getIdade());
        return repository.save(clienteExistente);
    }

}